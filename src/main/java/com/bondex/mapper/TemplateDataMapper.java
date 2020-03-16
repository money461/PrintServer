package com.bondex.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.stereotype.Repository;

import com.bondex.entity.LabelAndTemplate;
import com.bondex.mapper.provider.TemplateDataProvider;
import com.bondex.shiro.security.entity.Opid;

@Repository
@Mapper
public interface TemplateDataMapper {

	@SelectProvider(type=TemplateDataProvider.class,method="getqueryLabelAndTemplateSQL")
	public List<LabelAndTemplate> queryLabelAndTemplate(String sql);

	@SelectProvider(type=TemplateDataProvider.class,method="getqueryLabelAndTemplateSQL")
	public List<Opid> queryOpidName(String sql);
	
}
