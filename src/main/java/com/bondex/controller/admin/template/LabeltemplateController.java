package com.bondex.controller.admin.template;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bondex.controller.BaseController;
import com.bondex.entity.Template;
import com.bondex.entity.res.AjaxResult;
import com.bondex.service.LabelTemplateService;
@Controller
@RequestMapping(value="/template")
public class LabeltemplateController extends BaseController{

	@Autowired
	private LabelTemplateService labelTemplateService;

	/**
	 * 进入模板管理展示页面
	 * @param mawb
	 * @param modelAndView
	 * @return
	 */
	@RequestMapping(value={"/view/{code}","/view"},method = RequestMethod.GET)
	@ResponseBody
//	@RequiresPermissions(value={"viewdata","look"},logical=Logical.OR)
	public ModelAndView indexpage(@PathVariable(name="code",required=false) String code,ModelAndView modelAndView){
		modelAndView.addObject("code", code);
		modelAndView.setViewName("/admin/labeltemplate/labeltemplate"); //页面展示
		return modelAndView;
	}
	
	/**
	 * 获取分页查询所有的模板
	 * @param opid
	 * @return
	 */
	@RequestMapping(value="/getAlltemplate",method={RequestMethod.POST})
	@ResponseBody
	public Object getAlltemplate(Template template) {
		List<Template> list = labelTemplateService.getALLTemplate(template);
		return getDataTable(list);
	}
	
	/**
	 * 分页查询 用户权限中的业务pageCode
	 * @return
	 */
	@RequestMapping(value="/getAllCode",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Object getAllCode(@RequestParam(defaultValue="") String code) {
		startPage(true, null);
		List<Object> list = labelTemplateService.getAllCode(code);
		return  AjaxResult.success(list);
	}
	/**
	 * 获取用权限系统绑定的模板
	 * @param
	 * @return
	 */
	@RequestMapping(value="/getUserAuthenTemplate",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Object getUserAuthenTemplate(@RequestParam(defaultValue="") String templateId) {
		List<Object> list = labelTemplateService.getUserAuthorizationTemplate(templateId);
		return  AjaxResult.success(list);
	}
	
	
	
	 /**
     * 修改编辑模板跳转
     */
    @GetMapping("/edit/{templateId}")
    public String edit(@PathVariable("templateId") String templateId, ModelMap mmap)
    {
    	Template template = new Template();
    	template.setTemplateId(templateId);
    	List<Template> list = labelTemplateService.getALLTemplate(template);
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
    	labelTemplateService.saveorupdateTempalte(template);
    	
    	return  AjaxResult.success();
    }
    /**
     * 修改默认模板
     * @param template
     * @return
     */
    @PostMapping("/changeDefault")
    @ResponseBody
    public Object changeCodeDefaultTemplate(Template template){
    	labelTemplateService.changeCodeDefaultTemplate(template);
    	
    	return AjaxResult.success();
    }
    
    @PostMapping("/checkCodeBindTemplateUnique")
    @ResponseBody
    public int checkCodeBindTemplateUnique(Template template){
    	return labelTemplateService.checkCodeBindTemplateUnique(template);
    }
    
}
