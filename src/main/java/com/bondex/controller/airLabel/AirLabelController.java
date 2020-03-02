package com.bondex.controller.airLabel;

import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.bondex.common.Common;
import com.bondex.common.enums.ResEnum;
import com.bondex.config.exception.BusinessException;
import com.bondex.entity.Datagrid;
import com.bondex.jdbc.entity.Label;
import com.bondex.jdbc.entity.LabelAndTemplate;
import com.bondex.jdbc.entity.Template;
import com.bondex.jdbc.service.LabelInfoService;
import com.bondex.res.MsgResult;
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
	 * 获取用户拥有的模板
	 * @param opid
	 * @return
	 */
	@RequestMapping(value="getUserAuthtemplate",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public List<Template> getUsertemplate(Template template) {
		
		return labelInfoService.getUserAuthtemplate(template);
	}

	/**
	 * 获取数据库label标签数据
	 * @param page
	 * @param rows
	 * @param label
	 * @param start_time
	 * @param end_time
	 * @param sort desc 升序降序
	 * @param order 按照该字段排序
	 * @param opid
	 * @param businessType 业务类型 air michine
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/all",method=RequestMethod.POST)
	@ResponseBody
	public Object findByPage(String page, String rows, Label label, String start_time, String end_time, String sort, String order, String opid, String businessType, HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession();
		Map<String, Object> map = (Map<String, Object>) session.getAttribute(Common.Session_UserSecurity);
		if(StringUtils.isNull(map)||map.size()==0){
			throw new BusinessException(ResEnum.FORBIDDEN.CODE,"操作号没有配置任何操作权限,无法查看数据");
		}
		UserInfo userInfo = ShiroUtils.getUserInfo();
		opid = userInfo.getOpid();
		
		Datagrid<?> datagrid = labelInfoService.findByPage(page, rows, JSON.parseObject(URLDecoder.decode(JSON.toJSONString(label), "utf-8"), Label.class), start_time, end_time, sort, order,businessType);
		return MsgResult.result(ResEnum.SUCCESS.CODE, ResEnum.SUCCESS.MESSAGE, datagrid);
	}
	
	//更新打印模板
	@RequestMapping(value="update",method = {RequestMethod.POST },consumes={"application/json; charset=UTF-8"},produces = "application/json; charset=UTF-8")
	@ResponseBody
	public Object update(@RequestBody List<LabelAndTemplate> labels) {
		// String reserve3 = label.getReserve3();
		// List<Template> template = jdbcTemplate.query("select * from template where
		// template_id = ? or id = ? or template_name = ?", new Object[] { reserve3,
		// reserve3, reserve3 }, new BeanPropertyRowMapper<Template>(Template.class));
		for (LabelAndTemplate label : labels) {
			//查询修改的模板名称
			List<Template> template = jdbcTemplate.query("select * from template where template_name = ?", new Object[] { label.getTemplate_name() }, new BeanPropertyRowMapper<Template>(Template.class));
			label.setReserve3(String.valueOf(template.get(0).getId()));
			labelInfoService.updateLabel(label);
		}
		return MsgResult.nodata(ResEnum.SUCCESS.CODE, ResEnum.SUCCESS.MESSAGE);
	}

	//刪除標簽
	@RequestMapping("delete")
	public String delete(String label) {
		List<Label> labels = GsonUtil.GsonToList(label, Label.class);
		labelInfoService.delete(labels);
		return "airlabel/airlabel";
	}

}
