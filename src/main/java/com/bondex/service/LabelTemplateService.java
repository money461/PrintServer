package com.bondex.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.bondex.entity.Template;
import com.bondex.entity.page.PageBean;

public interface LabelTemplateService {

	
	/**
	 * 获取数据库中用户能看到的模板
	 * @param template
	 * @return
	 */
	public PageBean<Template> getALLTemplateByUserAuth(Template template);

	/**
	 * 修改更新模板
	 * @param template
	 */
	public void saveorupdateTempalte(Template template);

	/**
	 * 获取所有的code
	 * @param code 
	 * @return
	 */
	public List<Object> getAllCode(String code);

	/**
	 * 校验业务code  绑定模板唯一
	 * @param template
	 * @return
	 */
	public int checkCodeBindTemplateUnique(Template template);
	
	

	public void changeCodeDefaultTemplate(Template template);

	/**
	 * 获取用权限绑定的模板
	 * @param templateId
	 * @return
	 */
	public List<Object> getUserAuthorizationTemplate(String templateId);

	/**
	 * 
	 * @param files
	 * @param templateId 
	 * @param batchUploadSupport 是否批量上传
	 * @param updateSupport 支持更新
	 */
	public Object importTemplate(MultipartFile[] files, String id, Boolean batchUploadSupport, boolean updateSupport);

	/**
	 * 下载文件返回文件名称
	 * @param templateId
	 * @param response 
	 * @param request 
	 * @return
	 */
	 String exportTemplate(String templateId, HttpServletRequest request, HttpServletResponse response);

	 /**
	  * 删除打印模板
	  * @param template
	  */
	public void deleteTemplatefile(Template template);


}
