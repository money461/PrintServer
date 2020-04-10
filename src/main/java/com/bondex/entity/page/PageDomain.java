package com.bondex.entity.page;

import com.bondex.util.StringUtils;

/**
 * 分页数据
 * 
 * @author ruoyi
 */
public class PageDomain
{
    /** 当前记录起始索引 */
    private Integer pageNum;
    /** 每页显示记录数 */
    private Integer pageSize;
    /** 排序列 */
    private String orderByColumn;
    /** 排序的方向 "desc" 或者 "asc". */
    private String isAsc;

    
    
    /**
     * 排序字段是否驼峰命名
     * @param underScoreCase
     * @param tableName 表名称
     * @return
     */
    public String getOrderBy(Boolean underScoreCase,String tableName)
    {
    	String byColumn = orderByColumn(underScoreCase);
    	if(StringUtils.isNotNull(tableName) && StringUtils.isNotBlank(byColumn)){
    		return tableName + "." + byColumn;
    	}else{
    		return byColumn;
    	}
       
    }
    
    public String orderByColumn(Boolean underScoreCase){
    	 if (StringUtils.isEmpty(orderByColumn))
         {
             return "";
         }
         if(!underScoreCase){
        	 return orderByColumn + " " + isAsc; //不需要驼峰命名
         }
         return StringUtils.toUnderScoreCase(orderByColumn) + " " + isAsc; //默认下划线转驼峰命名
    }

    public Integer getPageNum()
    {
        return pageNum;
    }

    public void setPageNum(Integer pageNum)
    {
        this.pageNum = pageNum;
    }

    public Integer getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(Integer pageSize)
    {
        this.pageSize = pageSize;
    }

    public String getOrderByColumn()
    {
        return orderByColumn;
    }

    public void setOrderByColumn(String orderByColumn)
    {
        this.orderByColumn = orderByColumn;
    }

    public String getIsAsc()
    {
        return isAsc;
    }

    public void setIsAsc(String isAsc)
    {
        this.isAsc = isAsc;
    }
}
