 <#import "/common/host.ftl" as host>
<html>
<title>注册雷门易</title>
<head>
<script src="${host.js}/js/rem.js?${host.version}"></script>
<script src="${host.js}/js/jquery-1.11.3.min.js"></script>
<script src='${host.js}/js/jquery.sendCode.js?${host.version}'></script>
<script src="${host.js}/js/common.js?${host.version}"></script>
<link rel="stylesheet" href="${host.css}/css/bgmask.css?${host.version}">
<link rel="stylesheet" href="${host.css}/css/register.css?${host.version}">
<style>
</style>
<script>
var count =90;
$(function(){
	$("#code").on('click', function(){
		if(  !/\d{11}/.test($("#mobile").val()) || $(this).hasClass('disabled')){
			return;
	}else{
		var _this = this;
		$(this).addClass('disabled');
		$(this).html("90秒后获取");
		var interValObj = window.setInterval(function () {
			if (count === 0) {
				window.clearInterval(interValObj); //停止计时器
				$(_this).removeClass('disabled').html("获取验证码");
			} else {
				$(_this).addClass('disabled');
				$(_this).html( count + "秒后获取");
				count--;
			}
		}, 1000); //启动计时器，1秒执行一次
		sendCode(
			{mobile:$("#mobile").val()},
			function(data){
				if(data.head.code == '0000'){
					 mAlert.addAlert('获取验证码成功')
				}else{
					mAlert.addAlert(data.head.msg);
					$(_this).removeClass('disabled').html("获取验证码");
					count = 0;
				}
			},function(data){
				console.log(data)
			})
		}
	})
	$("#submit").on("click", submit);
	$("#codeInput").on("input propertychange", function(){
		var val = $(this).val().replace(/[^0-9]/,'').substr(0,6);
		$(this).val(val);
	});

	$(".source-item").click(selectSource);
});

function sendCode(data, callback,errBack){
	$.ajax({
		type: "POST",
		url: "${host.base}/usr/register/mobile_sent_code",
		data: data,
		dataType: "json",
		success: function(data){
				callback(data)
		},
		error: function(res){
			errBack?errBack(data):''
		}
	});
}

function selectSource() {
	var domClick = $(this);
	if (domClick.hasClass("selected")) {
		return;
	};

	var domSelected = $(".source-item.selected");
	domSelected.removeClass("selected");
	domClick.addClass("selected");
}

function submit(){
	var domSubmit = $("#submit");
	if (domSubmit.hasClass("disabled")) {
		return;
	};
	domSubmit.addClass("disabled");
	var domSource = $(".source-item.selected");
	var source = null;
	if (domSource.length > 0) {
		source = domSource.text();
	};
	var data = {
		mobile: $("#mobile").val(), 
		code : $("#codeInput").val(),
		source: source
	};
	if (!(/^1[0-9]{10}$/.test(data.mobile)) || !(/^[0-9]{6}$/.test(data.code))) {
		mAlert.addAlert("请输入正确的手机号和验证码");
		domSubmit.removeClass("disabled");
		return;
	}

	$.ajax({
		type: "POST",
		url: "${host.base}/usr/register/mobile_check_code",
		data: data,
		dataType: "json",
		success: function(data){
			if(data.head.code == "0000"){
				mAlert.addAlert('注册成功');
				if("${backUrl}" !="" ){
					window.location.href = "${backUrl}";
				} else {
					domSubmit.removeClass("disabled");
				}
			}else{
				domSubmit.removeClass("disabled");
				mAlert.addAlert(data.head.msg,2000);
			}
		},
		error: function(res){
			domSubmit.removeClass("disabled");
		}
	});
}
</script>
</head>

<body>
	<form id='form'>
		<div class="list form">
			<div class="list-body">
				<div class="list-item">
					<div class="list-label">手机号</div>
					<div class="list-control">
						<input type="tel" placeholder="请输入您的手机号" name="mobile" id="mobile" />
					</div>
				</div>
				<div class="list-item code">
					<div class="list-label">验证码</div>
					<div class="list-control">
						<div class="list-item-code" id="code">获取验证码</div>
						<input type="tel" placeholder="请输入验证码" name="code" id="codeInput" />
					</div>
				</div>
			</div>
		</div>
	</form>
	<div class="source">
		<div class="source-title">您是怎么知道雷门易的？</div>
		<div class="source-list clearfix">
			<div class="source-item-outter">
				<div class="source-item">微信公众号</div>
			</div>
			<div class="source-item-outter">
				<div class="source-item">微信推文</div>
			</div>
			<div class="source-item-outter">
				<div class="source-item">朋友推荐</div>
			</div>
			<div class="source-item-outter">
				<div class="source-item">微博</div>
			</div>
			<div class="source-item-outter">
				<div class="source-item">扫二维码</div>
			</div>
			<div class="source-item-outter">
				<div class="source-item">其他</div>
			</div>
		</div>
	</div>
	<div class="btn-register" id='submit'>完成注册</div>
	<div class="foot-info">
		注册即表示同意<a href="${host.base}/html/agreement_user.html">雷门易用户协议</a><br/>及时了解服务讯息，请关注【雷门易测算平台】公众号
	</div>
</body>
</html>