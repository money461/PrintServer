package com.bondex.client.dao;

import java.util.List;

import com.bondex.client.entity.DefaultRegion;
import com.bondex.client.entity.Region;
import com.bondex.client.entity.yml.RegionJDBC;
import com.bondex.jdbc.entity.Label;

public interface ClientDao {

	/**
	 * 获取所有的区域办公室名称
	 */
	public List<RegionJDBC> getALLRegion();
	
	//根据用户id 查询相应的多个办公室
	public List<RegionJDBC> getRegionByOpid(String opid);

	public List<RegionJDBC> search(String q);

	public void update(String string);

	public List<Label> getLabel(String label);

	//根据opid获取用户信息表
	public DefaultRegion getDefaultRegionByOpid(String opid);

	//添加或者更新办公地址信息表 用于树形展示
	public Object addOrUpdateRegion(List<Region> regionlist);

	//根据办公室id获取区域信息
	public Region getRegionById(String regionid);

	public Object updateOrAddUserRegion(String regionid);

	//根据opid获取当前用户绑定的办公室信息
	public Region getDefaultBindRegionByOpid(String opid);

}
