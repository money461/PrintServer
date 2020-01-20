function paiang(){
	
}

//定义全局 表格对象
var editRow = undefined;
var paiang_tab;
var baseName='paiang'; //数据库名称
var type ="medicine"; //派昂标签类型
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
var printAuth=""; //打印权限信息
var opid =""; //获取当前用户opid //全局变量引用
$(function(){
	//js初始化调用的接口
		$.ajax({
			url : "/labelPrint/getThisOpids",
			type : 'POST',
			async:false,
			success : function(result) {// 根据结果显示菜单
				opid= result;
			}
		});
	
	//初始化页面 获取打印区域 opid 当前操作号 code  办公司id
		$.ajax({
			url : "/labelPrint/client/getThisRegion?opid="+opid+"&code="+localStorage.getItem('ls'),
			type : 'POST',
			async:false,
			success : function(result) {
				var	rt = eval("("+result+")");
				$("#quyu").text(rt.parent_name+'/'+rt.region_name); //修改区域标签
			}
		});	
		
		//获取打印权限
		$.ajax({
			url : "/labelPrint/client/getPrint",
			type : 'POST',
			async:false,
			success : function(result) {
				printAuth = eval('(' + result + ')');
			}
		});
});
//初始化表格  返回格式 返回的参数必须是total和rows，total返回数据集总个数，rows返回table的json格式
paiang.prototype.initDataGridTable=function(){

	paiang_tab =  $('#paiang_tab').datagrid({
			url : "/labelPrint/paiang/search",
			iconCls : 'fa fa-truck',
			method : 'POST',
			pagination : true,
			singleSelect: false, //只允许选择一行
			//checkOnSelect : true,// 如果为false，当用户仅在点击该复选框的时候才会呗选中或取消。
			pageSize : 20,
			striped : true,// 隔行换色
			scrollbarSize : 0,// 去除右侧空白
			fitColumns : true,
			remoteSort:false, //定义是否通过远程服务器对数据排序。
			toolbar : '#toolbar', //该工具栏置于对话框的顶部，每个工具栏包含:text, iconCls, disabled, handler等属性。
			showFooter : true,
			pageList : [ 5, 10, 15, 20, 50 ],
		    queryParams: getQueryCondition(),
			width: 'auto', //默认10
			loadMsg: "正在加载数据...",
			//toolbar : toolbar.tools,
			columns: paiangcolumns.columns,
			loadFilter:function(data){ //数据过滤
				if("total" in data){
					return data;
				}else{
					
					if(data.status=="200"){
						return data.data;
					}else{
						layer.msg(data.message,{icon:2,time:5000});
						return null;
					}
					
				}
			},
			//当用户双击一行时触发
			onDblClickRow : function(rowIndex,rowData) { //在用户双击一行的时候触发，参数包括：index：点击的行的索引值，该索引值从0开始。row：对应于点击行的记录。
				
		    },
		  //当用户单击一个单元格时触发。
		    onClickCell:function(index,field,value){
		    	 if (editRow != undefined) {
					 $(this).datagrid('endEdit', editRow);
		 
		            }
		    },
		    //当用户双击一个单元格时触发。
		    onDblClickCell:function(rowIndex, field, value){
		    	if (editRow != undefined) {
					 $(this).datagrid('beginEdit', editRow);
				 }
				
			if (editRow == undefined) {
		    	
				if (field=="RecCustomerName"||field=="template_name"||field=="packages") {
			        var rows = $('#paiang_tab').datagrid('getRows');// 返回当前页的行。
			        var row = rows[rowIndex];// 根据index获得其中一行。
					
					$(this).datagrid('beginEdit', rowIndex); //开启编辑
					editRow = rowIndex; //记录当前编辑行索引
					
					var ed = $(this).datagrid('getEditor', {index:rowIndex,field:'template_name'}); //获取指定的编辑器， options 参数包含两个属性：index：行的索引。field：字段名。
					//使用编辑器对该字段添加多选框
					$(ed.target[0]).combobox({    
					    valueField:'template_name',  
					    textField:'text'
					});
					// 设置模版下拉框的值
					if (printAuth.length==0) {
						$(ed.target[0]).combobox('setValue', row.template_name);
						$(ed.target[0]).combobox("select",row.template_name); //将行中原来的模板设置为选中
						
					}else {
						var result1;
						$.ajax({
							url : "/labelPrint/label/getUsertemplate", //获取用户绑定的模板
							type : 'POST',
							async:false,
							data:{
								opid:JSON.stringify(printAuth)
							},
							success : function(result) {
								result1= eval('(' + result + ')');
							}
						});
						var data = [];
						for (var i = 0; i < result1.length; i++) {
							data.push({ "template_name": result1[i].template_name, "text": result1[i].template_name });
						}
						$(ed.target[0]).combobox('loadData', data);
						
						// 设置下拉框选中的值
						var result1;
						$.ajax({
							url : "/labelPrint/label/template",
							type : 'POST',
							async:false,
							data:{
								opid:JSON.stringify(printAuth),
								id:row.template_name
							},
							success : function(result) {
								result1= eval('(' + result + ')');
							}
						});
						$(ed.target[0]).combobox("select",result1.template_name); //将行中原来的模板设置为选中
					}
					
					/*var ed = $(this).datagrid('getEditor', {index:rowIndex,field:field});
					$(ed.target[0]).focus();// 获取焦点
					// 失去焦点事件,结束编辑
					$(ed.target[0]).blur(function(){
						 $("#beginEdit").datagrid('endEdit', rowIndex);
					});*/
				}
			  }
			},
	        onLoadSuccess: function (data) {
        	  if (data.total == 0) {
					$('#paiang_tab').datagrid('appendRow',{
						mawb: '无数据！请更改查询条件，或<a href="#"  onclick="subscribe.initWindow()" style="color:green;"><b>订阅</b></a>主单号...',
					}).datagrid('mergeCells',{
						index: 0,
						field: 'mawb',
						colspan: 12
					}).datagrid('hideColumn','label_id');
					
				}else{
					layer.msg('数据已刷新!',{icon:1});
	        	   
	           }
	        	
	        },
	        onLoadError: function () {
	        	layer.msg('警告!数据加载失败!',{icon:2});
	        },
	        onBeforeLoad: function(param){ // 在发出请求数据数据之前触发，如果返回false可终止载入数据操作
				
	        	
	        },
	        onBeforeEdit:function(index,row){
				updateActions(index);
			},
			/*onUnselect:function(rowIndex, rowData){
				$("#paiang_tab").datagrid('endEdit', rowIndex); //未选择该行的时候，结束编辑
			},*/
			// 结束编辑事件
			onAfterEdit:function(rowIndex, rowData, changes){
				/*$.ajax({
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
					$("#paiang_tab").datagrid('refreshRow', rowIndex);
					}
				});*/
				
				//更新完成后，刷新当前行
				rowData.template_id=''; //设置模板id 此时标签的id是未被修改的，只能制空
				rowData.reserve3='';
				$("#paiang_tab").datagrid('refreshRow', rowIndex);
				editRow = undefined;
			},
			onCancelEdit:function(index,row){
				updateActions(index);
			}
	       
       
	});
	
	//使用ajax方式加载数据
	/*$.ajax({
        type: 'POST',
        dataType: 'JSON',
        url: "/labelPrint/paiang/search",
        timeout: 20000,
        //async: false,     // 同步
        data: getQueryCondition(),
        success: function (result) {
            console.info("获取数据成功，返回的数据为：↓");
            console.info(result);
            if (result.status=="200") {
                console.info(result.data);
                $("#paiang_tab").datagrid('loadData', eval("("+result.data+")")); //JSON字符串转换为对象
            }else {
                layer.msg(result.message,{icon:2});
            }
        }
    });*/
	
}

