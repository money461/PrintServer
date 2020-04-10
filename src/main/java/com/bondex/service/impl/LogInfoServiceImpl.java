package com.bondex.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bondex.dao.LogInfoDao;
import com.bondex.entity.log.Log;
import com.bondex.service.LogInfoService;
@Service
@Transactional(rollbackFor = Exception.class)
public class LogInfoServiceImpl implements LogInfoService {

	@Autowired
	private LogInfoDao logInfoDao;
	
	@Override
	public List<Log> getlogDetail(Log log) {
		return logInfoDao.getlogDetail(log);
	}

	
}
