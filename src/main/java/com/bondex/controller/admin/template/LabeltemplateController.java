package com.bondex.controller.admin.template;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.bondex.controller.BaseController;
import com.bondex.entity.Template;
import com.bondex.entity.page.PageBean;
import com.bondex.entity.res.AjaxResult;
import com.bondex.service.LabelTemplateService;

import io.swagger.annotations.ApiOperation;
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
		PageBean<Template> pageBean = labelTemplateService.getALLTemplateByUserAuth(template);
		return getDataPageBeanTable(pageBean);
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
    @ResponseBody
    public Object edit(@PathVariable("templateId") String templateId, ModelAndView modelAndView)
    {
    	Template template = new Template();
    	template.setTemplateId(templateId);
    	PageBean<Template> bean = labelTemplateService.getALLTemplateByUserAuth(template);
    	List<Template> list = bean.getList();
    	if(list.isEmpty()){
    		modelAndView.addObject("data",null);
    	}else{
    		
    		modelAndView.addObject("data", list.get(0));
    	}
    	modelAndView.setViewName("admin/labeltemplate/edit");
        return  modelAndView;
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
    
    /**
     * 这里的MultipartFile[] files表示前端页面上传过来的多个文件，files对应页面中多个file类型的input标签的name，但框架只会将一个文件封装进一个MultipartFile对象，
     * MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
         List<MultipartFile> mFiles = multipartRequest.getFiles("files");
    // 并不会将多个文件封装进一个MultipartFile[]数组，直接使用会报[Lorg.springframework.web.multipart.MultipartFile;.<init>()错误，
    // 所以需要用@RequestParam校正参数（参数名与MultipartFile对象名一致），当然也可以这么写：@RequestParam("files") MultipartFile[] files。
     * @param files
     * @param updateSupport 是否 更新
     * @param batchUploadSupport 是否批量上传
     * @return
     * @throws Exception
     */
	 @ApiOperation(value="单个或者多个模板上传", notes="单个或者多个模板上传")
	 @RequestMapping(value="/importTemplate",method={RequestMethod.POST},consumes={MediaType.MULTIPART_FORM_DATA_VALUE},produces={MediaType.APPLICATION_JSON_UTF8_VALUE})
     @ResponseBody
	 public Object importTemplate(@RequestParam("file") MultipartFile[] files, @RequestParam(defaultValue="false") boolean updateSupport, @RequestParam(defaultValue="false") Boolean batchUploadSupport, @RequestParam(defaultValue="false") String id,  HttpServletRequest request) throws Exception
	 {
		Object res = labelTemplateService.importTemplate(files,id,batchUploadSupport,updateSupport);
        return AjaxResult.success("上传文件成功！",res);
     }
	 
	 @ApiOperation(value="下载单个或者多个模板", notes="下载单个或者多个模板")
	 @RequestMapping(value="/exportTemplate",method={RequestMethod.POST,RequestMethod.GET})
	 @ResponseBody
	 public void exportTemplate(@RequestParam(name="id") String id,HttpServletRequest request,HttpServletResponse response){
		 labelTemplateService.exportTemplate(id,request,response);
	 }
    
	 
	 @RequestMapping(value="/deleteTemplatefile",method={RequestMethod.POST,RequestMethod.GET})
	 @ResponseBody
	 public Object deleteTemplatefile(Template template){
		 labelTemplateService.deleteTemplatefile(template);
		 return AjaxResult.success();
		 
	 }

    
}
