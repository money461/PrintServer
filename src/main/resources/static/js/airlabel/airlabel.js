function label() {
	
}
	var editRow = undefined;
	var opid; //当前操作的id
	var thisdefaultRegion=null; //打印当前现中的办公室id
	
	//js初始化调用的接口
	//获取操作opid
$(function(){
		$.ajax({
			url : "/labelPrint/getThisOpids",
			type : 'POST',
			async:false,
			success : function(result) {// 根据结果显示菜单
				opid= result;
			}
		});
		
		setRegionByOpid();//初始化打印办公室
		
	}); 

//初始化结束

//修改办公室
function setRegionByOpid(){
	
		//初始化页面 获取打印区域 opid 当前操作号   defaultRegion办公司id
		$.ajax({
			url : "/labelPrint/client/getDefaultBindRegionByOpid",
			type : 'POST',
			data: {opid:opid,defaultRegion:storage.get('defaultRegion')},//chengdu/jichang id=2
			async:false,
			success : function(result) {
				if($.common.isEmpty(result)){
					return;
				}else{
					$("#quyu").text(result.parentName+'/'+result.regionName); //修改区域标签
					var bgs = storage.get('bgs');
					bgs = bgs!=null?bgs:'mr'; 
					storage.set('bgs',bgs);
					storage.set('defaultRegion',result.regionCode); //初始化获取用户绑定的办公室id
				}
			}
		});
}


function formatDate(time){
		if (time!=undefined&&undefined!="undefined") {
		    var date = new Date(time);
		    var year = date.getFullYear(),
		        month = date.getMonth()+1,// 月份是从0开始的
		        day = date.getDate(),
		        hour = date.getHours(),
		        min = date.getMinutes(),
		        sec = date.getSeconds();
		    var newTime = year + '-' +
		                (month < 10? '0' + month : month) + '-' +
		                (day < 10? '0' + day : day) + ' ' +
		                (hour < 10? '0' + hour : hour) + ':' +
		                (min < 10? '0' + min : min) + ':' +
		                (sec < 10? '0' + sec : sec);
	
		    return newTime;
		}else{
		    return null;         
		}
	}

var vda = $("#searchForm").myVdate();
	
label.prototype.update = function() {
	var updateform = $("#updateform").myVdate();
	if (!updateform.startValidate()) {
		return;
	}
	$("#updateform").submit();
}
	
//标签删除一行或者多行 事件
label.prototype.labelDelete = function() {
		var rows = $('#easyui-datagrid').datagrid('getSelections');
		if (rows.length > 0) {
			if(window.confirm('确定要删除？')){
				$.ajax({
					url : "/labelPrint/label/delete",
					contentType:"application/json",
					dataType:"json",      
					type : 'POST',
					data : JSON.stringify(rows),
				    beforeSend: function () {
			        	$.modal.loading("正在处理中，请稍后...");
			        },
					success : function(result) {
						$.modal.closeLoading();
						if("200"==result.status){
        					layer.msg("删除成功！",{icon:1,time:1000});
        					$('#easyui-datagrid').datagrid('reload');
        				}else{
        					layer.msg(result.message,{icon:2,time:1000});
        				}
					}
				});
	         }else{
	            return false;
	        }
		}else {
			alert("请选择数据后再删除！");
		}
		
	}

label.prototype.checksave= function(){
	if(editRow != undefined){
		layer.msg("请确认是否保存编辑修改的数据?",{icon:2,time:1000});
		return false;
	}else{
		return true;
	}
	
	
}

