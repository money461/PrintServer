package com.bondex.shiro.security.entity;
/**
 * 模块权限
 * @author Qianli
 * 
 * 2019年1月22日 下午3:56:54
 */
public class ModulePermission {
	
	private int ParentID;
	private String ModuleName;
	private boolean IsClosed;
	private String ModuleURL;
	private int ShowOrder;
	private int ModuleID;
	private String PageCode;
	private boolean IsSystem;
	private int ApplicationID;
	private boolean IsInherit;
	
	public int getParentID() {
		return ParentID;
	}
	public void setParentID(int parentID) {
		ParentID = parentID;
	}
	public String getModuleName() {
		return ModuleName;
	}
	public void setModuleName(String moduleName) {
		ModuleName = moduleName;
	}
	public boolean isIsClosed() {
		return IsClosed;
	}
	public void setIsClosed(boolean isClosed) {
		IsClosed = isClosed;
	}
	public String getModuleURL() {
		return ModuleURL;
	}
	public void setModuleURL(String moduleURL) {
		ModuleURL = moduleURL;
	}
	public int getShowOrder() {
		return ShowOrder;
	}
	public void setShowOrder(int showOrder) {
		ShowOrder = showOrder;
	}
	public int getModuleID() {
		return ModuleID;
	}
	public void setModuleID(int moduleID) {
		ModuleID = moduleID;
	}
	public String getPageCode() {
		return PageCode;
	}
	public void setPageCode(String pageCode) {
		PageCode = pageCode;
	}
	public boolean isIsSystem() {
		return IsSystem;
	}
	public void setIsSystem(boolean isSystem) {
		IsSystem = isSystem;
	}
	public int getApplicationID() {
		return ApplicationID;
	}
	public void setApplicationID(int applicationID) {
		ApplicationID = applicationID;
	}
	public boolean isIsInherit() {
		return IsInherit;
	}
	public void setIsInherit(boolean isInherit) {
		IsInherit = isInherit;
	}
	
	

}
