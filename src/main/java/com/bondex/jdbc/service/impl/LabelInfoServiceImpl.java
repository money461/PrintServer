package com.bondex.jdbc.service.impl;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bondex.controller.SubscribeController;
import com.bondex.entity.Datagrid;
import com.bondex.entity.Subscribe;
import com.bondex.jdbc.dao.LabelInfoDao;
import com.bondex.jdbc.entity.JsonRootBean;
import com.bondex.jdbc.entity.Label;
import com.bondex.jdbc.entity.LabelAndTemplate;
import com.bondex.jdbc.entity.Template;
import com.bondex.jdbc.service.LabelInfoService;
import com.bondex.security.entity.JsonResult;
import com.bondex.util.GsonUtil;
import com.google.gson.reflect.TypeToken;

@Component
@Transactional(rollbackFor = Exception.class)
public class LabelInfoServiceImpl implements LabelInfoService {
	@Autowired
	private LabelInfoDao labelInfoDao;

	@Override
	public boolean labelInfoSave(JsonRootBean jsonRootBean) {
		labelInfoDao.saveLabel(jsonRootBean);
		return true;
	}

	@Override
	public Datagrid findByPage(String page, String rows, Label label, String start_time, String end_time, String sort, String order, String opid, List<JsonResult> listop, String businessType) {
		// 获取用户拥有的标签模版，权限
		Type objectType = new TypeToken<List<List<JsonResult>>>() {}.getType();
		List<List<JsonResult>> jsonResults = GsonUtil.getGson().fromJson(GsonUtil.GsonString(listop), objectType);
		String rt = ""; //'a357f19f-6a1f-4171-ab8e-1f1a2b77377a','988b4162-00af-4924-a6fe-6b310d161900','2b7eddea-d953-4186-bc3b-a642940d57ea','4e2b8f59-9858-4297-962f-6ee4862085aa'
		for (JsonResult jsonResult : jsonResults.get(0)) {
			rt += "'" + jsonResult.getReportid() + "',";
		}
		rt = rt.substring(0, rt.length() - 1);

		page = null == page ? "1" : page;
		rows = null == rows ? "20" : rows;

		StringBuffer sql = new StringBuffer();
		sql.append("select * from label LEFT JOIN template t  on t.id = label.reserve3 where t.template_id in (" + rt + ") ");
		if (businessType.equals("medicine")) {
			sql.append(" and business_type = 0 ");
			if (label.getMBLNo() != null && !label.getMBLNo().equals("undefined") && !label.getMBLNo().equals("")) {
				sql.append("and MBLNo like '%" + label.getMBLNo() + "%' ");
			}
			if (label.getTakeCargoNo() != null && !label.getTakeCargoNo().equals("undefined") && !label.getTakeCargoNo().equals("")) {
				sql.append("and TakeCargoNo like '%" + label.getTakeCargoNo() + "%' ");
			}
			if (label.getIs_print() != null && !label.getIs_print().equals("undefined") && !label.getIs_print().equals("")) {
				sql.append("and is_print = '" + label.getIs_print() + "' ");
			} else if (label.getIs_print().equals("undefined")) {
				sql.append("and is_print = '0' ");
			}
		} else {
			sql.append(" and business_type <> 0 ");
		}
		sql.append("and reserve1 = '0' ");
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
				return new Datagrid<>("0", new ArrayList<>());
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
		if (label.getOpid_name() != null && !label.getOpid_name().equals("undefined") && !label.getOpid_name().equals("") && !label.getOpid_name().equals("所有")) {
			sql.append("and opid_name like '%" + label.getOpid_name() + "%' ");
		}
		if (label.getTotal() != null && !label.getTotal().equals("undefined") && !label.getTotal().equals("")) {
			sql.append("and total = '" + label.getTotal() + "' ");
		}
		if (label.getFlight_date() != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			sql.append("and flight_date = '" + dateFormat.format(label.getFlight_date()) + "' ");
		}
		if (label.getAirport_departure() != null && !label.getAirport_departure().equals("undefined") && !label.getAirport_departure().equals("") && !label.getAirport_departure().equals("所有")) {
			sql.append("and airport_departure like '%" + label.getAirport_departure() + "%' ");
		}
		if (label.getDestination() != null && !label.getDestination().equals("undefined") && !label.getDestination().equals("") && !label.getDestination().equals("所有")) {
			sql.append("and destination like '%" + label.getDestination() + "%' ");
		}
		if (label.getIs_print() != null && !label.getIs_print().equals("undefined") && !label.getIs_print().equals("")) {
			sql.append("and is_print = '" + label.getIs_print() + "' ");
		} else if (label.getIs_print().equals("undefined")) {
			sql.append("and is_print = '0' ");
		}
		sql.append("and airport_departure = (SELECT load_code from load_code where region_parent_code = (select parent_code from region where region_id = (SELECT office_id from default_region where opid = '" + opid + "' and type = 1))) ");
		if (sort != null && order != null) {
			sql.append("ORDER BY " + sort + " " + order + " ");
		} else {
			sql.append("ORDER BY is_print,CREATE_time desc ");
		}
		sql.append("limit " + (Integer.valueOf(page) - 1) * Integer.valueOf(rows) + "," + rows + "");
		List<LabelAndTemplate> keywords = labelInfoDao.findByPage(sql.toString());
		StringBuffer totalsql = new StringBuffer();
		totalsql.append("select count(1) from label LEFT JOIN template t  on t.id = label.reserve3 where t.template_id in (" + rt + ") ");
		totalsql.append("and reserve1 = '0' ");
		if (businessType.equals("medicine")) {
			totalsql.append("and business_type = 0 ");
			if (label.getMBLNo() != null && !label.getMBLNo().equals("undefined") && !label.getMBLNo().equals("")) {
				totalsql.append("and MBLNo like '%" + label.getMBLNo() + "%' ");
			}
			if (label.getTakeCargoNo() != null && !label.getTakeCargoNo().equals("undefined") && !label.getTakeCargoNo().equals("")) {
				totalsql.append("and TakeCargoNo like '%" + label.getTakeCargoNo() + "%' ");
			}
			if (label.getIs_print() != null && !label.getIs_print().equals("undefined") && !label.getIs_print().equals("")) {
				totalsql.append("and is_print = '" + label.getIs_print() + "' ");
			} else if (label.getIs_print().equals("undefined")) {
				totalsql.append("and is_print = '0' ");
			}
		} else {
			totalsql.append("and business_type <> 0 ");
		}
		if (label.getOpid_name() != null && !label.getOpid_name().equals("undefined") && !label.getOpid_name().equals("") && !label.getOpid_name().equals("所有")) {
			totalsql.append("and opid_name like '%" + label.getOpid_name() + "%' ");
		}

		if (start_time != null && !start_time.equals("undefined") && !start_time.equals("")) {
			if (businessType.equals("medicine")) {
				totalsql.append("and EDeparture >= '" + start_time + "' ");
			} else {
				totalsql.append("and create_time >= '" + start_time + "' ");
			}
		}
		if (end_time != null && !end_time.equals("undefined") && !end_time.equals("")) {
			if (businessType.equals("medicine")) {
				totalsql.append("and EDeparture <= '" + end_time + "' ");
			} else {
				totalsql.append("and create_time <= '" + end_time + "' ");
			}
		}
		if (listSubscribe != null) {
			StringBuilder builder = new StringBuilder();
			for (Subscribe subscribe : listSubscribe) {
				builder.append("'" + subscribe.getSrMawb() + "',");
			}
			totalsql.append("and mawb in (" + builder.substring(0, builder.length() - 1).toString() + ") ");
			SubscribeController.subscribeMap.remove(opid);// 查询完成后清除缓存
		} else if (label.getMawb() != null && !label.getMawb().equals("undefined") && !label.getMawb().trim().equals("")) {
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
			totalsql.append("and mawb in (" + mawb.substring(0, mawb.length() - 1) + ") ");
		}
		if (label.getHawb() != null && !label.getHawb().equals("undefined") && !label.getHawb().equals("")) {
			totalsql.append("and hawb like '%" + label.getHawb() + "%' ");
		}
		if (label.getFlight_date() != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			totalsql.append("and flight_date = '" + dateFormat.format(label.getFlight_date()) + "' ");
		}
		if (label.getTotal() != null && !label.getTotal().equals("undefined") && !label.getTotal().equals("")) {
			totalsql.append("and total = '" + label.getTotal() + "' ");
		}
		if (label.getAirport_departure() != null && !label.getAirport_departure().equals("undefined") && !label.getAirport_departure().equals("") && !label.getAirport_departure().equals("所有")) {
			totalsql.append("and airport_departure like '%" + label.getAirport_departure() + "%' ");
		}
		if (label.getDestination() != null && !label.getDestination().equals("undefined") && !label.getDestination().equals("") && !label.getDestination().equals("所有")) {
			totalsql.append("and destination like '%" + label.getDestination() + "%' ");
		}
		if (label.getIs_print() != null && !label.getIs_print().equals("undefined") && !label.getIs_print().equals("")) {
			totalsql.append("and is_print = '" + label.getIs_print() + "' ");
		} else if (label.getIs_print().equals("undefined")) {
			totalsql.append("and is_print = '0' ");
		}
		totalsql.append("and airport_departure = (SELECT load_code from load_code where region_parent_code = (select parent_code from region where region_id = (SELECT office_id from default_region where opid = '" + opid + "' and type = 1))) ");

		Datagrid datagrid = new Datagrid();
		datagrid.setRows(keywords);
		datagrid.setTotal(labelInfoDao.getTotel(totalsql.toString()));
		return datagrid;
	}

	@Override
	public void updateLabel(Label label) {
		labelInfoDao.update(label);
	}

	@Override
	public void delete(List<Label> label) {
		labelInfoDao.delete(label);
	}

	@Override
	public Template getTemplate(List<JsonResult> jsonResults, String id) {
		String rt = "";
		for (JsonResult jsonResult : jsonResults) {
			rt += "'" + jsonResult.getReportid() + "',";
		}
		rt = rt.substring(0, rt.length() - 1);
		Template template = labelInfoDao.getTemplate(rt, id);
		return template;
	}

}