function updateActions(index){
	$('#paiang_tab').datagrid('updateRow',{
		index: index,
		row:{}
	});
}


//封装查询参数
function getQueryCondition(){
   var formParm = getEntity('#searchForm'); //获取搜索表单form
   var tMawb = formParm.mawb;
   mawb = $.trim(tMawb) == undefined?"":$.trim(tMawb);
   mawb = mawb.replace(/\s+/g, ',');
   formParm.mawb=mawb;
    console.info(formParm);
  return formParm;
}

//获取提交表单的值返回{}对象
function getEntity(form) {
  var result = {};
	
  //循环
  $(form).find("[name]").each(function() {
      var field = $(this).attr("name");
      var val;

      if ($(this).attr('type') == 'checkbox') {
          val = $(this).prop('checked');
      } else if ($(this).attr('type') == 'radio') {
          val = $(this).prop('checked');
      } else if ($(this).attr('class') == 'select'){
    	  val = $(this).prop('selected');
      }
      else {
          val = $(this).val();
      }
      // 获取单个属性的值,并扩展到result对象里面
      getField(field.split('.'), val, result);
  });
  
  return result;
}

function getField(fieldNames, value, result) {
 if (fieldNames.length > 1) {
     for (var i = 0; i < fieldNames.length - 1; i++) {
         if (result[fieldNames[i]] == undefined) {
             result[fieldNames[i]] = {}
         }
         result = result[fieldNames[i]];
     }
     result[fieldNames[fieldNames.length - 1]] = value;
 } else {
     result[fieldNames[0]] = value;
 }
}



