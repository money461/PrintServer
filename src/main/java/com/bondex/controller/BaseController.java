package com.bondex.controller;

import java.util.List;

import com.bondex.entity.page.PageBean;
import com.bondex.entity.page.PageDomain;
import com.bondex.entity.page.TableDataInfo;
import com.bondex.entity.page.TableSupport;
import com.bondex.util.StringUtils;
import com.bondex.util.sql.SqlUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * web层通用数据处理
 * 
 * @author ruoyi
 */
public class BaseController
{
    

    /**
     * 设置请求分页数据 
     * @param tableStyle 表格类型
     * @param underScoreCase 排序字段是否驼峰命名
     */
    protected void startPage(Boolean underScoreCase,String tableName)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize))
        {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy(underScoreCase,tableName));
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

   
    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected TableDataInfo getDataTable(List<?> list)
    {
    	TableDataInfo rspData = new TableDataInfo();
    	rspData.setCode(0);
    	rspData.setRows(list);
    	rspData.setTotal(new PageInfo(list).getTotal());
    	return rspData;
    }
    
    protected TableDataInfo getDataPageBeanTable(PageBean<?> pageBean)
    {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(0);
        rspData.setRows(pageBean.getList());
        rspData.setTotal(pageBean.getTotal());
        return rspData;
    }
    

}