//保存被修改的行
label.prototype.save = function(){
		
	   $.messager.confirm("操作提示", "您确定要执行保存操作吗？", function (data) {
           if (data) {
        	   //点保存后无法点击撤销
        	    $("#rejectButton").attr("disabled",true); //禁用撤销
        		$("#easyui-datagrid").datagrid('endEdit', editRow);
        		editRow = undefined; //重置编辑行
        		
        		//使用JSON序列化datarow对象，发送到后台。
                var rows = $("#easyui-datagrid").datagrid('getChanges');

                var rowstr = JSON.stringify(rows);
                
              //保存数据库
        		$.ajax({
        			url : "/labelPrint/label/update",
        			type : 'POST',
        			dataType:"json",      
                    contentType:"application/json",
        			data:rowstr,
        			success : function(result) {
        				// 更新完成后，刷新当前行
        				//$("#easyui-datagrid").datagrid('refreshRow', rowIndex);
        				if("200"==result.status){
        					layer.msg(result.message,{icon:1,time:1000});
        				}else{
        					layer.msg(result.message,{icon:2,time:1000});
        				}
        				
        			}
        		});
        		
           }
           else {
               layer.msg("已取消保存");
           }
       });
	  
	}

//撤销被修改的行
label.prototype.rejectChange = function(){
		editRow = undefined;
        $("#easyui-datagrid").datagrid('rejectChanges');
        $("#easyui-datagrid").datagrid('unselectAll'); //取消选中的所有行
        $("#saveButton").attr("disabled",true); //禁用保存
	}
	
	
//刷新表单 重新加载数据
label.prototype.refresh = function(isRefresh) {
	    if(label.checksave()){
	    	$("#searchForm").form("clear");
	    	$('#searchForm')[0].reset();
	    	//$(':input','#searchForm').not(':button, :submit, :reset, :hidden').val('').removeAttr('checked').removeAttr('selected');
	    	$("#searchForm").find("select[name=status]").val("0").trigger('change'); //重置下拉
	    	label.select2(); //重置select2
	    	$("#printButton").attr("disabled",false); //解除
	    	if (isRefresh) {
	    		label.initLabelDatagrid(); //重新加载数据
	    	}
	    	
	    }
	}

	//查询事件
label.prototype.search = function() {
	    if(label.checksave()){
	    	var params = label.getQueryCondition();
	    	$('#easyui-datagrid').datagrid('load',params); //加载并显示第一页的行 指定 'param' 参数，它将替换 queryParams 属性。
	    	//label.initLabelDatagrid(); //初始化表格
	    }
		
}
	

	//查询参数 
label.prototype.getQueryCondition=function(){
	
	 var formParm  = $.common.formToJSON("searchForm");
	 var mawb = formParm.mawb;
	 mawb = $.trim(mawb) == undefined?"":$.trim(mawb);
	 mawb = mawb.replace(/\s+/g, ',');
     formParm.mawb=mawb;
	 if('0'!= formParm.isPrint){
		$("#printButton").attr("disabled",true); //禁用打印
	  }else{
		    $("#printButton").attr("disabled",false); //解除
	  }
	 
	  console.log('查询参数');
	  console.log(formParm);
	 
	  return formParm;
}
	
//点击打印按钮事件 进入打印程序
label.prototype.labelPrint = function() {
	    if(label.checksave()){
	    	var rows = $('#easyui-datagrid').datagrid('getSelections');// 获取所有选中行的数据
	    	// 获取用户选择的区域
	    	if (rows.length ==0) {
	    		layer.msg("请至少选择一条打印标签！",{icon:2,time:2000});
	    		return;
	    	}
	    	
	    	 //获取会话框中的网路选项
			//内网/外网
			var vpn = $("input[name='mqaddress']:checked").val();
		    storage.set('vpn',vpn);//获取内网外网
	    	
	    	//判断是否需要打印提示
	    	var xunwenprint = storage.get('xunwenprint'); //********全局询问打印设置
	    	xunwenprint = xunwenprint!=null?xunwenprint:'yes';
	    	var defaultRegion = storage.get('defaultRegion'); //打印办公室id
	    	if($.common.equalsIgnoreCase('yes',xunwenprint) || $.common.isEmpty(defaultRegion)){
	    		//弹出对话框
	    		$("#dyan").css("display", "block");// 显示打印按钮
	    		label.initdialog('打印提示框'); //显示打印会话框 不展示默认办公室选项
	    	}else{
	    		//进入打印程序
	    		label.printLabelSendClient();
	    	}
	    	
	    }
		
	}

