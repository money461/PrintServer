package com.bondex.service;

import java.util.List;

import com.bondex.entity.UserDefaultRegion;

public interface UserDefaultRegionService {

	List<UserDefaultRegion> selectUserDefaultRegion(UserDefaultRegion defaultRegion);

	Integer checkOpidUnique(String opid);

	Integer updateOrAddUserRegion(UserDefaultRegion userDefaultRegion);

	Integer changeStatus(UserDefaultRegion userDefaultRegion);
	
}
