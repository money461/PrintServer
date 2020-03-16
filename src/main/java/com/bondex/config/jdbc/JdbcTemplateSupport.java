package com.bondex.config.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bondex.entity.page.PageBean;
import com.github.pagehelper.Page;
/**
 * jdbcTemplate 使用 com.github.pagehelper.Page分页
 * @author Qianli
 * 
 * 2020年3月13日 下午2:01:45
 */
public class JdbcTemplateSupport extends JdbcTemplate {
	
	
	private final Logger log  = LoggerFactory.getLogger(this.getClass());
	
	
	    public JdbcTemplateSupport() {
	    }

	    public JdbcTemplateSupport(DataSource dataSource) {
	        super(dataSource);
	        log.debug("初始化->{}模板。。。","JdbcTemplateSupport");
	   
	    }

	    public JdbcTemplateSupport(DataSource dataSource, boolean lazyInit) {
	        super(dataSource,lazyInit);
	    }

	    public <T> PageBean<T> queryForPage(String sql, Page<T> pagination,RowMapper<T> rowMapper) throws DataAccessException {

	    	return queryForPage(sql,pagination,new PreparedStatementSetter() {
				@Override
	            public void setValues(PreparedStatement preparedStatement) throws SQLException {
	                return;
	            }
	        },rowMapper);
	    }


	    public <T> PageBean<T> queryForPage(String sql, Page<T> pagination,PreparedStatementSetter var2, RowMapper<T> var3) throws DataAccessException{

	    	PageBean<T> result= new PageBean<T>();

	        //获取记录条数
	        String countSql="select count(1) as count from ("+sql+") temp";
	        log.info("countSql {}",countSql);
	        List<Integer> countList= super.query(countSql, var2, new RowMapper<Integer>() {
	            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
	                return new Integer(resultSet.getInt("count"));
	            }
	        });
	        
	        
	        result.setTotal(countList.get(0));
	        result.setSize(countList.get(0));
	        result.setPageNum(pagination.getPageNum());
	        result.setPageSize(pagination.getPageSize());

	        int pageCount=result.getSize()%result.getPageSize();
	        result.setPages(pageCount==0?(result.getSize()/result.getPageSize()):(result.getSize()/result.getPageSize()+1));

	        sql+=parseLimit(result);
	        log.info("queryLimitSql {}",sql);

	        List<T> data= super.query(sql,var2,var3);
	        result.setList(data);
	        return result;
	    }

	    private <T> String parseLimit(PageBean<T> pagination){

	        StringBuffer stringBuffer=new StringBuffer();
	        stringBuffer.append(" ");
	        stringBuffer.append("limit");
	        stringBuffer.append(" ");
	        if(pagination.getPageNum() == 0) {
	        	stringBuffer.append("0");
	        }else {
	            stringBuffer.append((pagination.getPageNum()-1)*pagination.getPageSize());
	        }
	        stringBuffer.append(",");
	        stringBuffer.append(pagination.getPageSize());

	        return stringBuffer.toString();
	   }
	    
	  
}
