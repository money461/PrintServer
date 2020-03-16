/**
  * Copyright 2018 bejson.com 
  */
package com.bondex.entity.area;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Auto-generated: 2018-08-13 14:2:34
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
/**
 * 机场地址简码封装类
 * @author Qianli
 * 
 * 2020年1月20日 下午4:26:22
 */
public class AreaBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5699950768454120013L;
	
	private String name; //机场名称
	private String code; //机场简码
	private String area; //机场城市
	private String country; //国家

	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty(value="text") //jackson序列化为text
	public String getName() {
		if(StringUtils.isNoneBlank(code)){
			return "["+code+"]"+area+"/"+name;
		}
		return name;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@JsonProperty(value="id") //序列化为id
	public String getCode() {
		return code;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getArea() {
		return area;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountry() {
		return country;
	}

}