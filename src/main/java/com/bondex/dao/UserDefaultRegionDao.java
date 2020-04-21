package com.bondex.dao;

import java.util.List;

import com.bondex.entity.UserDefaultRegion;

public interface UserDefaultRegionDao {

	//根据opid获取用户信息表
	public List<UserDefaultRegion> selectUserDefaultRegion(UserDefaultRegion userDefaultRegion);
	
	//校验opid表中唯一
	public Integer checkOpidUnique(String opid);

	//当前操作更新或者添加办公室绑定
	public Integer updateOrAddUserRegion(UserDefaultRegion userDefaultRegion); //需要修改

	public Integer changeStatus(UserDefaultRegion userDefaultRegion);

	
}
