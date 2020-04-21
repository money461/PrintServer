function log(){
	
}



/**
 * 回显模板状态 value 0-正常 1 停用
 */
log.prototype.selectDictLabel = function(value) {
	//"<span class='badge badge-primary'>正常</span>"
	//"<span class='badge badge-danger'>停用</span>"
	var actions = [];
	var primaryClass = "badge badge-primary";
	var dangerClass = "badge badge-danger";
	var warningClass = "badge badge-warning";
	var infoClass = "badge badge-info";
	
        if ('0' ==  value) {
        	actions.push($.common.sprintf("<span class='%s'>%s</span>", primaryClass, "成功"));
        }else if('1' == value){
        	actions.push($.common.sprintf("<span class='%s'>%s</span>", dangerClass, "失败"));
        }else if('3' == value){
        	actions.push($.common.sprintf("<span class='%s'>%s</span>", warningClass, "异常"));
        }else{
        	actions.push($.common.sprintf("<span class='%s'>%s</span>", infoClass, "未知"));
        }
      return actions.join(''); 
}


/**初始化Select2 表单下拉框**/
log.prototype.select2=function(code,callback){
	$('.select2').each(function(){
		var $this = $(this);
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
			  callback(data);
		})
		
	});
	
}

log.prototype.initdate = function(){
	$('#startDate').attr('style','width: 93px;');
	$('#endDate').attr('style','width: 93px;');
	
	
	 function formatterDate(date) {
	        var day = date.getDate() > 9 ? date.getDate() : "0" + date.getDate();
	        var month = (date.getMonth() + 1) > 9 ? (date.getMonth() + 1) : "0" + (date.getMonth() + 1);
	        return date.getFullYear() + '-' + month + '-' + day;
	    };
	
	var beforedate = new Date();
	beforedate.setDate(beforedate.getDate()-30)
	var startDateConfig={
			elem: '#startDate',
	        max: $('#endDate').val(),
	        theme: 'molv',
	        trigger: 'click',
	        isInitValue: true,
	        value: beforedate,
	        done: function(value, date) {
	            // 结束时间大于开始时间
	            if (value !== '') {
	                endDate.config.min.year = date.year;
	                endDate.config.min.month = date.month - 1;
	                endDate.config.min.date = date.date;
	            } else {
	                endDate.config.min.year = '';
	                endDate.config.min.month = '';
	                endDate.config.min.date = '';
	            }
	        }
	};
	var nowdate = new Date();
	var endDateConfig={
			elem: '#endDate',
	        min: formatterDate(beforedate),
	        theme: 'molv',
	        trigger: 'click',
	        isInitValue: true,
	        value: nowdate,
	        done: function(value, date) {
	            // 开始时间小于结束时间
	            if (value !== '') {
	                startDate.config.max.year = date.year;
	                startDate.config.max.month = date.month - 1;
	                startDate.config.max.date = date.date;
	            } else {
	                startDate.config.max.year = '';
	                startDate.config.max.month = '';
	                startDate.config.max.date = '';
	            }
	        }
	};
	
	var laydate = layui.laydate;
	var startDate =laydate.render(startDateConfig);
	var endDate = laydate.render(endDateConfig);
}


var log = new log();

$(document).ready(function(){
	
});