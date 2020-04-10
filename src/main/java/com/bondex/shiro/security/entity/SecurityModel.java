package com.bondex.shiro.security.entity;

import java.util.SortedSet;

/**
 * 
 * @author bondex_public
 *
 */
public class SecurityModel {
	private String ApplicationID;
	private String IconURL;
	private String IsClosed;
	private String IsInherit;
	private String IsSystem;
	private String ModuleID;
	private String ModuleName;
	private String ModuleURL;
	private String MVCModuleIcon;
	private String MVCModuleURL;
	private String PageCode;
	private String ParentID;
	private String ShowOrder;
	 /** 子菜单 SortedSet TreeSet是边插入边比较 根据ShowOrder实现自动排序 */
	SortedSet<SecurityModel> children;

	public String getApplicationID() {
		return ApplicationID;
	}

	public void setApplicationID(String applicationID) {
		ApplicationID = applicationID;
	}

	public String getIconURL() {
		return IconURL;
	}

	public void setIconURL(String iconURL) {
		IconURL = iconURL;
	}

	public String getIsClosed() {
		return IsClosed;
	}

	public void setIsClosed(String isClosed) {
		IsClosed = isClosed;
	}

	public String getIsInherit() {
		return IsInherit;
	}

	public void setIsInherit(String isInherit) {
		IsInherit = isInherit;
	}

	public String getIsSystem() {
		return IsSystem;
	}

	public void setIsSystem(String isSystem) {
		IsSystem = isSystem;
	}

	public String getModuleID() {
		return ModuleID;
	}

	public void setModuleID(String moduleID) {
		ModuleID = moduleID;
	}

	public String getModuleName() {
		return ModuleName;
	}

	public void setModuleName(String moduleName) {
		ModuleName = moduleName;
	}

	public String getModuleURL() {
		return ModuleURL;
	}

	public void setModuleURL(String moduleURL) {
		ModuleURL = moduleURL;
	}

	public String getMVCModuleIcon() {
		return MVCModuleIcon;
	}

	public void setMVCModuleIcon(String mVCModuleIcon) {
		MVCModuleIcon = mVCModuleIcon;
	}

	public String getMVCModuleURL() {
		return MVCModuleURL;
	}

	public void setMVCModuleURL(String mVCModuleURL) {
		MVCModuleURL = mVCModuleURL;
	}

	public String getPageCode() {
		return PageCode;
	}

	public void setPageCode(String pageCode) {
		PageCode = pageCode;
	}

	public String getParentID() {
		return ParentID;
	}

	public void setParentID(String parentID) {
		ParentID = parentID;
	}

	public String getShowOrder() {
		return ShowOrder;
	}

	public void setShowOrder(String showOrder) {
		ShowOrder = showOrder;
	}

	public SortedSet<SecurityModel> getChildren() {
		return children;
	}

	public void setChildren(SortedSet<SecurityModel> children) {
		this.children = children;
	}

	

}
