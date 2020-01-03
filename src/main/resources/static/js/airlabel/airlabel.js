function label() {
	
}
	var type ="air"; //空运标签模式
	var mawb="";
	var hawb="";
	var destination="";// 目的地
	var total="";// 件数
	var airport_departure="";// 起始地
	var is_print="";// 是否打印
	var start_time;// 开始时间
	var end_time;// 结束时间
	var flight_date="";// 结束时间
	var opid_name="";// 录入人
	var MBLNo="";//
	var TakeCargoNo="";// 
	var opid=""; //打印权限信息
	var opid1; //当前操作id
	
	//js初始化调用的接口
	//获取操作opid
$(function(){
		$.ajax({
			url : "/labelPrint/getThisOpids",
			type : 'POST',
			async:false,
			success : function(result) {// 根据结果显示菜单
				opid1= result;
			}
		});
		//获取打印权限
		$.ajax({
			url : "/labelPrint/client/getPrint",
			type : 'POST',
			async:false,
			success : function(result) {
				opid = eval('(' + result + ')');
			}
		});
		
		/*
		 * $("label").click(function(){ alert($(this).attr("title")); });
		 */

		/**
		 * 录入人下拉选择
		 */
		$('#opid_name').combogrid({    
			idField:'opid_name',    
			textField:'opid_name',    
			mode:'remote',
			value:'所有',    
			height:34,
			fitColumns:true,
			scrollbarSize : 0,
			url:'/labelPrint/client/getOpidName',    
			columns:[[
					{field:'opid_name',title:'',width:50},
					{field:'opid',title:'',width:50}
				]],
			onSelect:function(rowIndex, rowData){
					opid_name =rowData.opid_name; 
				}
		}); 
		
		//起始地
		$('#airport_departure').combogrid({    
		    idField:'code',    
		    textField:'name',    
		    mode:'remote',
		    value:'所有',    
		    height:34,
		    fitColumns:true,
		    scrollbarSize : 0,
		    url:'/labelPrint/client/getRegion',    
		    columns:[[    
		        {field:'name',title:'',width:50}
		    ]],
		    onSelect:function(rowIndex, rowData){
		    	airport_departure =rowData.code; 
		    }
		});  
		//目的地
		$('#destination').combogrid({    
			idField:'code',    
			textField:'name',
			mode:'remote',
			value:'所有',
			height:34,
			fitColumns:true,
			scrollbarSize : 0,
			url:'/labelPrint/client/getRegion',    
			columns:[[    
				{field:'name',title:'',width:50}
				]],
			onSelect:function(rowIndex, rowData){
				destination =rowData.code; 
		    }
		}); 
		
		
		//初始化页面 获取打印区域 opid 当前操作号 code  办公司id
		$.ajax({
			url : "/labelPrint/client/getThisRegion?opid="+opid1+"&code="+localStorage.getItem('ls'),
			type : 'POST',
			async:false,
			success : function(result) {
				var	rt = eval("("+result+")");
				$("#quyu").text(rt.parent_name+'/'+rt.region_name); //修改区域标签
			}
		});
		
	}); 

//初始化结束

