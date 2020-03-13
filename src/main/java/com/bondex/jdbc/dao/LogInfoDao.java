package com.bondex.jdbc.dao;

import java.util.List;

import com.bondex.jdbc.entity.Log;

public interface LogInfoDao {

	List<Log> getlogDetail(Log log);

}
