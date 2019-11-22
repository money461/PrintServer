package com.bondex.jdbc.dao;

import java.util.List;

import com.bondex.jdbc.entity.JsonRootBean;
import com.bondex.jdbc.entity.Label;
import com.bondex.jdbc.entity.LabelAndTemplate;
import com.bondex.jdbc.entity.Template;

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

	public Template getTemplate(String rt, String id);
}
