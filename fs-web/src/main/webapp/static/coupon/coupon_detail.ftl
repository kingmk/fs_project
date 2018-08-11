<#import "/common/host.ftl" as host>
<#import "/common/funUtils.ftl" as funUtils>
<html>
<head>
<meta charset="UTF-8">
<title>优惠券详情</title>
<script src="${host.js}/js/rem.js?${host.version}"></script>
<script src="${host.js}/js/jquery-1.11.3.min.js"></script>
<script src="${host.js}/js/jquery.tmpl.min.js"></script>
<script src="${host.js}/js/common.js?${host.version}2"></script>
<link rel="stylesheet" href="${host.css}/css/coupon.css?${host.version}5">

<script type="text/javascript">
$(document).ready(function() {
	<#if result.body.descStatus=="可使用">
	$("#btnUse").click(function() {
		location.href = "${host.base}/cate/introduce_nav";
	})
	</#if>
})
</script>

</head>
<body class="coupon-detail <#if result.body.descStatus!="可使用"> disabled</#if>">
	<div class="top-block-detail">
		<div class="type">${result.body.descType}</div>
		<div class="price"><span class="sign">￥</span>${funUtils.formatNumber(result.body.discountAmt/100,"###,##0.00","--")}</div>
	</div>
	<div class="intro">
		<div class="intro-title">优惠券名称</div>
		<div class="intro-desc">${result.body.name}</div>
		<div class="intro-title">适用范围</div>
		<div class="intro-desc">${result.body.descCate}</div>
		<div class="intro-title">使用有效期</div>
		<div class="intro-desc">${result.body.descLastUseTime}</div>
		<div class="intro-title">详细规则</div>
		<div class="intro-desc">${result.body.rules}</div>
	</div>
	<#if result.body.descStatus!="可使用">
	<div id="btnUse" class="button disabled">${result.body.descStatus}</div>
	<#else>
	<div id="btnUse" class="button">立即使用</div>
	</#if>
</body>
</html>