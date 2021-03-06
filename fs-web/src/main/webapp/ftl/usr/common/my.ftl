 <#import "/common/host.ftl" as host>
  <#import "/common/funUtils.ftl" as funUtils>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>我的账户</title>
<script src="${host.js}/js/rem.js?${host.version}"></script>
<script src="${host.js}/js/jquery-1.11.3.min.js"></script>
<script src="${host.js}/js/common.js?${host.version}"></script>
<link rel="stylesheet" href="${host.css}/css/user_mine.css?${host.version}3">
<link rel="stylesheet" href="${host.css}/css/bgmask.css?${host.version}2">
<style>
.img-logo-box{width: 7.5rem; height: 7.5rem ;border-radius: 50%; border: 0.1rem solid #fff; overflow: hidden ;position: relative; margin: 0 auto 2.5rem;}
.img-logo-box img{position: absolute; z-index: 9;}
</style>
<script>
$(function(){
  $.initUserFooter({activedIndex:3,bubbleNum:0})
  chatUnreadNum('${host.base}');

});

function goRegister() {
  var backUrl = "/usr/common/my";
  location.href = "/usr/register/mobile_nav?backUrl="+encodeURIComponent(backUrl);
}

function showCustomerService() {
  $.bgmask({
      title: "客服联系方式",
      text: "有任何问题，请发送邮件至<span style='color:#d68b38;'>service@billinn.cn</span>，会有专人为您解答，感谢您的支持！",
      type: "normal",
      buttonTxt: "知道了"
  })
}
</script>
</head>
<body>
<div class="header">
	<div class="header-center">
		<div class="img-logo-box"><img id="headImgUrl" src="${usrHeadImgUrl}" onload="commonUtils.adjustHeadImg(this, 7.5, 'headImgUrl')"/></div>
    <#if registerMobile?? && registerMobile != "">
      <div class="name">${usrNickName}</div>
    <#else>
      <div class="btn" onclick="goRegister()">登录</div>
    </#if>
	</div>
</div>
<div class="content">
<div class="list">
    <div class="list-body">
        <a class="list-item" href="${host.base}/coupon/my_coupons">
            <div class="list-icon icon-coupon"></div>
            <div class="list-label">我的优惠券</div>
            <div class="list-arrow"></div>
        </a>
        <a class="list-item" href="${host.base}/usr/common/personal_data">
            <div class="list-icon icon-mine"></div>
            <div class="list-label">个人资料</div>
            <div class="list-arrow"></div>
        </a>
        <a class="list-item" href="${host.base}/usr/common/follow_list_nav?currentPage=0&perPageNum=20">
            <div class="list-icon icon-follow"></div>
            <div class="list-label">我的关注</div>
            <div class="list-arrow"></div>
        </a>
    </div>
</div>
<div class="list">
    <div class="list-body">
        <a class="list-item" href="${host.base}/html/agreement_user.html">
            <div class="list-icon icon-agreement"></div>
            <div class="list-label">用户协议</div>
            <div class="list-arrow"></div>
        </a>
        <a class="list-item" href="javascript:showCustomerService()">
            <div class="list-icon icon-service"></div>
            <div class="list-label">联系客服</div>
            <div class="list-arrow"></div>
        </a>
    </div>
</div>
</div>
</body>
</html>