//搜索按钮
paiang.prototype.search=function() {
	
	var queryParams = $('#paiang_tab').datagrid('options').queryParams;
	//重新加载
	$('#paiang_tab').datagrid('load',getQueryCondition());

}
//重新加载数据
paiang.prototype.securityReload=function() {
	//清除表单
	$('#searchForm')[0].reset();
	 $("#searchForm").find('input[type=text],input[type=hidden],textarea[type=textarea]').each(function() {
		 if($(this).attr('type') == 'textarea'){
			 //清除 textarea
			 $(this).text('');
		 }else if($(this).attr('type') == 'hidden'){
			 $(this).val('');
		 }
	});
	//重新加载
	//$('#paiang_tab').datagrid('reload');
	paiang.initDataGridTable();
	
}


//编辑 点击事件
/* 回显展示修改 */
 function btn_edit() {
                var rows = $('#paiang_tab').datagrid('getSelections'); //返回所有被选中的行，当没有记录被选中的时候将返回一个空数组。
                
                if(rows.length!=1){
                	layer.msg('请选择有且仅有一行数据操作！',{icon:2,time:1000});
                	return;
                }
                
                var partNo =rows[0].partNo; //获取属性值
                $.ajax({
                    type: "POST",
                    url: "/data/selectpaiang",
                    data:{partNo:partNo},
                    success: function (json) {  //返回的参数必须是total和rows，total返回数据集总个数，rows返回table的json格式
                        var info = json.rows; //集合数据
                        console.log(info[0]);//取出第一条
                        resetForm(info[0]); //赋值
                        //easyUi js实现 表单赋值
                       // $('#addOrUpdateForm').form('load',info[0]);
                        //展示弹框
                        $("#addOrUpdateModal").modal('show');
                        //修改抬头
                        $("#addOrUpdateModal").find(".modal-dialog .modal-content .modal-header h4.modal-title").html("更新数据");
                        
                        //更新保存	
                        bindSaveInfoEvent("/data/addOrUpdateData");

                    },
                    error: $.alert.ajaxError
                });
     };


//回显赋值
function resetForm(info) {
	//循环
  $("#addOrUpdateModal form input,#addOrUpdateModal form select,#addOrUpdateModal form textarea").each(function () {
      var $this = $(this);
      clearText($this, this.type, info);
  });
}

