package com.bondex.jdbc.service;

import java.util.List;

import com.bondex.entity.page.Datagrid;
import com.bondex.jdbc.entity.JsonRootBean;
import com.bondex.jdbc.entity.Label;
import com.bondex.jdbc.entity.Template;
import com.bondex.security.entity.JsonResult;

public interface LabelInfoService {
	
	
	public boolean labelInfoSave(JsonRootBean jsonRootBean);

	public Datagrid findByPage(String page, String rows, Label label, String start_time, String end_time, String sort, String order, String businessType) throws Exception;

	public void updateLabel(Label label);

	public void deleteLabel(List<Label> label);

	/**
	 * 获取用户绑定的模板
	 * @param template
	 * @return
	 */
	public List<Template> getUserAuthtemplate(Template template);
	
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
	
}
