package com.bondex.shiro.security.entity;

import java.util.List;

import lombok.Data;

/**
 * 根据用户opid  获取系统功能权限(包括所有菜单/按钮)权限
 * @author Qianli
 * 
 * 2020年4月13日 上午11:00:16
 */
@Data
public class OperatorPagePermission {

	/**
	 * "ExtPermissionID": 5558,
      "ApplicationID": 827,
      "PageCode": "AirPrintLabel",
      "BtnNames": "AirPrintLabel.label-search",
      "PermissionName": "搜索标签",
      "PermissionValue": 2,
      "Description": null,
      "ShowOrder": 0,
      "SysModulePrintButton": []
	 */
	private String ExtPermissionID;
    private String ApplicationID;
    private String PageCode;
    private String BtnNames;
    private String PermissionName;
    private String PermissionValue;
    private String Description;
    private String ShowOrder;
    private List<Object> SysModulePrintButton;
	
}
