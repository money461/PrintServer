package com.bondex.entity;

import java.util.Date;

public class Subscribe {
	private int id;
	private String srUser;
	private String srOpid;
	private String srMawb;
	private String srHawb;
	private String srEmail;
	private String srState;
	private String batch_id;
	private Date create_time;

	public String getBatch_id() {
		return batch_id;
	}

	public void setBatch_id(String batch_id) {
		this.batch_id = batch_id;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public String getSrState() {
		return srState;
	}

	public void setSrState(String srState) {
		this.srState = srState;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSrUser() {
		return srUser;
	}

	public void setSrUser(String srUser) {
		this.srUser = srUser;
	}

	public String getSrOpid() {
		return srOpid;
	}

	public void setSrOpid(String srOpid) {
		this.srOpid = srOpid;
	}

	public String getSrMawb() {
		return srMawb;
	}

	public void setSrMawb(String srMawb) {
		this.srMawb = srMawb;
	}

	public String getSrHawb() {
		return srHawb;
	}

	public void setSrHawb(String srHawb) {
		this.srHawb = srHawb;
	}

	public String getSrEmail() {
		return srEmail;
	}

	public void setSrEmail(String srEmail) {
		this.srEmail = srEmail;
	}

}
