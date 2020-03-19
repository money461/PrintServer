function region(){
	
}

//保存数据
region.prototype.saveOrUpdateRegion = function(url, data, callback) {
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
                    if (parent.table.options.type == table_type.bootstrapTreeTable) {
                    	$.modal.close();
                        parent.$.modal.msgSuccess(result.msg);
                        parent.$.treeTable.refresh();
                    }
	        		$.modal.closeLoading();
	        		$.modal.enable();
	        	}
	        }
	    };
	    $.ajax(config) //发送请求
}

//layui 引入treeSelect.js
layui.config({
    base: ctx+'/layer/layui/lay/'
}).extend({
    treeSelect: 'treeSelect/treeSelect',
    treeSelect2: 'treeSelect2/treeSelect2'
});

var region = new region();

$(document).ready(function(){
	
});
