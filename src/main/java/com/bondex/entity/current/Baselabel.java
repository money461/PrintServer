package com.bondex.entity.current;

import com.bondex.annoation.dao.Column;
import com.bondex.annoation.dao.Pk;
import com.bondex.annoation.dao.Table;
import com.bondex.entity.BaseEntity;

import lombok.Data;

@Data
@Table(name="base_label")
public class Baselabel extends BaseEntity {

	@Pk
	@Column(name="id")
	private String id;
	
	@Column(name="show_num")
	private String showNum;
	
	@Column(name="code")
	private String code; //业务code
	
	@Column(name="code_name")
	private String codeName; //打印标签名称
	
	@Column(name="template_id")
	private String templateId; //模板编号
	
	@Column(name="template_name")
	private String templateName; //模板名称
	
	@Column(name="doctype_id")
	private String doctypeId;
	
	@Column(name="doctype_name")
	private String doctypeName;
	
	@Column(name="copies")
	private Integer copies;
	
	@Column(name="print_status")
	private Integer printStatus;
	
	@Column(name="json_data")
	private String jsonData; //json数据
	
	@Column(name="opid")
	private String opid;
	
	@Column(name="opid_name")
	private String opidName;
	
	@Column(name="print_id")
	private String printId;
	
	@Column(name="print_name")
	private String printName;
	
	@Column(name="alert_name")
	private String alertName;
	

	
}
