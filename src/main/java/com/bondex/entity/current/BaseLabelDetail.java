package com.bondex.entity.current;

import com.bondex.annoation.dao.Ignore;
import com.bondex.annoation.dao.Table;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@Table(name="base_label")
@ApiModel(value = "BaseLabelDetail", description = "标签基本数据结构对象")
public class BaseLabelDetail extends Baselabel {
	

	@Ignore
	private String width; //模板宽度
	@Ignore
	private String height; //模板高度
	
	

}
