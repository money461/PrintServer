package com.bondex.entity.page;

import java.io.Serializable;
import java.util.List;

public class Datagrid<T> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7656894996768001892L;
	
	private long total; //总记录数
	private List<T> rows; //数据集

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	public Datagrid() {
		super();
	}

	public Datagrid(Long total) {
		super();
		this.total = total;
	}

	public Datagrid(Long total, List<T> rows) {
		super();
		this.total = total;
		this.rows = rows;
	}

}
