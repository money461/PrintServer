package com.bondex.jdbc.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class Label implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4781030285292455283L;
	private String label_id; //标签主键
	@JSONField(name="SerialNo")
	private String SerialNo; //序号
//	@NotNull(message="主单号不能为空！")
	private String mawb; //主单
	private String hawb; //分单
	@SerializedName(value="destination",alternate="destinationCity")
	@JSONField(alternateNames={"destinationCity"})
	private String destination;// 目的地
	private String total;// 件数 打印份数 总件数
	@JSONField(name="TotalAcount")
	private String TotalAcount; //收货总件数
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
	
	@SerializedName(value="Mblno",alternate={"MBLNo"})
	@JSONField(name="Mblno")
	private String MBLNo; //运单号
	
//	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@SerializedName(value="EDeparture",alternate="placeOrderDate") //使用Gson 序列化名称 反序列化备选名称placeOrderDate)
	private String EDeparture; //发货日期
	
	@SerializedName(value="SendAddress")
	@JSONField(name="SendAddress")
	private String SendAddress; //发货人信息
	@SerializedName(value="TakeCargoNo",alternate="takeCargoNo")
	@JSONField(alternateNames={"takeCargoNo"})
	private String TakeCargoNo; //随货同行单号
	
	private Integer business_type; //类型
	
	@JSONField(name="RecAddress")
	private String RecAddress; //收货地址
	private BigDecimal packages; //分单件数
	@SerializedName(value="RecCustomerName",alternate="receivingCustomer")
	@JSONField(name="RecCustomerName")
	private String RecCustomerName;//收货人
	
	@JsonProperty(value="MBLNo")
	public String getMBLNo() {
		return MBLNo;
	}

	@JSONField(name="MBLNo")
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

	public String getLabel_id() {
		return label_id;
	}

	@JsonProperty(value="TotalAcount")
	public String getTotalAcount() {
		return TotalAcount;
	}

	public void setTotalAcount(String totalAcount) {
		TotalAcount = totalAcount;
	}

	public void setLabel_id(String label_id) {
		this.label_id = label_id;
	}


	@JsonProperty(value="SerialNo")
	public String getSerialNo() {
		return SerialNo;
	}

	public void setSerialNo(String serialNo) {
		SerialNo = serialNo;
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

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getAirport_departure() {
		return airport_departure;
	}

	public void setAirport_departure(String airport_departure) {
		this.airport_departure = airport_departure;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Date getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}

	public String getIs_print() {
		return is_print;
	}

	public void setIs_print(String is_print) {
		this.is_print = is_print;
	}

	public String getReserve1() {
		return reserve1;
	}

	public void setReserve1(String reserve1) {
		this.reserve1 = reserve1;
	}

	public String getReserve2() {
		return reserve2;
	}

	public void setReserve2(String reserve2) {
		this.reserve2 = reserve2;
	}

	public String getReserve3() {
		return reserve3;
	}

	public void setReserve3(String reserve3) {
		this.reserve3 = reserve3;
	}

	public Date getFlight_date() {
		return flight_date;
	}

	public void setFlight_date(Date flight_date) {
		this.flight_date = flight_date;
	}

	public String getPrint_user() {
		return print_user;
	}

	public void setPrint_user(String print_user) {
		this.print_user = print_user;
	}

	public String getList_id() {
		return list_id;
	}

	public void setList_id(String list_id) {
		this.list_id = list_id;
	}

	public String getOpid() {
		return opid;
	}

	public void setOpid(String opid) {
		this.opid = opid;
	}

	public String getOpid_name() {
		return opid_name;
	}

	public void setOpid_name(String opid_name) {
		this.opid_name = opid_name;
	}

	public Integer getBusiness_type() {
		return business_type;
	}

	public void setBusiness_type(Integer business_type) {
		this.business_type = business_type;
	}

	@JsonProperty(value="packages")
	public BigDecimal getPackages() {
		return packages;
	}

	public void setPackages(BigDecimal packages) {
		this.packages = packages;
	}
	
	
	

}
