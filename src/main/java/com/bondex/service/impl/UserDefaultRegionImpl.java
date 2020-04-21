package com.bondex.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bondex.dao.UserDefaultRegionDao;
import com.bondex.entity.UserDefaultRegion;
import com.bondex.service.UserDefaultRegionService;
@Service
@Transactional(rollbackFor = Exception.class)
public class UserDefaultRegionImpl implements UserDefaultRegionService {

	@Autowired
	private UserDefaultRegionDao userDefaultRegionDao;
	
	@Override
	public List<UserDefaultRegion> selectUserDefaultRegion(UserDefaultRegion defaultRegion) {
		return userDefaultRegionDao.selectUserDefaultRegion(defaultRegion);
	}

	@Override
	public Integer checkOpidUnique(String opid) {
		return userDefaultRegionDao.checkOpidUnique(opid);
	}

	@Override
	public Integer updateOrAddUserRegion(UserDefaultRegion userDefaultRegion) {
		return userDefaultRegionDao.updateOrAddUserRegion(userDefaultRegion);
	}

	@Override
	public Integer changeStatus(UserDefaultRegion userDefaultRegion) {
		return userDefaultRegionDao.changeStatus(userDefaultRegion);
	}

}
