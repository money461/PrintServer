/**
 * 页面登陆后对用户opid做出弹框选择
 */
//页面加载完成后，判断该用户有几个操作号。如果有多个，则弹框让用户选择
function opids(){
	
}

opids.prototype.init=function(url){ //初始表格
	//初始化表格
	$('#opid_table').bootstrapTable({
		//url : url,
		//method : 'GET', // 请求方式（*）
		// toolbar: '#toolbar', //工具按钮用哪个容器
		striped : false, // 是否显示行间隔色
		cache : true, // 是否使用缓存
		columns : [ {
			field : 'opid',
			title : '操作号'
		}, {
			field : 'opidName',
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
				url : ctx+"/accountSwitch",
				type : 'POST',
				async: true,
				data : {
					opid : row.opid,
					opidName : row.opidName
				},
				 beforeSend: function () {
					 sessionstorage.clear();
					 storage.clear(); //localStorage有效期：永不失效，除非web应用主动删除。
					 sessionstorage.clear();
     	        },
				success : function(result) { //根据结果显示菜单
					if(result.status=='200'){
						//2秒关闭
						layer.msg(result.message, {icon:1,time:800},function(){
//							opids.ajaxOpid();
							window.parent.$.modal.loading("正在为您加载权限数据，请稍后...");
							window.location.href="login";
//							window.parent.$.modal.closeLoading(); //关闭加载
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
	
}

opids.prototype.ajaxOpid = function(){
	
	$.ajax({
		url : ctx+"/getUserInfo",
		type : 'GET',
		async: false, //必须同步加载
		success : function(result) {
			var userInfo = result.data;
			sessionstorage.set('userInfo',JSON.stringify(userInfo));
			var opid = userInfo.opid;
			if (null==opid || isNull(opid)) { //还未认证
				$('#myModal').modal('show'); //显示模态框 切换操作号
			}else{
				console.log("已绑定用户，无需切换账户！");
				//加载菜单
			     ajaxMenus();
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
	var userInfo =JSON.parse( sessionstorage.get('userInfo')); 
	var data  = userInfo.allOpid;
	$('#opid_table').bootstrapTable('load',data);
	
});


function isNull( str ){
	if ( str == "" ) return true;
	var regu = "^[ ]+$";
	var re = new RegExp(regu);
	return re.test(str);
}


function ajaxMenus(){
	
	 var config = {
		        url : "/labelPrint/getOpidSecurityModel",
				type : 'GET',
				async: false, //同步加载
				dataType:"json",      
		        contentType:"application/json",
		        beforeSend: function () {
		        	$.modal.loading("正在加载菜单，请稍后...");
		        },
		        success: function(data) {
		        	console.log('菜单数据');
		        	console.log(data);
		        	if($.common.isNotEmpty(data)){
		        		var html = MenuHtml(data);
		        		$("#side-menu").append(html);
		        	}
		        	$.modal.closeLoading();
		        }
		    };
	$.ajax(config);
}

function MenuHtml(data){
	var html = [];
	$.each(data, function(index, menu) {
		if($.common.isNotEmpty(menu.children)){
			html.push('<li><a href="#"><i class="'+menu.iconURL+'" style="width: 20px;"></i><span class="nav-label">'+menu.moduleName+'</span><span class="fa arrow"></span></a>');
			var cmenuhtml = [];
			$.each(menu.children, function(index,cmenu) { 
					if($.common.isNotEmpty(cmenu.children)){
						var emenuhtml = [];
						emenuhtml.push('<li><a href="#"><i class="'+cmenu.iconURL+'" style="width: 20px;"></i>'+cmenu.moduleName+'<span class="fa arrow"></span></a>');
						
						var minmenuhtml=[];
						$.each(cmenu.children, function(index,emenu) { 
							var url = ctx + emenu.moduleURL;
							minmenuhtml.push('<li><a class="menuItem" href="'+url+'"><i class="'+emenu.iconURL+'" style="width: 20px;"></i>'+emenu.moduleName+'</a></li>');
						});
						emenuhtml.push('<ul class="nav nav-third-level collapse" aria-expanded="false" style="height: 0px;">'+minmenuhtml.join('')+'</ul></li>');
						cmenuhtml.push(emenuhtml.join(''));
					}else{
						var url = ctx + cmenu.moduleURL ;
						cmenuhtml.push('<li><a class="menuItem" href="'+url+'"><i class="'+cmenu.iconURL+'" style="width: 20px;"></i>'+cmenu.moduleName+'</a></li>');
					}
			});
			html.push('<ul class="nav nav-second-level collapse" aria-expanded="false" style="height: 0px;">'+cmenuhtml.join('')+'</ul></li>'); //二级菜单
		}
		
	});
	return html.join('');
}

var opids =new opids();

$(document).ready(function(){
	console.log("主页面初始化");
    opids.init();
});

