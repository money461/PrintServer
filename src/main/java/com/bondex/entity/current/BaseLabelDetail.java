package com.bondex.entity.current;

import com.bondex.annoation.dao.Column;
import com.bondex.annoation.dao.Ignore;
import com.bondex.annoation.dao.Table;

import lombok.Data;

@Data
@Table(name="base_label")
public class BaseLabelDetail extends Baselabel {
	
	@Column(name="template_id")
	private String templateId; //模板编号
	@Ignore
	@Column(name="template_name")
	private String templateName; //模板名称
	@Ignore
	private String width; //模板宽度
	@Ignore
	private String height; //模板高度
	

}
