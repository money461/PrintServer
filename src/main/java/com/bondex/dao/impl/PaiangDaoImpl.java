package com.bondex.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bondex.config.jdbc.JdbcTemplateSupport;
import com.bondex.dao.PaiangDao;
import com.bondex.dao.base.BaseDao;
import com.bondex.entity.Label;
@Repository
public class PaiangDaoImpl extends  BaseDao<Label, String> implements PaiangDao {

	private JdbcTemplateSupport jdbcTemplateSupport;
	
	@Autowired
	public PaiangDaoImpl(JdbcTemplateSupport jdbcTemplateSupport) {
		super(jdbcTemplateSupport);
		this.jdbcTemplateSupport = jdbcTemplateSupport;
	}
	

	//根据主单号MBLNo删除数据
	@Override
	public void delete(List<Label> list) {
		List<String> mblno = list.stream().map(x->x.getMBLNo()).collect(Collectors.toList());
		List<String> TakeCargoNo = list.stream().map(x->x.getTakeCargoNo()).collect(Collectors.toList());
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("MBLNo", mblno);
		map.addValue("TakeCargoNo", TakeCargoNo);
		String sql = "DELETE FROM label WHERE reserve1=0 and is_print = 0 and TakeCargoNo in ( :TakeCargoNo ) OR MBLNo in ( :MBLNo ) ";
		jdbcTemplateSupport.update(sql, map);
}
	
	//保存数据
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void savePaiangData(List<Label> list) {
		
		for (Label label : list) {
			super.insert(label, true);
		}
		
	}

	@Override
	public void updatePaiangData(List<Label> datalist) {
		for (Label label : datalist) {
			super.updateById(label, label.getLabelId(), true);
		}
	}


	

	
	
}
