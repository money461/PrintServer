package com.bondex.common;

public enum ComEnum {
	
	
	AirLabel("air","空运标签"),
	PaiangLabel("medicine","派昂医药");
	
	public String code;
	
	public String name;
	
	ComEnum(String code, String name) {
		this.code=code;
		this.name=name;
	}

}
