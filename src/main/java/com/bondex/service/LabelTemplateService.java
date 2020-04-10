package com.bondex.service;

import java.util.List;

import com.bondex.entity.Template;

public interface LabelTemplateService {

	
	/**
	 * 获取数据库中的模板
	 * @param template
	 * @return
	 */
	public List<Template> getALLTemplate(Template template);

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

}
