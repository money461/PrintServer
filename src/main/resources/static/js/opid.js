/**
 * 页面登陆后对用户opid做出弹框选择
 */
//页面加载完成后，判断该用户有几个操作号。如果有多个，则弹框让用户选择
function opids(){
	
}

var userInfo=undefined;//全局
opids.prototype.init=function(){
	//初始化表格
	$('#opid_table').bootstrapTable({
//		url : 'getOpids',
//		method : 'GET', // 请求方式（*）
		// toolbar: '#toolbar', //工具按钮用哪个容器
		striped : false, // 是否显示行间隔色
		cache : true, // 是否使用缓存
		columns : [ {
			field : 'opid',
			title : '操作号'
		}, {
			field : 'opid_name',
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
					opid_name : row.opid_name
				},
				success : function(result) { //根据结果显示菜单
					if(result.status=='200'){
						//2秒关闭
						layer.msg(result.message, {icon: 1,time: 1000},function(){
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
	
	$.ajax({
		url : "getUserInfo",
		type : 'GET',
		async:false,
		success : function(result) {
			userInfo = result.data;
			var opid = userInfo.opid;
			if (null==opid || isNull(opid)) { //还未认证
				$('#myModal').modal('show'); //显示模态框 切换操作号
			}else{
				console.log("已绑定用户，无需切换账户！");
			}
			var data  = userInfo.allOpid;
			if(data.length==1){
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

//弹框加载表格数据
$('#myModal').on('shown.bs.modal', function() {
	var data  = userInfo.allOpid;
	$('#opid_table').bootstrapTable('load',data);
	
});


function isNull( str ){
	if ( str == "" ) return true;
	var regu = "^[ ]+$";
	var re = new RegExp(regu);
	return re.test(str);
}

var opids =new opids();

$(document).ready(function(){
	console.log("主页面初始化");
    opids.init();
});

