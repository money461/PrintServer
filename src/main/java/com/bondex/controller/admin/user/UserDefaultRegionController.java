package com.bondex.controller.admin.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bondex.controller.BaseController;
import com.bondex.entity.UserDefaultRegion;
import com.bondex.entity.page.TableDataInfo;
import com.bondex.entity.res.AjaxResult;
import com.bondex.service.UserDefaultRegionService;
import com.bondex.util.StringUtils;
@Controller
@RequestMapping(value="/user")
public class UserDefaultRegionController extends BaseController {

	@Autowired
	private UserDefaultRegionService userDefaultRegionService;
	
	@RequestMapping("/list")
	@ResponseBody
	public Object getAllUserDefaultRegion(UserDefaultRegion defaultRegion){
		startPage(); //分页插件
		List<UserDefaultRegion> list = userDefaultRegionService.selectUserDefaultRegion(defaultRegion);
		TableDataInfo info=  getDataTable(list); //数据转换
		if(StringUtils.isEmpty(list)){
			info.setCode(1);
			info.setMsg("没有找到匹配的操作！");
		}
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
		if(1==i){
			return AjaxResult.success();
		}else{
			return AjaxResult.error("操作失败！");
		}
	}
	
	
}