/**
 * 修改打印配置的信息
 */
label.prototype.alertprint = function() {
	$("#xunwen").css("display", "block");//展示询问打印
	$("#bgs").css("display", "block");// 展示默认办公室radio
	$("#xgmr").css("display", "block"); //显示修改默认
	$("#mqaddress").css("display", "block"); //显示修改默认
	label.initdialog('配置打印/办公室信息对话框'); //修改区域 展示默认radio
}

//导出标签按钮事件
label.prototype.labelExport = function() {
	//校验保存
    if(label.checksave()){
    	var rows = $('#easyui-datagrid').datagrid('getSelections');// 获取所有选中行的数据
    	
    	if (rows.length > 0) {
    		var ids = "";
    		for (var i = 0; i < rows.length; i++) {
    			if (i == (rows.length - 1)) {
    				ids += "'" + rows[i].labelId + "'";
    			} else {
    				ids += "'" + rows[i].labelId + "',";
    			}
    		}
    		window.location.href = "/labelPrint/client/exportLabel?labels=" + ids;
    		
    		$.messager.show({
    			title : '<center style="color:red;">提示</center>',
    			msg : '<center><p style="color:red;margin-top: 20px;">数据导出中，请勿刷新页面</p></center>',
    			timeout : 2000,
    			showType : 'show',
    			style : {
    				top : document.body.scrollTop + document.documentElement.scrollTop,
    			}
    		});
    		
    	} else {
    		alert("请选择数据后再导出");
    	}
    	
    }
}


/**
 * 弹出会话框后点击更新或者添加用户办公室信息
 */	
function dialogUpdateOrAddRegion (){
		
	  var bgs = $("input[name='bgs']:checked").val();
		//获取办公室id
		if(bgs=="mr" ){
			if($.common.isEmpty(thisdefaultRegion)){
				thisdefaultRegion = storage.get('defaultRegion');
				if($.common.isEmpty(thisdefaultRegion)){
					layer.msg("选择默认办公室必须选择绑定指定办公室",{icon:2,time:1000});
					return;
				}
			}
		     //默认办公司
				if (typeof(Storage) !== "undefined") {
					$.ajax({
						url : "/labelPrint/client/updateOrAddUserRegion", //修改办公室区域 chengdu/jichang
						type : 'POST',
						data :{defaultRegion:thisdefaultRegion},
					    success : function(result) {
						if (result.status=="200") {
							layer.msg("更改配置成功！",{icon:1,time:1000});
							label.dialogRegion();
						}else{
							layer.msg("更改失败，请联系管理员",{icon:2,time:1000});
						}
					}
					});
				} else {
					layer.msg("你的浏览器不兼容系统，建议更换谷歌浏览器",{icon:2,time:1000});
				}
		}else{
			label.dialogRegion();
		}
}

label.prototype.dialogRegion =function(){
	//获取办公室信息
	var bgs = $("input[name='bgs']:checked").val();
	storage.set('bgs',bgs);
	
	//询问打印
	var xunwenprint = $("input[name='xunwen']:checked").val();
	storage.set('xunwenprint',xunwenprint);
	
	//内网/外网
	var vpn = $("input[name='mqaddress']:checked").val();
	storage.set('vpn',vpn);
	
	if($.common.isNotEmpty(thisdefaultRegion)){
		storage.set('defaultRegion',thisdefaultRegion); //****************设置全局办公室id*************************
	}
	setRegionByOpid(bgs);//修改办公室
	//关闭会话
	$('#region').dialog("close");
}

/**
 * 初始化 打印对话框 按钮需要的选择控件
 */
