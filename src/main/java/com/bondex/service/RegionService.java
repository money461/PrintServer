package com.bondex.service;

import java.util.List;

import com.bondex.entity.Region;
import com.bondex.entity.tree.TreeBean;

public interface RegionService {
	
	/**
	 * 根据用户opid获取办公室区域 并封装为tree
	 * 
	 * @return
	 */
	List<TreeBean> getTreeRegionByOpid(String opid);

	//未知作用
	String searchRegion(String q);

	List<Region> getALLParentRegion(Region region);

	List<Region> getALLRegion(Region region);

	Object saveOrUpdateRegion(Region region);

	int checkregionCodeUnique(Region region);

	
}
