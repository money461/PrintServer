package com.bondex.shiro.security.entity;

import java.io.Serializable;

/**
 *  
 * @author Qianli
 * 
 * 2019年1月22日 上午11:14:06
 */
public class Opid implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1224192431868505094L;
	private String opid; //操作id
	private String opid_name; //操作姓名

	public String getOpid() {
		return opid;
	}

	public void setOpid(String opid) {
		this.opid = opid;
	}

	public String getOpid_name() {
		return opid_name;
	}

	public void setOpid_name(String opid_name) {
		this.opid_name = opid_name;
	}

	public Opid(String opid, String opid_name) {
		super();
		this.opid = opid;
		this.opid_name = opid_name;
	}

	public Opid() {
		super();
	}

	@Override
	public String toString() {
		return "Opid [opid=" + opid + ", opid_name=" + opid_name + "]";
	}

}
