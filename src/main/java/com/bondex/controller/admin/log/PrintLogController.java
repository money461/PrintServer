package com.bondex.controller.admin.log;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bondex.controller.BaseController;
import com.bondex.entity.log.PrintLog;
import com.bondex.entity.page.TableDataInfo;
import com.bondex.service.PrintLogService;

import cn.hutool.json.JSONObject;

@Controller
@RequestMapping(value="/printlog")
public class PrintLogController extends BaseController{

	@Autowired
	private PrintLogService printLogService;
	
	/**
	 * 进入打印日志库日志展示页面
	 * @param mawb
	 * @param modelAndView
	 * @return
	 */
	@RequestMapping(value={"/view/{code}","/view"},method = RequestMethod.GET)
	@ResponseBody
//	@RequiresPermissions(value={"viewdata","look"},logical=Logical.OR)
	public ModelAndView indexpage(@PathVariable(name="code",required=false) String code,ModelAndView modelAndView){
		modelAndView.addObject("code", code);
		modelAndView.setViewName("/admin/log/printLog"); //页面展示
		return modelAndView;
	}
	
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
		List<PrintLog> pageBean = printLogService.getPrintlogDetail(printLog);
		TableDataInfo dataTable = getDataTable(pageBean);
		return dataTable;
	}
	
	@RequestMapping(value="/childview",method=RequestMethod.POST)
	@ResponseBody
	public Object getChildView(PrintLog printLog){
		List<JSONObject> pageBean = printLogService.getChildView(printLog);
		TableDataInfo dataTable = getDataTable(pageBean);
		return dataTable;
	}
	
	
}
