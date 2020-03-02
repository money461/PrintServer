function paiang(){
	
}

//定义全局 表格对象
var editRow = undefined;
var paiang_tab;
var type ="medicine"; //派昂标签类型
var opid =null; //获取当前用户opid //全局变量引用

var thisRegionid=null; //打印当前现中的办公室id

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
	
		setRegionByOpid(); //初始化打印办公室
});



//修改办公室
function setRegionByOpid(){
	//初始化页面 获取打印区域 opid 当前操作号   regionid办公司id
	$.ajax({
		url : "/labelPrint/client/getDefaultBindRegionByOpid",
		type : 'POST',
		data: {opid:opid,regionid:storage.get('regionid')},//chengdu/jichang id=2
		async:false,
		success : function(result) {
			if($.common.isEmpty(result)){
				return;
			}else{
				$("#quyu").text(result.parent_name+'/'+result.region_name); //修改区域标签
				var bgs = storage.get('bgs');
				bgs = bgs!=null?bgs:'mr'; 
				storage.set('bgs',bgs);
				storage.set('regionid',result.region_id); //初始化获取用户绑定的办公室id
			}
		}
	});
}
	

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
		    	 
		    },
		    //当用户双击一个单元格时触发。
		    onDblClickCell:function(rowIndex, field, value){
		    	
				if (field=="RecCustomerName"||field=="template_name"||field=="packages") {
					
					if (editRow !=rowIndex) {
						$(this).datagrid('endEdit', editRow);
					}
			        var rows = $(this).datagrid('getRows');// 返回当前页的行。
			        var row = rows[rowIndex];// 根据index获得其中一行。
					
					$(this).datagrid('beginEdit', rowIndex); //开启编辑
					$(this).datagrid('selectRow',rowIndex); //选中该行
					$("#rejectButton").attr("disabled",false); //启用撤销
					$("#saveButton").attr("disabled",false); //启用保存
					editRow = rowIndex; //记录当前编辑行索引
					
					var ed = $(this).datagrid('getEditor', {index:rowIndex,field:'template_name'}); //获取指定的编辑器， options 参数包含两个属性：index：行的索引。field：字段名。
					
					$(ed.target[0]).combobox({
						url:"/labelPrint/label/getUserAuthtemplate",
						method:'GET',
						textField:'template_name',
					    valueField:'template_name',
					    onSelect: function(rec){
					    	row.reserve3=rec.id;//修改label外键id
					    	row.template_id=rec.template_id;//修改模板id
					    }
						
					});
					$(ed.target[0]).combobox("select",row.template_name); //设置默认的初始值
				
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
				//更新完成后，刷新当前行
				$("#paiang_tab").datagrid('refreshRow', rowIndex);
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
	 if(paiang.checksave()){
		 var queryParams = $('#paiang_tab').datagrid('options').queryParams;
		 //重新加载
		 $('#paiang_tab').datagrid('load',getQueryCondition());
		 
	 }

}
//重新加载数据刷新表单
paiang.prototype.securityReload=function() {
	 if(paiang.checksave()){
		 //清除表单
//	$('#searchForm')[0].reset();
		 $("#searchForm").form("clear");
		 $("#searchForm").find('input[type=text],input[type=hidden],textarea[type=textarea]').each(function() {
			 if($(this).attr('type') == 'textarea'){
				 //清除 textarea
				 $(this).text('');
			 }else if($(this).attr('type') == 'hidden'){
				 $(this).val('');
			 }
		 });
		 $("#searchForm").find("select[name='status']").val("0").trigger('change'); //重置下拉
		 //重新加载
		 //$('#paiang_tab').datagrid('reload');
		 paiang.initDataGridTable();
		 
	 }
	 
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
	
  $.messager.confirm("操作提示", "您确定要执行保存操作吗？", function (data) {
         if (data) {
				//点保存后无法点击撤销
				$("#rejectButton").attr("disabled",true); //禁用撤销
				$("#paiang_tab").datagrid('endEdit', editRow); //结束编辑
	    		 editRow = undefined; //重置编辑行
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
						//$("#paiang_tab").datagrid('refreshRow', rowIndex);
						if("200"==result.status){
							//重新加载数据
							$("#paiang_tab").datagrid('loadData',result.data);
							
							//layer.msg(result.message,{icon:1,time:1000});
						}else{
							layer.msg(result.message,{icon:2,time:1000});
						}
						
					}
				});
         }else{
        	 layer.msg("已取消保存");
        }
   });

}
//校验是否需要保存
paiang.prototype.checksave= function(){
	if(editRow != undefined){
		layer.msg("请确认是否保存编辑修改的数据?",{icon:2,time:1000});
		return false;
	}else{
		return true;
	}
	
	
}
//撤销被修改的行
paiang.prototype.rejectChange = function(){
	editRow = undefined;
    $("#paiang_tab").datagrid('rejectChanges');
    $("#paiang_tab").datagrid('unselectAll');
    $("#saveButton").attr("disabled",true); //禁用保存
}

      
 //打印按钮事件 打印权限判断 获取办公室区域信息
