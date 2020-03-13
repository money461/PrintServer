package com.bondex.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.stereotype.Repository;

import com.bondex.client.entity.UserDefaultRegion;
import com.bondex.jdbc.entity.Log;
import com.bondex.jdbc.entity.Template;
import com.bondex.mapper.provider.TemplateDataProvider;

@Repository
@Mapper
public interface AdminDataCurrentMapper {

	@SelectProvider(type=TemplateDataProvider.class,method="adminDataCurrentSQL")
	public List<Log> querylogDetail(String sql);
	
	@SelectProvider(type=TemplateDataProvider.class,method="adminDataCurrentSQL")
	public List<Template> queryALLTemplate(String sql);
	
	
	@SelectProvider(type=TemplateDataProvider.class,method="adminDataCurrentSQL")
	public List<UserDefaultRegion> selectUserDefaultRegion(String sql);
	
	
	
	
}
