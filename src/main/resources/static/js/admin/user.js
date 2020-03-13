function user(){
	
}

//保存数据
user.prototype.saveOrupdateUser = function(url, data, callback) {
	var config = {
			url: url,
	        type: "post",
	        dataType: "json",
	        data: data,
	        beforeSend: function () {
	        	$.modal.loading("正在处理中，请稍后...");
	        },
	        success: function(result) {
	        	if('200'==result.status){
	        		var topWindow = $(window.parent.document);
    	            var currentId = $('.page-tabs-content', topWindow).find('.active').attr('data-panel');
    	            var $contentWindow = $('.RuoYi_iframe[data-id="' + currentId + '"]', topWindow)[0].contentWindow;
    	            $.modal.close();
    	            $contentWindow.$.modal.msgSuccess(result.msg);
    	            $contentWindow.$(".layui-layer-padding").removeAttr("style");
    	            if ($contentWindow.table.options.type == table_type.bootstrapTable) {
    	        		$contentWindow.$.table.refresh();
    	        	}
    	            $.modal.closeTab();
	        	}
	        }
	    };
	    $.ajax(config) //发送请求
}

var user = new user();

$(document).ready(function(){
	
});
