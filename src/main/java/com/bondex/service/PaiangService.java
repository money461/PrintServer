package com.bondex.service;

import java.util.List;

import com.bondex.entity.Label;
import com.bondex.entity.LabelAndTemplate;

public interface PaiangService {

	/**
	 * 保存派昂数据
	 */
	public void paiangSaveService(String message);

	/**
	 * 查询派昂标签
	 * @param label
	 * @return
	 */
	List<LabelAndTemplate> selectPaiangLabelByPage(Label label);

	//修改标签
	public void updatePaiangData(List<Label> datalist);
	
	
}