label.prototype.initdialog = function(title) {
	// 初始化弹出 更新打印办公室
	$('#region').dialog({
		title : title,
		width : 550,
		height : 400,
		maximizable : true,
		cache : false,
		modal : true,
		onClose:function(){ //弹框恢复初始状态 隐藏所有的标签
			try {
				$("#dyan").css("display", "none");// 隐藏打印按钮
				$("#xgmr").css("display", "none");// 隐藏修改默认
				$("#mqaddress").css("display", "none");// 隐藏内网外网选项
				$("#bgs").css("display", "none");//隐藏办公室radio
				$("#xunwen").css("display", "none");//隐藏询问设置
				
				thisdefaultRegion=null;//清除当前选中的地址
			} catch (e) {
				console.info("初始化单选框失败");
			}
		},
		onBeforeOpen:function(){ //对话框初始化
			label.initTree(); //初始化办公室树
			var xunwenprint = storage.get('xunwenprint'); //********全局询问打印设置
			xunwenprint = xunwenprint!=null?xunwenprint:'yes';
			$("input:radio[value="+xunwenprint+"]").prop('checked','true'); //设置当前是询问还是不询问
			
			//加载当前设置
			var bgs=storage.get('bgs');
			 //$("input[name='bgs']:checked").prop('checked', false); //radio不被选中 不清楚当前是临时还是默认
			bgs = bgs!=null?bgs:'ls';
			 $("input:radio[value="+bgs+"]").prop('checked','true'); //设置当前是临时还是默认
			 
			 var vpn=storage.get('vpn');
			 vpn = vpn!=null?vpn:'outnet';
			 $("input:radio[value="+vpn+"]").prop('checked','true'); //设置当前是vpn/外网
			
		}
	});
	
}

// 初始化办公室下拉树
label.prototype.initTree = function(){
	$('#cc').combotree({
		required : true,
		url : "/labelPrint/region/getTreeRegionByOpid",
		method:'POST',
		loadFilter : function(data,parent){
			return data;
		},
		onLoadSuccess : function(node,data) {
			//combotree 设置默认值
			var defaultRegion = storage.get('defaultRegion');
			if($.common.isNotEmpty(defaultRegion)){
				 var trees = $('#cc').combotree('tree');
				 
				var nodedata = trees.tree('find',defaultRegion);
				var text = nodedata.pname + "/" + nodedata.text;
				$('#cc').combotree('setValue',text); 
				trees.tree('select', nodedata.target); 
			}else{
				$('#cc').combotree('setValue', '指定打印办公室位置'); 
			}
		},
		onBeforeSelect : function(node) {
			return false
		},
		onClick : function(node) {
			if ($('#cc').tree('isLeaf', node.target)) {// 判断是否是叶子节点
				//子节点即选择该节点
				$('#cc').combotree('setValue', node.pname + "/" + node.text); //设置值
//				storage.set("defaultRegion",node.id); //设置区域办公室id
				thisdefaultRegion=node.id;
			} else {
				// 如果是父节点，实现点击展开/关闭节点
				if (node.state == "closed") {
					$(this).tree('expand', node.target);
					$('#cc').combotree("showPanel");
				} else {
					$(this).tree('collapse', node.target);
					$('#cc').combotree("showPanel");
				}
			}
		}
	});
	
}

function dialogClose(){
	try {
		$('#region').dialog("close");  //关闭办公室选择弹框
	} catch (e) {
		console.info("面板关闭失败");
	}
}
//弹出会话框后点击打印
function dialogPrint() {
	   //立即执行打印
	   label.printLabelSendClient(thisdefaultRegion);
}
	//发送打印客户端 打印人 /区域
