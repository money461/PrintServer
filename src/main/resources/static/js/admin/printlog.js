function printlog(){
	
}



/**
 * 回显模板状态 value 0-正常 1 停用
 */
printlog.prototype.selectDictLabel = function(value) {
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

/**
 * 回显模板状态 value 0-正常 1 停用
 */
printlog.prototype.sendmqaddress = function(value) {
	//"<span class='badge badge-primary'>正常</span>"
	//"<span class='badge badge-danger'>停用</span>"
	var actions = [];
	var primaryClass = "badge badge-primary";
	var dangerClass = "badge badge-danger";
	var warningClass = "badge badge-warning";
	var infoClass = "badge badge-info";
	
	if ('vpnnet' ==  value) {
		actions.push($.common.sprintf("<span class='%s'>%s</span>", primaryClass, "内网"));
	}else if('outnet' == value){
		actions.push($.common.sprintf("<span class='%s'>%s</span>", primaryClass, "外网"));
	}else{
		actions.push($.common.sprintf("<span class='%s'>%s</span>", infoClass, "未知"));
	}
	return actions.join(''); 
}

var printlog = new printlog();

$(document).ready(function(){
	
});