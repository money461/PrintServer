package com.bondex.res;

import java.io.Serializable;
import java.util.List;

import com.github.pagehelper.PageInfo;

/**
 * 整合页面分页插件
 * 
 * @author wujing
 * @param <T>
 */
public class PageBean<T> implements Serializable {

	private static final long serialVersionUID = 6975089268517363694L;

	private String draw;//Datatables发送的draw是多少那么服务器就返回多少。
	
	// 当前记录
	private int iDisplayStart;  

	// 每页记录数
	private int iDisplayLength;

	// 总记录数
	private int iTotalRecords;

	// 过滤后记录
	private int iTotalDisplayRecords;
	
	// 总记录数
//	private int recordsTotal;  //没有过滤的记录数（数据库里总共记录数）

	// 过滤后记录
//	private int recordsFiltered; //过滤后的记录数（如果有接收到前台的过滤条件，则返回的是过滤后的记录数）

	// 记录
	private List<T> data;

	public PageBean() {
		
	}

	/**
	 *
	 * @param page
     */
	public PageBean(PageInfo<T> page) {
		this.iDisplayStart = page.getStartRow();
		this.iDisplayLength = page.getPageSize();
		this.iTotalRecords = (int)page.getTotal();
		this.iTotalDisplayRecords = (int)page.getTotal();
		this.data = page.getList();
	}

	public String getDraw() {
		return draw;
	}

	public void setDraw(String draw) {
		this.draw = draw;
	}

	public int getiDisplayStart() {
		return iDisplayStart;
	}

	public void setiDisplayStart(int iDisplayStart) {
		this.iDisplayStart = iDisplayStart;
	}

	public int getiDisplayLength() {
		return iDisplayLength;
	}

	public void setiDisplayLength(int iDisplayLength) {
		this.iDisplayLength = iDisplayLength;
	}


	public int getiTotalRecords() {
		return iTotalRecords;
	}

	public void setiTotalRecords(int iTotalRecords) {
		this.iTotalRecords = iTotalRecords;
	}

	public int getiTotalDisplayRecords() {
		return iTotalDisplayRecords;
	}

	public void setiTotalDisplayRecords(int iTotalDisplayRecords) {
		this.iTotalDisplayRecords = iTotalDisplayRecords;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}


}
