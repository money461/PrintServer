function labeltemplate(){
	
}
/**
 * 回显模板状态 value 0-正常 1 停用
 */
labeltemplate.prototype.statusTools = function(row) {
	
/* 用户状态显示 */
    if (row.isDefault == 1) {
		return '<i class=\"fa fa-toggle-off text-info fa-2x\" onclick="enable(\'' + row.id + '\')"></i> ';
	} else {
		return '<i class=\"fa fa-toggle-on text-info fa-2x\" onclick="disable(\'' + row.id + '\')"></i> ';
	}
}
/**
 * 初始化业务code  方式一
 */
labeltemplate.prototype.initCode = function(url, data, callback){
	var config={
			url: url,
	        type: "GET",
	        dataType: "json",
	        data: data,
	        async: false, //同步加载
	        beforeSend: function () {
	        	$.modal.loading("正在处理中，请稍后...");
	        },
	        success: function(result) {
	        	$.modal.closeLoading();
	        	if('200'==result.status){
	        		var data = result.data;
	        		var options=[];
	        		options.push('<option value="" check="checked">--请选择--</option>');
	        		$.each(data, function(key,value){
	        			options.push('<option value="'+value.text+'">'+value.text+'</option>');
	        		});
	        		
	        		$("#code").append( options.join('')); 
	        		
	        	}else{
	        		 $.modal.msgError(result.message);
	        	}
	        }
	    };
	
	$.ajax(config);
}

/**
 * 初始化业务code 方式二
 */
	
	//var url =  ctx + "/template/getAllCode"
labeltemplate.prototype.initCode2 = function (id, code, callback){
	var $this = $('#'+id);
	var url = $this.attr("href");
	function getDataByCode(){
	    var dataStore;
	    $.ajax({
	        dataType : 'json',
	        type : 'POST',
	        data : {'code':code},
	        url : url,
	        async : false,
	        success: function(data){
	            dataStore=data.data;
	        }
	     });
	    return dataStore;
	}   
	var dataStore = getDataByCode();
	
	$this.select2({
		 language : "zh-CN",// 指定语言为中文，国际化才起效
	    placeholder : $this.attr("inputMessage"),// 添加默认//默认文字提示
	    //dropdownParent : $("#myModal"),
	    allowClear : true,
	    tags: true,//允许手动添加
	    multiple:false,
	    width : 'auto',
	    minimumResultsForSearch: 1,
	    data : dataStore
	});
	
	 
	 $this.val(code).trigger("change"); //select2 回显
	 
	 //每当选择或删除选项时触发。
	 $this.on("select2:select",function(e){
		 var data = e.params.data;
		  console.log(data);
		  var id = $this.attr('id');
		  alert(id)
		  callback(data,id);
	})
}
	


