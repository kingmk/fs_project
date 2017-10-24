 <#import "/common/host.ftl" as host>
<html>
<title>注册雷门易</title>
<head>
	<script src="${host.js}/js/rem.js?${host.version}"></script>
    <script src="${host.js}/js/jquery-1.11.3.min.js"></script>
    <script src='${host.js}/js/jquery.sendCode.js?${host.version}'></script>
    <script src="${host.js}/js/common.js?${host.version}"></script>
    <link rel="stylesheet" href="${host.css}/css/bgmask.css?${host.version}">
    <style>
input{ -webkit-tap-highlight-color:rgba(0,0,0,0);}
input:-webkit-autofill {background: rgba(0,0,0,0) ;-webkit-box-shadow: 0 0 0px 1000px white inset; }
.bgmask-footer .button{background: #d88c3a!important;}
.sendcode-mask { position: fixed; top: 0; bottom: 0; right: 0; left: 0; background: rgba(0, 0, 0, 0.7); z-index: 99; }
.bgmask-wrap { width: 25rem; min-height: 20rem; background: #fff; margin: auto; position: relative; top: 15rem; left: 0; right: 0; overflow: hidden; }
.bgmask-wrap .bgmask-header { position: absolute; text-align: center; width: 100%; height: 5rem; line-height: 5rem; font-size: 1.6rem; font-weight: bold;}
.bgmask-wrap .bgmask-body { padding-top: 5rem; width: 100%; }
.bgmask-wrap .bgmask-body .form { width: 100%; padding: 0 2rem; box-sizing: border-box; margin-top: 1.5rem; }
.bgmask-wrap .bgmask-body .form .form-item { display: flex; width: 100%; border-bottom: 1px solid #ddd; padding: 0.5rem 0; }
.bgmask-wrap .bgmask-body .form input { border: none; outline: none; width: 100%; font-size: 1.4rem; }
.bgmask-wrap .bgmask-body .form .form-item.code { height: 4.5rem; padding: 1.5rem 0 0; box-sizing: border-box; }
.bgmask-wrap .bgmask-body .form .form-item .form-code { font-size: 1.2rem; line-height: 2.95rem; color: #d88c3a!important; text-align: center; flex-grow: 1; min-width: 7rem; position: relative; }
.bgmask-wrap .bgmask-body .form .form-item .form-code:before { content: ''; position: absolute; left: 0; top: 0.5rem; bottom: 0.5rem; border-left: 1px solid #ddd; }
.bgmask-wrap .bgmask-footer .button { width: 100%; height: 4.5rem; text-align: center; line-height: 4.5rem; color: #fff; font-size: 1.6rem; }
.bgmask-wrap .bgmask-footer { padding: 2.5rem; }
.form-code.disabled{color: #999!important;}
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
	  sendCode({mobile:$("#mobile").val()},function(data){
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
 	$("#submit").on("click",checkBtn);
 	$("#codeInput").on("input propertychange", function(){
 		var val = $(this).val().replace(/[^0-9]/,'').substr(0,6);
 		$(this).val(val);
 	})
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

 function checkBtn(){
 	$.ajax({
	    type: "POST",
	    url: "${host.base}/usr/register/mobile_check_code",
	    data:  {mobile: $("#mobile").val()  , code : $("#codeInput").val() },
	     dataType: "json",
	    success: function(data){
				if(data.head.code == "0000"){
					mAlert.addAlert('注册成功');
					if("${backUrl}" !="" ){
						window.location.href = "${host.base}${backUrl}";
					}
				}else{
						mAlert.addAlert(data.head.msg,2000);
				}
	    },
	    error: function(res){
	    }
	});
 }
 </script>
</head>
<body>
<div class="sendcode-mask">
	<div class="bgmask-wrap">
		<div class="bgmask-header">免费注册</div>
		<div class="bgmask-body">
			<div class="form" id="form">	
				<div class="form-item">
					<input type="text" name="mobile" id="mobile" placeholder="手机号">
				</div>
				<div class="form-item code">
					<input type="text" name="code" id="codeInput" placeholder="验证码">
					<span class="form-code" id="code">获取验证码</span>
				</div>
			</div>
		</div>
		<div class="bgmask-footer"><div class="button" id='submit'>注册</div></div></div></div>
</body>
</html>
