package com.bondex.shiro.security.entity;

import java.util.List;

public class TokenResult {
	private boolean success;	
	private String errormsg;	
	private List<UserInfo> message;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getErrormsg() {
		return errormsg;
	}
	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}
	public List<UserInfo> getMessage() {
		return message;
	}
	public void setMessage(List<UserInfo> message) {
		this.message = message;
	}
	
	
	
	
	
}
