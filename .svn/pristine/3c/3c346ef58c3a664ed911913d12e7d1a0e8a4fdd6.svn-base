package com.bondex.jdbc.controller;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.bondex.entity.Datagrid;
import com.bondex.jdbc.entity.Label;
import com.bondex.jdbc.entity.LabelAndTemplate;
import com.bondex.jdbc.entity.Template;
import com.bondex.jdbc.service.LabelInfoService;
import com.bondex.security.entity.JsonResult;
import com.bondex.util.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Controller
@RequestMapping("label")
public class LabelInfoController {
	@Autowired
	private LabelInfoService labelInfoService;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@RequestMapping("save")
	@ResponseBody
	public boolean saveLabel() {
		labelInfoService.labelInfoSave(null);
		return false;
	}

	@RequestMapping("template")
	@ResponseBody
	public String getTemplate(String opid, String id) {
		Type objectType = new TypeToken<List<List<JsonResult>>>() {}.getType();
		List<List<JsonResult>> jsonResults = GsonUtil.getGson().fromJson(opid, objectType);
		return GsonUtil.GsonString(labelInfoService.getTemplate(jsonResults.get(0), id));
	}

	@RequestMapping("getUsertemplate")
	@ResponseBody
	public String getUsertemplate(String opid) {
		Type objectType = new TypeToken<List<List<JsonResult>>>() {}.getType();
		List<List<JsonResult>> jsonResults = GsonUtil.getGson().fromJson(opid, objectType);
		String rt = "";
		for (JsonResult jsonResult : jsonResults.get(0)) {
			rt += "'" + jsonResult.getReportid() + "',";
		}
		rt = rt.substring(0, rt.length() - 1);

		List<Template> template = jdbcTemplate.query("select * from template where template_id in(" + rt + ")", new Object[] {}, new BeanPropertyRowMapper<Template>(Template.class));
		if (template.isEmpty()) {
			Template template2 = new Template();
			template2.setTemplate_name("无权限的异常数据");
			return GsonUtil.GsonString(template2);
		} else {
			return GsonUtil.GsonString(template);
		}
	}

	@RequestMapping("all")
	@ResponseBody
	public String findByPage(String page, String rows, Label label, String start_time, String end_time, String sort, String order, String opid, String businessType, HttpSession session) throws UnsupportedEncodingException {
		Map<String, Object> map = (Map<String, Object>) session.getAttribute("userSecurity");
		List<JsonResult> list = (List<JsonResult>) map.get(session.getAttribute("thisOpid") + "printButton");
		if (opid.equals("")) {
			opid = (String) session.getAttribute("thisOpid");
		}
		Datagrid<?> datagrid = labelInfoService.findByPage(page, rows, JSON.parseObject(URLDecoder.decode(JSON.toJSONString(label), "utf-8"), Label.class), start_time, end_time, sort, order, opid, list, businessType);
		return new Gson().toJson(datagrid);
	}

	@RequestMapping("update")
	public String update(LabelAndTemplate label) {
		// String reserve3 = label.getReserve3();
		// List<Template> template = jdbcTemplate.query("select * from template where
		// template_id = ? or id = ? or template_name = ?", new Object[] { reserve3,
		// reserve3, reserve3 }, new BeanPropertyRowMapper<Template>(Template.class));
		List<Template> template = jdbcTemplate.query("select * from template where template_name = ?", new Object[] { label.getTemplate_name() }, new BeanPropertyRowMapper<Template>(Template.class));
		label.setReserve3(String.valueOf(template.get(0).getId()));
		labelInfoService.updateLabel(label);
		return "label/label";
	}

	@RequestMapping("delete")
	public String delete(String label) {
		List<Label> labels = GsonUtil.GsonToList(label, Label.class);
		labelInfoService.delete(labels);
		return "label/label";
	}

}
