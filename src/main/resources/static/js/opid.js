/**
 * 页面登陆后对用户opid做出弹框选择
 */
//页面加载完成后，判断该用户有几个操作号。如果有多个，则弹框让用户选择
function opids(){
	
}

opids.prototype.init=function(){
	
	var opids=new Array();
	$.ajax({
		url : "getOpids",
		type : 'GET',
		async:false,
		success : function(result) {
			opids = result.rows;
			if (opids.length>1) { //多个操作号
				$('#myModal').modal('show'); //显示模态框 切换操作号
			}else {
				//禁止点击”切换操作号“按钮
				$("#qhczh").removeAttr('data-target');
				$("#qhczh").html('无可切操作号');
			}
		},
		error:function(){
			layer.msg("获取opdis网络异常！", {icon: 2});
		}
	});
	
	
}

//弹框选择opid
$('#myModal').on('shown.bs.modal', function() {
	$('#opid_table').bootstrapTable({
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
			$.tool.alertError("用户操作号加载失败！",-1);
		},
		onDblClickRow : function(row) { //双击选中opid后
			$('#myModal').modal('hide'); //隐藏模态框
			//选择opid后重新授权
			$.ajax({
				url : "accountSwitch",
				type : 'POST',
				data : {
					opid : row.opid,
					username : row.username
				},
				success : function(result) { //根据结果显示菜单
					if(result.status=='200'){
						//2秒关闭
						layer.msg(result.message, {icon: 1,time: 3000},function(){
							window.location.href="login";
						});
						
					}else{
						layer.msg(result.message, {icon: 2});
						
					}
				},
				error:function(){
					layer.msg("网络异常！", {icon: 2});
				}
			});

		}
	});
});

var opids =new opids();

$(document).ready(function(){
	console.log("主页面初始化");
    /*opids.init();*/
});

