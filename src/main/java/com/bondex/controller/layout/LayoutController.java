package com.bondex.controller.layout;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.bondex.common.Common;

/**
 * @version 2018年5月16日 10:01:20
 * @author bondex_public
 * 页面跳转
 */
@Controller
@RequestMapping("layout")
public class LayoutController {

	@RequestMapping("west")
	public ModelAndView west(ModelAndView modelAndView) {
		modelAndView.setViewName("west");
		return modelAndView;
	}

	@RequestMapping("conter")
	public ModelAndView conter(ModelAndView modelAndView, HttpServletRequest request) {
		modelAndView.addObject(Common.Session_UserInfo, request.getSession().getAttribute(Common.Session_UserInfo));
		modelAndView.setViewName("conter");
		return modelAndView;
	}

	/**
	 * 进入空运数据标签展示页面
	 * @param modelAndView
	 * @return
	 */
	@RequestMapping("west/airlabel")
	public ModelAndView label(ModelAndView modelAndView) {
		modelAndView.setViewName("airlabel/airlabel");
		return modelAndView;
	}

	@RequestMapping("west/system")
	public ModelAndView system(ModelAndView modelAndView) {
		modelAndView.setViewName("system/system");
		return modelAndView;
	}
}
