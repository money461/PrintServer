package com.bondex.controller.admin.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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
	 * 进入标签入库日志展示页面
	 * @param mawb
	 * @param modelAndView
	 * @return
	 */
	@RequestMapping(value={"/view/{code}","/view"},method = RequestMethod.GET)
	@ResponseBody
//	@RequiresPermissions(value={"viewdata","look"},logical=Logical.OR)
	public ModelAndView indexpage(@PathVariable(name="code",required=false) String code,ModelAndView modelAndView){
		modelAndView.addObject("code", code);
		modelAndView.setViewName("/admin/log/labelLog"); //页面展示
		return modelAndView;
	}
	

	/**
	 *   
	 * @param log
	 * @param params 请求参数  params[beginTime] --params[endTime]
	 * @return
	 */
	@RequestMapping(value="/logdetail",method=RequestMethod.POST)
	@ResponseBody
	public Object getlogDetail(Log log){
		PageBean<Log>  pageBean = logInfoService.getlogDetail(log);
		TableDataInfo dataTable = getDataPageBeanTable(pageBean);
		return dataTable;
		
	}
	
}
