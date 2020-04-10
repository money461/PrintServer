package com.bondex.entity;

import com.bondex.annoation.dao.Ignore;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class LabelAndTemplate extends Label {

	/**
	 * 
	 */
	@Ignore
	private static final long serialVersionUID = -5426506160226547998L;
	private String id; //模板主键
	private String templateId; //模板编号
	private String templateName; //模板名称
	private String width; //模板宽度
	private String height; //模板高度
	private String status; //模板状态 0-正常使用 1-暂停使用 

	

}