function clearText($this, type, info){
  var $div = $this.parents(".item");  
  $div.find("label.error").remove(); //移除提示样式
  
  if (info) {
      var thisName = $this.attr("name");
      var thisValue = info[thisName];
      if (type == 'radio') {
          $this.iCheck(((thisValue && 1 == $this.val()) || (!thisValue && 0 == $this.val())) ? 'check' : 'uncheck')
      } else if (type.startsWith('select')) {
          if(thisValue == 'true' || thisValue == true) {
              thisValue = 1;
          } else if(thisValue == 'false' || thisValue == false) {
              thisValue = 0;
          }
          $this.val(thisValue);
      } else {
          $this.val(thisValue);
      }
  } else {
      if (type === 'radio' || type === 'checkbox') {
          $this.iCheck('uncheck');
      }else{
          $this.val('');
      }
  }
}



//编写的表单验证程序　
function validateForm(formID) {
	
	jQuery.validator.addMethod("regex", //addMethod第1个参数:方法名称
			function(value,element,params){//addMethod第2个参数:验证方法，参数（被验证元素的值，被验证元素，参数）
			var exp = new RegExp(params);//实例化正则对象，参数为传入的正则表达式
			return exp.test(value);         //测试是否匹配
			},"格式错误");//addMethod第3个参数:默认错误信息
	
	jQuery.validator.addMethod("checkEnter", function (value, element) {

		    var pattern = new RegExp("[`~!@#$^&*=|{}':;',\\[\\]<>《》/?~！@#￥……&*|{}【】‘；：”“'。，、？' ']");
		    var reg = /^([0-9]+)$/;//全部为数字

		    if(pattern.test(value)){
		            return false;
		     }else if(value.indexOf(" ") != -1){ //存在空格
		            return false;
		     }else{
		            return true;
		     }

		}, "不能输入特殊字符!");
	
	jQuery.validator.addMethod("checkSpace", function (value, element) {
			    if(value.indexOf(" ") != -1){ //存在空格
				            return false;
				     }else{
					            return true;
		       }
		
	}, "不能存在空格字符!");
	
    return $("#"+formID).validate({
  	 // errorElement:'div', //设置标签 替换默认label 
  	  //errorClass:'alert', //设置提示样式名称 可以自定义错误提示的样式
         rules: {
             partNo: {
                 required: true
             },
             commodityCoding: {
          	   required: true,
          	   digits:true
             },
             commodityName: {
          	   required: true
             }
             
         },
         messages: {
      	   partNo: {
      		   required:"料号不能为空"
             },
             commodityCoding: {
          	   required:"商品编号不能为空",
          	   digits:"商品编号必须是整数数字"
             },
	           commodityName: {
	        	   required:"商品名称不能为空"
	          }
         }
     }).form();
 }

	

$("#btn_delete_ids").click(function (){
	paiang.btn_delete_ids();
}); 
		
		
//保存被修改的行
paiang.prototype.save = function(){
	$("#paiang_tab").datagrid('endEdit', editRow);
	
	//使用JSON序列化datarow对象，发送到后台。
//    var rows = $("#paiang_tab").datagrid('getChanges');
	var data=$('#paiang_tab').datagrid("getData").rows; // 获取所有数据

    var rowstr = JSON.stringify(data);
    
   //保存数据库
	$.ajax({
		url : "/labelPrint/paiang/update",
		type : 'POST',
		dataType:"json",      
        contentType:"application/json",
		data:rowstr,
		success : function(result) {
			// 更新完成后，刷新当前行
			//$("#dg1").datagrid('refreshRow', rowIndex);
			if("200"==result.status){
				//重新加载数据
				$("#paiang_tab").datagrid('loadData',result.data);
				
				//layer.msg(result.message,{icon:1,time:1000});
			}else{
				layer.msg(result.message,{icon:2,time:1000});
			}
			
		}
	});
	
}
//撤销被修改的行
paiang.prototype.rejectChange = function(){
	editRow = undefined;
    $("#paiang_tab").datagrid('rejectChanges');
    $("#paiang_tab").datagrid('unselectAll');
}

      
 //打印按钮事件 打印权限判断 获取办公室区域信息
