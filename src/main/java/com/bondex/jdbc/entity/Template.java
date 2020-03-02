package com.bondex.jdbc.entity;

public class Template {
	private String id; //数据库主键
	private String template_id; //模板id
	private String template_name; //模板名称
	
	public Template() {
		super();
	}
	
	public Template(String id, String template_id, String template_name) {
		super();
		this.id = id;
		this.template_id = template_id;
		this.template_name = template_name;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTemplate_id() {
		return template_id;
	}

	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}

	public String getTemplate_name() {
		return template_name;
	}

	public void setTemplate_name(String template_name) {
		this.template_name = template_name;
	}

}
