package com.bondex.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bondex.common.enums.ResEnum;
import com.bondex.config.exception.BusinessException;
import com.bondex.controller.subscribe.SubscribeController;
import com.bondex.dao.LabelInfoDao;
import com.bondex.entity.Label;
import com.bondex.entity.LabelAndTemplate;
import com.bondex.entity.Subscribe;
import com.bondex.entity.Template;
import com.bondex.entity.msg.JsonRootBean;
import com.bondex.entity.page.Datagrid;
import com.bondex.service.LabelInfoService;
import com.bondex.shiro.security.entity.UserInfo;
import com.bondex.util.GsonUtil;
import com.bondex.util.shiro.ShiroUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service(value="labelInfoServiceImpl")
@Transactional(rollbackFor = Exception.class)
public class LabelInfoServiceImpl implements LabelInfoService {

	/**
	 * org.slf4j.Logger
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private LabelInfoDao labelInfoDao;

	//保存标签数据
	@Override
	public boolean labelInfoSave(JsonRootBean jsonRootBean) {
		labelInfoDao.saveLabel(jsonRootBean);
		return true;
	}

	@Override
	public Datagrid findByPage(String page, String rows, Label label, String start_time, String end_time, String sort, String order,String businessType) {
	 try {
			

		UserInfo userInfo = ShiroUtils.getUserInfo();
		String opid = userInfo.getOpid();
		List<String> opidData = userInfo.getOpidData();
		String opids = opidData.stream().collect(Collectors.joining(",")); //获取用户部门所有的opid
		page = null == page ? "1" : page;
		rows = null == rows ? "20" : rows;
		//分页
		PageHelper.startPage(Integer.valueOf(page),Integer.valueOf(rows));

		//拼接SQL语句
		StringBuffer sql = new StringBuffer();
		sql.append("select * from label LEFT JOIN template t  on t.id = label.reserve3 where label.opid in (" + opids + ") ");
//		sql.append("select * from label LEFT JOIN template t  on t.id = label.reserve3 where t.template_id in (" + rt + ") ");
		sql.append(" and business_type <> 0 "); //不是派昂医药的数据
		sql.append("and reserve1 = '0' "); //未删除
		
		if (start_time != null && !start_time.equals("undefined") && !start_time.equals("")) {
			if (businessType.equals("medicine")) {
				sql.append("and EDeparture >= '" + start_time + "' ");
			} else {
				sql.append("and create_time >= '" + start_time + "' ");
			}
		}
		if (end_time != null && !end_time.equals("undefined") && !end_time.equals("")) {
			if (businessType.equals("medicine")) {
				sql.append("and EDeparture <= '" + end_time + "' ");
			} else {
				sql.append("and create_time <= '" + end_time + "' ");
			}
		}
		
		
		//订阅信息
		List<Subscribe> listSubscribe;
		
		if ((listSubscribe = SubscribeController.subscribeMap.get(opid)) != null) {
			rows = "100";// 改为100，防止出现第二页
			StringBuilder builder = new StringBuilder();
			for (Subscribe subscribe : listSubscribe) {
				builder.append("'" + subscribe.getSrMawb() + "',");
			}
			sql.append("and mawb in (" + builder.substring(0, builder.length() - 1).toString() + ") ");
			
		} else if (label.getMawb() != null && !label.getMawb().equals("undefined") && !label.getMawb().trim().equals("")) {
			if (label.getMawb().length() < 11) {
				return new Datagrid<>(0l, new ArrayList<>());
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
			sql.append("and mawb in (" + mawb.substring(0, mawb.length() - 1) + ") ");
		}
		
		if (label.getHawb() != null && !label.getHawb().equals("undefined") && !label.getHawb().equals("")) {
			sql.append("and hawb like '%" + label.getHawb() + "%' ");
		}
		if (label.getOpid_name() != null && !label.getOpid_name().equals("undefined") && !label.getOpid_name().equals("") && !label.getOpid_name().equals("全部")) {
			sql.append("and opid_name like '%" + label.getOpid_name() + "%' ");
		}
		if (label.getTotal() != null && !label.getTotal().equals("undefined") && !label.getTotal().equals("")) {
			sql.append("and total = '" + label.getTotal() + "' ");
		}
		if (label.getFlight_date() != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			sql.append("and flight_date = '" + dateFormat.format(label.getFlight_date()) + "' ");
		}
		if (label.getAirport_departure() != null && !label.getAirport_departure().equals("undefined") && !label.getAirport_departure().equals("") && !label.getAirport_departure().equals("全部")) {
			sql.append("and airport_departure like '%" + label.getAirport_departure() + "%' ");
		}
		if (label.getDestination() != null && !label.getDestination().equals("undefined") && !label.getDestination().equals("") && !label.getDestination().equals("全部")) {
			sql.append("and destination like '%" + label.getDestination() + "%' ");
		}
		if (label.getIs_print() != null && !label.getIs_print().equals("undefined") && !label.getIs_print().equals("")) {
			sql.append("and is_print = '" + label.getIs_print() + "' ");
		} else if (label.getIs_print().equals("undefined")) {
			sql.append("and is_print = '0' ");
		}
		
		//**********根据用户办公室地址检索查询标签起始地址  考虑废除
		//sql.append("and airport_departure = (SELECT load_code from load_code where region_parent_code = (select parent_code from region where region_id = (SELECT office_id from default_region where opid = '" + opid + "' and type = 1))) ");
		
		if (sort != null && order != null) {
			sql.append("ORDER BY " + sort + " " + order + " ");
		} else {
			sql.append("ORDER BY is_print,create_time desc ");
		}
//		sql.append("limit " + (Integer.valueOf(page) - 1) * Integer.valueOf(rows) + "," + rows + "");
		//查询模板及其数据
		List<LabelAndTemplate> keywords = labelInfoDao.findByPage(sql.toString());
		PageInfo<LabelAndTemplate> pageInfo = new PageInfo<LabelAndTemplate>(keywords);
		if (listSubscribe != null) {
			SubscribeController.subscribeMap.remove(opid);// 查询完成后清除缓存
		}
		
		Datagrid datagrid = new Datagrid();
		datagrid.setRows(pageInfo.getList());
		datagrid.setTotal(pageInfo.getTotal());
		logger.debug(GsonUtil.GsonString(datagrid));
		
		return datagrid;
	 } catch (Exception e) {
		 throw new BusinessException(ResEnum.FAIL.CODE, "查询数据异常,请正确写入参数!");
	 }
	}

	@Override
	public void updateLabel(Label label) {
		labelInfoDao.update(label);
	}

	@Override
	public void deleteLabel(List<Label> label) {
		labelInfoDao.delete(label);
	}


	@Override
	public List<Template> getUserAuthtemplate(Template template) {
		return labelInfoDao.getUserAuthtemplate(template);
	}

	@Override
	public List<Template> getALLTemplate(Template template) {
		return labelInfoDao.getALLTemplate(template);
	}

	@Override
	public void saveorupdateTempalte(Template template) {
		labelInfoDao.saveorupdateTempalte(template);
	}

}
