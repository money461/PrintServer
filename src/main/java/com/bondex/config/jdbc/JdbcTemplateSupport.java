package com.bondex.config.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.bondex.entity.page.PageBean;
import com.bondex.entity.page.PageDomain;
import com.bondex.entity.page.TableSupport;
import com.bondex.util.StringUtils;
import com.bondex.util.sql.SqlUtil;
import com.bondex.util.sql.Sqls;
/**
 * jdbcTemplate 使用 com.github.pagehelper.Page分页
 * NamedParameterJdbcTemplate 使用 org.springframework.data.domain.Page分页
 * @author Qianli
 * 
 * 2020年3月13日 下午2:01:45
 */
public class JdbcTemplateSupport extends NamedParameterJdbcTemplate {
	
	
	   private final Logger log  = LoggerFactory.getLogger(this.getClass());
	
	    public JdbcTemplateSupport(DataSource dataSource) {
	        super(dataSource);
	        log.debug("初始化->{}模板。。。","自定义JdbcTemplateSupport,NamedParameterJdbcTemplate,JdbcTemplate");
	   
	    }
	    
	    //SQL无参数
	    public <T> PageBean<T> queryForPage(String sql,Boolean underScoreCase,String tableName,RowMapper<T> rowMapper) throws DataAccessException {

	    	return queryForPage(sql,underScoreCase,tableName,new PreparedStatementSetter() {
				@Override
	            public void setValues(PreparedStatement preparedStatement) throws SQLException {
					
	                return;
	            }
	        },rowMapper);
	    }
	    

	    /**
	     * 分页查询
	     * @param sql
	     * @param underScoreCase
	     * @param tableName
	     * @param pagination
	     * @param var2
	     * @param var3
	     * @return
	     * @throws DataAccessException
	     */
	    public <T> PageBean<T> queryForPage(String sql,Boolean underScoreCase,String tableName, PreparedStatementSetter var2, RowMapper<T> var3) throws DataAccessException{

	    	PageBean<T> result= new PageBean<T>();

	        //获取记录条数
	        String countSql="select count(1) as count from ("+sql+") temp";
	        log.info("countSql {}",countSql);
	        List<Integer> countList= getJdbcOperations().query(countSql, var2, new RowMapper<Integer>() {
	            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
	                return new Integer(resultSet.getInt("count"));
	            }
	        });
	        
	        
	        result.setTotal(countList.get(0));
	        result.setSize(countList.get(0));
	        sql+=parseLimit(underScoreCase,tableName,result);//拼接条件
	        if(0!=result.getPageSize()){
	        	int pageCount=result.getSize()%result.getPageSize();
	        	result.setPages(pageCount==0?(result.getSize()/result.getPageSize()):(result.getSize()/result.getPageSize()+1));
	        }
	        log.info("queryLimitSql ==> {}",sql);

