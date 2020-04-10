package com.bondex.entity;

import com.bondex.annoation.dao.Table;
/**
 * 用户办公室信息表
 * @author Qianli
 * 
 * 2020年2月27日 下午4:52:50
 */
@Table(name="default_region")
public class UserDefaultRegion extends Region {
	private Integer id; //自增id
	private String opid; //用户opid
	private String opidName; //用户名称
	private String defaultRegion; //默认区域
	private int type;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
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
	public String getDefaultRegion() {
		return defaultRegion;
	}
	public void setDefaultRegion(String defaultRegion) {
		this.defaultRegion = defaultRegion;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	
	
	

}
