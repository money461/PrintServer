package com.bondex.dao;

import java.util.List;

import com.bondex.entity.Label;
import com.bondex.entity.LabelAndTemplate;
import com.bondex.entity.Template;
import com.bondex.entity.log.Log;

public interface LabelInfoDao {
	/**
	 * 数据入库
	 * 
	 * @param messge
	 * @param log 
	 */
	public Integer saveLabel(String messge, Log log);

	/**
	 * 分页查询
	 * 
	 * @return
	 */
	public List<LabelAndTemplate> selectLabelByPage(String sql);

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
	 * 校验用户是否可以使用打印模板
	 * @param templateId
	 */
	public Template checkUseTemplate(String templateId);
}
