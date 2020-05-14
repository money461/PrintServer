package com.bondex.service.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import com.alibaba.fastjson.JSONObject;
import com.bondex.common.enums.ComEnum;
import com.bondex.common.enums.ResEnum;
import com.bondex.config.exception.BusinessException;
import com.bondex.controller.subscribe.SubscribeController;
import com.bondex.dao.LabelInfoDao;
import com.bondex.dao.LogInfoDao;
import com.bondex.entity.Label;
import com.bondex.entity.LabelAndTemplate;
import com.bondex.entity.Subscribe;
import com.bondex.entity.Template;
import com.bondex.entity.log.Log;
import com.bondex.entity.msg.Head;
import com.bondex.entity.msg.JsonRootBean;
import com.bondex.service.LabelInfoService;
import com.bondex.shiro.security.entity.UserInfo;
import com.bondex.util.GsonUtil;
import com.bondex.util.LocalDateTimeUtils;
import com.bondex.util.StringUtils;
import com.bondex.util.shiro.ShiroUtils;
import com.google.gson.JsonSyntaxException;

@Service(value="labelInfoServiceImpl")
public class LabelInfoServiceImpl implements LabelInfoService {

	/**
	 * org.slf4j.Logger
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private LabelInfoDao labelInfoDao;
	
	@Autowired
	private LogInfoDao logInfoDao;
	

	//保存标签数据
	@Override
	public boolean labelInfoSave(String messge,String correlationId) {
		Boolean flag = logInfoDao.checkCorrelationIdUnique(correlationId);
		if(flag){return false;} //重复消费 消息自动应答Ack,结束此次消费
		
		Log log = new Log();
		Date date=  Date.from(LocalDateTime.now().toInstant(ZoneOffset.of("+8"))); //默认时区为东8区
		log.setUpdateTime(date);
		log.setCreateTime(date);
		log.setJson(messge);
		log.setStatus(1);
		log.setCorrelationId(correlationId);//消息唯一标识
		log.setCode(ComEnum.AirLabel.code); //监听业务固定 所以标签固定写死 
		log.setCodeName(ComEnum.AirLabel.codeName);
		Integer i=0;
		try {
			
			JsonRootBean jsonRootBean = GsonUtil.GsonToBean(messge, JsonRootBean.class);
			Head head = jsonRootBean.getHead();
			log.setSeqNo(head.getSeqNo());
			log.setSenderName(head.getSenderName());
			log.setReciverName(head.getReciverName());	
			log.setDoctypeName(head.getDocTypeName());
			log.setStatus(0); //成功
			String main = jsonRootBean.getMain();
			JSONObject parseObject = JSONObject.parseObject(main);
			String mawb = parseObject.getString("PARENT_BILL_NO");
			log.setMawb(mawb);
			log.setHawb(parseObject.getString("BILL_NO"));
			i =labelInfoDao.saveLabel(main,log);
			if(i <= 0){
				log.setDetail("入库未抛异常，但结果返回小于1");
			}else{
				log.setDetail("入库成功！");
			}
			
		}catch (JsonSyntaxException e){
			//JSON转换异常
			log.setDetail("报文json转换失败。入库数据已经回滚");
			
		} catch (BusinessException e) { //自定义异常
			log.setStatus(3);
			log.setDetail(e.getMessage());
			
		} catch (HttpClientErrorException e) {
			log.setDetail("获取部门信息接口失败异常！");
		} catch (Exception e) {
			log.setDetail("数据入库未知异常！");
			
		}finally {
			logInfoDao.insertLableLog(log); //日志入库
		}
		
		if(1==i){
			return true;
		}
		return false;
		
	}

	@Override
	public List<LabelAndTemplate>  selectLabelByPage( Label label){
		
	 try {
			
		UserInfo userInfo = ShiroUtils.getUserInfo();
		String opid = userInfo.getOpid();
		List<String> opidData = userInfo.getOpidData();
		String opids = opidData.stream().collect(Collectors.joining("','")); //获取用户部门所有的opid
		//拼接SQL语句
		StringBuffer sql = new StringBuffer();
		sql.append("select 	label.*, t.template_id,	t.template_name from label LEFT JOIN template t  on t.id = label.reserve3 where label.opid in ('" + opids + "') ");
		sql.append(" and label.business_type <> 0 "); //不是派昂医药的数据
		sql.append(" and label.reserve1 = '0' "); //未删除
		sql.append(" and label.code = '"+label.getCode()+"' "); 
		
		Map<String, Object> params = label.getParams();
		if(StringUtils.isNotEmpty(params)&& StringUtils.isNotBlank((String)params.get("startTime"))){
			String beginTime = LocalDateTimeUtils.getBeginTime((String)params.get("startTime"),LocalDateTimeUtils.YYYY_MM_DD).toString();
			sql.append(" AND label.create_time >= '"+beginTime+"'");
		}
		if(StringUtils.isNotEmpty(params) && StringUtils.isNotBlank((String)params.get("endTime"))){
			String endTime = LocalDateTimeUtils.getEndTime((String)params.get("endTime"),LocalDateTimeUtils.YYYY_MM_DD).toString();
			sql.append(" AND label.create_time <= '"+endTime+"'");
		}
		
		//订阅信息
		List<Subscribe> listSubscribe;
		
		if ((listSubscribe = SubscribeController.subscribeMap.get(opid)) != null) {
			StringBuilder builder = new StringBuilder();
			for (Subscribe subscribe : listSubscribe) {
				builder.append("'" + subscribe.getSrMawb() + "',");
			}
			sql.append("and label.mawb in (" + builder.substring(0, builder.length() - 1).toString() + ") ");
			
		} else if (label.getMawb() != null && !label.getMawb().equals("undefined") && !label.getMawb().trim().equals("")) {
			if (label.getMawb().length() < 11) {
				return new ArrayList<>();
			}

			List<String> list = GsonUtil.GsonToList(label.getMawb(), String.class);
			String mawb = "";
			for (String string : list) {
				String tmp = "";
				if (!string.equals("")) {
					tmp = Pattern.compile("[^0-9]").matcher(string).replaceAll("");// 提取数字
					String head = tmp.substring(0, 3);
					String body = tmp.substring(3, 11);
					mawb += "'" + head + "-" + body + "',";
				}
			}
			sql.append("and label.mawb in (" + mawb.substring(0, mawb.length() - 1) + ") ");
		}
		
		if (label.getLabelId() != null && !label.getLabelId().equals("undefined") && !label.getLabelId().equals("")) {
			sql.append("and label.label_id = " + label.getLabelId() + " ");
		}
		if (label.getHawb() != null && !label.getHawb().equals("undefined") && !label.getHawb().equals("")) {
			sql.append("and label.hawb like '%" + label.getHawb() + "%' ");
		}
		if (label.getOpidName() != null && !label.getOpidName().equals("undefined") && !label.getOpidName().equals("") && !label.getOpidName().equals("全部")) {
			sql.append("and label.opid_name like '%" + label.getOpidName() + "%' ");
		}
		if (label.getTotal() != null && !label.getTotal().equals("undefined") && !label.getTotal().equals("")) {
			sql.append("and label.total = '" + label.getTotal() + "' ");
		}
		if (label.getFlightDate() != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			sql.append("and label.flight_date = '" + dateFormat.format(label.getFlightDate()) + "' ");
		}
		if (label.getAirportDeparture() != null && !label.getAirportDeparture().equals("undefined") && !label.getAirportDeparture().equals("") && !label.getAirportDeparture().equals("全部")) {
			sql.append("and label.airport_departure like '%" + label.getAirportDeparture() + "%' ");
		}
		if (label.getDestination() != null && !label.getDestination().equals("undefined") && !label.getDestination().equals("") && !label.getDestination().equals("全部")) {
			sql.append("and label.destination like '%" + label.getDestination() + "%' ");
		}
		if (label.getIsPrint() != null && !label.getIsPrint().equals("undefined") && !label.getIsPrint().equals("")) {
			sql.append("and label.is_print = '" + label.getIsPrint() + "' ");
		} else {
			sql.append("and label.is_print = '0' ");
		}
		
		//**********根据用户办公室地址检索查询标签起始地址  考虑废除
		//sql.append("and airport_departure = (SELECT load_code from load_code where region_parent_code = (select parent_code from region where region_id = (SELECT office_id from default_region where opid = '" + opid + "' and type = 1))) ");
		//查询模板及其数据
		List<LabelAndTemplate> list = labelInfoDao.selectLabelByPage(sql.toString());
		
		if (listSubscribe != null) {
			SubscribeController.subscribeMap.remove(opid);// 查询完成后清除缓存
		}
		
		logger.debug(GsonUtil.GsonString(list));
		
		return list;
	 } catch (Exception e) {
		 e.printStackTrace();
		 throw new BusinessException(ResEnum.FAIL.CODE, "查询数据异常,请正确写入参数!");
	 }
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateLabel(Label label) {
		labelInfoDao.update(label);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteLabel(List<Label> label) {
		labelInfoDao.delete(label);
	}


	
	@Override
	public List<Template> getUserAuthtemplate(Template template) {
		return labelInfoDao.getUserAuthtemplate(template);
	}


}
