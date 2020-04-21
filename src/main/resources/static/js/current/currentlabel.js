function currentlabel(){
	
}

//定义全局 表格对象
var editRow = undefined;
var currentlabel_tab;
var opid =null; //获取当前用户opid //全局变量引用
var prefix = ctx + "/currentlabel"; // /labelPrint/
var url = prefix + "/all";

var thisdefaultRegion=null; //打印当前现中的办公室id

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
	

//初始化表格  返回格式 返回的参数必须是total和rows，total返回数据集总个数，rows返回table的json格式
currentlabel.prototype.initDataGridTable=function(){
	//var url = prefix + "/all";
	currentlabel_tab =  $('#currentlabel_tab').datagrid({
			url : null,
			iconCls : 'fa fa-truck',
			method : 'POST',
			pagination : true, //分页
			singleSelect: false, //只允许选择一行
			//checkOnSelect : true,// 如果为false，当用户仅在点击该复选框的时候才会呗选中或取消。
			pageSize : 20,
			striped : true,// 隔行换色
			scrollbarSize : 0,// 去除右侧空白
			fitColumns : true,
			toolbar : '#toolbar', //该工具栏置于对话框的顶部，每个工具栏包含:text, iconCls, disabled, handler等属性。
			showFooter : true,
			pageList : [ 5, 10, 15, 20, 50 ],
			sortName:'updateTime',//定义可以排序的列。
			sortOrder:'desc',
			multiSort:true, //多列排序
			remoteSort: true, //定义是否通过远程服务器对数据排序。
		    queryParams: getQueryCondition(),
			width: 'auto', //默认10
			loadMsg: "正在加载数据...",
			//toolbar : toolbar.tools,
			columns: currentlabelcolumns.columns,
			rowStyler : function(index, row) {
				if (row.printStatus != 0) {//打印状体修改展示颜色
					return 'background-color:#5cb85c';
				}
			},
			loadFilter:function(data){ //数据过滤
					return data;
			},
			//当用户双击一行时触发
			onDblClickRow : function(rowIndex,rowData) { //在用户双击一行的时候触发，参数包括：index：点击的行的索引值，该索引值从0开始。row：对应于点击行的记录。
				
		    },
		  //当用户单击一个单元格时触发。
		    onClickCell:function(index,field,value){
		    	 
		    },
		    //当用户双击一个单元格时触发。
		    onDblClickCell:function(rowIndex, field, value){
		    	
		    	var rows = $(this).datagrid('getRows');// 返回当前页的行。
		    	var row = rows[rowIndex];// 根据index获得其中一行。
		    	var status = row.printStatus;
		    	if(0!=status){return};
				if (field=="templateName" || field=="copies") {
					
					if (editRow !=rowIndex) {
						$(this).datagrid('endEdit', editRow);
					}
					
					$(this).datagrid('beginEdit', rowIndex); //开启编辑
					$(this).datagrid('selectRow',rowIndex); //选中该行
					$("#rejectButton").attr("disabled",false); //启用撤销
					$("#saveButton").attr("disabled",false); //启用保存
					editRow = rowIndex; //记录当前编辑行索引
					
					var ed = $(this).datagrid('getEditor', {index:rowIndex,field:'templateName'}); //获取指定的编辑器， options 参数包含两个属性：index：行的索引。field：字段名。
					
					
					$(ed.target[0]).combobox({
						url:"/labelPrint/label/getUserAuthtemplate?code="+code,
						method:'GET',
						textField:'templateName',
					    valueField:'templateName',
					    onSelect: function(rec){
					    	row.templateId=rec.templateId;//修改模板id
					    	row.templateName = rec.templateName;
					    	row.width=rec.width; //修改宽度
					    	row.height=rec.height; //修改高度
					    }
						
					});
					$(ed.target[0]).combobox("select",row.templateName); //设置默认的初始值
				
				}
			},
	        onLoadSuccess: function (data) {
	         $('#currentlabel_tab').datagrid("showColumn",'id');//显示指定的列。
        	  if (data.total == 0) {
					$('#currentlabel_tab').datagrid('appendRow',{
						showNum: data.msg,
					}).datagrid('mergeCells',{
						index: 0,
						field: 'showNum',
						colspan: 8
					}).datagrid('hideColumn','id');
					
				}else{
					layer.msg('数据已刷新!',{icon:1});
	        	   
	           }
	        	
	        },
	        onLoadError: function () {
	        	layer.msg('警告!数据加载失败!',{icon:2});
	        },
	        onBeforeLoad: function(param){ // 在发出请求数据数据之前触发，如果返回false可终止载入数据操作
				var queryParams = $('#currentlabel_tab').datagrid('options').queryParams; //初始化datatGrid后才可以获取options所有相关数据
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
	        onBeforeEdit:function(index,row){
				updateActions(index);
			},
			/*onUnselect:function(rowIndex, rowData){
				$("#currentlabel_tab").datagrid('endEdit', rowIndex); //未选择该行的时候，结束编辑
			},*/
			// 结束编辑事件
			onAfterEdit:function(rowIndex, rowData, changes){
				//更新完成后，刷新当前行
				$("#currentlabel_tab").datagrid('refreshRow', rowIndex);
			},
			onCancelEdit:function(index,row){
				updateActions(index);
			},
			view: detailview,
	    	detailFormatter:function(index,row){
	    		//return '<div class="ddv" style="padding:5px 0"></div>';
	    		var columns = $(this).datagrid('options').columns[0];
	    		return detailFormatter(index,row,columns);
	    	},
	    	/*onExpandRow: function(index,row){
	    		var ddv = $(this).datagrid('getRowDetail',index).find('div.ddv');
	    		ddv.panel({
	    			border:false,
	    			cache:false,
	    			href:url+'?id='+row.id,
	    			onLoad:function(){
	    				$('#currentlabel_tab').datagrid('fixDetailRowHeight',index);
	    			}
	    		});
	    		$('#currentlabel_tab').datagrid('fixDetailRowHeight',index);
	    	},*/
	       
       
	});
	
	//使用ajax方式加载数据
	/*$.ajax({
        type: 'POST',
        dataType: 'JSON',
        url: "/labelPrint/currentlabel/search",
        timeout: 20000,
        //async: false,     // 同步
        data: getQueryCondition(),
        success: function (result) {
            console.info("获取数据成功，返回的数据为：↓");
            console.info(result);
            if (result.status=="200") {
                console.info(result.data);
                $("#currentlabel_tab").datagrid('loadData', eval("("+result.data+")")); //JSON字符串转换为对象
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

function updateActions(index){
	$('#currentlabel_tab').datagrid('updateRow',{
		index: index,
		row:{}
	});
}


//封装查询参数
function getQueryCondition(){
   //var formParm = getEntity('#searchForm'); //获取搜索表单form
   var formParm  = $.common.formToJSON("searchForm");
   var showNum = formParm.showNum;
   showNum = $.trim(showNum) == undefined?"":$.trim(showNum);
   showNum = showNum.replace(/\s+/g, ',');
   formParm.showNum=showNum;
   var is_print = formParm.printStatus;
   if('0'!=is_print){
		$("#printButton").attr("disabled",true); //禁用打印
   }else{
	   $("#printButton").attr("disabled",false); //解除
   }
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
currentlabel.prototype.search=function() {
	 if(currentlabel.checksave()){
//		 var queryParams = $('#currentlabel_tab').datagrid('options').queryParams;
		 
		 $(currentlabel_tab).datagrid('options').url=url;
//		 $(currentlabel_tab).datagrid('reload'); //加载网络数据
		 //重新加载
		 $(currentlabel_tab).datagrid('load',getQueryCondition());
		 
	 }

}
//重新加载数据刷新表单
currentlabel.prototype.securityReload=function() {
	 if(currentlabel.checksave()){
		 //清除表单
	    $('#searchForm')[0].reset();
		 $("#searchForm").form("clear");
		 $("#searchForm").find('input[type=text],input[type=hidden],textarea[type=textarea]').each(function() {
			 if($(this).attr('type') == 'textarea'){
				 //清除 textarea
				 $(this).text('');
			 }else if($(this).attr('type') == 'hidden'){
				 $(this).val('');
			 }
		 });
		 $("#searchForm").find("select[name='printStatus']").val("0").trigger('change'); //重置下拉
		 //重新加载
		 //$('#currentlabel_tab').datagrid('reload');
		 $("#printButton").attr("disabled",false); //解除
		 
		 $(currentlabel_tab).datagrid('options').url=url; //设置url
		 
		 currentlabel.initDate(); //时间初始值
		 //重新加载
		 $(currentlabel_tab).datagrid('load',getQueryCondition()); //加载网络数据
		 
	 }
	 
}


//编辑 点击事件
/* 回显展示修改 */
 function btn_edit() {
                var rows = $('#currentlabel_tab').datagrid('getSelections'); //返回所有被选中的行，当没有记录被选中的时候将返回一个空数组。
                
                if(rows.length!=1){
                	layer.msg('请选择有且仅有一行数据操作！',{icon:2,time:1000});
                	return;
                }
                
                var partNo =rows[0].partNo; //获取属性值
                $.ajax({
                    type: "POST",
                    url: "/data/selectcurrentlabel",
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
	currentlabel.btn_delete_ids();
}); 
		
		
//保存被修改的行
currentlabel.prototype.save = function(){
	
  $.messager.confirm("操作提示", "您确定要执行保存操作吗？", function (data) {
         if (data) {
				//点保存后无法点击撤销
				$("#rejectButton").attr("disabled",true); //禁用撤销
				$("#currentlabel_tab").datagrid('endEdit', editRow); //结束编辑
	    		 editRow = undefined; //重置编辑行
				//使用JSON序列化datarow对象，发送到后台。
			    var rows = $("#currentlabel_tab").datagrid('getChanges');
				//var data=$('#currentlabel_tab').datagrid("getData").rows; // 获取所有数据
			
			    var rowstr = JSON.stringify(rows);
			    
			   //保存数据库
				$.ajax({
					url : prefix+"/update",
					type : 'POST',
					dataType:"json",      
			        contentType:"application/json",
					data:rowstr,
					success : function(result) {
						if("200"==result.status){
							// 更新完成后，刷新当前行
							//$("#currentlabel_tab").datagrid('refreshRow', rowIndex);
							$('#currentlabel_tab').datagrid('getChanges',' updated');
							//重新加载数据
							//$("#currentlabel_tab").datagrid('loadData',result.data);
							
							layer.msg(result.message,{icon:1,time:1000});
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
currentlabel.prototype.checksave= function(){
	if(editRow != undefined){
		layer.msg("请确认是否保存编辑修改的数据?",{icon:2,time:1000});
		return false;
	}else{
		return true;
	}
	
	
}
//撤销被修改的行
currentlabel.prototype.rejectChange = function(){
	editRow = undefined;
    $("#currentlabel_tab").datagrid('rejectChanges');
    $("#currentlabel_tab").datagrid('unselectAll');
    $("#saveButton").attr("disabled",true); //禁用保存
}

      
 //打印按钮事件 打印权限判断 获取办公室区域信息
currentlabel.prototype.tableprint = function() {
	
	 if(currentlabel.checksave()){
		 var rows = $('#currentlabel_tab').datagrid('getSelections');// 获取所有选中行的数据
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
			 currentlabel.initdialog('打印提示框'); //显示打印会话框 不展示默认办公室选项
		 }else{
			 //进入打印程序
			 currentlabel.printLabelSendClient();
		 }
		 
	 }
	
}

    //点击打印按钮事件
  	//初始化标签打印发送客户端 report打印人 
 currentlabel.prototype.printLabelSendClient = function(defaultRegion) {
	  
	  var defaultRegion_storage = storage.get('defaultRegion'); //打印办公室id
	  
	  if($.common.isEmpty(defaultRegion)){
		  defaultRegion = defaultRegion_storage;
	  }
	  
	  if($.common.isEmpty(defaultRegion)){
		  layer.msg("请选择打印办公室",{icon:2,time:1000});
		  return;
	  }
	   var vpn=storage.get('vpn');//获取内网外网
	    var url = prefix + "/printCurrentLabelSendClient";
	  
			var rows = $('#currentlabel_tab').datagrid('getSelections');
			window.parent.$.modal.loading("打印数据发送中，请稍后...");
			$.ajax({
				url : url,
				type : 'POST',
				dataType : 'json',
				data : {
					labels :   JSON.stringify(rows),
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
	   					$('#currentlabel_tab').datagrid('reload');
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

 /**
	 * 修改打印配置的信息
	 */
currentlabel.prototype.alertprint = function() {
	$("#xunwen").css("display", "block");//展示询问打印
	$("#bgs").css("display", "block");// 展示默认办公室radio
	$("#xgmr").css("display", "block"); //显示修改默认
	$("#mqaddress").css("display", "block"); //显示修改内网外网
	currentlabel.initdialog('配置打印/办公室信息对话框'); //修改区域 展示默认radio
}
 

/**
 * 初始化 打印对话框 按钮需要的选择控件
 */
currentlabel.prototype.initdialog = function(title) {
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
				$("#mqaddress").css("display", "none"); //显示修改内网外网
				$("#xunwen").css("display", "none");//隐藏询问设置
				
				thisdefaultRegion=null;//清除当前选中的地址
			} catch (e) {
				console.info("初始化单选框失败");
			}
		},
		onBeforeOpen:function(){ //对话框初始化
			currentlabel.initTree(); //初始化办公室树
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
currentlabel.prototype.initTree = function(){
	$('#cc').combotree({
		required : true,
		url : "/labelPrint/region/getTreeRegionByOpid",
		method : 'POST',
		onBeforeSelect : function(node) {
			return false
		},
		onLoadSuccess : function(node,data) {
			//combotree 设置默认值
			var defaultRegion = storage.get('defaultRegion');
			if($.common.isNotEmpty(defaultRegion)){
				 var tree = $('#cc').combotree('tree');
				var nodedata = tree.tree('find',defaultRegion);
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
               //storage.set("defaultRegion",node.id); //设置区域办公室id
				//thisdefaultRegion=node.regionCode;
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
	//获取会话框中的网路选项
	//内网/外网
	var vpn = $("input[name='mqaddress']:checked").val();
    storage.set('vpn',vpn);//获取内网外网
   //立即执行打印
   currentlabel.printLabelSendClient(thisdefaultRegion);
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
							currentlabel.dialogRegion();
						}else{
							layer.msg("更改失败，请联系管理员",{icon:2,time:1000});
						}
					}
					});
				} else {
					layer.msg("你的浏览器不兼容系统，建议更换谷歌浏览器",{icon:2,time:1000});
				}
		}else{
			currentlabel.dialogRegion();
		}
}


currentlabel.prototype.dialogRegion =function(){
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
 * 初始化打印模板下拉
 * @param callback
 */
currentlabel.prototype.initPrintTemplate = function(callback){
	var url = "/labelPrint/label/getUserAuthtemplate?code="+code;
	function getInitData(){
	    var dataStore=[];
	    
	    $.ajax({
	        dataType : 'json',
	        type : 'GET',
	        data : {'templateName':null}, //参数
	        url : url,
	        async : false,
	        success: function(data){
	        	 for (var i = 0; i < data.length; i++) {
	        		 var tem={};
	        		 tem.id = data[i].templateId;
	        		 tem.text = data[i].templateName;
	        		 dataStore.push(tem);
	                }
	        }
	     });
	    return dataStore;
	}   
	var initdata = getInitData();
	
	 var TemplateNameSelect2 = $("#templateName").select2({
		 width : 'auto', //宽度自动
		//这里就是解决modal对话框下，搜索输入框不显示的方法
		 dropdownParent: $("#select2-ProductId241"),
		 data:initdata,
		  ajax: {
		    url: url,
		    type: "GET",
		    dataType: 'json',
		    delay: 250,
		    data: function (params) {
                return {
                	templateName: params.term, // 搜索参数
                };
            },
            processResults: function (data, params) {
                for (var i = 0; i < data.length; i++) {
                    data[i].id = data[i].templateId;
                    data[i].text = data[i].templateName;
                }
                return {
                    results: data
                };
            },
            cache: true
		  },
		  placeholder: "请输入或者选择打印模板名称",
          allowClear: true,    //选中之后，可手动点击删除
          escapeMarkup: function (markup) { return markup; }, // 让template的html显示效果，否则输出代码
          minimumInputLength: -1,    //搜索框至少要输入的长度，此处设置后需输入才显示结果
          language: "zh-CN",         //中文
          multiple:false, //是否多选
          closeOnSelect:true,
          tags: true,//允许手动添加
          escapeMarkup: function (markup) { return markup; }, // 自定义格式化防止xss注入
          formatResult: function formatRepo(repo){return repo.text;}, // 函数用来渲染结果
	      formatSelection: function formatRepoSelection(repo){return repo.text;} // 函数用于呈现当前的选择
		});
	 return TemplateNameSelect2;
}

/**
 * 批量修改
 */
currentlabel.prototype.batchAlertTemplate = function(){
	var rows =  $(currentlabel_tab).datagrid('getSelections');// 获取所有选中行的数据
	if(rows.length==0){
		$.modal.alertWarning("请选择需要更新模板的标签数据！");
		return;
	}else{
		$("#batchAlertTemplate").modal('show'); //显示模态框
	}
	$("#batchAlertTemplate").on('show.bs.modal',function(){
		
	});
}

/**
 * 确认修改
 */
function confirmChangesTemplate() {
	var res=$("#templateName").select2("data")[0] ; ////获取选中的模板
	var templateId = res.id;
	if($.common.isEmpty(templateId)){
		$.modal.alertWarning("请选择更新的模板！");
		return;
	}
	
	$.modal.confirm("确定修改打印模板吗？", function() {
		//关闭模态框
		$("#batchAlertTemplate").modal('hide'); //隐藏模态框
		//获取更新的模板
		var templateName = res.text; //获取选中的模板
		var url=prefix+"/update";
		var rows =  $(currentlabel_tab).datagrid('getSelections');// 获取所有选中行的数据
		$.each(rows, function(index, row){ 
			row.templateId=templateId;
			row.templateName=templateName;
	    });
		
		 var rowstr = JSON.stringify(rows);
		 var config={
				url: url,
		        type: "POST",
		        dataType: "json",
		        contentType:"application/json",
		        data: rowstr,
		        async: false, //同步加载
		        beforeSend: function () {
		        	$.modal.loading("正在处理中，请稍后...");
		        },
		        success: function(result) {
		        	$.modal.closeLoading();
		        	if('200'==result.status){
		        		$.modal.msg(result.message);
		        		 $(currentlabel_tab).datagrid('load',getQueryCondition()); //加载网络数据
		        	}else{
		        		 $.modal.msgError(result.message);
		        	}
		        }
		    };
		
	$.ajax(config);
		
	});
	
}

//删除
currentlabel.prototype.deletedata = function(){
	$.messager.confirm("操作提示", "删除无法恢复,您确定要执行删除操作吗？", function (data) {
if (data) {
	 var rows = $('#currentlabel_tab').datagrid('getSelections'); //获取被选中的数据
	 if(rows.length<1){
     	layer.msg('请至少选择一行打印数据！',{icon:2,time:1000});
     	return;
     }
	 
	 var data = JSON.stringify(rows);
	 var config = {
		        url : prefix+"/delete",
				type : 'POST',
				dataType:"json",      
		        contentType:"application/json",
		        data: data,
		        beforeSend: function () {
		        	$.modal.loading("正在处理中，请稍后...");
		        },
		        success: function(result) {
		        	$.modal.closeLoading();
		        	if('200'==result.status){
	                    $.modal.msgSuccess(result.message);
		        	}
		        	$('#currentlabel_tab').datagrid('reload');
		        }
		    };
		    $.ajax(config) //发送请求
	   }else{
		   layer.msg("已经取消删除操作！");
	   }
	});
}
/**
 * 初始化时间
 */
currentlabel.prototype.initDate = function(){
	  
    function formatterDate(date) {
        var day = date.getDate() > 9 ? date.getDate() : "0" + date.getDate();
        var month = (date.getMonth() + 1) > 9 ? (date.getMonth() + 1) : "0" + (date.getMonth() + 1);
        return date.getFullYear() + '-' + month + '-' + day;
    };
     var nowdate =  new Date();
     $(endTime).datebox('setValue',formatterDate(nowdate));	// set datebox value
     nowdate.setDate(nowdate.getDate()-30);
     $(startTime).datebox('setValue',formatterDate(nowdate));	// set datebox value
}

var toolbar ={
			tools:[ {
				 text : '查询',
				 iconCls : 'icon-search',
				 handler : function() {
						   currentlabel.search();
					}
				},
				'-',{
					text : '打印',
					 iconCls : 'icon-print',
					 handler : function() {
							   currentlabel.printLabelSendClient(); //发送打印数据
						}
				}
				,'-',{// 在dategrid表单的头部添加按钮
				text : "添加",
				iconCls : "icon-add",
				handler : function() {
					if (editFlag != undefined) {
						$("#currentlabel_tab").datagrid('endEdit', editFlag);// 结束编辑，传入之前编辑的行
					}
					if (editFlag == undefined) {// 防止同时打开过多添加行
						$("#currentlabel_tab").datagrid('insertRow', {// 在指定行添加数据，appendRow是在最后一行添加数据
							index : 0, // 行数从0开始计算
							row : {
								partNo : '',
								commodityCoding : '',
								commodityName : ''
							}
						});
						$("#currentlabel_tab").datagrid('beginEdit', 0);// 开启编辑并传入要编辑的行
						editFlag = 0;
					}
				}
				},
				  '-',
						{// '-'就是在两个按钮的中间加一个竖线分割，看着舒服
							text : "删除",
							iconCls : "icon-remove",
							handler : function() {
								currentlabel.btn_delete_ids();
							}
						},
						'-',
						{
							text : "修改",
							iconCls : "icon-edit",
							handler : function() {
								// 选中一行进行编辑
								var rows = $("#currentlabel_tab").datagrid('getSelections');
								if (rows.length == 1) {// 选中一行的话触发事件
									if (editFlag != undefined) {
										$("#currentlabel_tab").datagrid('endEdit', editFlag);// 结束编辑，传入之前编辑的行
									}
									if (editFlag == undefined) {
										var index = $("#currentlabel_tab").datagrid('getRowIndex',rows[0]);//获取选定行的索引
										$("#currentlabel_tab").datagrid('beginEdit', index);//开启编辑并传入要编辑的行
										editFlag = index;
										 $("#currentlabel_tab").datagrid('unselectAll');
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
								$("#currentlabel_tab").datagrid('endEdit', editFlag);
								layer.msg('保存成功！', {icon: 1,time: 2000 }); 
								  //如果调用acceptChanges(),使用getChanges()则获取不到编辑和新增的数据。
				                //使用JSON序列化datarow对象，发送到后台。
				               /* var rows = $("#currentlabel_tab").datagrid('getChanges');
				                
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
								$("#currentlabel_tab").datagrid('rejectChanges');
								$("#currentlabel_tab").datagrid('unselectAll');
							}
						}								
						]
	} ;

//派昂展示的字段
var currentlabelcolumns={
		columns:[[{
			    field: 'id',
			    title:'索引编号',
			    width : 50,
			    checkbox: true
			},{
				field: 'showNum',
			    title: '打印单号',
			    width:6
			},{
				field: 'code',
				title: '业务code',
				hidden:true
			},
			 {
				field:'templateName',
				title:'打印模板',
				width:10,
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
				field:'doctypeId',
				title:'数据来源编码',
				hidden:true
			},{
				field:'doctypeName',
				title:'数据来源',
				width:10
			       
			},{
				field:'copies',
				title: '打印份数',
				width:6,
			        editor:{
			            type:'text',
			            options:{
			                valueField:'copies',
			                required:true
			            }
			        }
			},{
				field:'jsonData',
				title:'打印内容',
				hidden:true
				
			},{
				field : 'printStatus',
				width : 10,
				title : '标签状态',
				//sortable : true,
				//sortOrder:'asc',
				formatter : function(value, row, index) {
					if (row.printStatus == 1) {
						return "已打印";
					}else if (row.printStatus == 2) {
						return "已导出";
					} else {
						return "未打印";
					}
				}
			},{
				field:'opidName',
				title:'录入人',
				width:10
			       
			},{
				field:'printName',
				title:'打印人',
				width:4
			},{
				field:'updateTime',
				title:'更新日期',
				sortable:'true', //排序字段
				order:'asc',
				width:10
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
			{field:'opid',title:'',width:50}
		]],
	onSelect:function(rowIndex, rowData){
			opid_name =rowData.opid_name; 
		}
}); 

var currentlabel =new currentlabel();

$(document).ready(function(){
	console.log("通用打印页面初始化");
	//初始化表格
    currentlabel.initDataGridTable();
  //初始化打印模板下拉
    currentlabel.initPrintTemplate();
});
