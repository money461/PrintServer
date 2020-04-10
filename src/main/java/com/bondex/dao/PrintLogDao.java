package com.bondex.dao;

import java.util.List;

import com.bondex.entity.log.PrintLog;

public interface PrintLogDao {

	
	void insertPrintLog(PrintLog printLog);

	List<PrintLog> getPrintlogDetail(PrintLog printLog);

}
