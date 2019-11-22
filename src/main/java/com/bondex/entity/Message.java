package com.bondex.entity;

import java.util.ArrayList;

public class Message<T> {
	private String templateId;// 模版id
	private ArrayList<T> data;// 模版id
	private String printerName;

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public ArrayList<T> getData() {
		return data;
	}

	public void setData(ArrayList<T> data) {
		this.data = data;
	}

	public String getPrinterName() {
		return printerName;
	}

	public void setPrinterName(String printerName) {
		this.printerName = printerName;
	}

	public Message(String templateId, ArrayList<T> data, String printerName) {
		super();
		this.templateId = templateId;
		this.data = data;
		this.printerName = printerName;
	}

	public Message() {
		super();
	}

}
