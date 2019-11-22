function layout() {
}
layout.prototype.opids = "";
/**
 * 初始化，页面顶部用户信息
 */
layout.prototype.top_userinfo = function() {
	$('#mb').menubutton({
		iconCls : 'icon-man',
		menu : '#mm'
	});
}
/**
 * 切换操作号窗口
 */
layout.prototype.openDialog = function() {
	// 初始化窗口
	$('#dd').dialog({
		title : ' ',
		width : 400,
		height : 200,
		closed : false,
		cache : false,
		modal : true,
		buttons : [ {
			text : '切换',
			iconCls : 'icon-redo',
			handler : function() {

			}
		} ],
	});
	// 初始化数据表格
	$('#dg').datagrid({
		url : 'getOpids',
		pageSize : 5,
		fitColumns : true,
		scrollbarSize : 0,// 去除右侧空白
		pagination : false,// 不开启分页工具栏
		singleSelect : true,
		fit : true,
		pageList : [ 5, 10, 15, 20, 50 ],
		data : {
			"total" : "1",
			"rows" : [ {
				"opid" : "280600",
				"username" : "成都资讯/杨鑫"
			} ]
		},
		columns : [ [ {
			field : 'opid',
			title : '操作号',
			width : 100
		}, {
			field : 'username',
			title : '组织部门',
			width : 100
		} ] ]
	});
}
/**
 * 初始化树菜单
 */
layout.prototype.initTree = function() {
	$('#tree').tree({
		url : '',
		lines : true,
		dnd : true,
		data : [ {
			iconCls : "icon-print",
			text : '标签打印'
		}, {
			iconCls : "icon-edit",
			text : '打印机配置'
		} ],
		onClick : function(node) {
			if ($('#tt').tabs('exists', node.text)) {
				$('#tt').tabs('select', node.text);
			} else {
				if (node.text == "标签打印") {
					$('#tt').tabs('add', {
						title : node.text,
						closable : false,// 不显示关闭按钮
						href : "layout/west/label",
						tools : [ {
							iconCls : 'icon-reload',
							// 刷新当前datagrid数据
							handler : function() {
								$('#dg').datagrid('reload'); // 重新载入当前页面数据
							}
						} ],
						selected : true
					});
				} else if (node.text == "打板管理") {
					$('#tt').tabs('add', {
						title : node.text,
						closable : true,// 显示关闭按钮
						href : "board/boardInfo.jsp",
						selected : true
					});
				} else {
					$('#tt').tabs('add', {
						title : node.text,
						closable : true,// 显示关闭按钮
						content : '<h1>开发中</h1>',
						selected : true
					});
				}
			}
		}
	});
}
var layout = new layout();
layout.initTree();
layout.top_userinfo();