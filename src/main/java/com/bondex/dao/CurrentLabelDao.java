package com.bondex.dao;

import java.util.List;

import com.bondex.entity.current.BaseLabelDetail;
import com.bondex.entity.page.PageBean;

public interface CurrentLabelDao {

	//是否需要权限
	PageBean<BaseLabelDetail> selectBaseLabelList(BaseLabelDetail baseLabelDetail,Boolean authorization);

	void updateBaseLabel(List<BaseLabelDetail> list);

	Integer updateBaseLabelById(BaseLabelDetail baseLabelDetail);


}
