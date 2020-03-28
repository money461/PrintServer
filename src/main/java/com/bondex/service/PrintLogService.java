package com.bondex.service;

import com.bondex.entity.log.PrintLog;
import com.bondex.entity.page.PageBean;

import cn.hutool.json.JSONObject;

public interface PrintLogService {

	PageBean<PrintLog> getPrintlogDetail(PrintLog printLog);

	PageBean<JSONObject> getChildView(PrintLog printLog);

}
