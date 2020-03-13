package com.bondex.controller.layout;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @version 2018年5月16日 10:01:20
 * @author bondex_public
 * 页面跳转
 */
@Controller
@RequestMapping("layout")
public class LayoutController {


	/**
	 * 进入空运数据标签展示页面
	 * @param modelAndView
	 * @return
	 */
//	@RequiresPermissions(value={"AirPrintLabel"})
	@RequestMapping(value="west/airlabel")
	public ModelAndView label(ModelAndView modelAndView) {
		modelAndView.setViewName("airlabel/airlabel");
		return modelAndView;
	}

	
}
