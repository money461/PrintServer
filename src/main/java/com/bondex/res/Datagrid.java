package com.bondex.res;

import java.util.List;

public class Datagrid<T> {
	
	private String total;
	private List<?> rows;

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public List<?> getRows() {
		return rows;
	}

	public void setRows(List<?> rows) {
		this.rows = rows;
	}

	public Datagrid(String total, List<?> rows) {
		super();
		this.total = total;
		this.rows = rows;
	}

	public Datagrid() {
		super();
	}

}
