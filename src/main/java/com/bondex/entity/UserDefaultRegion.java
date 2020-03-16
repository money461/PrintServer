package com.bondex.entity;

import org.springframework.stereotype.Component;
/**
 * 用户办公室信息表
 * @author Qianli
 * 
 * 2020年2月27日 下午4:52:50
 */
@Component
public class UserDefaultRegion extends Region {
	private Integer id; //自增id
	private String opid; //用户opid
	private Integer office_id; //打算不再使用
	private String username;
	private String  default_region_code;
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
	public Integer getOffice_id() {
		return office_id;
	}
	public void setOffice_id(Integer office_id) {
		this.office_id = office_id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getDefault_region_code() {
		return default_region_code;
	}
	public void setDefault_region_code(String default_region_code) {
		this.default_region_code = default_region_code;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	
	

}
