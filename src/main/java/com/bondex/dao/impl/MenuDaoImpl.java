package com.bondex.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bondex.config.jdbc.JdbcTemplateSupport;
import com.bondex.dao.MenuDao;
import com.bondex.dao.base.BaseDao;
import com.bondex.entity.menu.Menu;
@Repository
public class MenuDaoImpl extends BaseDao<Menu, Long> implements MenuDao {
	private JdbcTemplateSupport jdbcTemplateSupport;
	
	@Autowired
	public MenuDaoImpl(JdbcTemplateSupport jdbcTemplateSupport) {
		super(jdbcTemplateSupport);
		this.jdbcTemplateSupport = jdbcTemplateSupport;
	}
	

}
