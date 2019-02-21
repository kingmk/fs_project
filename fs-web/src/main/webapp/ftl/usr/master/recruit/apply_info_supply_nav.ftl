 <#import "/common/host.ftl" as host>
<html>
<head>
<title>我的资料</title>
<script src="${host.js}/js/rem.js?${host.version}"></script>
<script src="${host.js}/js/jquery-1.11.3.min.js"></script>
<script src="${host.js}/js/common.js?${host.version}&t=11"></script>
<meta name="viewport"/>
<script src="${host.js}/js/mobiscroll.custom-3.0.0-beta2.min.js"></script>
<link rel="stylesheet" href="${host.css}/css/mobiscroll.custom-3.0.0-beta2.min.css">
<link rel="stylesheet" href="${host.css}/css/info_supply.css?${host.version}">
<style>
.img-logo-box{width: 5rem; height: 5rem ;border-radius: 50%; border: 0.1rem solid #fff; overflow: hidden ;position: relative; margin: 0 auto;}
.img-logo-box img{position: absolute; z-index: 9;}
</style>
<script>
var fontSize = $("html").css("fontSize");
var hasUrl = '${masterInfo.headImgUrl}';
var isInit = commonUtils.getUrlParam('isInit');
$(function(){
	var fontSize = $("html").css("font-size");
	$("#workYearStr").val(<#if masterInfo.workDate ??> commonUtils.getTime('${masterInfo.workDate?string('yyyy-MM-dd')}')<#else>''</#if>)
	if(!isInit){
		$("#saveBtn").html('保存并返回');
	}
	$(".list-textarea").on('input propertychange', function(){
	if ($(this).val().length > 200) {
		$(this).val( $(this).val().substring(0,200));
	}
	$(this).siblings('.list-textarea-tips').html('还可填写' + (200 - $(this).val().length) + '字');
	})
	$("#radio").on('click',radioChange);
	mobiscroll.select('#isFullTime', {
		theme: 'ios',
		display: 'bottom',
		lang: 'zh',
		data: [
			{
				text: '是',
				value: 'Y',
				valueText: '是'
			},{
				text: '否',
				value: 'N',
				valueText:'否'
			}
		],
		height: 68 / 20 * parseFloat(fontSize),
		rows: 2
	});

	$("#saveBtn").on('click', save)
})
//签约风水平台change事件
function radioChange() {
	var $el = $(this);
	if ($el.data('checked')) {
		$el.removeClass('on');
		$el.data('checked', false);
		$el.find(':radio').attr({
			checked: false,
		});
	} else {
		$el.addClass('on');
		$el.data('checked', true);
		$el.find(':radio').attr({
			checked: true,
		});
	}
}
//图片上传事件
function fileUpload(self) {
	var $file = $(self);
	var fileObj = $file[0];
	var windowURL = window.URL || window.webkitURL;
	var dataURL;
	$file.siblings(".tips").hide();
	var $img = $("<div id='fileImg'>div class='img-logo-box'><img id='headImgUrl' onload=\"commonUtils.adjustHeadImg(this, 5, 'headImgUrl')\"/></div>");
	$file.parents(".list-control").find("#fileImg").remove();
	$file.parents(".list-control").append($img);
	if(!fileObj ||  !fileObj.files || !fileObj.files[0]){
		if(hasUrl){
			$img.find('img').attr('src', hasUrl);
		}else{
			$file.siblings(".tips").show();
			$file.parents(".list-control").find("#fileImg").remove();
		}
		return;
	}
	if (fileObj && fileObj.files && fileObj.files[0]) {
		dataURL = windowURL.createObjectURL(fileObj.files[0]);
		$img.find('img').attr('src', dataURL);
	} else {
		dataURL = $file.val();
		var imgObj = $file.siblings("img");
		imgObj.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale)";
		imgObj.filters.item("DXImageTransform.Microsoft.AlphaImageLoader").src = dataURL;

	}
}
function save(){
	var isSignOther = '';
	$("#radio").data("checked") ? isSignOther = 'N' : isSignOther ='Y';
	$("#isSignOther").val(isSignOther);
	if(!$("#fileImg") || $("#fileImg").length == 0){
		mAlert.addAlert("请上传头像");
		return;
	}

	if($("#realName").val() == ''){
		 mAlert.addAlert('请输入真实姓名，该姓名不会对外公');
		return;
	}

	if($("#nickName").val() == ''){
		 mAlert.addAlert('请输入昵称');
		return;
	}
	if(!/^\d+(\.\d{1,2})?$/.test($("#workYearStr").val())){
		if($("#workYearStr").val() != ''){
			mAlert.addAlert('从业年限只能为整数或2位小数的数字');
		 	return;
		}

	}else{
		$("#workYearStr").val() !='' ? $("#workDate").val(commonUtils.setTime(-365*$("#workYearStr").val(),'-')):'';
	}

	if($("#school").val() == ''){
		 mAlert.addAlert('请输入所学门派');
		return;
	}
	if($("#experience").val() == ''){
		 mAlert.addAlert("请输入相关经历");
		return;
	}
	if($("#achievement").val() == ''){
		 mAlert.addAlert("请输入所获成就");
		return;
	}
	if($("#goodAt").val() == ''){
		 mAlert.addAlert("请输入擅长领域");
		return;
	}
	var formData = new FormData($("#form")[0]);

	var tmpfile = formData.get("headImg");
	if (tmpfile.size == 0 ) {
		if (typeof(formData.delete) == "function") {
			formData.delete("headImg");
		};
	};

	$.ajax({
		url: '/usr/master/recruit/supply_submit',
		type: 'POST',
		data: formData,
		cache: false,
		processData: false,
		contentType: false,
		beforeSend: function(){
			loading.addLoading('${host.img}/images/ajax-loader.gif');
		},
		success: function(data) {
			loading.removeLoading();
			var rlt = $.parseJSON(data);
			if (rlt.head.code == '0000') {
				mAlert.addAlert('保存成功');
				var isInit = commonUtils.getUrlParam('isInit');
				setTimeout(function(){
					if(isInit){
						location.href = '${host.base}/usr/master/recruit/service_cate_config_nav'
					}else{
				 		location.href = '${host.base}/usr/master/account'
					}
				},1000)
			} else {
				mAlert.addAlert(rlt.head.msg);
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			alert(JSON.stringify(textStatus));
		}
	});

}
</script>
</head>
<body>
<div class="center" style="margin-bottom: 6rem;">
<form id='form'>
	<#-- 上面三个input -->
	<div class="list form">
		<div class="list-body">
			<div class="list-item">
				<div class="list-label">老师姓名</div>
				<div class="list-control">
					<input type="text" placeholder="姓名" name="realName" id="realName" value="${masterInfo.name}" readonly="readonly" />
				</div>
			</div>
			<div class="list-item">
				<div class="list-label">昵称</div>
				<div class="list-control">
					<input type="text" placeholder="昵称会对外公开" name="nickName" id="nickName" value="${masterInfo.nickName}"/>
				</div>
			</div>
			<div class="list-item  photo-img">
				<div class="list-label">头像</div>
				<div class="list-control">
					<#if masterInfo.headImgUrl??>
					<div id='fileImg'>
					<div class="img-logo-box"><img id="headImgUrl" src='${masterInfo.headImgUrl}' onload="commonUtils.adjustHeadImg(this, 5, 'headImgUrl')"/></div>
					</div>
					</#if>
					<div class="tips" style="display: <#if masterInfo.headImgUrl??>none<#else>block</#if>">拍摄/本地照片</div>
					<input type="file" accept="image/*" id="img"  name="headImg" value="" onchange="fileUpload(this)" class="file-image">
				</div>
				<div class="list-arrow"></div>
			</div>
			<div class="list-item">
				<div class="list-label">从业年限</div>
				<div class="list-control">
					<input type="text" placeholder="从业年限" name="workYearStr" id="workYearStr" value=''/>
				</div>
				<div class="text-right" style="font-size: 1.4rem; align-self: center;">年</div>
			</div>
		</div>
	</div>
	<#-- textarea区域 -->
	<div class="list form" style="margin-top: 1.5rem">
		<div class="list-body">
			<div class="list-item">
				<div class="list-label">所学门派</div>
				<div class="list-control">
					<textarea class="list-textarea" rows="4" id="school" name='school' placeholder="必填">${masterInfo.school}</textarea>
					<div class="list-textarea-tips">
						<#if masterInfo.school??><#else>还可填写200字</#if>
					</div>
				</div>
			</div>
			<div class="list-item">
				<div class="list-label">相关经历</div>
				<div class="list-control">
					<textarea class="list-textarea" rows="4" id="experience" name='experience' placeholder="必填">${masterInfo.experience}</textarea>
					<div class="list-textarea-tips">
					<#if masterInfo.experience??><#else>还可填写200字</#if>
					</div>
				</div>
			</div>
			<div class="list-item">
				<div class="list-label">所获成就</div>
				<div class="list-control">
					<textarea class="list-textarea" rows="4" id="achievement" name='achievement' placeholder="必填">${masterInfo.achievement}</textarea>
					<div class="list-textarea-tips">
						<#if masterInfo.achievement??><#else>还可填写200字</#if>
					</div>
				</div>
			</div>
			<div class="list-item">
				<div class="list-label">擅长领域</div>
				<div class="list-control">
					<textarea class="list-textarea" rows="4" id="goodAt" name='goodAt' placeholder="必填">${masterInfo.goodAt}</textarea>
					<div class="list-textarea-tips">
						<#if masterInfo.goodAt??><#else>还可填写200字</#if>
					</div>
				</div>
			</div>
			<div class="list-item">
				<div class="list-label">是否专职</div>
				<div class="list-control">
					<input type="text"  placeholder="是否专职" name="isFullTime" value='<#if masterInfo.isFullTime ??>${masterInfo.isFullTime}<#else>Y</#if>' id="isFullTime"/>
				</div>
				<div class="list-arrow"></div>
			</div>
			<div class="list-item">
				<div class="list-label">愿意成为雷门易独家签约风水师</div>
				<div class="list-control">
					<div class="radio <#if masterInfo.isSignOther=='N'>on</#if>" id='radio' data-checked='<#if masterInfo.isSignOther=="N">true<#else>false</#if>'>
						<input type="radio" name='isSignOther1'  checked="<#if masterInfo.isSignOther=='Y'>checked<#else>false</#if>" class="reset-radio"/>
					</div>

				</div>
			</div>
		</div>
	</div>

	<input style="display: none;" name ='masterInfoId' id='masterInfoId' value="${masterInfo.id}">
	<input style="display: none;" name ='isSignOther' id='isSignOther' value="Y">
	<input style="display: none;" type="text" id='workDate' name = 'workDate'>
</form>
</div>

<div class="button fix" style='margin-top: 2rem' id='saveBtn'>
	保存并下一步
</div>
</body>
</html>
