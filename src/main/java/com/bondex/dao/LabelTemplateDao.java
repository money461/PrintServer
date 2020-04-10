package com.bondex.dao;

import java.util.List;

import com.bondex.entity.Template;

public interface LabelTemplateDao {

	/**
	 * 获取数据库中的模板
	 * @param template
	 * @return
	 */
	public List<Template> getALLTemplate(Template template);
	
	/**
	 * 更新模板
	 * @param template
	 */
	public void saveorupdateTempalte(Template template);

	
	public List<String> getAllCode(String code);

	
	public Template getTemplateById(String id);

	/**
	 * 取消code 绑定的模板
	 * @param code
	 */
	public void cancelCodeDefaultTemplate(String code);
	
}
