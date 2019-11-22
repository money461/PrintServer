function subscribe() {

}
var mawb;
var hawb;
var srEmail;
var srUser;
var srOpid;
subscribe.prototype.clearForm = function() {
	$("#srHawb").val("");
	$("#srMawb").val("");
	label.refresh(false);
}
/**
 * 用户点击订阅按钮
 */
subscribe.prototype.initWindow = function() {
	subscribe.initDataGrid();
	$('#subscribeDialog').dialog({
		title : ' ',
		width : "70%",
		height : '50%',
		shadow : true,
		border : 'thick',
		maximizable : true,
		closed : false,
		cache : false,
		constrain : true,
		modal : true,
		onClose : function() {
			$("#srMawb").val("");
			$("#srHawb").val("");
			label.refresh(false);
		}
	});
	// 从搜索框获取主单号、分单号
	mawb = $("#srMawb").val() == '' ? $("#mawb").val() : $("#srMawb").val();
	hawb = $("#srHawb").val() == '' ? $("#hawb").val() : $("#srHawb").val();

	srEmail = $("#srEmail").val();
	srUser = $("#srUser").val();
	srOpid = $("#srOpid").val();

	$("#srMawb").val(mawb);
	$("#srHawb").val(hawb);
}
subscribe.prototype.initDataGrid = function() {
	$('#srDg').datagrid({
		url : '../../subscribe/all',
		method : 'POST',
		pagination : true,
		pageSize : 5,
		striped : true,
		scrollbarSize : 0,// 去除右侧空白
		fitColumns : true,
		fit : true,
		pageList : [ 5, 10, 15, 20, 50 ],
		toolbar : '#sr',
		columns : [ [ {
			field : 'srMawb',
			width : 50,
			title : 'MAWB'
		}, {
			field : 'srHawb',
			width : 50,
			title : 'HAWB'
		}, {
			field : 'srUser',
			width : 50,
			title : '订阅人'
		}, {
			field : 'srEmail',
			width : 50,
			title : 'Email'
		}, {
			field : 'srState',
			width : 50,
			title : '订阅状态',
			formatter : function(value, row, index) {
				if (value == '1') {
					return "订阅完成"
				} else {
					return "<b style='color:green;'>订阅中</b>"
				}
			}
		} ] ],
		onClickCell : function(rowIndex, field, value) {
		},
		// 在用户双击一行的时候触发
		onDblClickRow : function(rowIndex, rowData) {
		}
	});
}
/**
 * 用户点击订阅，提交表单
 */
subscribe.prototype.submitForm = function() {
	mawb = $("#srMawb").val() == '' ? $("#mawb").val() : $("#srMawb").val();
	hawb = $("#srHawb").val() == '' ? $("#hawb").val() : $("#srHawb").val();
	$.ajax({
		url : "/subscribe/add",
		type : 'POST',
		async : true,
		data : {
			srEmail : srEmail,
			srUser : srUser,
			srMawb : mawb,
			srOpid : srOpid,
			srHawb : hawb
		},
		success : function(result) {
			if (result == "true") {
				$.messager.show({
					title : '<b style="font-size:12;text-align:center;">我的消息</b>',
					msg : '<h3 style="color:green;text-align:center;">订阅成功</h3>',
					timeout : 3000,
					showType : 'fade'
				});
				$('#srDg').datagrid("reload");
			} else if (result == "false") {
				$.messager.show({
					title : '<b style="font-size:12;text-align:center;">我的消息</b>',
					msg : '<h3 style="color:red;text-align:center;">订阅失败,请稍后重试</h3>',
					timeout : 3000,
					showType : 'fade'
				});
			} else {
				$.messager.alert({
					title : '订阅失败',
					width : 500,
					headerCls : 'jz',
					bodyCls : 'jz',
					msg : result
				});
			}
		}
	});

}
var subscribe = new subscribe();