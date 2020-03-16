package com.bondex.dao;

import java.util.List;

import com.bondex.entity.Region;

public interface RegionDao {

	/**
	 * 获取所有的区域办公室名称
	 */
	public List<Region> getALLRegion(Region regionJDBC);
	
	public List<Region> getALLParentRegion(Region regionJDBC);
	
	//根据用户id 查询相应的多个办公室
	public List<Region> getRegionByOpid(String opid);

	public List<Region> search(String q);
	
	//校验办公室唯一性
	public int checkregionCodeUnique(Region regionJDBC);

	//更新保存办公室
	public Object saveOrUpdateRegion(Region regionJDBC);
	
}
