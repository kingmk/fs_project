 <#import "/common/host.ftl" as host>
<html>
<head>
<title>类目编辑</title>
<script src="${host.js}/js/rem.js?${host.version}"></script>
<script src="${host.js}/js/jquery-1.11.3.min.js"></script>
<script src="${host.js}/js/common.js?${host.version}&t=11"></script>
<meta name="viewport"/>
<script src="${host.js}/js/mobiscroll.custom-3.0.0-beta2.min.js"></script>
<link rel="stylesheet" href="${host.css}/css/mobiscroll.custom-3.0.0-beta2.min.css">
<link rel="stylesheet" href="${host.css}/css/mastercatenav.css?${host.version}1">
<style>
.img-logo-box{width: 5rem; height: 5rem ;border-radius: 50%; border: 0.1rem solid #fff; overflow: hidden ;position: relative; margin: 0 auto;}
.img-logo-box img{position: absolute; z-index: 9;}


</style>
<script>
$(function() {
	$(".cate-inner").click(function() {
		selectCate($(this));
	});

	$($(".cate-inner")[0]).click();

	$("#cate-intro").on('input propertychange', function() {
		var intro = $(this).val();
		if (intro.length > 100) {
			$(this).val(intro.substring(0,100));
		}
		if (intro.length > 0) {
			$("#saveBtn").removeClass("disabled");
		} else {
			$("#saveBtn").addClass("disabled");
		}
	});

	$("#saveBtn").click(save);
})

function selectCate(dom) {
	if (dom.hasClass("selected")) {
		return;
	}
	$(".cate-inner.selected").removeClass("selected");
	dom.addClass("selected");
	var intro = dom.attr("data-intro");
	$("#cate-intro").val(intro);
	if (intro.length > 0) {
		$("#saveBtn").removeClass("disabled");
	} else {
		$("#saveBtn").addClass("disabled");
	}
}

function save() {
	var domBtn = $("#saveBtn");
	if (domBtn.hasClass("disabled")) {
		return;
	};
	domBtn.addClass("disabled");
	var domSel = $(".cate-inner.selected");
	var cateId = domSel.attr("data-cateid");
	var cateIntro = $("#cate-intro").val();

	$.ajax({
		url: '${host.base}/usr/master/cate_intro_save_ajax',
		type: 'POST',
		data: {
			cateId: cateId,
			cateIntro: cateIntro
		},
		dataType: "json",
		beforeSend: function(){
			loading.addLoading('${host.img}/images/ajax-loader.gif');
		},
		success: function(data) {
			loading.removeLoading();
			if (data.head.code == '0000') {
				mAlert.addAlert('保存成功');
				domSel.attr("data-intro", cateIntro);
			} else {
				mAlert.addAlert(data.head.msg);
			}
			domBtn.removeClass("disabled");
		}
	});
}

</script>
</head>
<body>
	<div class="cate-nav-title">请选择类目：</div>
	<div class="cate-list clearfix">
	<#list result.body.serviceCateList as cate>
		<div class="cate-out">
			<div class="cate-inner" data-cateid="${cate.cateId}" data-intro="${cate.cateIntro}">${cate.cateName}</div>
		</div>
	</#list>
	</div>
	<div class="cate-nav-title">个人观点：</div>
	<div class="intro-box">
		<textarea id="cate-intro" class="cate-intro" placeholder="限100字" rows="5"></textarea>
	</div>
	<div class="info">
		<span>备注：</span>
		<span>1. 每编辑一个类目记得保存<br>2. 只显示老师所开通的服务项目</span>
	</div>

	<div class="button fix disabled" style='margin-top: 2rem' id='saveBtn'>保存</div>
</body>
</html>