label.prototype.printLabelSendClient = function(defaultRegion) {
  
  var defaultRegion_storage = storage.get('defaultRegion'); //打印办公室id
  
  if($.common.isEmpty(defaultRegion)){
	  defaultRegion = defaultRegion_storage;
  }
  
  if($.common.isEmpty(defaultRegion)){
	  layer.msg("请选择打印办公室",{icon:2,time:1000});
	  return;
  }
  		window.parent.$.modal.loading("打印数据发送中，请稍后...");
        var vpn=storage.get('vpn');//获取内网外网
        var url = "/labelPrint/client/printLabelSendClient";
		var rows = $('#easyui-datagrid').datagrid('getSelections');
		$.ajax({
			url : url,
			type : 'POST',
			data : {
				labels : JSON.stringify(rows),
				regionCode : defaultRegion, //chengdu/jichang id=2
				mqaddress: vpn //内网还是外网
			},
			success : function(result) {
				window.parent.$.modal.closeLoading(); //关闭加载
				if(result.status=="200"){
   					//弹框提示
   					$.messager.show({
   						title:'<b style="font-size:12;text-align:center;">我的消息</b>',
   						msg:'<h3 style="color:green;text-align:center;">标签已发往客户端打印</h3>',
   						timeout:3000,
   						showType:'fade'
   					});
   					dialogClose();//关闭会话
   					//刷新表格
   					$('#easyui-datagrid').datagrid('reload');
   				}else{
   					layer.msg(result.message,{icon:2,time:1000});
   				}
				
			}
			
		});
  
//点击单选按钮radio后触发，即，我们  选择默认办公室/临时办公室时，触发一个事件，弹出选中的值
/*  $("#mqaddress input[name=mqaddress]").click(function(){
      mqaddress = $(this).val();
  });*/
  
}
	
//弹出会话框后导出数据
function dialogExport() {
		var rows = $('#easyui-datagrid').datagrid('getSelections');// 获取所有选中行的数据
		var ids = "";
		for (var i = 0; i < rows.length; i++) {
			if (i == (rows.length - 1)) {
				ids += "'" + rows[i].labelId + "'";
			} else {
				ids += "'" + rows[i].labelId + "',";
			}
		}
		//导出所有的标签数据
	    window.location.href = "/labelPrint/client/exportLabel?labels=" + ids;
		
		$("#dcan").css("display", "none");// 隐藏导出按钮
		$('#region').dialog("close");
		
		$.messager.show({
			title : '<center style="color:red;">提示</center>',
			msg : '<center><p style="color:red;margin-top: 20px;">数据导出中，请勿刷新页面</p></center>',
			timeout : 2000,
			showType : 'show',
			style : {
				top : document.body.scrollTop + document.documentElement.scrollTop,
			}
		});
	}
	
	

//表格展示字段
label.prototype.columns = function() {
		var columns = [ [ {
				field : 'labelId',
				title : '索引编号',
				checkbox : true,
				width : 50
			}, {
				field : 'mawb',
				width : 50,
				//sortable : true,
				//sortOrder:'asc',
				title : 'MAWB'
			}, {
				field : 'hawb',
				width : 50,
				//sortable : true,
				//sortOrder:'asc',
				editor:'text',
				title : 'HAWB'
			}, {
				field : 'airportDeparture',
				width : 50,
				//sortable : true,
				//sortOrder:'asc',
				title : '起始地'
			}, {
				field : 'destination',
				width : 50,
				//sortable : true,
				//sortOrder:'asc',
				editor:'text',
				title : '目的地'
			}, {
				field : 'total',
				width : 50,
				//sortable : true,
				//sortOrder:'asc',
				title : '件数'
			},{
				field : 'templateName',
				width : 50,
				//sortable : true,
				//sortOrder:'asc',
				title : '当前模版',
				editor:{
			        type:'combobox',
			        options:{
			            valueField:'templateName',
			            required:true
			        }
			    },
			    styler: function(value, row, index) {
			    	if ($.common.isEmpty(value)) {
			    		return 'color:red;';
			    	}
			    },
			   formatter : function(value, row, index) {
				   if($.common.isEmpty(row.templateName)){
					   value="未配置打印模板";
				   }
				   return value;
			   }
			
			},{
				field : 'flightDate',
				width : 50,
				sortable : true,
				sortOrder:'asc',
				title : '航班日期',
				formatter : function(value, row, index) {
					if (value != undefined) {
						return  formatDate(value).split(" ")[0];
					}else {
						return "";
					}
				}
			}, {
				field : 'isPrint',
				width : 50,
				title : '状态',
				//sortable : true,
				//sortOrder:'asc',
				formatter : function(value, row, index) {
					if (row.isPrint == 1) {
						return "已打印";
					}else if (row.isPrint == 2) {
						return "已导出";
					} else {
						return "未打印";
					}
				}
			},{
				field : 'printTime',
				width : 50,
				//sortable : true,
				//sortOrder:'asc',
				title : '打印时间'
			},{ 
				field : 'updateTime', 
				width : 50, 
				sortable : true,
				sortOrder:'desc',
				title : '录入时间', 
				formatter : function(value, row, index) { 
					return formatDate(value); 
				} 
			},{ 
				field : 'opidName', 
				width : 50, 
				title : '录入人'
			},{
				field : 'printUser',
				width : 50,
				//sortable : true,
				//sortOrder:'asc',
				title : '打印人'
			}] ];	
		return columns;
	}
	
	//初始化标签表格
