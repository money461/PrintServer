package com.bondex.entity.current;

import com.bondex.annoation.dao.Column;
import com.bondex.annoation.dao.Table;
import com.bondex.entity.BaseEntity;

import lombok.Data;

@Data
@Table(name="base_label")
public class Baselabel extends BaseEntity {

	@Column(name="show_num")
	private String showNum;
	
	@Column(name="docType_id")
	private String docTypeId;
	
	@Column(name="docType_name")
	private String docTypeName;
	
	@Column(name="copies")
	private Integer copies;
	
	@Column(name="json_data")
	private String jsonData;
	
	@Column(name="alert_user")
	private String alertUser;
	
	@Column(name="print_user")
	private String printUser;
	
	
}
