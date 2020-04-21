package com.bondex.dao;

import com.bondex.entity.log.PrintLog;
import com.bondex.entity.page.PageBean;

public interface PrintLogDao {

	
	void insertPrintLog(PrintLog printLog);

	PageBean<PrintLog> getPrintlogDetail(PrintLog printLog);

}
