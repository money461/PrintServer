package com.bondex.dao;

import java.util.List;

import com.bondex.entity.Label;
import com.bondex.entity.LabelAndTemplate;
import com.bondex.entity.Template;
import com.bondex.entity.msg.JsonRootBean;

public interface LabelInfoDao {
	/**
	 * 数据入库
	 * 
	 * @param jsonRootBean
	 */
	public void saveLabel(JsonRootBean jsonRootBean);

	/**
	 * 分页查询
	 * 
	 * @return
	 */
	public List<LabelAndTemplate> findByPage(String sql);

	/**
	 * 获取记录数
	 * 
	 * @param sql
	 * @return
	 */
	public String getTotel(String sql);

	public void update(Label label);

	public void delete(List<Label> label);

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

	
	public void saveorupdateTempalte(Template template);
}
