package com.bondex.entity;

import com.bondex.annoation.dao.Column;
import com.bondex.annoation.dao.Pk;
import com.bondex.annoation.dao.Table;
/**
 * 用户办公室信息表
 * @author Qianli
 * 
 * 2020年2月27日 下午4:52:50
 */
@Table(name="default_region")
public class UserDefaultRegion extends Region {
	@Pk
	private Integer id; //自增id
	private String opid; //用户opid
	@Column(name="opid_name")
	private String opidName; //用户名称
	@Column(name="default_region")
	private String defaultRegion; //默认区域
	private Integer type;
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
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	

}
