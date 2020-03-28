package com.bondex.controller.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bondex.controller.BaseController;
import com.bondex.entity.log.Log;
import com.bondex.entity.page.PageBean;
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
		startPage(true);//设置分页pagehelper
		PageBean<Log> pageBean = logInfoService.getlogDetail(log);
		TableDataInfo dataTable = getDataTable(pageBean);
		return dataTable;
		
	}
	
}
