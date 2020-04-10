package com.bondex.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bondex.common.Common;
import com.bondex.config.exception.BusinessException;
import com.bondex.dao.ClientDao;
import com.bondex.dao.CurrentLabelDao;
import com.bondex.dao.LabelInfoDao;
import com.bondex.dao.LogInfoDao;
import com.bondex.dao.PrintLogDao;
import com.bondex.entity.Region;
import com.bondex.entity.Template;
import com.bondex.entity.client.Client;
import com.bondex.entity.current.BaseLabelDetail;
import com.bondex.entity.log.Log;
import com.bondex.entity.log.PrintLog;
import com.bondex.entity.res.AjaxResult;
import com.bondex.rabbitmq.Producer;
import com.bondex.service.CurrentLabelService;
import com.bondex.shiro.security.entity.UserInfo;
import com.bondex.util.GsonUtil;
import com.bondex.util.StringUtils;
import com.bondex.util.shiro.ShiroUtils;
import com.google.gson.JsonSyntaxException;
@Service
public class CurrentLabelServiceImpl implements CurrentLabelService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CurrentLabelDao currentLabelDao;
	
	@Autowired
	private LogInfoDao logInfoDao;
	
	@Autowired
	private ClientDao clientDao;
	
	@Autowired
	private LabelInfoDao  LabelInfoDao;
	
	@Autowired
	private PrintLogDao  printLogDao;
	
	@Autowired
	private Producer producer;
	

	@Override
	public List<BaseLabelDetail> selectBaseLabelList(BaseLabelDetail baseLabelDetail) {
		return currentLabelDao.selectBaseLabelList(baseLabelDetail,true);
	}

	@Override
	public void updateBaseLabel(List<BaseLabelDetail> list) {
		
		currentLabelDao.insertforUpdateBaseLabel(list);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Object saveBaseLabelMsg(String message) {
		Log log = new Log();
		Date date=  Date.from(LocalDateTime.now().toInstant(ZoneOffset.of("+8"))); //默认时区为东8区
		log.setUpdateTime(date);
		log.setCreateTime(date);
		log.setJson(message);
		log.setStatus(1); //入库失败
		String showNumLog="";
		String docTypeNameLog="";
		try {
			ArrayList<BaseLabelDetail> list = new ArrayList<BaseLabelDetail>();
			JSONArray parseArray = JSONObject.parseArray(message);
			for(int i=0;i<parseArray.size();i++){
				JSONObject jsonObject = parseArray.getJSONObject(i);
				String code = jsonObject.getString("code");
				log.setCode(code); //业务code
				String codoName = jsonObject.getString("codeName");
				log.setCodeName(codoName);
				String showNum = jsonObject.getString("showNum");
				showNum = showNum==null?"":showNum;
				showNumLog += showNum +",";
				String jsonData = jsonObject.getString("jsonData");
				String docTypeName = jsonObject.getString("doctypeName");
				docTypeName = docTypeName==null?"":docTypeName;
				docTypeNameLog += docTypeName + ",";
				String opidName = jsonObject.getString("opidName");
				if(StringUtils.isBlank(showNum) ||StringUtils.isBlank(code) || StringUtils.isBlank(code) || StringUtils.isBlank(jsonData) || StringUtils.isBlank(opidName) ||StringUtils.isBlank(docTypeName)  ){
					throw new BusinessException("必填字段为空！");
				}
				BaseLabelDetail baseLabelDetail = GsonUtil.GsonToBean(jsonObject.toJSONString(), BaseLabelDetail.class);
				baseLabelDetail.setCreateTime(date);//设置创建时间
				list.add(baseLabelDetail);
			}
			
			//保存数据
			currentLabelDao.insertBaseLabel(list);
			log.setStatus(0); //入库成功
			log.setDetail("入库成功！");
			
		}catch (JsonSyntaxException e){
			//JSON转换异常
			log.setDetail("报文json转换失败。入库数据已经回滚");
			
		} catch (BusinessException e) { //自定义异常
			log.setStatus(3);
			log.setDetail(e.getMessage());
			
		} catch (Exception e) {
			log.setStatus(3);
			log.setDetail("数据入库未知异常！");
			System.out.println(Thread.currentThread().getName()+"发生异常："+e.getMessage());
			e.printStackTrace();
			
		}finally {
			//日志入库
			log.setMawb(showNumLog);
			log.setDoctypeName(docTypeNameLog);
			logInfoDao.insertLableLog(log); //日志入库
			
		}
		int i = log.getStatus();
		AjaxResult result = null;
		if(0==i){
			result = AjaxResult.success(log.getDetail());
		}else{
			result = AjaxResult.error(log.getDetail());
		}
		return result;
	}

	//打印标签并写入打印日志
	//异常回滚状态
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void printCurrentLabelSendClient(List<BaseLabelDetail> list, String regionCode, String mqaddress) {
		UserInfo userInfo = ShiroUtils.getUserInfo();
		String gsonString = GsonUtil.GsonString(list);
		logger.debug("打印的数据：{}",gsonString);
		PrintLog printLog = new PrintLog();
		Date date=  Date.from(LocalDateTime.now().toInstant(ZoneOffset.of("+8"))); //默认时区为东8区
		printLog.setUpdateTime(date);
		printLog.setCreateTime(date);
		printLog.setMessage(gsonString);
		printLog.setStatus(1); //打印失败
		printLog.setMqaddress(mqaddress);
		printLog.setOpid(userInfo.getOpid());
		printLog.setOpidName(userInfo.getOpname());
		try {
			String id = list.stream().map(res -> String.valueOf(res.getId())).collect(Collectors.joining(","));
			printLog.setLabelId(id);
			String showNum = list.stream().map(res -> String.valueOf(res.getShowNum())).collect(Collectors.joining(","));
			printLog.setShowNum(showNum);
			BaseLabelDetail baseLabelDetail = list.get(0);
			printLog.setCode(baseLabelDetail.getCode()); //业务code
			printLog.setCodeName(baseLabelDetail.getCodeName());
			//校验打印办公室
			// 获取打印区域 从数据库中获取
			Region region = clientDao.getRegionByRegioncode(regionCode);
			if(StringUtils.isNull(region)){
				throw new BusinessException("请指定打印办公室！");
			}
			printLog.setQueueCode(region.getParentCode()+"_"+region.getRegionCode()); //打印办公室
			printLog.setRegionName(region.getParentName()+"/"+region.getRegionName());
			
			//封装打印客户端 且发送客户端
			List<Client> clientList = SendClientList(list,userInfo,mqaddress,region);
			printLog.setStatus(0); //打印成功
			printLog.setReason("打印数据发送成功！");
			printLog.setMessage(GsonUtil.GsonString(clientList));
			
		} catch (BusinessException e) { //自定义异常
			printLog.setReason(e.getMessage());
			
		} catch (Exception e) {
			printLog.setReason("通用打印逻辑发生未知异常！"+e.getMessage());
			
		}finally {
			printLogDao.insertPrintLog(printLog); //打印日志入库
		}
		
		if(1==printLog.getStatus()){
			throw new BusinessException("失败！"+printLog.getReason());
		}
		
	}
	


	private void sendClient(Client client,String mqaddress,Region region) throws Exception{
		try {
			// 发送MQ 执行打印
			if(Common.MQAddress_VPNNET.equals(mqaddress)){
				producer.vpnNetPrint(client, region); //发送内网
			}else if(Common.MQAddress_OUTNET.equals(mqaddress)){
				producer.outNetPrint(client, region); //发送外网
			}else{
				producer.print(client, region); //封装打印信息 携带打印区域
			}
			
		} catch (Exception e) {
			throw new BusinessException("发送至"+mqaddress+"打印消息失败！");
		}
	}
	
	
	private List<Client> SendClientList(List<BaseLabelDetail> list, UserInfo userInfo, String mqaddress, Region region) throws Exception {
		List<Client> clientList = new ArrayList<Client>();
		for (BaseLabelDetail baseLabelDetail : list) {
			String templateId = baseLabelDetail.getTemplateId();
			//获取当前用户的打印权限
			Template template = LabelInfoDao.checkUseTemplate(templateId);
			//开始封装
			//为了防止前端传过来的数据有误
			baseLabelDetail.setTemplateId(template.getTemplateId());
			baseLabelDetail.setTemplateName(template.getTemplateName());
			baseLabelDetail.setWidth(template.getWidth());
			baseLabelDetail.setHeight(template.getHeight());
			baseLabelDetail.setPrintName(userInfo.getOpname()); //设置打印人
			Client currentClient = getCurrentClients(baseLabelDetail, userInfo); //封装打印客户端
			clientList.add(currentClient);
			//修改打印状态 并入库
			baseLabelDetail.setPrintStatus(1);//已打印
			baseLabelDetail.setPrintId(userInfo.getOpid());
			baseLabelDetail.setPrintName(userInfo.getOpname());
			currentLabelDao.updateBaseLabelById(baseLabelDetail);
		}
		for (Client currentClient : clientList) {
			sendClient(currentClient, mqaddress, region);
		}
		return clientList;
		
	}
	
	
	//封装打印数据
	private Client getCurrentClients(BaseLabelDetail label, UserInfo userInfo) {
				//循环封装打印数据
				Client client = new Client();// 打印客户端数据结构
				String templateId =label.getTemplateId();
				client.setReportID(templateId);// 模板id
				client.setReportTplName(label.getTemplateName());// 标签名称
				client.setSenderName(userInfo.getOpname() + "/" + userInfo.getPsnname()); //此处改动不是很清楚
				client.setSendOPID(userInfo.getOpid());// 操作号
				client.setNoToShow(label.getShowNum());// 要显示的单号
				client.setOtherToShow("");
				client.setReportWidth(label.getWidth());// 标签宽度，单位毫米（目前定死）
				client.setReportHeight(label.getHeight());// 标签高度，单位毫米（目前定死）
				client.setCopies(label.getCopies());// 标签打印份数
				
				JSONArray array = new JSONArray();
				array.add(label.getJsonData()); //打印数据
				
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("vwOrderAll", array);
				client.setData(jsonObject.toJSONString());
				return client;
		}

}