paiang.prototype.tableprint = function() {
	
	 if(paiang.checksave()){
		 var rows = $('#paiang_tab').datagrid('getSelections');// 获取所有选中行的数据
		 // 获取用户选择的区域
		 if (rows.length ==0) {
			 layer.msg("请至少选择一条打印标签！",{icon:2,time:2000});
			 return;
		 }
		 
		 //判断是否需要打印提示
		 var xunwenprint = storage.get('xunwenprint'); //********全局询问打印设置
		 xunwenprint = xunwenprint!=null?xunwenprint:'yes';
		 var regionid = storage.get('regionid'); //打印办公室id
		 
		 if($.common.equalsIgnoreCase('yes',xunwenprint) || $.common.isEmpty(regionid)){
			 //弹出对话框
			 $("#dyan").css("display", "block");// 显示打印按钮
			 paiang.initdialog('打印提示框'); //显示打印会话框 不展示默认办公室选项
		 }else{
			 //进入打印程序
			 paiang.printLabelSendClient();
		 }
		 
	 }
	
}

    //点击打印按钮事件
  	//初始化标签打印发送客户端 report打印人 
 paiang.prototype.printLabelSendClient = function(regionid) {
	  
	  var regionid_storage = storage.get('regionid'); //打印办公室id
	  
	  if($.common.isEmpty(regionid)){
		  regionid = regionid_storage;
	  }
	  
	  if($.common.isEmpty(regionid)){
		  layer.msg("请选择打印办公室",{icon:2,time:1000});
		  return;
	  }
	  
	    var url = "/labelPrint/client/printLabelSendClient";
	  
	       var mqaddress = "vpnnet"; //默认外网
			var rows = $('#paiang_tab').datagrid('getSelections');
			$.ajax({
				url : url,
				type : 'POST',
				data : {
					labels : JSON.stringify(rows),
					regionid : regionid, //chengdu/jichang id=2
					businessType : type,
					mqaddress: mqaddress //内网还是外网
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
	   					dialogClose();//关闭会话
	   					//刷新表格
	   					$('#paiang_tab').datagrid('reload');
	   				}else{
	   					layer.msg(result.message,{icon:2,time:1000});
	   				}
					
				}
			});
	  
	//点击单选按钮radio后触发，即，我们  选择默认办公室/临时办公室时，触发一个事件，弹出选中的值
	  $("#mqaddress input[name=mqaddress]").click(function(){
	      mqaddress = $(this).val();
	  });
	  
	}

 /**
	 * 修改打印配置的信息
	 */
paiang.prototype.alertprint = function() {
	$("#xunwen").css("display", "block");//展示询问打印
	$("#bgs").css("display", "block");// 展示默认办公室radio
	$("#xgmr").css("display", "block"); //显示修改默认
	paiang.initdialog('配置打印/办公室信息对话框'); //修改区域 展示默认radio
}
 

/**
 * 初始化 打印对话框 按钮需要的选择控件
 */
paiang.prototype.initdialog = function(title) {
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
				$("#bgs").css("display", "none");//隐藏办公室radio
				$("#xunwen").css("display", "none");//隐藏询问设置
				
				thisRegionid=null;//清除当前选中的地址
			} catch (e) {
				console.info("初始化单选框失败");
			}
		},
		onBeforeOpen:function(){ //对话框初始化
			paiang.initTree(); //初始化办公室树
			var xunwenprint = storage.get('xunwenprint'); //********全局询问打印设置
			xunwenprint = xunwenprint!=null?xunwenprint:'yes';
			$("input:radio[value="+xunwenprint+"]").prop('checked','true'); //设置当前是询问还是不询问
			
			//加载当前设置
			var bgs=storage.get('bgs');
			 //$("input[name='bgs']:checked").prop('checked', false); //radio不被选中 不清楚当前是临时还是默认
			bgs = bgs!=null?bgs:'ls';
			 $("input:radio[value="+bgs+"]").prop('checked','true'); //设置当前是临时还是默认
			 
			 var vpn=storage.get('vpn');
			 vpn = vpn!=null?vpn:'vpnnet';
			 $("input:radio[value="+vpn+"]").prop('checked','true'); //设置当前是vpn/外网
			
		}
	});
	
}

