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

	private String LIST_ID;
	private String PARENT_BILL_NO;
	private String BILL_NO;
	private double PACK_NO;
	private String LOAD_CODE;
	private String UNLOAD_CODE;
	private String VOYAGE_DATE;
	private String GEN_ER;
	private String GEN_NAME;

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

}