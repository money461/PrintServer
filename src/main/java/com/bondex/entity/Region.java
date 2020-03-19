package com.bondex.entity;

import lombok.Data;

@Data
public class Region {
	private Integer region_id;
	private String region_code;  //始终全表唯一
	private String region_name;
	private String parent_code;
	private String parent_name;
	private String create_time;


}