label.prototype.initLabelDatagrid = function(isPrint2) {
		if (isPrint2 != undefined) {
			isPrint = isPrint2;
		}
		var url="/labelPrint/label/all";

		$('#easyui-datagrid').datagrid({
			url : url,
			method : 'POST',
			iconCls : 'fa fa-truck',
			pagination : true,
			pageSize : 20,
			striped:true,
			scrollbarSize : 0,// 去除右侧空白
			fitColumns : true,
			sortName:'updateTime',//定义可以排序的列。
			sortOrder:'desc',
			pageList : [ 5, 10, 15, 20, 50 ],
			toolbar:'#toolbar',
			width: 'auto', //默认10
			showFooter : true,
			loadMsg: "正在加载数据...",
			queryParams: label.getQueryCondition(),
			columns : label.columns(),
			rowStyler : function(index, row) {
				if (row.isPrint == 1||row.isPrint == 2) {//打印状体修改展示颜色
					return 'background-color:#5cb85c';
				}
			},
			loadFilter:function(data){ //数据过滤
				if("total" in data){
					return data;
				}else{
					
					if(data.status=="200"){
						return data.data;
					}else{
						layer.msg(data.message,{icon:2,time:5000});
						return;
					}
					
				}
			},
			onLoadError: function () {
		        	layer.msg('警告!数据加载失败!',{icon:2});
		     },
	        onBeforeLoad: function(param){ // 在发出请求数据数据之前触发，如果返回false可终止载入数据操作
				var queryParams = $('#easyui-datagrid').datagrid('options').queryParams; //初始化datatGrid后才可以获取options所有相关数据
				console.debug('请求发出前的自定义表单参数:');
				console.info(queryParams);
				console.debug('请求发出前的所有参数:');
				param['pageNum']=param.page;
				param['pageSize']=param.rows;
				param['orderByColumn']=param.sort;
				param['isAsc']=param.order;
				param['code']=code;
				console.info(param);
	        },
			/*onUnselect:function(rowIndex, rowData){
				$("#easyui-datagrid").datagrid('endEdit', editRow); //未选择该行的时候，结束编辑
			},*/
			//当用户单击一个单元格时触发。
			onClickCell:function(index,field,value){
				/* if (editRow != undefined) {
					 $(this).datagrid('endEdit', editRow);
		 
		            }*/
			},
			//当用户双击一个单元格时触发。
			onDblClickCell:function(rowIndex, field, value){
				var rows = $('#easyui-datagrid').datagrid('getRows');// 获得所有行
				var row = rows[rowIndex];// 根据index获得其中一行。
				var status = row.isPrint;
				if(0!=status){return};
				if (field=="hawb"||field=="templateName"||field=="destination"||(field=="total"&&type=="air")) {
					
					 if (editRow !=rowIndex) {
						 $(this).datagrid('endEdit', editRow);
					 }
					
						$(this).datagrid('beginEdit', rowIndex);
						$(this).datagrid('selectRow',rowIndex); //选中该行
						$("#rejectButton").attr("disabled",false); //启用撤销
						$("#saveButton").attr("disabled",false); //启用保存
						editRow = rowIndex; //记录当前编辑行索引
			        
						var ed = $(this).datagrid('getEditor', {index:rowIndex,field:'templateName'});
						
						
						$(ed.target[0]).combobox({
							url:"/labelPrint/label/getUserAuthtemplate?code="+code,
							method:'GET',
							textField:'templateName',
						    valueField:'templateName',
						    onSelect: function(rec){
						    	row.reserve3=rec.id;//修改label外键id
						    	row.templateId=rec.templateId;//修改模板id
						    	row.templateName = rec.templateName;
						    	row.width=rec.width; //修改宽度
						    	row.height=rec.height; //修改高度
						    }
							
						});
						$(ed.target[0]).combobox("select",row.templateName); //设置默认的初始值
				}
			},
			// 结束编辑事件 当用户完成编辑一行时触发，参数包括：
			onAfterEdit:function(rowIndex, rowData, changes){
				//更新完成后，刷新当前行
				$("#easyui-datagrid").datagrid('refreshRow', rowIndex);
				//editRow = undefined;
			},
			// 取消编辑事件
			onCancelEdit:function(rowIndex, rowData){
				
			},
			onLoadSuccess: function (data) {
				   if (data.total == 0) {
					   $('#easyui-datagrid').datagrid('appendRow',{
						   mawb: '无数据！请更改查询条件，或<a href="#"  onclick="subscribe.initWindow()" style="color:red;"><b>订阅</b></a>主单号...',
					   }).datagrid('mergeCells',{
						   index: 0,
						   field: 'mawb',
						   colspan: 12
					   }).datagrid('hideColumn','labelId');
					   
				   }else{
					   layer.msg('数据已刷新!',{icon:1});
				   }
					   
			},
	        view: detailview,
	    	detailFormatter:function(index,row){ //在行下面展示其他数据列表
	    		//return '<div class="ddv" style="padding:5px 0"></div>';
	    		var columns = $(this).datagrid('options').columns[0];
	    		return detailFormatter(index,row,columns);
	    	},
	    	/*onExpandRow: function(index,row){
	    		var ddv = $(this).datagrid('getRowDetail',index).find('div.ddv');
	    		ddv.panel({
	    			border:false,
	    			cache:false,
	    			href:url+'?labelId='+row.labelId,
	    			onLoad:function(){
	    				$('#easyui-datagrid').datagrid('fixDetailRowHeight',index);
	    			}
	    		});
	    		$('#easyui-datagrid').datagrid('fixDetailRowHeight',index);
	    	},*/
			
		});
		
		/*
		var options = $('#tableGoods').datagrid('getPager').data("pagination").options;
		var page = options.pageNumber;//当前页数  
		var total = options.total;
		var rows = options.pageSize;//每页的记录数（行数）  
		
		//使用ajax方式加载数据
		$.ajax({
			type: 'POST',
			dataType: 'JSON',
			url: url,
			timeout: 20000,
			//async: false,     // 同步
			data: {},
			success: function (result) {
				console.info("获取数据成功，返回的数据为：↓");
				console.info(result);
				if (result.status=="200") {
					console.info(result.data);
					$("#easyui-datagrid").datagrid('loadData', result.data); //JSON字符串转换为对象
				}else {
					layer.msg(result.message,{icon:2});
				}
			}
		});*/
	}