//修改办公室
function setName(){
		$.ajax({
			url : "/labelPrint/client/getThisRegion?opid="+opid1+"&code="+localStorage.getItem('ls'),
			type : 'POST',
			success : function(result) {
				var	rt = eval("("+result+")");
				$("#quyu").text(rt.parent_name+'/'+rt.region_name);
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
		// $('#dd4').dialog("close");
		// $('#dg1').datagrid("reload");
	}
	label.prototype.delete1 = function() {
		var rows = $('#dg1').datagrid('getSelections');
		if (rows.length > 0) {
			if(window.confirm('确定要删除？')){
				$.ajax({
					url : "/labelPrint/label/delete",
					type : 'POST',
					data : {
						label : JSON.stringify(rows)
					},
					success : function(result) {
						$('#dg1').datagrid('reload');
					}
				});
	            return true;
	         }else{
	            return false;
	        }
		}else {
			alert("请选择数据后再删除！");
		}
		
	}
	//刷新表单 重新加载数据
	label.prototype.refresh = function(isRefresh) {
		$("#searchForm").form("clear");
		$("#total").val("");
		
		mawb = "";
		hawb = "";
		destination = "";// 目的地
		total = "";// 件数
		airport_departure = "";// 起始地
		is_print = "";
		start_time = "";
		end_time = "";
		flight_date= "";
		$('#airport_departure').combogrid('setValue', '所有');// 替换为所有
		$('#destination').combogrid('setValue', '所有');// 替换为所有
		$('#opid_name').combogrid('setValue', '所有');// 替换为所有
		opid_name= "";
		if (isRefresh) {
			label.initLabelDatagrid();
		}
	}
	//查询事件
	label.prototype.search = function() {
		var mr;
		
		if (!vda.startValidate()) {
			return;
		}
		// $('#dd3').dialog('close');
		var tMawb = $.trim($("#mawb").val()) == undefined?"":$.trim($("#mawb").val());
		if (tMawb!="") {
			mawb = JSON.stringify(tMawb.split("\n")); //多个主单号，数组形式的字符串
		}else{
			mawb = $("#mawb").val();
		}
		hawb = $("#hawb").val()== undefined?"":$("#hawb").val();
		total = $("#total").val()== undefined?"":$("#total").val();
		destination = $("#destination").val()== undefined?"":$("#destination").val();
		airport_departure = $("#airport_departure").val()== undefined?"":$("#airport_departure").val();
		start_time = $("#start_time").val();
		end_time = $("#end_time").val();
		flight_date = $("#flight_date").val()== undefined?"":$("#flight_date").val();
		opid_name = $("#opid_name").val()== undefined?"":$("#opid_name").val();
		MBLNo=$("#MBLNo").val()== undefined?"":$("#MBLNo").val()
		TakeCargoNo=$("#TakeCargoNo").val()== undefined?"":$("#TakeCargoNo").val()
		
		is_print = $("#status").val()==null?"":$("#status").val();
		/*
		 * if ($('[name="status"]').bootstrapSwitch('state')) { is_print = "1"; }
		 * else { is_print = "0"; }
		 */
		label.initLabelDatagrid();
	}
	
	//打印按钮事件
	label.prototype.print1 = function() {
		var rows = $('#dg1').datagrid('getSelections');// 获取所有选中行的数据
		// 查询是否是第一次登录
		$.ajax({
			url : "/labelPrint/client/isFrist?opid="+opid1,
			type : 'POST',
			async:false,
			success : function(result) {
					if (result=="false") {// 第一次登录
						// 获取用户选择的区域
						if (rows.length > 0) {
							$.ajax({
								url : "/labelPrint/client/getPrint",
								type : 'POST',
								success : function(result) {
									opid = eval('(' + result + ')');
									if (opid.length==0) {
										alert("你没有该功能权限，请先申请权限");
										return
									}
									if (opid[0].length > 1) {// 打印标签模版不止一个
										$("#dyan").css("display", "block");// 显示打印按钮
										$("#dcan").css("display", "none");// 隐藏导出按钮
										$("#xgmr").css("display", "none");// 隐藏按钮
										label.initSend();
										$("#xzdy").css("display", "block");
										$("#ssdy").css("display", "block");
										// $("#bqys").css("display", "block");
										$("#selectbq").children().remove();// 清理缓存
										$("#selectbq").append('<option selected="selected">请选择标签样式</option>');
										for (var i = 0; i < opid[0].length; i++) {
											$("#selectbq").append('<option value =' + opid[0][i].Reportid + ',' + opid[0][i].Reportname + '>' + opid[0][i].Reportname + '</option>');
										}
									} else {
										$("#dyan").css("display", "block");// 显示打印按钮
										$("#dcan").css("display", "none");// 隐藏导出按钮
										$("#xgmr").css("display", "none");// 隐藏按钮
										
										$("#xzdy").css("display", "block");
										$("#ssdy").css("display", "block");
										label.initSend();
									}
								}
							});
					} else {
						alert('请选择一条数据，再打印');
					}
				}else {// 不是第一次登录
					if(rows.length <= 0){
						alert('请选择一条数据，再打印');
						return;
					}
					mr = eval('(' + result + ')');
					if (localStorage.getItem("ls")!=null) {
						label.printLabelSendClient("", localStorage.getItem("ls"));
					}else {
						label.printLabelSendClient("", mr[0].office_id);
					}
				}
			}
		});
	}
	
/**
 * 更新办公室
 */	
function updateRegion() {
		var bgs = $("input[name='bgs']:checked").val();
		$("input[name='bgs']:checked").prop('checked', false);
		
		var t = $("#code").val();
		if (t!=""&&undefined!=bgs) {
			if (bgs=="ls") {
				if (typeof(Storage) !== "undefined") {
				    localStorage.setItem("ls", t);
					$('#region').dialog("close");
					$("#code").val("");
					setName();
				} else {
					alert("你的浏览器不兼容系统，建议更换谷歌浏览器");
				}
			}else if (bgs="mr") {
				$.ajax({
					url : "/labelPrint/client/updateRn?region="+t,
					type : 'POST',
					success : function(result) {
						if (result=="true") {
							alert("更改成功");
							$('#region').dialog("close");
							$("#code").val("");
							setName();
						}else{
							alert("更改失败，请联系管理员");
						}
					}
				});
			}
		}else{
			alert("请选择打印办公室，并勾选（默认）或（临时）");
		}
	}
	
	/**
	 * 修改打印配置的信息
	 */
	label.prototype.alertprint = function() {
		$.ajax({
				url : "/labelPrint/client/getPrint",
				type : 'POST',
				success : function(result) {
					opid = eval('(' + result + ')');
					if (opid.length==0) {
						alert("你没有该功能权限，请先申请权限");
						return
					}
					$("#xgmr").css("display", "block");
					$("#dcan").css("display", "none");// 隐藏导出按钮
					$("#dyan").css("display", "none");// 显示打印按钮
					
					
					$("#xzdy").css("display", "block");
					$("#ssdy").css("display", "block");
					label.initSend(true);
				}
			});
	}
	
	label.prototype.export1 = function() {
		var rows = $('#dg1').datagrid('getSelections');// 获取所有选中行的数据
		if (rows.length > 0) {
			$.ajax({
				url : "/labelPrint/client/getPrint",
				type : 'POST',
				success : function(result) {
					opid = eval('(' + result + ')');
					if (opid.length==0) {
						alert("你没有该功能权限，请先申请权限");
						return
					}
					
					$("#xzdy").css("display", "none");
					$("#ssdy").css("display", "none");
					$("#dcan").css("display", "block");// 显示导出按钮
					$("#dyan").css("display", "node");
					$("#dcan").css("display", "block");
					var ids = "";
					for (var i = 0; i < rows.length; i++) {
						if (i == (rows.length - 1)) {
							ids += "'" + rows[i].label_id + "'";
						} else {
							ids += "'" + rows[i].label_id + "',";
						}
					}
				    window.location.href = "/labelPrint/client/exportLabel?labels=" + ids+"&report="+opid[0][0].Reportid + ',' + opid[0][0].Reportname;
					
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
			});

		} else {
			alert("请选择数据后再导出");
		}
	}

	/**
	 * 初始化 打印 按钮需要的选择控件
	 */
	label.prototype.initSend = function(isRadio) {
		// 初始化弹出 更新打印办公室
		$('#region').dialog({
			title : ' ',
			width : 500,
			height : 250,
			maximizable : true,
			cache : false,
			modal : true,
			onClose:function(){
				try {
					$("input[name='bgs']:checked").prop('checked', false);
					$("#code").val("");
					$("#bgs").css("display","none");
				} catch (e) {
					console.info("初始化单选框失败");
				}
			},
			onBeforeOpen:function(){
				if(isRadio){
					try {
						$("#bgs").css("display","block");
					} catch (e) {
					}
				}
			}
		});
		
		// 初始化下拉树
		$('#cc').combotree({
			required : true,
			url : "/labelPrint/client/printLabel?opid="+opid1,
			onBeforeSelect : function(node) {
				return false
			},
			onClick : function(node) {
				if ($('#cc').tree('isLeaf', node.target)) {// 判断是否是叶子节点
					$('#cc').combotree('setValue', node.pname + "/" + node.text);
					$('#code').val(node.parent_code + "/" + node.region_code);
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
		
		$('#cc').combotree('setValue', '选择办公区域');
	}
	
	//发送打印客户端 打印人 /区域
	label.prototype.printLabelSendClient = function(bq, t) {
		var url = "/labelPrint/client/printLabelSendClient";
		var rows = $('#dg1').datagrid('getSelections');
		$.ajax({
			url : url,
			type : 'POST',
			data : {
				labels : JSON.stringify(rows),
				regions : t,
				businessType : type,
				report : bq
			},
			success : function(result) {
				if(result.status=="200"){
   					//弹框提示
   					$.messager.show({
   						title:'<b style="font-size:12;text-align:center;">我的消息</b>',
   						msg:'<h3 style="color:green;text-align:center;">标签已发往客户端打印</h3>',
   						timeout:3000,
   						showType:'fade'
   					});
   				}else{
   					layer.msg(result.message,{icon:2,time:1000});
   				}
				
				try {
					$('#region').dialog("close");  //关闭办公室选择弹框
				} catch (e) {
					console.info("面板关闭失败");
				}
				$('#dg1').datagrid('reload');
			}
		});
	}
	
	
	label.prototype.export = function() {
		var rows = $('#dg1').datagrid('getSelections');// 获取所有选中行的数据
		var bq = $("#selectbq").val();
		var ids = "";
		for (var i = 0; i < rows.length; i++) {
			if (i == (rows.length - 1)) {
				ids += "'" + rows[i].label_id + "'";
			} else {
				ids += "'" + rows[i].label_id + "',";
			}
		}
	    window.location.href = "/labelPrint/client/exportLabel?labels=" + ids+"&report="+bq;
		
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
	
	//弹框后打印
	label.prototype.print = function() {
		var bq = $("#selectbq").val();
		var t = $("#code").val();
		if (opid[0].length > 1) {// 打印标签模版不止一个
			if (t == "") {
				alert("请先选择办公区域再打印");
				return;
			}
			label.printLabelSendClient(bq, t);
		} else {
			bq = opid[0][0].Reportid + ',' + opid[0][0].Reportname;
			if (t == "") {
				alert("请先选择办公区域再打印");
				return;
			}
			label.printLabelSendClient(bq, t);
		}

		$("#code").val("");
	}
	
	label.prototype.columns = function(is_print2) {
		var columns;
		if (type =="air") {
			columns = [ [ {
				field : 'label_id',
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
				field : 'airport_departure',
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
				field : 'template_name',
				width : 50,
				//sortable : true,
				//sortOrder:'asc',
				editor:'combobox',
				title : '当前模版',
				styler: function(value, row, index) {
			    	if (value == "未配置打印模板") {
			    		return 'color:red;';
			    	}
			    },
				formatter : function(value, row, index) {
					// 用户没有申请模版权限
					if (opid.length==0) {
						return '无模版权限';
					}
					return value;
			/*
			 * var result1; $.ajax({ url : "/label/template", type : 'POST',
			 * async:false, data:{ opid:JSON.stringify(opid), id:value }, success :
			 * function(result) { result1= eval('(' + result + ')'); } }); return
			 * result1.template_name;
			 */
				}
			
			},{
				field : 'flight_date',
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
				field : 'is_print',
				width : 50,
				title : '状态',
				//sortable : true,
				//sortOrder:'asc',
				formatter : function(value, row, index) {
					if (row.is_print == 1) {
						return "已打印";
					}else if (row.is_print == 2) {
						return "已导出";
					} else {
						return "未打印";
					}
				}
			},{
				field : 'reserve2',
				width : 50,
				//sortable : true,
				//sortOrder:'asc',
				title : '打印时间'
			},{ 
				field : 'update_time', 
				width : 50, 
				sortable : true,
				sortOrder:'asc',
				title : '录入时间', 
				formatter : function(value, row, index) { 
					return formatDate(value); 
				} 
			},{ 
				field : 'opid_name', 
				width : 50, 
				sortable : true,
				sortOrder:'asc',
				title : '录入人'
			},{
				field : 'print_user',
				width : 50,
				//sortable : true,
				//sortOrder:'asc',
				title : '打印人'
			}] ];	
		}else if (type == "medicine") {
			columns = [[
				{field : 'label_id',checkbox : true,width : 50},
		        {field:'MBLNo',title:'运单号',width:100},    
		        {field:'total',editor:'text',title:'打印份数',width:100},    
		        {field:'EDeparture',title:'发货日期',width:100,
		         formatter : function(value, row, index) { 
					return formatDate(value); 
				}},    
		        {field:'SendAddress',title:'发货人信息',width:100},    
		        {field:'RecCustomerName',title:'收货人',width:100},    
		        {field:'RecAddress',title:'收货地址',width:100},    
		        {field:'TakeCargoNo',title:'随货同行单号',width:100},
		        {
					field : 'reserve2',
					width : 50,
					title : '打印时间'
				},
				{
					field : 'print_user',
					width : 50,
					title : '打印人'
				}
			]];
		}
		return columns;
	}
	
	//初始化表格
	label.prototype.initLabelDatagrid = function(is_print2) {
		if (is_print2 != undefined) {
			is_print = is_print2;
		}
//		var url=encodeURI(encodeURI('/labelPrint/label/all?mawb=' + mawb + '&hawb=' + hawb + '&destination=' + destination + '&total=' + total + '&airport_departure=' + airport_departure + '&is_print=' + is_print + '&start_time=' + start_time+ '&end_time=' + end_time+ '&opid=' + opid1+'&flight_date='+flight_date+'&opid_name='+opid_name+'&businessType='+type+'&MBLNo='+MBLNo+'&TakeCargoNo='+TakeCargoNo));
		var url='/labelPrint/label/all?mawb=' + mawb + '&hawb=' + hawb + '&destination=' + destination + '&total=' + total + '&airport_departure=' + airport_departure + '&is_print=' + is_print + '&start_time=' + start_time+ '&end_time=' + end_time+ '&opid=' + opid1+'&flight_date='+flight_date+'&opid_name='+opid_name+'&businessType='+type+'&MBLNo='+MBLNo+'&TakeCargoNo='+TakeCargoNo;
		$('#dg1').datagrid({
			url : url,
			method : 'POST',
			iconCls : 'fa fa-truck',
			pagination : true,
			pageSize : 20,
			striped:true,
			scrollbarSize : 0,// 去除右侧空白
			fitColumns : true,
			pageList : [ 5, 10, 15, 20, 50 ],
			toolbar:'#toolbar',
			width: 'auto', //默认10
			showFooter : true,
			loadMsg: "正在加载数据...",
			columns : label.columns(),
			rowStyler : function(index, row) {
				if (row.is_print == 1||row.is_print == 2) {//打印状体修改展示颜色
					return 'background-color:#5cb85c';
				}
			},
			loadFilter:function(data){
				if(data.status=="200"){
					return data.data;
				}else{
					layer.msg(data.message,{icon:2,time:5000});
					return null;
				}
			},
			onUnselect:function(rowIndex, rowData){
				$("#dg1").datagrid('endEdit', rowIndex); //未选择该行的时候，结束编辑
			},
			//当用户双击一个单元格时触发。
			onDblClickCell:function(index,field,value){
				
			},
			//当用户单击一个单元格时触发。
			onClickCell:function(rowIndex, field, value){
				
				if (field=="hawb"||field=="template_name"||field=="destination"||(field=="total"&&type=="medicine")) {
			        var rows = $('#dg1').datagrid('getRows');// 获得所有行
			        var row = rows[rowIndex];// 根据index获得其中一行。
					
					$(this).datagrid('beginEdit', rowIndex);
			        if (type=="air") {
						var ed = $(this).datagrid('getEditor', {index:rowIndex,field:'template_name'});
						$(ed.target[0]).combobox({    
						    valueField:'template_name',  
						    textField:'text'
						});
						// 设置模版下拉框的值
						if (opid.length==0) {
							$(ed.target[0]).combobox('setValue', result1.template_name);
						}else {
							var result1;
							$.ajax({
								url : "/labelPrint/label/getUsertemplate", //获取用户绑定的模板
								type : 'POST',
								async:false,
								data:{
									opid:JSON.stringify(opid)
								},
								success : function(result) {
									result1= eval('(' + result + ')');
								}
							});
							var data = [];
							for (var i = 0; i < result1.length; i++) {
								data.push({ "text": result1[i].template_name, "template_name": result1[i].template_name });
							}
							$(ed.target[0]).combobox('loadData', data);
							// 设置下拉框选中的值
							var result1;
							$.ajax({
								url : "/labelPrint/label/template",
								type : 'POST',
								async:false,
								data:{
									opid:JSON.stringify(opid),
									id:row.template_name
								},
								success : function(result) {
									result1= eval('(' + result + ')');
								}
							});
							$(ed.target[0]).combobox("select",result1.template_name);
						}
			        }
					/*var ed = $(this).datagrid('getEditor', {index:rowIndex,field:field});
					$(ed.target[0]).focus();// 获取焦点
					// 失去焦点事件,结束编辑
					$(ed.target[0]).blur(function(){
						// $("#dg1").datagrid('endEdit', rowIndex);
					});*/
				}
			},
			// 结束编辑事件 当用户完成编辑一行时触发，参数包括：
			onAfterEdit:function(rowIndex, rowData, changes){
				$.ajax({
					url : "/labelPrint/label/update",
					type : 'POST',
					data:{
						hawb:rowData.hawb,
						template_name:rowData.template_name,
						destination:rowData.destination,
						label_id:rowData.label_id,
						total:rowData.total
					},
					success : function(result) {
						// 更新完成后，刷新当前行
						$("#dg1").datagrid('refreshRow', rowIndex);
					}
				});
			},
			// 取消编辑事件
			onCancelEdit:function(rowIndex, rowData){
				
			},
			onLoadSuccess: function (data) {
		           if (data.total == 0) {
						$('#dg1').datagrid('appendRow',{
							mawb: '无数据！请更改查询条件，或<a href="#"  onclick="subscribe.initWindow()" style="color:green;"><b>订阅</b></a>主单号...',
						}).datagrid('mergeCells',{
							index: 0,
							field: 'mawb',
							colspan: 12
						}).datagrid('hideColumn','label_id');
						
					}else{
						layer.msg('数据加载成功!',{	icon:1});
		        	   
		           }
			},
	        onLoadError: function () {
	        	layer.msg('警告!数据加载失败!',{	icon:2});
	        },
			
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
					$("#dg1").datagrid('loadData', result.data); //JSON字符串转换为对象
				}else {
					layer.msg(result.message,{icon:2});
				}
			}
		});*/
	}

var label = new label();
	
$(document).ready(function(){
	//初始化表格
	label.initLabelDatagrid();
});