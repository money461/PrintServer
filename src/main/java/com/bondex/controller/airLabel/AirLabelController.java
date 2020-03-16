package com.bondex.controller.airLabel;

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
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.bondex.common.Common;
import com.bondex.common.enums.ResEnum;
import com.bondex.config.exception.BusinessException;
import com.bondex.controller.BaseController;
import com.bondex.entity.Label;
import com.bondex.entity.LabelAndTemplate;
import com.bondex.entity.Template;
import com.bondex.entity.page.Datagrid;
import com.bondex.entity.res.AjaxResult;
import com.bondex.entity.res.MsgResult;
import com.bondex.service.LabelInfoService;
import com.bondex.shiro.security.entity.UserInfo;
import com.bondex.util.GsonUtil;
import com.bondex.util.StringUtils;
import com.bondex.util.shiro.ShiroUtils;

@Controller
@RequestMapping(value="/label")
public class AirLabelController extends BaseController {
	
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
	 * 获取所有的模板
	 * @param opid
	 * @return
	 */
	@RequestMapping(value="/getAlltemplate",method={RequestMethod.POST})
	@ResponseBody
	public Object getAlltemplate(Template template) {
		startPage();//设置分页pagehelper
		List<Template> list = labelInfoService.getALLTemplate(template);
		return getDataTable(list);
	}
	
	 /**
     * 修改编辑模板跳转
     */
    @GetMapping("/edit/{template_id}")
    public String edit(@PathVariable("template_id") String template_id, ModelMap mmap)
    {
    	Template template = new Template();
    	template.setTemplate_id(template_id);
    	List<Template> list = labelInfoService.getALLTemplate(template);
    	if(list.isEmpty()){
    		mmap.put("data",null);
    	}else{
    		
    		mmap.put("data", list.get(0));
    	}
        return   "admin/labeltemplate/edit";
    }
    
    /**
     * 更新保存模板
     * @param template
     * @return
     */
    @PostMapping("/saveorupdateTempalte")
    @ResponseBody
    public Object saveorupdateTempalte(Template template )
    {
    	labelInfoService.saveorupdateTempalte(template);
    	
    	return  AjaxResult.success();
    }
    
    

	/**
	 * 获取数据库label标签数据
	 * @param page 页码
	 * @param rows 每页记录数
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
	
	//更新打印标签数据
	@RequestMapping(value="/update",method = {RequestMethod.POST },consumes={"application/json; charset=UTF-8"},produces = "application/json; charset=UTF-8")
	@ResponseBody
	public Object update(@RequestBody List<LabelAndTemplate> labels) {
		for (LabelAndTemplate label : labels) {
			//查询修改的模板名称
			List<Template> template = jdbcTemplate.query("select * from template where template_name = ?", new Object[] { label.getTemplate_name() }, new BeanPropertyRowMapper<Template>(Template.class));
			label.setReserve3(String.valueOf(template.get(0).getId()));
			labelInfoService.updateLabel(label);
		}
		return MsgResult.nodata(ResEnum.SUCCESS.CODE, ResEnum.SUCCESS.MESSAGE);
	}

	//刪除標簽
	@RequestMapping(value="/delete",method = {RequestMethod.POST },consumes={"application/json; charset=UTF-8"},produces = "application/json; charset=UTF-8")
	@ResponseBody
	public Object delete(String label) {
		List<Label> labels = GsonUtil.GsonToList(label, Label.class);
		labelInfoService.deleteLabel(labels);
		return MsgResult.nodata(ResEnum.SUCCESS.CODE, ResEnum.SUCCESS.MESSAGE);
	}

}