//在行下面展示其他数据列表
function detailFormatter(index, row, columns) {
	var html = [];
	$.each(row, function(key, value) {
		 for (var i = 0; i < columns.length; i++) {
			 var field = columns[i].field;
			 if(key==field){
				 key =  columns[i].title +"【"+key+"】";
				 break;
			 }
		 }
		 html.push('<p><b>' + key + ':</b> ' + value + '</p>');
	});
	return html.join('');
}


/**初始化Select2 表单下拉框**/
label.prototype.select2=function(){
	var selected = [{id:'全部',text:"全部"},{id:'CTU',text:"[CTU]成都/双流机场"},{id:'CKG',text:"[CKG]重庆/江北机场"}];
	
	$('.select2').each(function(){
		 var $this = $(this);
		 $this.select2({
			 language : "zh-CN",// 指定语言为中文，国际化才起效
			 data: selected,
             ajax:{
            	 url : $this.attr("href"),
            	 tyle: 'POST',
                 dataType : 'json',
                 delay : 250,// 延迟显示
                 data : function(params) {
                	 return {
                		 code : params.term, // 搜索框内输入的内容，传递到Java后端的parameter为code
                		 curPage : params.page,// 第几页，分页哦
                		 pageSize : 10// 每页显示多少行
                     };
                 },
                 // 分页
                 processResults : function(data, params) {
                     params.curPage = params.curPage || 1;
                     var resultList = data.rows; //后台数据
                     return {
                    	//返回的选项必须处理成以下格式
                         //var resultList =  [{ id: 0, text: 'enhancement' }, { id: 1, text: 'bug' }, { id: 2, text: 'duplicate' }, { id: 3, text: 'invalid' }, { id: 4, text: 'wontfix' }];
                         results : resultList,// 后台返回的数据集 必须赋值给results并且必须返回一个obj
                         pagination : {
                             more : params.curPage < data.total// 总页数为10，那么1-9页的时候都可以下拉刷新
                         }
                     };
                 },
                 cache : false
             },
	         placeholder:$this.attr("inputMessage"),// 添加默认//默认文字提示
	         tags: true,//允许手动添加
	         allowClear: true,//允许清空
	         escapeMarkup: function (markup) { return markup; }, // 自定义格式化防止xss注入
	         minimumInputLength: 1,//最少输入多少个字符后开始查询
	         formatResult: function formatRepo(repo){return repo.text;}, // 函数用来渲染结果
	         formatSelection: function formatRepoSelection(repo){return repo.text;} // 函数用于呈现当前的选择
		 });
	});
}


