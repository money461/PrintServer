package org.jasig.cas.client.authentication;

import java.util.List;
import java.util.Map;

public class TokenResult {
	private boolean success;	
	private String errormsg;	
	private List<Map> message;
	public List<Map> getMessage() {
		return message;
	}
	public void setMessage(List<Map> message) {
		this.message = message;
	}
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
	@Override
	public String toString() {
		return "TokenResult [success=" + success + ", errormsg=" + errormsg + ", message=" + message + "]";
	}
	
	
}
