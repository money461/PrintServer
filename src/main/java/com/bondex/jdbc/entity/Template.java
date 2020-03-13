package com.bondex.jdbc.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模板信息
 * @author Qianli
 * 
 * 2019年11月5日 下午3:14:28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Template {
	private String id; //数据库主键
	private String template_id; //模板id
	private String template_name; //模板名称
	private String width; //模板宽度
	private String height; //模板高度
	private String status; //模板状态 0-正常使用 1-暂停使用 
	private Date createTime; //时间
	
}
