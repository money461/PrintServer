package com.bondex.entity.log;

import java.io.Serializable;

import com.bondex.annoation.dao.Column;
import com.bondex.annoation.dao.Ignore;
import com.bondex.annoation.dao.Pk;
import com.bondex.annoation.dao.Table;
import com.bondex.entity.BaseEntity;

@Table(name="log")
public class Log  extends BaseEntity implements Serializable{
	/**
	 * 
	 */
	@Ignore //忽略字段
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	@Pk
	private Integer id;
	private String seqNo; //消息ID
	private String senderName; //消息来源
	private String reciverName; //消息接受系统
	private String docTypeName; //消息类型声明
	private String mawb; //主单号
	private String hawb; //分单号
	private int status;// 0-入库失败 1-入库成功
	private String detail;// 失败原因
	@Column(name="handleType")
	private String handleType;// 处理类型。成功/失败
	private String Json;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getReciverName() {
		return reciverName;
	}
	public void setReciverName(String reciverName) {
		this.reciverName = reciverName;
	}
	public String getDocTypeName() {
		return docTypeName;
	}
	public void setDocTypeName(String docTypeName) {
		this.docTypeName = docTypeName;
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

	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
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
	public String getJson() {
		return Json;
	}
	public void setJson(String json) {
		Json = json;
	}
	
	
	
	

}
