 <#import "/common/host.ftl" as host>
 <#import "/common/funUtils.ftl" as funUtils>
<html>
<head>
 <meta charset="UTF-8">
    <title>订单确认</title>
    <script src="${host.js}/js/rem.js?${host.version}"></script>
    <script src="${host.js}/js/jquery-1.11.3.min.js"></script>
    <script src="${host.js}/js/common.js?${host.version}"></script>
    <link rel="stylesheet" href="${host.css}/css/confirm_nav.css?${host.version}">
    <script src="${host.js}/js/components.js?${host.version}"></script>
 <script>
 $(function(){
 	//模拟微信统一下单
 	$("#unifiedorder_weixin").click(function(){
 				$.ajax({
				    type: "POST",
				    url: "${host.base}/order/unifiedorder_weixin",
				    data: {masterInfoId:  ${masterInfo.id} , masterServiceCateId: ${masterServiceCate.id}} ,
				    dataType: "json",
				    success: function(data){
						console.log(data);
						if(data.head.code == "0000"){
							weixinPay(data.body);
						}else{
						   <#--0003 不在服务状态;0002 不能刷单;0001 参数错误;9999 系统错误  -->
							mAlert.addAlert(data.head.msg);
						}
				    },
				    error: function(res){
				    }
				});
 	});
 });

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
		<div class="text">¥   ${funUtils.formatNumber(masterServiceCate.amt/100,"###,##0.00","--")}</div>
	</div>
</div>
</div>
<div class="list">
<div class="list-header">
	支付方式
</div>
	<div class="list-body">
		<div class="list-item">
			<div class="list-icon weixin"></div>
			<div class="list-label">微信支付</div>
			<div class="list-control" style="margin-right: 0;">
                <div class="text-right" style="font-size: 1.6rem; padding-right: 0;">¥ ${funUtils.formatNumber(masterServiceCate.amt/100,"###,##0.00","--")}</div>
            </div>
		</div>
	</div>
</div>
<div class="button-box" id="unifiedorder_weixin">
	<div class="button">确认支付</div>
</div>
<div class="tips">
	支付成功后即可与老师对话
</div>
</body>
</html>
