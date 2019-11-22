/**
  * Copyright 2018 bejson.com 
  */
package com.bondex.client.entity;

import java.util.List;

/**
 * Auto-generated: 2018-08-13 14:2:34
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Search {

	private List<Listarea> list;
	private boolean success;
	private String msg;

	public List<Listarea> getList() {
		return list;
	}

	public void setList(List<Listarea> list) {
		this.list = list;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean getSuccess() {
		return success;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

}