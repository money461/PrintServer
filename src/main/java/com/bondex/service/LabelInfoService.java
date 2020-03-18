package com.bondex.service;

import java.util.List;

import com.bondex.entity.Label;
import com.bondex.entity.Template;
import com.bondex.entity.msg.JsonRootBean;
import com.bondex.entity.page.Datagrid;

public interface LabelInfoService {
	
	//监听消息保存标签数据
	public boolean labelInfoSave(String message);

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
