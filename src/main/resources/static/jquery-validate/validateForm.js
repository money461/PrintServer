(function($) {
	// required:true 必须输入的字段。
	// remote:"check.php" 使用 ajax 方法调用 check.php 验证输入值。
	// email:true 必须输入正确格式的电子邮件。
	// url:true 必须输入正确格式的网址。
	// date:true 必须输入正确格式的日期。日期校验 ie6 出错，慎用。
	// dateISO:true 必须输入正确格式的日期（ISO），例如：2009-06-23，1998/01/22。只验证格式，不验证有效性。
	// number:true 必须输入合法的数字（负数，小数）。
	// digits:true 必须输入整数。
	// creditcard: 必须输入合法的信用卡号。
	// equalTo:"#field" 输入值必须和 #field 相同。
	// accept: 输入拥有合法后缀名的字符串（上传文件的后缀）。
	// maxlength:5 输入长度最多是 5 的字符串（汉字算一个字符）。
	// minlength:10 输入长度最小是 10 的字符串（汉字算一个字符）。
	// rangelength:[5,10] 输入长度必须介于 5 和 10 之间的字符串（汉字算一个字符）。
	// range:[5,10] 输入值必须介于 5 和 10 之间。
	// max:5 输入值不能大于 5。
	// min:10 输入值不能小于 10。
	$.fn.myVdate = function(options) {
		// meta:"validate"
		var defaults = {
			onkeyup : false,
			onsubmit : false,
		}
		options = $.extend(defaults, options);

		var param = {
			submitHandler : function(form) {
			},
			showErrors : function(errorMap, errorList) {
				for (var n = 0; n < errorList.length; n++) {
					layer.tips(errorList[n].message, errorList[n].element, {
						tips : [ 1, '#DC143C' ],
						time : 2000,
						tipsMore : true,
						success : function(layero) {
							layer.setTop(layero);
						}
					});
				}
			}
		};
		jQuery.each(options, function(key, val) {
			param[key] = val;
		});
		var formObject = $(this).validate(param);
		// 验证表单
		this.startValidate = function() {
			return $(this).valid();
		}
		// 验证表单值
		this.validateSingle = function(e) {
			return formObject.element(e);
		}
		// 添加表单属性验证
		this.addRules = function(key, opt) {
			// {required:true,min:0,messages:{min:"请选择省份"}}
			$(this).find(key).rules("add", opt);
		}
		// 清除表单属性验证
		this.clearRules = function(key) {
			$(this).find(key).rules("remove");
		}
		// 恢复初始化验证
		this.clearFormValidate = function() {
			formObject.resetForm();
		}
		// 清除表单数据
		this.clearFormData = function() {
			$(':input', this).not(':button,:checkbox,:submit,:reset,:hidden,:disabled,:radio').val('');
			$('input:checkbox', this).prop("checked", false);
		}
		this.getValidate = function() {
			return formObject;
		}
		return this;
	};
})(jQuery)