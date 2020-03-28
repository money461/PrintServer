package com.bondex.entity.page;

import com.bondex.common.enums.Constants;
import com.bondex.util.ServletUtils;

/**
 * 表格数据处理
 * 
 * @author ruoyi
 */
public class TableSupport
{
    /**
     * BootStrap-table封装分页对象
     */
    public static PageDomain getBootstrapTablePageDomain()
    {
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(ServletUtils.getParameterToInt(Constants.PAGE_NUM));
        pageDomain.setPageSize(ServletUtils.getParameterToInt(Constants.PAGE_SIZE));
        pageDomain.setOrderByColumn(ServletUtils.getParameter(Constants.ORDER_BY_COLUMN));
        pageDomain.setIsAsc(ServletUtils.getParameter(Constants.IS_ASC));
        return pageDomain;
    }

    public static PageDomain buildPageRequest()
    {
	  return getBootstrapTablePageDomain();
    }


    /*
     * easyui 分页排序字段 暂不使用
     */
	public static PageDomain getEasyUITablePageDomain() {
		 	PageDomain pageDomain = new PageDomain();
	        pageDomain.setPageNum(ServletUtils.getParameterToInt(Constants.PAGE));
	        pageDomain.setPageSize(ServletUtils.getParameterToInt(Constants.ROWS));
	        pageDomain.setOrderByColumn(ServletUtils.getParameter(Constants.SORT));
	        pageDomain.setIsAsc(ServletUtils.getParameter(Constants.ORDER));
	        return pageDomain;
	}

	 
}
