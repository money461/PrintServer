package com.bondex.controller.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bondex.test.FrameworkTest;

@Controller
public class UserController {
	@Autowired
	private FrameworkTest frameworkTest;

	@RequestMapping("/list2")
	public String userList2(Model model) throws Exception {
		model.addAttribute("hello", "aaaaaaaaaaaaaaaaaaaa");
		return "test";
	}

	@RequestMapping("/test")
	public String userList(Model model) throws Exception {
		frameworkTest.aa();
		return "test";
	}
}
