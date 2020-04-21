package com.bondex.common;

public enum ComEnum {
	
	
	AirLabel("air_label","空运标签"),
	ChongqingLabel("chongqing_label","重庆标签"),
	PaiangLabel("paiang_label","派昂标签");
	
	public String code;
	
	public String codeName;
	
	ComEnum(String code, String codeName) {
		this.code=code;
		this.codeName=codeName;
	}

}
