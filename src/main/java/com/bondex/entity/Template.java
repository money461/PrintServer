package com.bondex.entity;

import com.bondex.annoation.dao.Column;
import com.bondex.annoation.dao.Pk;
import com.bondex.annoation.dao.Table;
import com.bondex.util.StringUtils;

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
@Table(name="template")
public class Template extends BaseEntity{
	@Pk
	private String id; //数据库主键
	@Column(name="template_id")
	private String templateId; //模板id
	@Column(name="template_name")
	private String templateName; //模板名称
	private String width; //模板宽度
	private String height; //模板高度
	private String status; //模板状态 0-正常使用 1-暂停使用 
	private String code; //业务code
	@Column(name="code_name")
	private String codeName; //业务标签名称
	@Column(name="is_default")
	private Integer isDefault; //是否默认绑定该业务
	@Column(name="template_url")
	private String templateUrl; //模板地址
	
	@Column(name="extend_data")
	private String extendData; //扩展字段
	
	@Column(name="create_opid")
	private String createOpid; //模板创建者 opid
	
	@Column(name="create_name")
	private String createName; //姓名

	
}
