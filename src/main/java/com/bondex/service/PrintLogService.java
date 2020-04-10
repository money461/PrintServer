package com.bondex.service;

import java.util.List;

import com.bondex.entity.log.PrintLog;

import cn.hutool.json.JSONObject;

public interface PrintLogService {

	List<PrintLog> getPrintlogDetail(PrintLog printLog);

	List<JSONObject> getChildView(PrintLog printLog);

}
