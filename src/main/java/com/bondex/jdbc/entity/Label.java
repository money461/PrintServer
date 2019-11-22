package com.bondex.jdbc.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class Label {
	private Integer label_id;
	private String mawb;
	private String hawb;
	private String destination;// 目的地
	private String total;// 件数
	private String airport_departure;// 起始地
	private Date create_time;
	private Date update_time;
	private String is_print;
	private String reserve1;
	private String reserve2;
	private String reserve3;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date flight_date;
	private String print_user;
	private String list_id;
	private String opid;
	private String opid_name;
	private String MBLNo;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Object EDeparture;
	private String SendAddress;
	private String RecCustomerName;
	private String TakeCargoNo;
	private Integer business_type;
	private String RecAddress;

	public String getMBLNo() {
		return MBLNo;
	}

	public void setMBLNo(String mBLNo) {
		MBLNo = mBLNo;
	}

	public Object getEDeparture() {
		return EDeparture;
	}

	public void setEDeparture(Object eDeparture) {
		EDeparture = eDeparture;
	}

	public String getSendAddress() {
		return SendAddress;
	}

	public void setSendAddress(String sendAddress) {
		SendAddress = sendAddress;
	}

	public String getRecCustomerName() {
		return RecCustomerName;
	}

	public void setRecCustomerName(String recCustomerName) {
		RecCustomerName = recCustomerName;
	}

	public String getTakeCargoNo() {
		return TakeCargoNo;
	}

	public void setTakeCargoNo(String takeCargoNo) {
		TakeCargoNo = takeCargoNo;
	}

	public Integer getBusiness_type() {
		return business_type;
	}

	public void setBusiness_type(Integer business_type) {
		this.business_type = business_type;
	}

	public String getRecAddress() {
		return RecAddress;
	}

	public void setRecAddress(String recAddress) {
		RecAddress = recAddress;
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

	public String getList_id() {
		return list_id;
	}

	public void setList_id(String list_id) {
		this.list_id = list_id;
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


	public Integer getLabel_id() {
		return label_id;
	}

	public void setLabel_id(Integer label_id) {
		this.label_id = label_id;
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

	public Label(Integer label_id, String mawb, String hawb, String destination, String total, String airport_departure, Date create_time, Date update_time, String is_print, String reserve1, String reserve2, String reserve3) {
		super();
		this.label_id = label_id;
		this.mawb = mawb;
		this.hawb = hawb;
		this.destination = destination;
		this.total = total;
		this.airport_departure = airport_departure;
		this.create_time = create_time;
		this.update_time = update_time;
		this.is_print = is_print;
		this.reserve1 = reserve1;
		this.reserve2 = reserve2;
		this.reserve3 = reserve3;
	}

	public Label() {
		super();
	}

}
