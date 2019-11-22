package com.bondex.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.stereotype.Repository;

import com.bondex.jdbc.entity.LabelAndTemplate;
import com.bondex.mapper.provider.TemplateDataProvider;

@Repository
@Mapper
public interface TemplateDataMapper {

	@SelectProvider(type=TemplateDataProvider.class,method="getqueryLabelAndTemplateSQL")
	public List<LabelAndTemplate> queryLabelAndTemplate(String sql);
	
}
