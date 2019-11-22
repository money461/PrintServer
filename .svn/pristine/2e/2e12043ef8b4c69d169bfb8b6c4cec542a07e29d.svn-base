package com.bondex.controller.layout;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @version 2018年5月16日 10:01:20
 * @author bondex_public
 *
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
		modelAndView.addObject("userInfo", request.getSession().getAttribute("userInfo"));
		modelAndView.setViewName("conter");
		return modelAndView;
	}

	@RequestMapping("west/label")
	public ModelAndView label(ModelAndView modelAndView) {
		modelAndView.setViewName("label/label");
		return modelAndView;
	}

	@RequestMapping("west/system")
	public ModelAndView system(ModelAndView modelAndView) {
		modelAndView.setViewName("label/system");
		return modelAndView;
	}
}
