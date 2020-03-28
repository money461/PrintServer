package com.bondex.entity.log;

import com.bondex.annoation.dao.Column;
import com.bondex.annoation.dao.Pk;
import com.bondex.annoation.dao.Table;
import com.bondex.entity.BaseEntity;

import lombok.Data;

@Data
@Table(name="print_log")
public class PrintLog extends BaseEntity {
	
	@Pk
	@Column(name="id")
	private Integer id;
	
	@Column(name="label_id")
	private String labelId; 
	
	@Column(name="show_num")
	private String showNum;
	
	private Integer status;
	
	private String mqaddress;
	
	@Column(name="queue_code")
	private String queueCode;
	
	@Column(name="region_name")
	private String regionName;
	
	private String reason;
	
	private String message;
	
	private String opid;
	
	@Column(name="opid_name")
	private String opidName;
	
	
}
