package com.bondex.entity;

public class PageBean<T> extends Datagrid<T> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1539615028848351426L;
	
	private Integer curPage; //当前页码

	public Integer getCurPage() {
		return curPage;
	}

	public void setCurPage(Integer curPage) {
		this.curPage = curPage;
	}

	
	
	

}
