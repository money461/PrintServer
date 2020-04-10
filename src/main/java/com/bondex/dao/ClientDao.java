package com.bondex.dao;

import java.util.List;

import com.bondex.entity.Label;
import com.bondex.entity.Region;

public interface ClientDao {

	

	public void update(String string);

	public List<Label> getLabel(String label);


	//根据opid获取当前用户绑定的办公室信息
	public Region getDefaultBindRegionByOpid(String opid);
	
	//添加或者更新办公地址信息表 用于树形展示
	public Object addOrUpdateRegion(List<Region> regionlist);

	//根据办公室id获取区域信息
	public Region getRegionByRegioncode(String regionCode); //需要修改


}
