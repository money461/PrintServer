package com.bondex.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.bondex.entity.Template;
import com.bondex.util.StringUtils;

public class TemplateRowMapper implements RowMapper<Template> {
	
	private String exportFileUrl;
	
	public TemplateRowMapper(String exportFileUrl) {
		super();
		this.exportFileUrl = exportFileUrl;
	}


	@Override
	public Template mapRow(ResultSet rs, int rowNum) throws SQLException {
		Template template2 = new Template();
		String templateUrl = rs.getString("template_url");
		if(StringUtils.isNotBlank(templateUrl)){
			templateUrl = exportFileUrl+"/"+templateUrl;
		}
		template2.setTemplateUrl(templateUrl);
		template2.setId(rs.getString("id"));
		template2.setTemplateId(rs.getString("template_id"));
		template2.setTemplateName(rs.getString("template_name"));
		template2.setWidth(rs.getString("width"));
		template2.setHeight(rs.getString("height"));
		template2.setCode(rs.getString("code"));
		template2.setCodeName(rs.getString("code_name"));
		template2.setIsDefault(rs.getInt("is_default"));
		template2.setStatus(rs.getString("status"));
		template2.setExtendData(rs.getString("extend_data"));
		template2.setCreateOpid(rs.getString("create_opid"));
		template2.setCreateName(rs.getString("create_name"));
		template2.setUpdateTime(rs.getTimestamp("update_time"));
		template2.setCreateTime(rs.getTimestamp("create_time"));
		return template2;
	}

}
