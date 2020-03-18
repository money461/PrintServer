package com.bondex.dao;

import java.util.List;

import com.bondex.entity.log.Log;

public interface LogInfoDao {

	List<Log> getlogDetail(Log log);

	//日志入库
	void insertLable(Log log);

}
