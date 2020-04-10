package com.bondex.entity;

import com.bondex.annoation.dao.Column;
import com.bondex.annoation.dao.Pk;
import com.bondex.annoation.dao.Table;

import lombok.Data;

@Data
@Table(name="region")
public class Region extends BaseEntity {
	
	@Pk
	@Column(name="region_id")
	private Integer regionId;
	
	@Column(name="region_code")
	private String regionCode;  //始终全表唯一
	
	@Column(name="region_name")
	private String regionName;
	
	@Column(name="parent_code")
	private String parentCode; 
	
	@Column(name="parent_name")
	private String parentName; //父级区域
	
	@Column(name="printer_name")
	private String printerName;  //打印机名称
	
	@Column(name="printer_ip")
	private String printerIp; //打印IP地址

}