// 初始化办公室下拉树
paiang.prototype.initTree = function(){
	$('#cc').combotree({
		required : true,
		url : "/labelPrint/client/getTreeRegionByOpid",
		method : 'POST',
		onBeforeSelect : function(node) {
			return false
		},
		onLoadSuccess : function(node,data) {
			//combotree 设置默认值
			var regionid = storage.get('regionid');
			if($.common.isNotEmpty(regionid)){
				 var tree = $('#cc').combotree('tree');
				var nodedata = tree.tree('find',regionid);
				var text = nodedata.pname + "/" + nodedata.text;
				$('#cc').combotree('setValue',text); 
				tree.tree('select', nodedata.target); 
			}else{
				$('#cc').combotree('setValue', '指定打印办公室位置'); 
			}
		},
		onClick : function(node) {
			if ($('#cc').tree('isLeaf', node.target)) {// 判断是否是叶子节点
				//子节点即选择该节点
				$('#cc').combotree('setValue', node.pname + "/" + node.text); //设置值
//				storage.set("regionid",node.id); //设置区域办公室id
				thisRegionid=node.id;
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

//关闭办公室选择弹框
function dialogClose(){
	try {
		$('#region').dialog("close");  
	} catch (e) {
		console.info("面板关闭失败");
	}
}

//弹出会话框后点击打印
function dialogPrint() {
	   //立即执行打印
	   paiang.printLabelSendClient(thisRegionid);
}

/**
 * 弹出会话框后点击更新或者添加用户办公室信息
 */	
function dialogUpdateOrAddRegion (){
		
	  var bgs = $("input[name='bgs']:checked").val();
		//获取办公室id
		if(bgs=="mr" ){
			if($.common.isEmpty(thisRegionid)){
				thisRegionid = storage.get('regionid');
				if($.common.isEmpty(thisRegionid)){
					layer.msg("选择默认办公室必须选择绑定指定办公室",{icon:2,time:1000});
					return;
				}
			}
		     //默认办公司
				if (typeof(Storage) !== "undefined") {
					$.ajax({
						url : "/labelPrint/client/updateOrAddUserRegion", //修改办公室区域 chengdu/jichang
						type : 'POST',
						data :{regionid:thisRegionid},
					    success : function(result) {
						if (result.status=="200") {
							layer.msg("更改配置成功！",{icon:1,time:1000});
							paiang.dialogRegion();
						}else{
							layer.msg("更改失败，请联系管理员",{icon:2,time:1000});
						}
					}
					});
				} else {
					layer.msg("你的浏览器不兼容系统，建议更换谷歌浏览器",{icon:2,time:1000});
				}
		}else{
			paiang.dialogRegion();
		}
}


paiang.prototype.dialogRegion =function(){
	//获取办公室信息
	var bgs = $("input[name='bgs']:checked").val();
	storage.set('bgs',bgs);
	
	//询问打印
	var xunwenprint = $("input[name='xunwen']:checked").val();
	storage.set('xunwenprint',xunwenprint);
	
	//内网/外网
	var vpn = $("input[name='mqaddress']:checked").val();
	storage.set('vpn',vpn);
	
	if($.common.isNotEmpty(thisRegionid)){
		storage.set('regionid',thisRegionid); //****************设置全局办公室id*************************
	}
	setRegionByOpid(bgs);//修改办公室
	//关闭会话
	$('#region').dialog("close");
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

//删除
paiang.prototype.deletedata = function(){
	 var rows = $('#paiang_tab').datagrid('getSelections'); //获取被选中的数据
	 if(rows.length<1){
     	layer.msg('请至少选择一行打印数据！',{icon:2,time:1000});
     	return;
     }
	 
	 layer.msg('暂不支持删除！',{icon:2,time:1000});
	 return;
}

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