/**
 * bootstrap-select
 */
label.prototype.selectpicker=function(){
	$('.selectpicker').each(function(){
		 var $this = $(this);
		 $this.selectpicker({
			 noneSelectedText : '全部'    //默认显示内容
		 });
		 $.ajax({
			 url : $this.attr("href"),    //后台controller中的请求路径
			 type : 'POST',
			 async : false,
			 datatype : 'json',
			 success : function(data) {
				 if(data){
					 var values = [];
					 var datarr =[];
					 var list = data.rows;
					 datarr.push( '<option value="全部">全部</option>');
					 $.each(list,function (index,item) {
						   values.push(item.id);
			                //拼接成多个<option><option/>
			                datarr.push( '<option value="'+item.id+'">'+item.text+'</option>');
			            });
					 
					 //$this.html(datarr.join(''));    //根据parkID(根据你自己的ID写)填充到select标签中
					 $this.append(datarr.join(''));
					 //级联选择的使用场景中，经常需要在赋值的时候顺便触发一下组件的change事件
					 $this.selectpicker('val',values).trigger('change');
				 }
			 },
			 error : function() {
				 alert('查询场地名称出错');
			 }
		 });
		 
		//初始化刷新数据
         $(window).on('load', function() {
        	 $this.selectpicker('refresh');
         });
         
        $this.on('shown.bs.select',function(e){
        	$this.prev().find("input").keydown(function(){
        		$this.prev().find("input").attr('id',"deviceInput"); //为input增加id属性
         		console.log($('#deviceInput').val()); //获取输入框值输出到控制台
         	})

         });
		 
	});
	
	
}


var label = new label();
	
$(document).ready(function(){
	//初始化表格
	label.initLabelDatagrid();
	label.select2();
//	label.selectpicker();
});