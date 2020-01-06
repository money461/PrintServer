package com.bondex.controller.paiang;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authz.AuthorizationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bondex.common.Common;
import com.bondex.common.enums.ResEnum;
import com.bondex.config.exception.BusinessException;
import com.bondex.entity.Datagrid;
import com.bondex.jdbc.entity.Label;
import com.bondex.jdbc.service.LabelInfoService;
import com.bondex.res.MsgResult;
import com.bondex.security.entity.JsonResult;
import com.bondex.security.entity.UserInfo;
import com.bondex.shiro.security.ShiroUtils;
import com.bondex.util.StringUtils;

@Controller
@RequestMapping("/paiang")
@Validated
public class PaiangController {


	@Resource(name="paiangServiceImple")
	private LabelInfoService labelInfoService;
	/**
	 * 进入展示页面
	 * @param mawb
	 * @param modelAndView
	 * @return
	 */
	@RequestMapping(value="/viewdata",method = RequestMethod.GET)
	@ResponseBody
//	@RequiresPermissions(value={"viewdata","look"},logical=Logical.OR)
	public ModelAndView viewdata(@RequestParam(name="mawb",required=false)String mawb,ModelAndView modelAndView){
		try {
			modelAndView.addObject("mawb", mawb);
			modelAndView.setViewName("paiang/paiangList"); //页面展示
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return modelAndView;
	}
	
	/*
	 * 查询数据 
	 * mawb 主单号
	 * page 页面
	 * rows 行数
	 */
	@RequestMapping(value="/search",method = RequestMethod.POST,headers = {"content-type=application/x-www-form-urlencoded; charset=UTF-8"},produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public Object search(String page, String rows,Label label,String start_time, String end_time, String sort, String order, String opid,HttpServletRequest request) throws Exception{
		HttpSession session = request.getSession();
		Map<String, Object> map = (Map<String, Object>) session.getAttribute(Common.Session_UserSecurity);
		if(StringUtils.isNull(map)||map.size()==0){
			throw new BusinessException(ResEnum.FORBIDDEN.CODE,"你没有相关的打印权限,无法查看数据");
		}
		UserInfo userInfo = ShiroUtils.getUserInfo();
		opid = userInfo.getOpid();
		//获取打印模板权限
		List<JsonResult> list = (List<JsonResult>)map.get(opid + Common.UserSecurity_PrintButton);
		Datagrid datagrid = labelInfoService.findByPage(page, rows, label, start_time, end_time, sort, order, opid, list, "medicine");
		 return MsgResult.result(ResEnum.SUCCESS.CODE,ResEnum.SUCCESS.MESSAGE,datagrid);
	}
	
}
