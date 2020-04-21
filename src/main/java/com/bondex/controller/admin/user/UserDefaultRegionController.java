package com.bondex.controller.admin.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bondex.common.enums.ResEnum;
import com.bondex.controller.BaseController;
import com.bondex.entity.UserDefaultRegion;
import com.bondex.entity.page.TableDataInfo;
import com.bondex.entity.res.AjaxResult;
import com.bondex.service.UserDefaultRegionService;
import com.bondex.util.GsonUtil;
@Controller
@RequestMapping(value="/user")
public class UserDefaultRegionController extends BaseController {

	@Autowired
	private UserDefaultRegionService userDefaultRegionService;
	
	
	/**
	 * 进入用户管理展示页面
	 * @param mawb
	 * @param modelAndView
	 * @return
	 */
	@RequestMapping(value={"/view/{code}","/view"},method = RequestMethod.GET)
	@ResponseBody
//	@RequiresPermissions(value={"viewdata","look"},logical=Logical.OR)
	public ModelAndView indexpage(@PathVariable(name="code",required=false) String code,ModelAndView modelAndView){
		modelAndView.addObject("code", code);
		modelAndView.setViewName("/admin/user/userlist"); //页面展示
		return modelAndView;
	}
	
	@RequestMapping(value={"/adduser/{code}","/adduser"},method = RequestMethod.GET)
	@ResponseBody
//	@RequiresPermissions(value={"viewdata","look"},logical=Logical.OR)
	public ModelAndView addpage(@PathVariable(name="code",required=false) String code,ModelAndView modelAndView){
		modelAndView.addObject("code", code);
		modelAndView.setViewName("/admin/user/add"); //页面展示
		return modelAndView;
	}
	
	@RequestMapping(value={"/edit/{code}/{id}","/edit/{id}"},method = RequestMethod.GET)
	@ResponseBody
//	@RequiresPermissions(value={"viewdata","look"},logical=Logical.OR)
	public ModelAndView editpage(@PathVariable(name="code",required=false) String code,@PathVariable(name="id",required=false) Integer id,ModelAndView modelAndView){
		UserDefaultRegion userDefaultRegion = new UserDefaultRegion();
		userDefaultRegion.setId(id);
		List<UserDefaultRegion> list = userDefaultRegionService.selectUserDefaultRegion(userDefaultRegion);
		modelAndView.addObject("user",list.get(0));
		modelAndView.addObject("code", code);
		modelAndView.setViewName("/admin/user/edit"); //页面展示
		return modelAndView;
	}
	
	
	@RequestMapping("/list")
	@ResponseBody
	public Object getAllUserDefaultRegion(UserDefaultRegion defaultRegion){
		startPage(true,"d"); //分页插件 ,sql 表别名
		List<UserDefaultRegion> list = userDefaultRegionService.selectUserDefaultRegion(defaultRegion);
		TableDataInfo info=  getDataTable(list); //数据转换
	/*	if(StringUtils.isEmpty(list)){
			info.setCode(1);
			info.setMsg("没有找到匹配的操作人员！");
		}*/
		System.out.println(GsonUtil.GsonString(info));
		return info;
		
	}
	
	/**
	 * 校验唯一opid
	 * @param opid
	 * @return
	 */
	@RequestMapping(value="/checkOpidUnique",method=RequestMethod.POST)
	@ResponseBody
	public Integer checkOpidUnique(String opid){
		
		Integer i = userDefaultRegionService.checkOpidUnique(opid);
		return i;
		
	}
	
	/**
	 * 后台管理员 新增用户且绑定办公室
	 * @param userDefaultRegion
	 * @return
	 */
	@RequestMapping(value="/saveOrupdateUser",method=RequestMethod.POST)
	@ResponseBody
	public Object saveOrUpdateUserDefault(UserDefaultRegion userDefaultRegion){
		Integer i = (Integer)userDefaultRegionService.updateOrAddUserRegion(userDefaultRegion);
		return AjaxResult.success();
	}
	
	/*
	 * 
	 * 是否停用用户
	 */
	@RequestMapping(value="/changeStatus",method=RequestMethod.POST)
	@ResponseBody
	public Object changeStatus(UserDefaultRegion userDefaultRegion){
		Integer i = (Integer)userDefaultRegionService.changeStatus(userDefaultRegion);
		TableDataInfo info = new TableDataInfo(0,ResEnum.SUCCESS.MESSAGE);
		return info;
	}
	
	
}
