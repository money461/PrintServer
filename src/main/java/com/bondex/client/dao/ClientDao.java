package com.bondex.client.dao;

import java.util.List;

import com.bondex.client.entity.Region;
import com.bondex.client.entity.UserDefaultRegion;
import com.bondex.jdbc.entity.Label;

public interface ClientDao {

	/**
	 * 获取所有的区域办公室名称
	 */
	public List<Region> getALLRegion(Region regionJDBC);
	
	public List<Region> getALLParentRegion(Region regionJDBC);
	
	//根据用户id 查询相应的多个办公室
	public List<Region> getRegionByOpid(String opid);

	public List<Region> search(String q);

	public void update(String string);

	public List<Label> getLabel(String label);

	//根据opid获取用户信息表
	public List<UserDefaultRegion> selectUserDefaultRegion(UserDefaultRegion userDefaultRegion);

	//根据opid获取当前用户绑定的办公室信息
	public Region getDefaultBindRegionByOpid(String opid);
	
	//添加或者更新办公地址信息表 用于树形展示
	public Object addOrUpdateRegion(List<Region> regionlist);

	//根据办公室id获取区域信息
	public Region getRegionById(String regionid); //需要修改

	//当前操作更新或者添加办公室绑定
	public Object updateOrAddUserRegion(UserDefaultRegion userDefaultRegion); //需要修改

	//校验办公室唯一性
	public int checkregionCodeUnique(Region regionJDBC);

	//更新保存办公室
	public Object saveOrUpdateRegion(Region regionJDBC);

	//校验opid表中唯一
	public Integer checkOpidUnique(String opid);

}
