package com.bondex.entity;

import java.io.Serializable;
import java.util.List;

public class Datagrid<T> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7656894996768001892L;
	
	private String total; //总记录数
	private List<T> rows; //数据集

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
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

	public Datagrid(String total) {
		super();
		this.total = total;
	}

	public Datagrid(String total, List<T> rows) {
		super();
		this.total = total;
		this.rows = rows;
	}

}
