package com.bondex.jdbc.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class Log {
	private Integer id;
	private String mawb;
	private String hawb;
	private int state;// 0-入库失败 1-入库成功
	private String detail;// 失败原因
	private String handleType;// 处理类型。成功/失败
	@DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
	private Date updateDate;
	private String Json;

	public String getJson() {
		return Json;
	}

	public void setJson(String json) {
		Json = json;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMawb() {
		return mawb;
	}

	public void setMawb(String mawb) {
		this.mawb = mawb;
	}

	public String getHawb() {
		return hawb;
	}

	public void setHawb(String hawb) {
		this.hawb = hawb;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getHandleType() {
		return handleType;
	}

	public void setHandleType(String handleType) {
		this.handleType = handleType;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

}
