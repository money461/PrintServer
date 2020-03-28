package com.bondex.service;

import com.bondex.entity.log.Log;
import com.bondex.entity.page.PageBean;

public interface LogInfoService {

	PageBean<Log> getlogDetail(Log log);

}
