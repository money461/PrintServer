package com.bondex.controller.log;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bondex.controller.BaseController;
import com.bondex.entity.page.TableDataInfo;
import com.bondex.jdbc.dao.LogInfoDao;
import com.bondex.jdbc.entity.Log;
@Controller
@RequestMapping(value="/log")
public class LogController extends BaseController {
	
	@Autowired
	@Qualifier(value="logInfoDaoImpl")
	private LogInfoDao logInfoDao;

	@RequestMapping(value="/logdetail",method=RequestMethod.POST)
	@ResponseBody
	public Object getlogDetail(Log log){
		
		startPage();//分页
		List<Log> list = logInfoDao.getlogDetail(log);
		TableDataInfo dataTable = getDataTable(list);
		return dataTable;
		
	}
	
}
