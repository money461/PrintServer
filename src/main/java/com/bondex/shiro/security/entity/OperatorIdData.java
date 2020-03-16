package com.bondex.shiro.security.entity;

import java.util.Map;

public class OperatorIdData {
	private String cstoken;
	private String email;
	private String logintype;
	private String opid;
	private Map<String, String> opids;
	private String opname;
	private String psncode;
	private String psnname;
	private String type;

	public String getCstoken() {
		return cstoken;
	}

	public void setCstoken(String cstoken) {
		this.cstoken = cstoken;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLogintype() {
		return logintype;
	}

	public void setLogintype(String logintype) {
		this.logintype = logintype;
	}

	public String getOpid() {
		return opid;
	}

	public void setOpid(String opid) {
		this.opid = opid;
	}

	public Map<String, String> getOpids() {
		return opids;
	}

	public void setOpids(Map<String, String> opids) {
		this.opids = opids;
	}

	public String getOpname() {
		return opname;
	}

	public void setOpname(String opname) {
		this.opname = opname;
	}

	public String getPsncode() {
		return psncode;
	}

	public void setPsncode(String psncode) {
		this.psncode = psncode;
	}

	public String getPsnname() {
		return psnname;
	}

	public void setPsnname(String psnname) {
		this.psnname = psnname;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public OperatorIdData(String cstoken, String email, String logintype, String opid, Map<String, String> opids, String opname, String psncode, String psnname, String type) {
		super();
		this.cstoken = cstoken;
		this.email = email;
		this.logintype = logintype;
		this.opid = opid;
		this.opids = opids;
		this.opname = opname;
		this.psncode = psncode;
		this.psnname = psnname;
		this.type = type;
	}

	public OperatorIdData() {
		super();
	}

}
