package com.bondex.common.enums;

public enum ComEnum implements BaseEnum {
	
	
	AirLabel("air","空运标签"),
	PaiangLabel("medicine","派昂医药");
	
	public String code;
	
	public String name;
	
	ComEnum(String code, String name) {
		this.code=code;
		this.name=name;
	}

	@Override
	public Object toValue() {
		// TODO Auto-generated method stub
		return code;
	}

}
