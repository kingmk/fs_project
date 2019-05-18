 <#import "/common/host.ftl" as host>
<html>
<head>
<title>我的银行卡</title>
<script src="${host.js}/js/rem.js?${host.version}"></script>
<script src="${host.js}/js/jquery-1.11.3.min.js"></script>
<script src="${host.js}/js/common.js?${host.version}&t=11"></script>
<meta name="viewport"/>
<script src="${host.js}/js/mobiscroll.custom-3.0.0-beta2.min.js"></script>
<link rel="stylesheet" href="${host.css}/css/mobiscroll.custom-3.0.0-beta2.min.css">
<link rel="stylesheet" href="${host.css}/css/my_bankcard.css?${host.version}">
<style>
.img-logo-box{width: 5rem; height: 5rem ;border-radius: 50%; border: 0.1rem solid #fff; overflow: hidden ;position: relative; margin: 0 auto;}
.img-logo-box img{position: absolute; z-index: 9;}


</style>
<script>
$(function() {
	$("#saveBtn").click(save);
})

function save() {
	var domBtn = $("#saveBtn");
	if (domBtn.hasClass("disabled")) {
		return;
	};

	domBtn.addClass("disabled");

	var fields = ["holderName", "bankNo", "bankName", "province", "city"];
	var params = {};
	for (var i = 0; i < fields.length; i++) {
		var fieldName = fields[i];
		var val = $("#"+fieldName).val();
		if (!val || val.length == 0) {
			mAlert.addAlert("所有信息都必填");
			domBtn.remove("disabled");
			return;
		};
		params[fieldName] = val;
	};
	var formData = new FormData($("#form")[0]);

	$.ajax({
		url: '/usr/master/save_bankcard',
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
				setTimeout(function() {
					location.href = "${host.base}/usr/master/account";
				}, 1000);
			} else {
				mAlert.addAlert(rlt.head.msg);
			}
			domBtn.removeClass("disabled");
		}
	});
}

</script>
</head>
<body>
<#assign mastercard=result.body>
<div class="center" style="margin-bottom: 6rem;">
	<div class="infowarn"><span class="icon-warn"></span>为保证您的收入到账，请确保银行卡信息准确</div>
	<form id='form'>
		<div class="list form">
			<div class="list-body">
				<div class="list-item">
					<div class="list-label">姓名</div>
					<div class="list-control">
						<input type="text" placeholder="请确保是本人" name="holderName" id="holderName" value="${mastercard.holderName}" />
					</div>
				</div>
				<div class="list-item">
					<div class="list-label">卡号</div>
					<div class="list-control">
						<input type="tel" placeholder="未填写" name="bankNo" id="bankNo" value="${mastercard.bankNo}"/>
					</div>
				</div>
				<div class="list-item">
					<div class="list-label">开户支行名称</div>
					<div class="list-control">
						<input type="text" placeholder="例如：招商银行上海曹杨支行" name="bankName" id="bankName" value="${mastercard.bankName}"/>
					</div>
				</div>
				<div class="list-item">
					<div class="list-label">开户省</div>
					<div class="list-control">
						<input type="text" placeholder="开户行所在省份" name="province" id="province" value="${mastercard.province}"/>
					</div>
				</div>
				<div class="list-item">
					<div class="list-label">开户市</div>
					<div class="list-control">
						<input type="text" placeholder="开户行所在市" name="city" id="city" value="${mastercard.city}"/>
					</div>
				</div>
			</div>
		</div>
	</form>
</div>

<div class="button fix" style='margin-top: 2rem' id='saveBtn'>保存</div>
</body>
</html>
