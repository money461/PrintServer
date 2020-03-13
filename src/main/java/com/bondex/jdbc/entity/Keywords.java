/**
  * Copyright 2018 bejson.com 
  */
package com.bondex.jdbc.entity;

/**
 * Auto-generated: 2018-06-26 10:59:45
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Keywords {

	private String LIST_ID; //mq报文id，用于判断报文是否重复
	private String PARENT_BILL_NO; //主单号
	private String BILL_NO; //分单号 需要 切割
	private double PACK_NO; //件数
	private String LOAD_CODE; //起始地
	private String UNLOAD_CODE;  //目的地
	private String VOYAGE_DATE; //航班日期
	private String GEN_ER; //opid
	private String GEN_NAME; //opname
	private String Create_User_ID;
	private String Create_User_Name;
	private String Create_Date;
	private String Update_User_ID;
	private String Update_User_Name;
	private String Update_Date;
	

	public String getGEN_ER() {
		return GEN_ER;
	}

	public void setGEN_ER(String gEN_ER) {
		GEN_ER = gEN_ER;
	}

	public String getGEN_NAME() {
		return GEN_NAME;
	}

	public void setGEN_NAME(String gEN_NAME) {
		GEN_NAME = gEN_NAME;
	}

	public String getVOYAGE_DATE() {
		return VOYAGE_DATE;
	}

	public void setVOYAGE_DATE(String vOYAGE_DATE) {
		VOYAGE_DATE = vOYAGE_DATE;
	}

	public String getLIST_ID() {
		return LIST_ID;
	}

	public void setLIST_ID(String lIST_ID) {
		LIST_ID = lIST_ID;
	}

	public String getPARENT_BILL_NO() {
		return PARENT_BILL_NO;
	}

	public void setPARENT_BILL_NO(String pARENT_BILL_NO) {
		PARENT_BILL_NO = pARENT_BILL_NO;
	}

	public String getBILL_NO() {
		return BILL_NO;
	}

	public void setBILL_NO(String bILL_NO) {
		BILL_NO = bILL_NO;
	}

	public double getPACK_NO() {
		return PACK_NO;
	}

	public void setPACK_NO(double pACK_NO) {
		PACK_NO = pACK_NO;
	}

	public String getLOAD_CODE() {
		if (LOAD_CODE != null) {
			String rt[] = LOAD_CODE.split("/");
			return rt[0];
		} else {
			return "";
		}
	}

	public void setLOAD_CODE(String lOAD_CODE) {
		LOAD_CODE = lOAD_CODE;
	}

	public String getUNLOAD_CODE() {
		return UNLOAD_CODE;
	}

	public void setUNLOAD_CODE(String uNLOAD_CODE) {
		UNLOAD_CODE = uNLOAD_CODE;
	}

	public String getCreate_User_ID() {
		return Create_User_ID;
	}

	public void setCreate_User_ID(String create_User_ID) {
		Create_User_ID = create_User_ID;
	}

	public String getCreate_User_Name() {
		return Create_User_Name;
	}

	public void setCreate_User_Name(String create_User_Name) {
		Create_User_Name = create_User_Name;
	}

	public String getCreate_Date() {
		return Create_Date;
	}

	public void setCreate_Date(String create_Date) {
		Create_Date = create_Date;
	}

	public String getUpdate_User_ID() {
		return Update_User_ID;
	}

	public void setUpdate_User_ID(String update_User_ID) {
		Update_User_ID = update_User_ID;
	}

	public String getUpdate_User_Name() {
		return Update_User_Name;
	}

	public void setUpdate_User_Name(String update_User_Name) {
		Update_User_Name = update_User_Name;
	}

	public String getUpdate_Date() {
		return Update_Date;
	}

	public void setUpdate_Date(String update_Date) {
		Update_Date = update_Date;
	}

	
	
}