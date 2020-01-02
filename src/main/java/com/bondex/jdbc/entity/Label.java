package com.bondex.jdbc.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Getter
@Setter
public class Label implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4781030285292455283L;
	private String label_id; //标签主键
	private String SerizalNo; //序号
//	@NotNull(message="主单号不能为空！")
	private String mawb; //主单
	private String hawb; //分单
	@SerializedName(value="destination",alternate="destinationCity")
	@JSONField(alternateNames={"destinationCity"})
	private String destination;// 目的地
	private String total;// 件数 打印份数 总件数
	private String TotalAccount; //收货总件数
	private String airport_departure;// 起始地 出发机场
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@SerializedName(value="create_time",alternate="createTime")
	@JSONField(alternateNames={"createTime"},format="yyyy-MM-dd HH:mm:ss")
	private Date create_time;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@SerializedName(value="update_time",alternate="updateTime")
	@JSONField(alternateNames={"updateTime"},format="yyyy-MM-dd HH:mm:ss")
	private Date update_time;
	private String is_print; //是否打印 1 已打印 2 已导出 其它 未打印
	private String reserve1; //是否删除 1-删除 0-未删除'
	private String reserve2; //打印时间
	private String reserve3; //模版外键 模板主键 也可是 模板名称
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JSONField(format="yyyy-MM-dd")
	private Date flight_date; //航班日期
	private String print_user; //打印人
	private String list_id; //mq报文id，用于判断报文是否重复
	@SerializedName(value="opid",alternate="opId") //使用Gson 序列化名称 反序列化备选名称opId)
	@JSONField(alternateNames={"opId"})
	private String opid;
	@SerializedName(value="opid_name",alternate="opIdName") //使用Gson 序列化名称 反序列化备选名称opIdName)
	@JSONField(alternateNames={"opIdName"})
	private String opid_name; //录入人
	@SerializedName(value="Mblno")
	private String MBLNo; //运单号
	
//	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@SerializedName(value="EDeparture",alternate="placeOrderDate") //使用Gson 序列化名称 反序列化备选名称placeOrderDate)
	private String EDeparture; //发货日期
	
	@SerializedName(value="SendAddress")
	private String SendAddress; //发货人信息
	@SerializedName(value="TakeCargoNo",alternate="takeCargoNo")
	@JSONField(alternateNames={"takeCargoNo"})
	private String TakeCargoNo; //随货同行单号
	
	private Integer business_type; //类型
	
	private String RecAddress; //收货地址
	private BigDecimal packages; //总件数
	@SerializedName(value="RecCustomerName",alternate="receivingCustomer")
	private String RecCustomerName;//收货人
	
	@JsonProperty(value="MBLNo")
	public String getMBLNo() {
		return MBLNo;
	}

	@JSONField(name="MBLNo",alternateNames={"mawb"})
	public void setMBLNo(String mBLNo) {
		MBLNo = mBLNo;
	}

	@JSONField(name="EDeparture")
	@JsonProperty(value="EDeparture")
	public String getEDeparture() {
		return EDeparture;
	}

	@JSONField(name="EDeparture",alternateNames={"placeOrderDate"})
	@JsonProperty(value="EDeparture")
	public void setEDeparture(String eDeparture) {
		EDeparture = eDeparture;
	}
	
	@JsonProperty(value="SendAddress")
	public String getSendAddress() {
		return SendAddress;
	}

	public void setSendAddress(String sendAddress) {
		SendAddress = sendAddress;
	}

	public String getTakeCargoNo() {
		return TakeCargoNo;
	}

	public void setTakeCargoNo(String takeCargoNo) {
		TakeCargoNo = takeCargoNo;
	}
	
	

	public String getRecAddress() {
		return RecAddress;
	}

	public void setRecAddress(String recAddress) {
		RecAddress = recAddress;
	}

	@JSONField(name="RecCustomerName",alternateNames={"receivingCustomer"})
	@JsonProperty(value="RecCustomerName")
	public String getRecCustomerName() {
		return RecCustomerName;
	}


	@JSONField(name="RecCustomerName",alternateNames={"receivingCustomer"})
	@JsonProperty(value="RecCustomerName")
	public void setRecCustomerName(String recCustomerName) {
		RecCustomerName = recCustomerName;
	}

	@JsonProperty(value="TotalAccount")
	public String getTotalAccount() {
		return TotalAccount;
	}

	public void setTotalAccount(String totalAccount) {
		TotalAccount = totalAccount;
	}
	
	
	

}
