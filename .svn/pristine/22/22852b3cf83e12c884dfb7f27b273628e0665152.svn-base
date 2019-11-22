package com.bondex.jdbc.service;

import java.util.List;

import com.bondex.entity.Datagrid;
import com.bondex.jdbc.entity.JsonRootBean;
import com.bondex.jdbc.entity.Label;
import com.bondex.jdbc.entity.Template;
import com.bondex.security.entity.JsonResult;

public interface LabelInfoService {
	public boolean labelInfoSave(JsonRootBean jsonRootBean);

	public Datagrid findByPage(String page, String rows, Label label, String start_time, String end_time, String sort, String order, String opid, List<JsonResult> list, String businessType);

	public void updateLabel(Label label);

	public void delete(List<Label> label);

	public Template getTemplate(List<JsonResult> jsonResults, String id);
}
