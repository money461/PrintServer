package com.bondex.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.bondex.annoation.dao.Column;
import com.bondex.annoation.dao.Ignore;
import com.bondex.annoation.dao.Pk;
import com.bondex.annoation.dao.Table;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

@Table(name="label")
public class Label implements Serializable {
	/**
	 * 
	 */
	@Ignore
	private static final long serialVersionUID = 4781030285292455283L;
	@Pk
	@Column(name="label_id")
	private String labelId; //标签主键
	@Ignore
	@JSONField(name="SerialNo")
	private String SerialNo; //序号
//	@NotNull(message="主单号不能为空！")
	private String mawb; //主单
	private String hawb; //分单
	@SerializedName(value="destination",alternate="destinationCity")
	@JSONField(alternateNames={"destinationCity"})
	private String destination;// 目的地
	private String total;// 件数 打印份数 总件数
	@Ignore
	@JSONField(name="TotalAcount")
	private String TotalAcount; //收货总件数
	@Column(name="airport_departure")
	private String airportDeparture;// 起始地 出发机场
	
	@Column(name="is_print")
	private String isPrint; //是否打印 1 已打印 2 已导出 其它 未打印
	private String reserve1; //是否删除 1-删除 0-未删除'
	
	private String reserve3; //模版外键 模板主键 也可是 模板名称
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JSONField(format="yyyy-MM-dd")
	@Column(name="flight_date")
	private Date flightDate; //航班日期
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JSONField(format="yyyy-MM-dd")
	@Column(name="print_time")
	private Date printTime; //打印时间
	
	@Column(name="print_user")
	private String printUser; //打印人
	
	@Column(name="list_id")
	private String listId; //mq报文id，用于判断报文是否重复
	
	@Column(name="code")
	private String code; //业务code
	
	@Column(name="code_name")
	private String codeName; //打印标签名称
	
	
	@SerializedName(value="opid",alternate="opId") //使用Gson 序列化名称 反序列化备选名称opId)
	@JSONField(alternateNames={"opId"})
	private String opid;
	
	@SerializedName(value="opidName",alternate="opIdName") //使用Gson 序列化名称 反序列化备选名称opIdName)
	@Column(name="opid_name")
	private String opidName; //录入人
	
	@SerializedName(value="Mblno",alternate={"MBLNo"})
	@JSONField(name="Mblno")
	private String MBLNo; //运单号
	
//	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@SerializedName(value="EDeparture",alternate="placeOrderDate") //使用Gson 序列化名称 反序列化备选名称placeOrderDate)
	private Date EDeparture; //发货日期
	
	@SerializedName(value="SendAddress")
	@JSONField(name="SendAddress")
	private String SendAddress; //发货人信息
	
	@SerializedName(value="TakeCargoNo",alternate="takeCargoNo")
	@JSONField(alternateNames={"takeCargoNo"})
	private String TakeCargoNo; //随货同行单号
	
	@Column(name="business_type")
	private Integer businessType; //类型
	
	@JSONField(name="RecAddress")
	private String RecAddress; //收货地址
	
	@Column(name="packages")
	private BigDecimal packages; //分单件数
	
	@SerializedName(value="RecCustomerName",alternate="receivingCustomer")
	@JSONField(name="RecCustomerName")
	private String RecCustomerName;//收货人
	
	@SerializedName(value="createTime")
	@JSONField(alternateNames={"createTime"},format="yyyy-MM-dd HH:mm:ss")
	@Column(name="create_time")
	private Date createTime;
	
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@SerializedName(value="updateTime")
	@JSONField(alternateNames={"updateTime"},format="yyyy-MM-dd HH:mm:ss")
	@Column(name="update_time")
	@Ignore
	private Date updateTime;

    /** 请求参数 */ //params[endTime] params[beginTime]
    private Map<String, Object> params;
	
	@JsonProperty(value="MBLNo")
	public String getMBLNo() {
		return MBLNo;
	}

	@JSONField(name="MBLNo")
	public void setMBLNo(String mBLNo) {
		MBLNo = mBLNo;
	}

	@JSONField(name="EDeparture")
	@JsonFormat(pattern = "yyyy-MM-dd")  
	@JsonProperty(value="EDeparture")
	public Date getEDeparture() {
		return EDeparture;
	}

	@JSONField(name="EDeparture",alternateNames={"placeOrderDate"})
	@JsonProperty(value="EDeparture")
	public void setEDeparture(Date eDeparture) {
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

	public String getLabelId() {
		return labelId;
	}
	
	public void setLabelId(String labelId) {
		this.labelId = labelId;
	}

	@JsonProperty(value="TotalAcount")
	public String getTotalAcount() {
		return TotalAcount;
	}

	public void setTotalAcount(String totalAcount) {
		TotalAcount = totalAcount;
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

	public String getAirportDeparture() {
		return airportDeparture;
	}

	public void setAirportDeparture(String airportDeparture) {
		this.airportDeparture = airportDeparture;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getIsPrint() {
		return isPrint;
	}

	public void setIsPrint(String isPrint) {
		this.isPrint = isPrint;
	}

	public String getReserve1() {
		return reserve1;
	}

	public void setReserve1(String reserve1) {
		this.reserve1 = reserve1;
	}


	public Date getPrintTime() {
		return printTime;
	}

	public void setPrintTime(Date printTime) {
		this.printTime = printTime;
	}

	public String getReserve3() {
		return reserve3;
	}

	public void setReserve3(String reserve3) {
		this.reserve3 = reserve3;
	}

	public Date getFlightDate() {
		return flightDate;
	}

	public void setFlightDate(Date flightDate) {
		this.flightDate = flightDate;
	}

	public String getPrintUser() {
		return printUser;
	}

	public void setPrintUser(String printUser) {
		this.printUser = printUser;
	}

	public String getListId() {
		return listId;
	}

	public void setListId(String listId) {
		this.listId = listId;
	}

	public String getOpid() {
		return opid;
	}

	public void setOpid(String opid) {
		this.opid = opid;
	}

	public String getOpidName() {
		return opidName;
	}

	public void setOpidName(String opidName) {
		this.opidName = opidName;
	}

	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public Integer getBusinessType() {
		return businessType;
	}

	public void setBusinessType(Integer businessType) {
		this.businessType = businessType;
	}

	@JsonProperty(value="packages")
	public BigDecimal getPackages() {
		return packages;
	}

	public void setPackages(BigDecimal packages) {
		this.packages = packages;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	
	
	
	

}
