package com.bondex.controller.airLabel;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authz.AuthorizationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.bondex.common.Common;
import com.bondex.entity.Datagrid;
import com.bondex.jdbc.entity.Label;
import com.bondex.jdbc.entity.LabelAndTemplate;
import com.bondex.jdbc.entity.Template;
import com.bondex.jdbc.service.LabelInfoService;
import com.bondex.security.entity.JsonResult;
import com.bondex.security.entity.UserInfo;
import com.bondex.shiro.security.ShiroUtils;
import com.bondex.util.GsonUtil;
import com.bondex.util.StringUtils;
import com.google.gson.reflect.TypeToken;

@Controller
@RequestMapping("label")
public class AirLabelController {
	@Resource(name="labelInfoServiceImpl")
	private LabelInfoService labelInfoService;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@RequestMapping("save")
	@ResponseBody
	public boolean saveLabel() {
		labelInfoService.labelInfoSave(null);
		return false;
	}

	/**
	 * 获取模板
	 * @param opid
	 * @param id 模板数据库表 id 或者    template_name 名称
	 * @return
	 */
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
			template2.setTemplate_name("未配置打印模板");
			return GsonUtil.GsonString(template2);
		} else {
			return GsonUtil.GsonString(template);
		}
	}

	/**
	 * 获取数据库label标签数据
	 * @param page
	 * @param rows
	 * @param label
	 * @param start_time
	 * @param end_time
	 * @param sort
	 * @param order
	 * @param opid
	 * @param businessType 业务类型 air michine
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("all")
	@ResponseBody
	public Object findByPage(String page, String rows, Label label, String start_time, String end_time, String sort, String order, String opid, String businessType, HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession();
		Map<String, Object> map = (Map<String, Object>) session.getAttribute(Common.Session_UserSecurity);
		if(StringUtils.isNull(map)){
			throw new AuthorizationException();
		}
		UserInfo userInfo = ShiroUtils.getUserInfo();
		opid = userInfo.getOpid();
		//获取打印模板权限
		List<JsonResult> list = (List<JsonResult>)map.get(opid + Common.UserSecurity_PrintButton);
		Datagrid<?> datagrid = labelInfoService.findByPage(page, rows, JSON.parseObject(URLDecoder.decode(JSON.toJSONString(label), "utf-8"), Label.class), start_time, end_time, sort, order, opid, list, businessType);
//		return MsgResult.result(ResEnum.SUCCESS.CODE, ResEnum.SUCCESS.MESSAGE, datagrid);
		return datagrid;
	}
	
	//更新打印模板
	@RequestMapping("update")
	public String update(LabelAndTemplate label) {
		// String reserve3 = label.getReserve3();
		// List<Template> template = jdbcTemplate.query("select * from template where
		// template_id = ? or id = ? or template_name = ?", new Object[] { reserve3,
		// reserve3, reserve3 }, new BeanPropertyRowMapper<Template>(Template.class));
		List<Template> template = jdbcTemplate.query("select * from template where template_name = ?", new Object[] { label.getTemplate_name() }, new BeanPropertyRowMapper<Template>(Template.class));
		label.setReserve3(String.valueOf(template.get(0).getId()));
		labelInfoService.updateLabel(label);
		return "airlabel/airlabel";
	}

	//刪除標簽
	@RequestMapping("delete")
	public String delete(String label) {
		List<Label> labels = GsonUtil.GsonToList(label, Label.class);
		labelInfoService.delete(labels);
		return "airlabel/airlabel";
	}

}
