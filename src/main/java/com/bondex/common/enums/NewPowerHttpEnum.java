package com.bondex.common.enums;

public enum NewPowerHttpEnum implements BaseEnum  {

		GetUserRoleOp("GET","/Api/Roles/GetUserRoleOp","根据操作及系统获取操作数据权限"),
		GetFlowNo("GET","/Api/FlowNumber/GetFlowNo","获取流水号"), 
		GetHasPermissionModuleList ("GET","/Api/Module/GetHasPermissionModuleList","根据系统及操作编号获取用户的菜单权限接口"),
		GetHasPermissionPageButton ("GET","/Api/ModuleExtPermission/GetHasPermissionPageButton","根据操作及页面获取所有页面有权限的按钮接口"),
		GetPageButtonByPermissionValue("GET","/Api/ModuleExtPermission/GetPageButtonByPermissionValue","获取页面有权限的按钮"),
		GetCompanyInfoOfDeptByOperatorID("GET","/Api/Department/GetCompanyInfoOfDeptByOperatorID","根据当前登录用户的操作ID获取所在部门信息"),
		GetOperatorPagePermission("GET","/Api/ModuleExtPermission/GetOperatorPagePermission","获取功能权限"),
		GetOperator("POST","/Api/Operator/GetOperator","获取操作");
		
	    public String method;
	    
	    public String url;
		
		public String message;
		
		NewPowerHttpEnum(String method,String url, String message) {
			this.method=method;
			this.url=url;
			this.message=message;
		}
	
		@Override
		public Object toValue() {
			return message;
		}
	
}
