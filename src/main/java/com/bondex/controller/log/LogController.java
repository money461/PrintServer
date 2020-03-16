package com.bondex.controller.log;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bondex.controller.BaseController;
import com.bondex.dao.LogInfoDao;
import com.bondex.entity.log.Log;
import com.bondex.entity.page.TableDataInfo;
import com.bondex.service.LogInfoService;
@Controller
@RequestMapping(value="/log")
public class LogController extends BaseController {
	
	@Autowired
	private LogInfoService logInfoService;

	/**
	 *   
	 * @param log
	 * @param params 请求参数  params[beginTime] --params[endTime]
	 * @return
	 */
	@RequestMapping(value="/logdetail",method=RequestMethod.POST)
	@ResponseBody
	public Object getlogDetail(Log log){
		startPage();//分页
		List<Log> list = logInfoService.getlogDetail(log);
		TableDataInfo dataTable = getDataTable(list);
		return dataTable;
		
	}
	
}
