<#import "/common/host.ftl" as host>
<#import "/common/funUtils.ftl" as funUtils>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>订单确认</title>
<script src="${host.js}/js/rem.js?${host.version}"></script>
<script src="${host.js}/js/jquery-1.11.3.min.js"></script>
<script src="${host.js}/js/common.js?${host.version}1"></script>
<link rel="stylesheet" href="${host.css}/css/confirm_nav.css?${host.version}5">
<link rel="stylesheet" href="${host.css}/css/coupon.css?${host.version}3">
<script src="${host.js}/js/components.js?${host.version}"></script>



<script>
var orderAmt = ${orderAmt};
var discountAmt = 0;
var coupons = ${strCoupons};
var couponSelId = -1;
var couponIdMap = {};
<#if countUsable != 0>
discountAmt = ${couponSel.discountAmt};
var couponSelId = ${couponSel.id};
for (var i = 0; i < coupons.length; i++) {
	var coupon = coupons[i];
	couponIdMap[coupon.id] = coupon;
};
</#if>


$(document).ready(function(){

	updateCouponPay();
	selectCoupon(couponSelId);
	$(".coupon").click(function(){
		selectCoupon($(this).attr("data-id"));
	})

	$(".list-coupon").click(function() {
		$(".page-pay").hide();
		$(".page-coupons").show();
	})

	$("#btnSelCoupon").click(function() {
		updateCouponPay();
		$(".page-coupons").hide();
		$(".page-pay").show();
	})

	//模拟微信统一下单
	$("#unifiedorder_weixin").click(function(){
		var data = {
			masterInfoId: ${masterInfo.id},
			masterServiceCateId: ${masterServiceCate.id}
		};
		if (couponSelId!=-1) {
			data.couponId = couponSelId;
		};
		$.ajax({
			type: "POST",
			url: "${host.base}/order/unifiedorder_weixin",
			data: data,
			dataType: "json",
			success: function(data){
				console.log(data);
				if(data.head.code == "0000"){
					weixinPay(data.body);
				}else{
					mAlert.addAlert(data.head.msg);
				}
			},
			error: function(res){
			}
		});
 	});
});

function selectCoupon(couponId) {
	var domSel = $("#coupon"+couponId);
	if (domSel.length <= 0) {
		return;
	};
	if (domSel.hasClass("disabled")) {
		return;
	};
	if (domSel.hasClass("selected")) {
		domSel.removeClass("selected");
		couponSelId = -1;
		discountAmt = 0;
		$("#btnSelCoupon").text("不使用优惠券");
	} else {
		$(".coupon.selected").removeClass("selected");
		domSel.addClass("selected");
		couponSelId = parseInt(couponId);
		var coupon = couponIdMap[couponId];
		discountAmt = coupon.discountAmt
		$("#btnSelCoupon").text("可优惠￥"+commonUtils.numberCurrencyFix(discountAmt/100));
	}
}

function updateCouponPay() {
	$("#payActHdr").html("￥&nbsp;"+commonUtils.numberCurrencyFix((orderAmt-discountAmt)/100));
	if (discountAmt > 0) {
		$(".list-coupon .discount").html("-￥&nbsp;"+commonUtils.numberCurrencyFix(discountAmt/100));
	} else {
		$(".list-coupon .discount").html("暂不使用优惠券");
	}
}

function weixinPay(bodyData){
	WeixinJSBridge.invoke(
		'getBrandWCPayRequest',{
			"appId" : bodyData.appId, //公众号名称，由商户传入
			"timeStamp" : bodyData.timeStamp, //时间戳
			"nonceStr" : bodyData.nonceStr, //随机串
			"package" : bodyData.package,//扩展包
			"signType" : bodyData.signType, //微信签名方式:MD5
			"paySign" : bodyData.paySign //微信签名
		},
		function(res){
			if(res.err_msg == "get_brand_wcpay_request:ok" ) {
					location.href ="${host.base}/order/order_pay_succ?orderId="+bodyData.orderId ;
			} else if (res.err_msg == "get_brand_wcpay_request:cancel"){
				mAlert.addAlert("支付已取消");
			} else if (res.err_msg == "get_brand_wcpay_request:fail") {
				mAlert.addAlert("支付失败，请确认您的余额充足");
			}
	});
}
</script>
</head>
<body>
<div class="page-pay">
	<div class="header">
		<div class="header-top">
			<div class="img">
				<img src="${masterInfo.headImgUrl}"/>
			</div>
			<div class="name">
				${masterNickName}老师即将为您服务
			</div>
		</div>
		<div class="header-bottom">
			<div class="item">
				<div class="type">服务类型</div>
				<div class="text">${masterServiceCate.name}</div>
			</div>
			<div class="item">
				<div class="type">共计花费</div>
				<div class="text" id="payActHdr"></div>
			</div>
		</div>
	</div>
	<div class="list">
		<div class="list-item first">
			<div class="list-label">订单金额</div>
			<div class="list-control">
				<div class="text-right">¥&nbsp;${funUtils.formatNumber(masterServiceCate.amt/100,"###,##0.00","--")}</div>
			</div>
		</div>
		<div class="list-item list-coupon">
			<div class="list-label">优惠券</div>
			<div class="list-control">
				<#if couponSel??>
				<div class="text-right"><span class="discount">&nbsp;</span><span class="icon-arrow"></span></div>
				<#else>
				<div class="text-right disabled">暂无优惠券可用<span class="icon-arrow"></span></div>
				</#if>
			</div>
		</div>
	</div>
	<div class="button-box" id="unifiedorder_weixin">
		<div class="button">确认支付</div>
	</div>
	<div class="tips">
		支付成功后即可与老师对话
	</div>
</div>
<div class="page-coupons" style="display:none;">
	<div class="desc-count">您有<span class="count-usable">${countUsable}</span>张优惠券可用</div>
	<#if (couponSize>0)>
	<div class="coupon-list">
	<#list coupons as coupon>
		<div class="coupon<#if coupon.useForOrder!='Y'> disabled</#if>" data-id="${coupon.id}" id="coupon${coupon.id}">
			<div class="coupon-main">
				<div class="coupon-name">${coupon.name}</div>
				<div class="coupon-desc">满${coupon.descPayAmtMin}可用</div>
				<div class="coupon-desc">有效期至&nbsp;${coupon.descLastUseTime}</div>
				<div class="coupon-right">
					<div class="coupon-discount"><span class="coupon-sign">￥</span>${coupon.descDiscountAmt}</div>
				</div>
			</div>
			<div class="coupon-sep">
				<div class="circle left"></div>
				<div class="circle right"></div>
			</div>
			<#if coupon.containsCate == "Y">
			<div class="coupon-foot">
				<div class="icon-sel"></div>
				<div class="coupon-desc">${coupon.descCate}</div>
			</div>
			<#else>
			<div class="coupon-foot">
				<div class="coupon-desc disabled">不可用于当前服务类型</div>
			</div>
			</#if>
		</div>
	</#list>
	</div>
	<div class="button" id="btnSelCoupon"><#if countUsable == 0>无优惠券可用，继续下单</#if></div>
	<#else>
	<div class="button" id="btnSelCoupon">无优惠券可用，继续下单</div>
	</#if>
</div>
</body>
</html>