	        List<T> data= getJdbcOperations().query(sql,var2,var3);
	        result.setList(data);
	        return result;
	    }
			
	    /**
	     * 分页查询数据
	     * @param sql
	     * @param underScoreCase
	     * @param tableName
	     * @param values
	     * @param rowMapper
	     * @return
	     */
		public <T> PageBean<T> queryForPage(String sql,Boolean underScoreCase,String tableName, Object[] values,RowMapper<T> rowMapper) {
					
					PageBean<T> result= new PageBean<T>();
		
			        //获取记录条数
			        String countSql="select count(1) as count from ("+sql+") temp";
			        log.info("countSql {}",countSql);
			        List<Integer> countList= getJdbcOperations().query(countSql, values, new RowMapper<Integer>() {
			            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
			                return new Integer(resultSet.getInt("count"));
			            }
			        });
			        
			        
			        result.setTotal(countList.get(0));
			        result.setSize(countList.get(0));
			        sql+=parseLimit(underScoreCase,tableName,result); //拼接条件
			        
			        if(0!=result.getPageSize()){
			        	int pageCount=result.getSize()%result.getPageSize();
			        	result.setPages(pageCount==0?(result.getSize()/result.getPageSize()):(result.getSize()/result.getPageSize()+1));
			        }
			        
			        log.info("queryLimitSql ==> {}",sql);
			        List<T> data= getJdbcOperations().query(sql,values,rowMapper);
			        result.setList(data);
			        return result;
	 }
		
		/**
		 * NamedParameterJdbcTemplate 实现分页
		 * 空参数
		 * @param sql
		 * @param underScoreCase 默认true
		 * @param tableName 可以不填
		 * @param rowMapper
		 * @return
		 */
		public <T> PageBean<T> queryEmptySqlParamForPage(String sql,Boolean underScoreCase,String tableName,RowMapper<T> rowMapper) {
			
			return queryForPage(sql, underScoreCase, tableName,EmptySqlParameterSource.INSTANCE,rowMapper);
			
		}
		
		/**
		 * NamedParameterJdbcTemplate 实现分页
		 * @param sql
		 * @param underScoreCase
		 * @param tableName
		 * @param paramSource
		 * @param rowMapper
		 * @return
		 */
		public <T> PageBean<T> queryForPage(String sql,Boolean underScoreCase,String tableName, SqlParameterSource paramSource,RowMapper<T> rowMapper) {
			
			PageBean<T> result= new PageBean<T>();
			
			//获取记录条数
			String countSql="select count(1) as count from ("+sql+") temp";
			log.info("countSql {}",countSql);
			List<Integer> countList= super.query(countSql, paramSource, new RowMapper<Integer>() {
				public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
					return new Integer(resultSet.getInt("count"));
				}
			});
			
			
			result.setTotal(countList.get(0));
			result.setSize(countList.get(0));
			sql+=parseLimit(underScoreCase,tableName,result); //拼接条件
			
			if(0!=result.getPageSize()){
				int pageCount=result.getSize()%result.getPageSize();
				result.setPages(pageCount==0?(result.getSize()/result.getPageSize()):(result.getSize()/result.getPageSize()+1));
			}
			
			log.info("queryLimitSql ==> {}",sql);
			List<T> data= super.query(sql,paramSource,rowMapper);
			result.setList(data);
			return result;
		}
			    

		/**
		 * 
		 * @param underScoreCase 排序字段是否 需要 驼峰命名
		 * @param tableName 排序字段 关联表 表别名
		 * @return
		 */
	    private <T> String parseLimit(Boolean underScoreCase,String tableName,PageBean<T> result){
	    	PageDomain pageDomain = TableSupport.buildPageRequest();
	        StringBuffer stringBuffer=new StringBuffer();
	        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy(underScoreCase,tableName));
	        if(StringUtils.isNotBlank(orderBy)){
	        	result.setOrderBy(orderBy);
	        	stringBuffer.append(" ");
	        	stringBuffer.append("order by");
	        	stringBuffer.append(" ");
	        	stringBuffer.append(orderBy);
	        }
	        Integer pageNum = pageDomain.getPageNum();
	        Integer pageSize = pageDomain.getPageSize();
	        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize))
	        {
	        	result.setPageNum(pageNum);
	        	result.setPageSize(pageSize);
		        stringBuffer.append(" ");
		        stringBuffer.append("limit");
		        stringBuffer.append(" ");
		        if(pageNum == 0) {
		        	stringBuffer.append("0");
		        }else {
		            stringBuffer.append((pageNum-1)*pageSize);
		        }
		        stringBuffer.append(",");
		        stringBuffer.append(pageSize);
	        }
	        
	        return stringBuffer.toString();
	   }

	    
	    

	    //
	    // find by sql and entityClass for DTO
	    // --------------------------------------------------------------------------------------------------------------------------------
	    public <E> List<E> find(String sql, Class<E> entityClass) {
	        return this.query(sql, BeanPropertyRowMapper.newInstance(entityClass));
	    }

	    public <E> List<E> find(String sql, Class<E> entityClass, Map<String, ?> params) {
	        return super.query(sql, params, BeanPropertyRowMapper.newInstance(entityClass));
	    }

	    public <E> List<E> find(String sql, Class<E> entityClass, Object... params) {
	        return getJdbcOperations().query(sql, BeanPropertyRowMapper.newInstance(entityClass), params);
	    }

	    //
	    // page by sql and entityClass for DTO
	    // --------------------------------------------------------------------------------------------------------------------------------
	    public <E> org.springframework.data.domain.Page<E> page(String sql, Class<E> entityClass, int pageNumber, int pageSize) {
	        return this.page(sql, entityClass, pageNumber, pageSize, Maps.newHashMap());
	    }

	    public <E> org.springframework.data.domain.Page<E> page(String sql, Class<E> entityClass, int pageNumber, int pageSize, Map<String, ?> params) {
	        int count = this.queryForCount(sql, params);
	        List<E> entities = Lists.newArrayList();
	        if (count > 0) {
	            entities = this.find(Sqls.buildPageSql(sql, pageNumber, pageSize), entityClass, params);
	        }
	        return new PageImpl<E>(entities, new PageRequest(pageNumber - 1, pageSize), count);
	    }

	    public <E> org.springframework.data.domain.Page<E> page(String sql, Class<E> entityClass, int pageNumber, int pageSize, Object... params) {
	        int count = this.queryForCount(sql, params);
	        List<E> entities = Lists.newArrayList();
	        if (count > 0) {
	            entities = this.find(Sqls.buildPageSql(sql, pageNumber, pageSize), entityClass, params);
	        }
	        return new PageImpl<E>(entities, new PageRequest(pageNumber - 1, pageSize), count);
	    }

	    //
	    // find by sql and RowMapper
	    // --------------------------------------------------------------------------------------------------------------------------------
	    public <E> List<E> find(String sql, RowMapper<E> mapper) {
	        return super.query(sql, mapper);
	    }

	    public <E> List<E> find(String sql, RowMapper<E> mapper, Map<String, ?> params) {
	        return super.query(sql, params, mapper);
	    }

	    public <E> List<E> find(String sql, RowMapper<E> mapper, Object... params) {
	        return getJdbcOperations().query(sql, mapper, params);
	    }

	    //
	    // page by sql and RowMapper
	    // --------------------------------------------------------------------------------------------------------------------------------
	    public <E> org.springframework.data.domain.Page<E> page(String sql, RowMapper<E> mapper, int pageNumber, int pageSize) {
	        return this.page(sql, mapper, pageNumber, pageSize, new Object[] {});
	    }

	    public <E> org.springframework.data.domain.Page<E> page(String sql, RowMapper<E> mapper, int pageNumber, int pageSize, Map<String, ?> params) {
	        int count = this.queryForCount(sql, params);
	        List<E> entities = Lists.newArrayList();
	        if (count > 0) {
	            entities = this.find(Sqls.buildPageSql(sql, pageNumber, pageSize), mapper, params);
	        }
	        return new PageImpl<E>(entities, new PageRequest(pageNumber - 1, pageSize), count);
	    }

	    public <E> org.springframework.data.domain.Page<E> page(String sql, RowMapper<E> mapper, int pageNumber, int pageSize, Object... params) {
	        int count = this.queryForCount(sql, params);
	        List<E> entities = Lists.newArrayList();
	        if (count > 0) {
	            entities = this.find(Sqls.buildPageSql(sql, pageNumber, pageSize), mapper, params);
	        }
	        return new PageImpl<E>(entities, new PageRequest(pageNumber - 1, pageSize), count);
	    }

	    //
	    // query for count
	    // --------------------------------------------------------------------------------------------------------------------------------
	    private Integer queryForCount(String sql, Map<String, ?> params) {
	        // return this.namedParameterJdbcTemplate.queryForObject(Sqls.buildCountSql(sql), params, Integer.class);
	        return super.queryForObject(this.buildCountSql(sql), params, Integer.class);
	    }

	    private Integer queryForCount(String sql, Object... params) {
	        // return super.queryForObject(Sqls.buildCountSql(sql), params, Integer.class);
	        return getJdbcOperations().queryForObject(this.buildCountSql(sql), params, Integer.class);
	    }

	    private String buildCountSql(String sql) {
	        return "SELECT COUNT(*) " + StringUtils.substring(sql, StringUtils.indexOfIgnoreCase(sql, "from", 0));
	    }
}
