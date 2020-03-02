/**
  * Copyright 2018 bejson.com 
  */
package com.bondex.client.entity;

/**
 * Auto-generated: 2018-06-01 10:12:36
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class VwOrderAll {

	private String Mblno; //主单
	private String Hblno; //分单 
	private String Tquantity; //件数
	private String Dportcode; //起始地
	private String Aprotcode; //目的地

	public void setMblno(String Mblno) {
		this.Mblno = Mblno;
	}

	public String getMblno() {
		return Mblno;
	}

	public void setHblno(String Hblno) {
		this.Hblno = Hblno;
	}

	public String getHblno() {
		return Hblno;
	}

	public void setTquantity(String Tquantity) {
		this.Tquantity = Tquantity;
	}

	public String getTquantity() {
		return Tquantity;
	}

	public void setDportcode(String Dportcode) {
		this.Dportcode = Dportcode;
	}

	public String getDportcode() {
		return Dportcode;
	}

	public String getAprotcode() {
		return Aprotcode;
	}

	public void setAprotcode(String aprotcode) {
		Aprotcode = aprotcode;
	}

}