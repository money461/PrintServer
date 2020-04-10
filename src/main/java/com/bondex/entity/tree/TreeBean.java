package com.bondex.entity.tree;

import java.util.List;

import lombok.Data;

@Data
public class TreeBean {

	private String id; //id：绑定到节点的标识值。 //regionCode
	private String state;  //state：节点状态，'open' 或 'closed'。
	private String text; //text：要显示的文本。
	private List<TreeBean> children;
	private String pname;
	private String regionCode;
	private String parentCode;

}