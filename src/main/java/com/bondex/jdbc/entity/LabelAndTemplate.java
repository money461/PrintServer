package com.bondex.jdbc.entity;

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
	private static final long serialVersionUID = -5426506160226547998L;
	private Integer id; //模板主键
	private String template_id; //模板编号
	private String template_name; //模板名称

	

}