/**初始化Select2 表单下拉框**/
labeltemplate.prototype.select2=function(id,code,callback){
		
		var $this = $('#'+id);
		var url = $this.attr("href");
		if($.common.isEmpty(url)){ return ;}//实现continue功能  
		function getInitData(){
		    var dataStore;
		    $.ajax({
		        dataType : 'json',
		        type : 'POST',
		        data : {'code':code}, //参数
		        url : url,
		        async : false,
		        success: function(data){
		            dataStore=data.data;
		        }
		     });
		    return dataStore;
		}   
		var initdata = getInitData();
		
		 $this.select2({
			 language : "zh-CN",// 指定语言为中文，国际化才起效
			 data: initdata,
			 multiple:false, //是否多选
			 width : 'auto', //宽度自动
             ajax:{
            	 url : url,
            	 tyle: 'POST',
                 dataType : 'json',
                 async : false,
                 delay : 250,// 延迟显示
                 data : function(params) {
                	 return {
                		 code : params.term, // 搜索框内输入的内容，传递到Java后端的parameter为code
                		 pageNum : params.page,// 第几页，分页哦
                		 pageSize : 10// 每页显示多少行
                     };
                 },
                 // 分页
                 processResults : function(data, params) {
                     params.curPage = params.curPage || 1;
                     var resultList = data.data; //后台数据
                     return {
                    	//返回的选项必须处理成以下格式
                            //results : [{ id: 0, text: 'enhancement' }, { id: 1, text: 'bug' }, { id: 2, text: 'duplicate' }, { id: 3, text: 'invalid' }, { id: 4, text: 'wontfix' }],
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
	         minimumInputLength: -1,//最少输入多少个字符后开始查询
	         formatResult: function formatRepo(repo){return repo.text;}, // 函数用来渲染结果
	         formatSelection: function formatRepoSelection(repo){return repo.text;} // 函数用于呈现当前的选择
		 });
		 
		 $this.val(code).trigger("change"); //select2 回显
		 
		 //每当选择或删除选项时触发。
		 $this.on("select2:select",function(e){
			 var data = e.params.data;
			  console.log(data);
			  var id = $this.attr('id');
			  callback(data,id);
		})
}

//方式三
labeltemplate.prototype.initCode3 = function(url, data, callback){
	
	function initSelect2(dom,url,multiple){
	    var subject = $(dom);//元素
	    var Select2 = subject.select2({
	        ajax: {
	            url: url,
	            dataType: 'json',
	            data: function (params) {
	                return {
	                    name: params.term, // 搜索参数
	                };
	            },
	            processResults: function (data, params) {
	                for (var i = 0; i < data.length; i++) {
	                    data[i].id = data[i].id;
	                    data[i].text = data[i].name;
	                }
	                return {
	                    results: data
	                };
	            },
	            cache: true
	        },
	        placeholder: "请输入名称搜索",
	        allowClear: true,    //选中之后，可手动点击删除
	        escapeMarkup: function (markup) { return markup; }, // 让template的html显示效果，否则输出代码
	        minimumInputLength: 2,    //搜索框至少要输入的长度，此处设置后需输入才显示结果
	        language: "zh-CN",         //中文
	        multiple:multiple, //是否多选
	        width: 'resolve',
	        closeOnSelect:false,
	        templateResult: formatSubject, // 自定义下拉选项的样式模板
	        templateSelection: formatSubjectSelection     // 自定义选中选项的样式模板
	    });

	    return Select2;
	}

	function formatSubject(item) {
	    if (item.loading) return item;
	    var markup = '<div> <p class="text-primary">学科名称：' + item.name|| item.text + '</p>';
	    //markup += '这里可以添加其他选项...';
	    markup += ' </div>';
	    return markup;
	}

	function formatSubjectSelection(item) {
	    return item.name || item.text;
	}
	//回显数据
	function echoSelect2(dom,value){
	    $.each(value,function(index,value){
	        $(dom).append(new Option(value.name, value.id, false, true));
	    });
	    $(dom).trigger("change");
	}
	//3.使用方式
	var select2 = initSelect2("#school","/school/searchSchoolByName",true);
	//4. 回显方式
	//获取初始化数据定义成类似的数组形式：var data = [{id:1001,name:“哈哈哈”}];
	//调用方法echoSelect2("#school",data); 就完事了
}

/**
 * 回显模板状态 value 0-正常 1 停用
 */
labeltemplate.prototype.selectDictLabel = function(value) {
	//"<span class='badge badge-primary'>正常</span>"
	//"<span class='badge badge-danger'>停用</span>"
	var actions = [];
	var primaryClass = "badge badge-primary";
	var dangerClass = "badge badge-danger";
	var infoClass = "badge badge-info";
	
	if ('0' ==  value) {
		actions.push($.common.sprintf("<span class='%s'>%s</span>", primaryClass, "正常"));
	}else if('1' == value){
		actions.push($.common.sprintf("<span class='%s'>%s</span>", dangerClass, "停用"));
	}else{
		actions.push($.common.sprintf("<span class='%s'>%s</span>", infoClass, "未知"));
	}
	return actions.join(''); 
}


// 保存信息 刷新表格
labeltemplate.prototype.saveorupdatetemplate = function(url, data, callback) {
    	var config = {
	        url: url,
	        type: "post",
	        dataType: "json",
	        data: data,
	        beforeSend: function () {
	        	$.modal.loading("正在处理中，请稍后...");
	        	$.modal.disable();
	        },
	        success: function(result) {
	        	$.modal.closeLoading();
	        	if('200'==result.status){
	        		$.modal.close();
	        		var parent = window.parent;
                    if (parent.table.options.type == table_type.bootstrapTable) {
                        parent.$.modal.msgSuccess(result.message);
                        parent.$.table.refresh();
                    }
	        	}else{
	        		 parent.$.modal.msgError(result.message);
	        	}
	        	$.modal.enable();
	        }
	    };
	    $.ajax(config) //发送请求
}
//表单重置
labeltemplate.prototype.reset = function(formId, tableId) {
	var currentId = $.common.isEmpty(formId) ? $('form').attr('id') : formId;
	//$("#" + currentId)[0].reset();
	jQuery("#" + currentId).form("clear");
	$('.select2').each(function(){
		$(this).val(null).trigger("change");
	});
	$("#" + table.options.id).bootstrapTable('refresh');
}

/**
 * 导入数据
 */
labeltemplate.prototype.importrepx = function(formId,callback) {
    	table.set();
    	var fileList = [];
    	var currentId = $.common.isEmpty(formId) ? 'importTpl' : formId;
    	layer.open({
    		type: 1,
    		area: ['400px', '230px'],
    		fix: false,
    		//不固定
    		maxmin: true,
    		shade: 0.3,
    		title: '导入' + table.options.modalName + '数据',
    		content: $('#' + currentId).html(),
    		btn: ['<i class="fa fa-check"></i> 导入', '<i class="fa fa-remove"></i> 取消'],
    		// 弹层外区域关闭
    		shadeClose: true,
    		btn1: function(index, layero){
    			/*var file = layero.find('#file').val();*/
    			//校验文件
    			if(fileList.length==0){
    				$.modal.msgWarning("请选择后缀为 “repx”的文件。");
    				return false;
    			}
    			if(fileList.length>7){
    				$.modal.msgWarning("上传文件数量不能超过6份!。");
    				return false;
    			}
    			fileList.forEach(function (file) {
    				if (file == '' || !$.common.endWith(file.name, '.repx')){
    					$.modal.msgWarning("请选择后缀为 “repx”的文件。");
    					throw new Error("请选择后缀为 “repx”的文件。");
    				}
    			});
    			var formDataform = new FormData($('#importTemplateForm')[0]); //jquery1.8以上
    			/*var formData = new FormData();
    			var fileList2 = $('#file')[0].files;
    			for(var i=0;i<fileList2.length;i++){ //也可以使用 fileList遍历
    				var file = fileList2[i];
    				formData.append("file",file, file.name );
    			}*/
    			formDataform.append("updateSupport", $("input[name='updateSupport']").is(':checked'));
    			var index = layer.load(2, {shade: false}); //加载动画
    			$.ajax({
    				url: table.options.importTemplateUrl,
    				data: formDataform,
    				cache: false, //cache设置为false，上传文件不需要缓存。
    				contentType: false, //contentType设置为false。因为是由<form>表单构造的FormData对象，且已经声明了属性enctype="multipart/form-data"，所以这里设置为false。
    				processData: false, //processData设置为false。因为data值是FormData对象，不需要对数据做处理。
    				type: 'POST',
    				beforeSend: function () {
    		        	$.modal.loading("正在上传中，请稍后...");
    		        	$.modal.disable();
    		        },
    				success: function (result) {
    					$.modal.closeLoading();
    					layer.close(index); //关闭加载动画
    					if (result.status == "200") {
    						$.modal.closeAll();
    						$.modal.alertSuccess(result.message);
    						if (typeof callback == "function") {
            	        	    callback(result);
            	        	    $.modal.enable();
            	        	}else{
            	        		$.table.refresh();
            	        	}
    						
    					} else {
    						$.modal.enable();
    						$.modal.alertError(result.message);
    					}
    				}
    			});
    		},
    		 cancel: function(index) {
    			 alert("取消导入");
    			 layer.close(index); //关闭加载动画
     	       //清除表单数据
     	       $('#importTemplateForm')[0].reset();
     	        //清除页面数据
     	       $('#file-list-display').html('');
     	        //清除数组数据
     	       fileList.length=0;
     	    }
    	});
    	
    	labeltemplate.initaddEventListener(fileList);
}

/**
 * 导出模板
 */
labeltemplate.prototype.exportrepx = function(id) {
	table.set();
	var json=[];
	if($.common.isEmpty(id)){
		var rowarray = $("#" + table.options.id).bootstrapTable('getSelections');
		if ($.common.isEmpty(rowarray)) {
			$.modal.alertWarning("请选择需要导出的模板！");
			return false;
		}
		rowarray.forEach(function (currentValue,index,arr) {
			json.push(currentValue.id);
		});
	}else{
		json.push(id);
	}
	$.modal.confirm("确定导出选择的" + table.options.modalName + "吗？", function() {
		var data = json.join(",");
		window.location.href = table.options.exportTemplateUrl+"?id=" +data ;
		var index = layer.load(2, {time: 2*1000}); //又换了种风格，并且设定最长等待2秒 
		/*window.parent.$.modal.loading("正在导出"+ table.options.modalName +"，请稍后...");
		setTimeout(function(){
			window.parent.$.modal.closeLoading(); //关闭加载
		},3000);*/
	});
}

labeltemplate.prototype.initaddEventListener = function(fileList){
     var files = document.getElementById("file"), renderFileList;
     var fileListDisplay = document.getElementById('file-list-display');
     
    files.addEventListener("change", function (event) {
         for (var i = 0; i < files.files.length; i++) {
             fileList.push(files.files[i]);
         }
         renderFileList();
     });
    
    renderFileList = function () {
         fileListDisplay.innerHTML = '';
         fileList.forEach(function (file, index) {
             var fileDisplayEl = document.createElement("p");
             fileDisplayEl.innerHTML = (index + 1) + ":" + file.name;
             fileListDisplay.appendChild(fileDisplayEl);
         })
     };
}

//删除模板
labeltemplate.prototype.deleteTemplatefile = function(url, data, callback) {
	$.modal.confirm("确定删除数据库中的打印模板吗？", function() {
		var config = {
	        url: url,
	        type: "post",
	        dataType: "json",
	        data: data,
	        beforeSend: function () {
	        	$.modal.loading("正在处理中，请稍后...");
	        	$.modal.disable();
	        },
	        success: function(result) {
	        	$.modal.closeLoading();
	        	if('200'==result.status){
	        		var parent = window.parent;
	        		parent.$.modal.msgSuccess(result.message);
	        		callback();//执行回调
	        	}else{
	        		 parent.$.modal.msgError(result.message);
	        	}
	        	$.modal.enable();
	        }
	    };
	    $.ajax(config); //发送请求
	});
	
}


var labeltemplate = new labeltemplate();

$(document).ready(function(){
	//var url =  ctx + "/template/getAllCode"
	//labeltemplate.initCode2(url);
	
});
