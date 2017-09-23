$(function() {
	var fontSize = $("html").css("font-size");
	mobiscroll.select('#liveAddress', {
		theme: 'ios',
		display: 'bottom',
		lang: 'zh',
		group: {
			groupWheel: true,
      header: false
    },
    layout: 'liquid',
    placeholder: 'Please select...',
		headerText: '选择现居住地',
		width: [60, 235],
		data: cityDate,
		height: 68 / 20 * parseFloat(fontSize),
		rows: 3,
	});
	// $("#form :input").bind('input propertychange', checkForm);
	$("#form #radio").on('click',radioChange)
	$("#next").click(function() {
		var formData = new FormData($("#form")[0]);
		var bascInfo = {};
		$("#form").serializeArray().map(function(item, index){
			bascInfo[item.name] = item.value;
		})
		if (!commonUtils.checkName(bascInfo['realName'])) {
			 mAlert.addAlert('请输入身份证上的姓名');
			return;
		}
		if (!commonUtils.checkCertNo(bascInfo['certNo'].toString())) {
			 mAlert.addAlert('请输入正确的身份证号');
			return;
		}
		//校验有没有上传身份证照片
		if( !($("#img1").find('img').length>0 && $("#img2").find('img').length>0)){
			 mAlert.addAlert('请上传身份证正反面！');
			return;
		}
		if (!commonUtils.checkMobile(bascInfo['contactMobile'])) {
			 mAlert.addAlert('请输入正确的手机号');
			return;
		}
		$("#personalData").hide();
		$("#personalInfo").show();

	});
	$("#next2").on('click', formSubmit);
	$('.photo-example').on( "touchmove", function (e) {
	   e.preventDefault();
	});
	$("#showPhoto").on('click', function(){
		$('.photo-example').show();
	})
	$('.photo-example .button').on('click', function(){
		$('.photo-example').hide();
	})
	$('.photo-example').on("click",function(){
		$('.photo-example').hide();
	});
	$('.photo-example .photo-wrapper').on("click",function (e) {
		e.stopPropagation()
	});
	$(".list-textarea").on('input propertychange', function(){
        if ($(this).val().length > 200) {
            $(this).val( $(this).val().substring(0,200));
        }
        $(this).siblings('.list-textarea-tips').html('还可填写' + (200 - $(this).val().length) + '字');
    })
});
//签约风水平台change事件
function radioChange() {
	var $el = $(this);
	if($("#radio").hasClass('on')){
		$("#radio").removeClass('on');
	}else{
		$("#radio").addClass('on');
	}
}

function formSubmit() {
	if($(this).hasClass('disabled')){
		return;
	}
	var isSignOther = '';
	$("#radio").hasClass('on') ? isSignOther = 'Y' : isSignOther ='N';
	$("#isSignOther").val(isSignOther);
	var formData = new FormData($("#form")[0]);
	$.ajax({
		url: '/usr/master/recruit/apply_submit',
		type: 'POST',
		data: formData,
		cache: false,
		processData: false,
		contentType: false,
		beforeSend: function(){
        loading.addLoading(hostImg+'/images/ajax-loader.gif');
    },
		success: function(data) {
			loading.removeLoading();
			var rlt = $.parseJSON(data);
			if (rlt.head.code == '0000') {
				location.href = "/usr/master/recruit/apply_nav?isFirstAfterApply=Y";
			} else {
				mAlert.addAlert(rlt.head.msg);
			}
		}
	});
}

function checkForm() {
	var $this = $(this);
	var $parent = $this.parent();
	var flag = null;
	if ($this.is("#realName")) {
		flag = '请输入真实姓名';
	} else if ($this.is("#certNo")) {
		flag = '请输入正确的身份证号';
	} else if ($this.is("#contactMobile")) {
		flag = '请输入正确的电话号码';
	}
	// else if ($this.is("#contactWeixin") || $this.is("#contactQq")) {
	// 	flag = 'checkNotNull';
	// }
	if (flag && commonUtils[flag]) {
		if (!commonUtils[flag]($this.val())) {
			$parent.find('.onSuccess').remove();
			if ($parent.find(".onError").length == 0) {
				$parent.append('<span class="onError">'+flag+'</span>');
			}
		} else {
			$parent.find('.onError').remove();
			if ($parent.find(".onSuccess").length == 0) {
				$parent.append('<span class="onSuccess"></span>');
			}
		}
	}
	if ($("#form .onError").length == 0 && $("#form .onSuccess").length == 3) {
		// $("#next").removeClass('disabled');
	} else {
		// $("#next").addClass('disabled');
	}
}
function fileUpload(self) {
	var $file = $(self);
	var fileObj = $file[0];
	var windowURL = window.URL || window.webkitURL;
	var dataURL;
	if(!fileObj ||  !fileObj.files || !fileObj.files[0]){
		$file.siblings(".a-upload-tips").show();
		$file.parents(".a-upload").find(".imgbox").remove();
		$file.parents(".a-upload").find(".edit").remove();
		return;
	}
	$file.siblings(".a-upload-tips").hide();
	var $img = $("<img class='imgbox'/>");
	var $edit = $("<div class='edit imgbox'>修改</div>")
	$file.parents(".a-upload").find(".imgbox").remove();
	$file.parents(".a-upload").append($img).append($edit);
	if (fileObj && fileObj.files && fileObj.files[0]) {
		dataURL = windowURL.createObjectURL(fileObj.files[0]);
		$img.attr('src', dataURL);
	} else {
		dataURL = $file.val();
		var imgObj = $file.siblings("img");
		imgObj.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale)";
		imgObj.filters.item("DXImageTransform.Microsoft.AlphaImageLoader").src = dataURL;
	}
}