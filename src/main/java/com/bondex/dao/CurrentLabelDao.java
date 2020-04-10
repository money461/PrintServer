package com.bondex.dao;

import java.util.List;

import com.bondex.entity.current.BaseLabelDetail;

public interface CurrentLabelDao {

	//是否需要权限
	List<BaseLabelDetail> selectBaseLabelList(BaseLabelDetail baseLabelDetail,Boolean authorization);

	void insertforUpdateBaseLabel(List<BaseLabelDetail> list);

	void insertBaseLabel(List<BaseLabelDetail> list);
	
	Integer updateBaseLabelById(BaseLabelDetail baseLabelDetail);

	List<BaseLabelDetail> selectBaseLabelListByInId(String labelId);




}
