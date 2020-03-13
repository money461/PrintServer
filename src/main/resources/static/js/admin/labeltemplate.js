function labeltemplate(){
	
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
	        	if('200'==result.status){
	        		var parent = window.parent;
                    if (parent.table.options.type == table_type.bootstrapTable) {
                        $.modal.close();
                        parent.$.modal.msgSuccess(result.message);
                        parent.$.table.refresh();
                    }
	        		$.modal.closeLoading();
	        		$.modal.enable();
	        	}
	        }
	    };
	    $.ajax(config) //发送请求
}

var labeltemplate = new labeltemplate();

$(document).ready(function(){
	
});
