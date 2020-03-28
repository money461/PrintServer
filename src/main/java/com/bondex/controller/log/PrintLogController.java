package com.bondex.controller.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bondex.controller.BaseController;
import com.bondex.entity.log.PrintLog;
import com.bondex.entity.page.PageBean;
import com.bondex.entity.page.TableDataInfo;
import com.bondex.service.PrintLogService;

import cn.hutool.json.JSONObject;

@Controller
@RequestMapping(value="/printlog")
public class PrintLogController extends BaseController{

	@Autowired
	private PrintLogService printLogService;
	
	/**
	 *   
	 * @param log
	 * @param params 请求参数  params[beginTime] --params[endTime]
	 * @return
	 */
	@RequestMapping(value="/all",method=RequestMethod.POST)
	@ResponseBody
	public Object getlogDetail(PrintLog printLog){
		//startPage(Constants.Bootstrap_TableStyle,true);//设置分页pagehelper
		PageBean<PrintLog> pageBean = printLogService.getPrintlogDetail(printLog);
		TableDataInfo dataTable = getDataTable(pageBean);
		return dataTable;
	}
	
	@RequestMapping(value="/childview",method=RequestMethod.POST)
	@ResponseBody
	public Object getChildView(PrintLog printLog){
		PageBean<JSONObject> pageBean = printLogService.getChildView(printLog);
		TableDataInfo dataTable = getDataTable(pageBean);
		return dataTable;
	}
	
	
}
