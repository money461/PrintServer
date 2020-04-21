package com.bondex.entity;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

import com.bondex.annoation.dao.Column;
import com.bondex.annoation.dao.Pk;
import com.bondex.annoation.dao.Table;
import com.bondex.util.Regexs;

import lombok.Data;

@Data
@Table(name="region")
public class Region extends BaseEntity {
	
	@Pk
	@Column(name="region_id")
	private Integer regionId;
	
	@NotBlank(message="区域code不能为空！")
	@Column(name="region_code")
	private String regionCode;  //始终全表唯一
	
	@NotBlank(message="区域code名称不能为空！")
	@Column(name="region_name")
	private String regionName;
	
	@Column(name="parent_code")
	private String parentCode; 
	
	@Column(name="parent_name")
	private String parentName; //父级区域
	
	@NotBlank(message="打印机名称不能为空！")
	@Column(name="printer_name")
	private String printerName;  //打印机名称
	
	@Pattern(regexp = Regexs.ip, message = "IP地址格式不正确")
	@Column(name="printer_ip")
	private String printerIp; //打印IP地址

}
