package com.bondex.dao;

import com.bondex.entity.log.Log;
import com.bondex.entity.page.PageBean;

public interface LogInfoDao {

	PageBean<Log> getlogDetail(Log log);

	//日志入库
	void insertLableLog(Log log);

}
