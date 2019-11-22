function opid() {
}
/**
 * 打开切换操作号模态框后，初始化数据表格
 */
opid.prototype.initbootstrapTable = function() {
	$('#myModal').on('shown.bs.modal', function() {
		$('#table').bootstrapTable({
			url : 'getOpids',
			method : 'GET', // 请求方式（*）
			// toolbar: '#toolbar', //工具按钮用哪个容器
			striped : false, // 是否显示行间隔色
			cache : true, // 是否使用缓存
			columns : [ {
				field : 'opid',
				title : '操作号'
			}, {
				field : 'username',
				title : '组织部门'
			} ],
			responseHandler : function(res) {
				return res.rows;
			},
			onLoadSuccess : function(row) {
			},
			onLoadError : function() {
			},
			onDblClickRow : function(row) {
				$('#myModal').modal('hide'); //隐藏模态框
				$('#top_username').html(row.username);
				$('#top_opid').html(row.opid);
				$.ajax({
					url : "getOpidSecurity",
					type : 'POST',
					data : {
						opid : row.opid,
						username : row.username
					},
					success : function(result) {// 根据结果显示菜单
						var menu = result.split(",");
						$("#menuFrame").hide();
						$("#bqdy").hide();
						$("#xtgl1").hide();
						for (var i = 0; i < menu.length; i++) {
							if (menu[i] == "标签打印") {
								$("#bqdy").show();
								$("#menuFrame").show();
								$("#bqdy").addClass("active");
								document.getElementById("menuFrame").contentWindow.label.initLabelDatagrid();
							}
							if (menu[i] == "系统管理") {
								$("#bqdy").addClass("active");
								$("#xtgl1").show();
							}
						}
					}
				});

			}
		});
	})
}
var opid = new opid()
opid.initbootstrapTable();