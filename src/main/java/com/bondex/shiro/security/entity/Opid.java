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
	private String opidName; //操作姓名

	public String getOpid() {
		return opid;
	}

	public void setOpid(String opid) {
		this.opid = opid;
	}

	public String getOpidName() {
		return opidName;
	}

	public void setOpidName(String opidName) {
		this.opidName = opidName;
	}

	public Opid(String opid, String opidName) {
		super();
		this.opid = opid;
		this.opidName = opidName;
	}

	public Opid() {
		super();
	}

	@Override
	public String toString() {
		return "Opid [opid=" + opid + ", opidName=" + opidName + "]";
	}

}
