/**
  * Copyright 2018 bejson.com 
  */
package com.bondex.entity.client;

/**
 * Auto-generated: 2018-05-31 9:52:53
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
/**
 * 打印客户端封装类
 * @author Qianli
 * 
 * 2019年12月27日 上午9:08:54
 */
public class Client {

	private String Version = "2.0";
	private String ReportID; //模板id
	private String ReportTplName; //标签名称
	private String SenderName; //发送人
	private String SendOPID;// 操作id
	private String NoToShow; //业务单号
	private String OtherToShow; //
	private String ReportWidth; //标签宽度 毫米
	private String ReportHeight; //标签高度  毫米
	private String data; //json数据
	private String Copies;// 打印份数

	public String getCopies() {
		return Copies;
	}

	public void setCopies(String copies) {
		Copies = copies;
	}

	public String getSendOPID() {
		return SendOPID;
	}

	public void setSendOPID(String sendOPID) {
		SendOPID = sendOPID;
	}

	public void setVersion(String Version) {
		this.Version = Version;
	}

	public String getVersion() {
		return Version;
	}

	public void setReportID(String ReportID) {
		this.ReportID = ReportID;
	}

	public String getReportID() {
		return ReportID;
	}

	public void setReportTplName(String ReportTplName) {
		this.ReportTplName = ReportTplName;
	}

	public String getReportTplName() {
		return ReportTplName;
	}

	public void setSenderName(String SenderName) {
		this.SenderName = SenderName;
	}

	public String getSenderName() {
		return SenderName;
	}

	public void setNoToShow(String NoToShow) {
		this.NoToShow = NoToShow;
	}

	public String getNoToShow() {
		return NoToShow;
	}

	public void setOtherToShow(String OtherToShow) {
		this.OtherToShow = OtherToShow;
	}

	public String getOtherToShow() {
		return OtherToShow;
	}

	public void setReportWidth(String ReportWidth) {
		this.ReportWidth = ReportWidth;
	}

	public String getReportWidth() {
		return ReportWidth;
	}

	public void setReportHeight(String ReportHeight) {
		this.ReportHeight = ReportHeight;
	}

	public String getReportHeight() {
		return ReportHeight;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getData() {
		return data;
	}

}