paiang.prototype.tableprint = function() {
  		var rows = $('#paiang_tab').datagrid('getSelections');// 获取所有选中行的数据
  		// 查询是否是第一次登录
  		$.ajax({
  			url : "/labelPrint/client/isFrist?opid="+opid, //查询打印办公室
  			type : 'POST',
  			async:false,
  			success : function(result) {
  					if (result=="false") {// 第一次登录 还未获取办公司信息
  						// 获取用户选择的区域
  						if (rows.length > 0) {
  							$.ajax({
  								url : "/labelPrint/client/getPrint",
  								type : 'POST',
  								success : function(result) {
  									printAuth = eval('(' + result + ')');
  									if (printAuth.length==0) {
  										alert("你没有该功能权限，请先申请权限");
  										return
  									}
  									if (printAuth[0].length > 1) {// 打印标签模版不止一个
  										$("#dyan").css("display", "block");// 显示打印按钮
  										$("#dcan").css("display", "none");// 隐藏导出按钮
  										$("#xgmr").css("display", "none");// 隐藏按钮
  										paiang.initSelectRegion();
  										$("#xzdy").css("display", "block");
  										$("#ssdy").css("display", "block");
  										// $("#bqys").css("display", "block");
  										$("#selectbq").children().remove();// 清理缓存
  										$("#selectbq").append('<option selected="selected">请选择标签样式</option>');
  										for (var i = 0; i < printAuth[0].length; i++) { //追加标签样式
  											$("#selectbq").append('<option value =' + printAuth[0][i].Reportid + ',' + printAuth[0][i].Reportname + '>' + printAuth[0][i].Reportname + '</option>');
  										}
  									} else {
  										$("#dyan").css("display", "block");// 显示打印按钮
  										$("#dcan").css("display", "none");// 隐藏导出按钮
  										$("#xgmr").css("display", "none");// 隐藏按钮
  										
  										$("#xzdy").css("display", "block");
  										$("#ssdy").css("display", "block");
  										paiang.initSelectRegion();
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
  					if (localStorage.getItem("ls")!=null) { //临时办公室
  						paiang.printLabelSendClient("", localStorage.getItem("ls"));
  					}else {
  						paiang.printLabelSendClient("", mr[0].office_id);
  					}
  				}
  			}
  		});
  	}

    //点击打印按钮事件
  	//初始化标签打印发送客户端 report打印人 
 paiang.prototype.printLabelSendClient=function(report,region) {
	 var rows = $('#paiang_tab').datagrid('getSelections'); //获取被选中的数据
	 if(rows.length<1){
     	layer.msg('请至少选择一行打印数据！',{icon:2,time:1000});
     	return;
     }
	 $.messager.confirm("操作提示", "您确定要执行打印办公室:[ "+$("#quyu").text()+" ]打印操作吗？", function (flag) {
         if (flag) {
        	 var url = "/labelPrint/client/printLabelSendClient"; //发送打印客户端
       		 $.ajax({
       			url : url,
       			type : 'POST',
       			data : {
       				labels : JSON.stringify(rows), //置为JSON字符串
       				regions : region,
    				businessType : type, //默认mechine
    				report : report //打印人
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
    					$('#region').dialog("close");
    				} catch (e) {
    					console.info("面板关闭失败");
    				}
       			},
       		    //网络请求失败
   	            error: $.tool.ajaxError
       		});
         }
         else {
             alert("取消打印");
         }
     });
  		
  	}

 /**
	 * 修改打印配置的信息
	 */
paiang.prototype.alertprint = function() {
	$.ajax({
			url : "/labelPrint/client/getPrint",
			type : 'POST',
			success : function(result) {
				printAuth = eval('(' + result + ')');
				if (printAuth.length==0) {
					alert("你没有该功能权限，请先申请权限");
					return
				}
				$("#xgmr").css("display", "block");
				$("#dcan").css("display", "none");// 隐藏导出按钮
				$("#dyan").css("display", "none");// 显示打印按钮
				
				
				$("#xzdy").css("display", "block");
				$("#ssdy").css("display", "block");
				paiang.initSelectRegion(true);
			}
		});
}
 
/**
 * 弹框修改打印配置 初始化 打印 按钮需要的选择控件
 */
paiang.prototype.initSelectRegion= function(isRadio) {
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
			url : "/labelPrint/client/printLabel?opid="+opid,
			onBeforeSelect : function(node) {
				return false
			},
			onClick : function(node) {
				if ($('#cc').tree('isLeaf', node.target)) {// 判断是否是叶子节点
					$('#cc').combotree('setValue', node.pname + "/" + node.text);
					$('#code').val(node.parent_code + "/" + node.region_code); //子节点数据设置 办公室区域
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
	
/**
 * 更新按钮事件修改 办公室
 */	
function updateRegion() {
		var bgs = $("input[name='bgs']:checked").val();
		$("input[name='bgs']:checked").prop('checked', false);
		
		var t = $("#code").val(); //获取下拉菜单数据 chengdu/jichang
		if (t!=""&&undefined!=bgs) {
			if (bgs=="ls") { //临时办公室
				if (typeof(Storage) !== "undefined") {
				    localStorage.setItem("ls", t); //设置办公司  chengdu/jichang
					$('#region').dialog("close"); //关闭弹框
					$("#code").val("");
					setName(); //修改办公室
				} else {
					alert("你的浏览器不兼容系统，建议更换谷歌浏览器");
				}
			}else if (bgs="mr") { //默认办公司
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

//修改办公室
function setName(){
		$.ajax({
			url : "/labelPrint/client/getThisRegion?opid="+opid+"&code="+localStorage.getItem('ls'),
			type : 'POST',
			success : function(result) {
				var	rt = eval("("+result+")");
				$("#quyu").text(rt.parent_name+'/'+rt.region_name);
			}
		});
}
	

//设置时间插件
var buttons = $.extend([], $.fn.datetimebox.defaults.buttons);
$('#start_time').datetimebox({  
	width:115,
    required : false,  
    editable:false,
    buttons:buttons,
    onShowPanel:function(){  
        $(this).datetimebox("spinner").timespinner("setValue","00:00:00");  
    },
    onChange: function(newValue, oldValue){
    }  
});
//设置时间插件
$('#end_time').datetimebox({
	width:115,
    required : false,  
    editable:false,
    buttons:buttons,
    onShowPanel:function(){
        $(this).datetimebox("spinner").timespinner("setValue","23:59:59");  
    },
    onChange: function(newValue, oldValue){
    }   
});

var toolbar ={
			tools:[ {
				 text : '查询',
				 iconCls : 'icon-search',
				 handler : function() {
						   paiang.search();
					}
				},
				'-',{
					text : '打印',
					 iconCls : 'icon-print',
					 handler : function() {
							   paiang.printLabelSendClient(); //发送打印数据
						}
				}
				,'-',{// 在dategrid表单的头部添加按钮
				text : "添加",
				iconCls : "icon-add",
				handler : function() {
					if (editFlag != undefined) {
						$("#paiang_tab").datagrid('endEdit', editFlag);// 结束编辑，传入之前编辑的行
					}
					if (editFlag == undefined) {// 防止同时打开过多添加行
						$("#paiang_tab").datagrid('insertRow', {// 在指定行添加数据，appendRow是在最后一行添加数据
							index : 0, // 行数从0开始计算
							row : {
								partNo : '',
								commodityCoding : '',
								commodityName : ''
							}
						});
						$("#paiang_tab").datagrid('beginEdit', 0);// 开启编辑并传入要编辑的行
						editFlag = 0;
					}
				}
				},
				  '-',
						{// '-'就是在两个按钮的中间加一个竖线分割，看着舒服
							text : "删除",
							iconCls : "icon-remove",
							handler : function() {
								paiang.btn_delete_ids();
							}
						},
						'-',
						{
							text : "修改",
							iconCls : "icon-edit",
							handler : function() {
								// 选中一行进行编辑
								var rows = $("#paiang_tab").datagrid('getSelections');
								if (rows.length == 1) {// 选中一行的话触发事件
									if (editFlag != undefined) {
										$("#paiang_tab").datagrid('endEdit', editFlag);// 结束编辑，传入之前编辑的行
									}
									if (editFlag == undefined) {
										var index = $("#paiang_tab").datagrid('getRowIndex',rows[0]);//获取选定行的索引
										$("#paiang_tab").datagrid('beginEdit', index);//开启编辑并传入要编辑的行
										editFlag = index;
										 $("#paiang_tab").datagrid('unselectAll');
									}
								}else{
					                	layer.msg('请选择有且仅有一行数据操作！',{icon:2,time:1000});
					                	return;
								}
							}
						},
						'-',
						{
							text : "保存",
							iconCls : "icon-save",
							handler : function() {
								$("#paiang_tab").datagrid('endEdit', editFlag);
								layer.msg('保存成功！', {icon: 1,time: 2000 }); 
								  //如果调用acceptChanges(),使用getChanges()则获取不到编辑和新增的数据。
				                //使用JSON序列化datarow对象，发送到后台。
				               /* var rows = $("#paiang_tab").datagrid('getChanges');
				                
				                //循环发送数据
				               for(var i=0;i<rows.length;i++){
				            	   saveData(rows[i]);
				               } */
				                
								
							}
						},
						'-',
						{
							text : "撤销",
							iconCls : "icon-redo",
							handler : function() {
								editFlag = undefined;
								$("#paiang_tab").datagrid('rejectChanges');
								$("#paiang_tab").datagrid('unselectAll');
							}
						}								
						]
	} ;

//派昂展示的字段
var paiangcolumns={
		columns:[[{
			    field: 'id',
			    title: '复选框',
			    checkbox: true,
			    visible: true
			},{
				field: 'MBLNo',
			    sortable:true,
			    order:'asc',
			    title: '运单号',
			    width:6
			},
			 {
			    field: 'hawb',
			    title: '分单',
			    width:6
			
			},{
				field:'template_name',
				title:'当前模板',
				width:10,
				editor:{
			        type:'combobox',
			        options:{
			            valueField:'template_name',
			            required:true
			        }
			    },
			    styler: function(value, row, index) {
			    	if (value == "未配置打印模板") {
			    		return 'color:red;';
			    	}
			    },
			   formatter : function(value, row, index) {
					return value;
			   }
				
			},{
				field:'EDeparture',
				title:'发货日期',
				width:10

			},{
				field:'takeCargoNo',
				title:'交货凭证号',
				width:10
			       
			},{
				field:'RecCustomerName',
				title: '收货人',
				width:10,
			        editor:{
			            type:'text',
			            options:{
			                valueField:'RecCustomerName',
			                required:true
			            }
			        }
			},{
				field:'SendAddress',
				title:'发货人',
				width:10
			       
			},{
				field:'TotalAcount',
				title:'总件数',
				width:4
			},{
				field:'packages',
				title:'件数',
				width:3,
				editor:{
		            type:'text',
		            options:{
		                valueField:'packages',
		                required:true
		            }
		        }
			},{
				field:'SerialNo',
				title:'打印序号',
				width:12
			}
			]]
	};


/**
 * 录入人下拉选择
 */
$('#opid_name11').combogrid({    
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

var paiang =new paiang();

$(document).ready(function(){
	console.log("派昂页面初始化");
	//初始化表格
    paiang.initDataGridTable();
});
