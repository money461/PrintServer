package com.bondex.dao;

import java.util.List;

import com.bondex.entity.Template;
import com.bondex.entity.page.PageBean;

public interface LabelTemplateDao {

	/**
	 * 获取数据库中的模板
	 * @param template
	 * @return
	 */
	
	public List<Template> getALLTemplate(Template template);
	
	/**
	 * 获取数据库中用户所能看到的模板
	 * @param template
	 * @return
	 */
	public PageBean<Template> getALLTemplateByUserAuth(Template template);
	
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
