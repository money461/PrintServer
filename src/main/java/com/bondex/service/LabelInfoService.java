package com.bondex.service;

import java.util.List;
import java.util.Map;

import com.bondex.entity.Label;
import com.bondex.entity.LabelAndTemplate;
import com.bondex.entity.Template;

public interface LabelInfoService {
	
	//监听消息保存标签数据
	public boolean labelInfoSave(String message);

	//分页查询标签
	public List<LabelAndTemplate>  selectLabelByPage( Label label);

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
