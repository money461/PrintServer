package com.bondex.entity.log;

import java.io.Serializable;

import com.bondex.annoation.dao.Column;
import com.bondex.annoation.dao.Ignore;
import com.bondex.annoation.dao.Pk;
import com.bondex.annoation.dao.Table;
import com.bondex.entity.BaseEntity;

import lombok.Data;

@Table(name="log")
@Data
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
	
	@Column(name="correlation_id")
	private String correlationId; //消息唯一标识
	
	@Column(name="seq_no")
	private String seqNo; //消息ID
	@Column(name="sender_name")
	private String senderName; //消息来源
	@Column(name="reciver_name")
	private String reciverName; //消息接受系统
	
	@Column(name="doctype_name")
	private String doctypeName; //消息类型声明
	
	private String code; //业务code
	
	@Column(name="code_name")
	private String codeName; //打印标签名称
	
	private String mawb; //主单号
	private String hawb; //分单号
	private int status;// 0-入库失败 1-入库成功
	private String detail;// 失败原因
	@Column(name="handle_type")
	private String handleType;// 处理类型。成功/失败
	private String Json;
	

}
