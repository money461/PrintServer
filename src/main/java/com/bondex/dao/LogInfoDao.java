package com.bondex.dao;

import org.springframework.transaction.annotation.Transactional;

import com.bondex.entity.log.Log;
import com.bondex.entity.page.PageBean;

public interface LogInfoDao {

	PageBean<Log> getlogDetail(Log log);

	//日志入库
	@Transactional(rollbackFor = Exception.class)
	void insertLableLog(Log log);

	//校验消息唯一
	Boolean checkCorrelationIdUnique(String correlationId);

}
