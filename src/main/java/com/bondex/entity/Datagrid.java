package com.bondex.entity;

import java.util.List;

public class Datagrid<T> {
	private String total;
	private List<T> rows;

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
