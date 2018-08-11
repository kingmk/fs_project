<#import "/common/host.ftl" as host>
<#import "/common/funUtils.ftl" as funUtils>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>我的优惠券</title>
<script src="${host.js}/js/rem.js?${host.version}"></script>
<script src="${host.js}/js/jquery-1.11.3.min.js"></script>
<script src="${host.js}/js/jquery.tmpl.min.js"></script>
<script src="${host.js}/js/dropload.min.js"></script>
<script src="${host.js}/js/common.js?${host.version}"></script>
<link rel="stylesheet" href="${host.css}/css/coupon.css?${host.version}19">
<link rel="stylesheet" type="text/css" href="${host.css}/css/dropload.css?v=3">
<#noparse>
<script id='couponTmpl' type="text/x-jquery-tmpl">
	<div class="coupon{{if descStatus != "可使用"}} disabled{{/if}}" data-id="${id}">
		<div class="coupon-main">
			<div class="coupon-name">${name}</div>
			<div class="coupon-desc">满${descPayAmtMin}可用</div>
			{{if descStatus == "已使用"}}
			<div class="coupon-desc">已于&nbsp;${descUsedTime}&nbsp;使用</div>
			{{else descStatus == "已过期"}}
			<div class="coupon-desc">已于&nbsp;${descLastUseTime}&nbsp;过期</div>
			{{else}}
			<div class="coupon-desc">有效期至&nbsp;${descLastUseTime}</div>
			{{/if}}
			<div class="coupon-right">
				<div class="coupon-discount"><span class="coupon-sign">￥</span>${descDiscountAmt}</div>
				{{if descStatus != "可使用"}}<div class="coupon-desc">${descStatus}</div>{{/if}}
			</div>
		</div>
		<div class="coupon-sep">
			<div class="circle left"></div>
			<div class="circle right"></div>
		</div>
		<div class="coupon-foot">
			<div class="coupon-desc coupon-cates">${descCate}</div>
		</div>
	</div>
</script>
</#noparse>

<script type="text/javascript">
var page = 0;
var pagesize = 10;
var usable = "Y";
var dropload = null;

$(document).ready(function() {

	$(".tab").click(function(){
		changeTab($(this));
	});
	// $("#tab-usable").click();

	dropload = $(".content").dropload({
		scrollArea: window,
		domDown: {
			domClass: 'dropload-down',
			domLoad: '<div class="dropload-load"><span class="loading"></span>加载中</div>',
			domRefresh: '',
			domNoData: '<div class="dropload-noData">到底了</div>'
		},
		loadDownFn: function(me) {
			fetchList(function(data) {
				afterFetch(data);
			})
		}
	})
})

function changeTab(domClick) {
	if (domClick.hasClass("selected")) {
		return;
	};
	$(".tab.selected").removeClass("selected");
	domClick.addClass("selected");

	if (domClick.attr("id") == "tab-usable") {
		usable = "Y";
	} else {
		usable = "N";
	}
	page = 0;
	$(".content .list").empty();

	fetchList(function(data) {
		afterFetch(data);
	})
}

function fetchList(callback) {
	$.ajax({
		url: "${host.base}/coupon/my_coupons_ajax",
		method: "GET",
		data: {
			usable: usable,
			page: page,
			pagesize: pagesize
		},
		dataType: "json",
		success: function(data) {
			if (data.head.code != "0000") {
				mAlert.addAlert(data.head.msg);
			} else {
				callback(data);
			}
		}
	})
}

function afterFetch(data) {
	var list = data.body.list;
	if (list.length < pagesize) {
		dropload.lock();
		dropload.noData();
	} else {
		dropload.unlock();
		dropload.noData(false);
	}
	page = page+1;
	updateList(data);
	dropload.resetload();
}

function updateList(data) {
	var couponList = data.body.list;
	var total = data.body.total;

	if (total == 0) {
		$(".content .empty").show();
		$(".dropload-down").hide();
		return;
	};
	$(".content .empty").hide();

	var domList = $(".content .list");
	for (var i = 0; i < couponList.length; i++) {
		var coupon = couponList[i];
		var domCoupon = $('#couponTmpl').tmpl(coupon);
		domCoupon.click(function() {
			var id = $(this).attr("data-id");
			location.href = "${host.base}/coupon/coupon_detail?couponId="+id;
		})
		domList.append(domCoupon);
	};
}

</script>
</head>

<body>
	<div class="tabbar clearfix">
		<div class="tab selected" id="tab-usable">
			<div class="tab-title">${result.body.total}张优惠券</div>
			<div class="tab-btm"><div class="tab-line"></div></div>
		</div>
		<div class="tab" id="tab-unusable">
			<div class="tab-title">已使用&amp;已过期</div>
			<div class="tab-btm"><div class="tab-line"></div></div>
		</div>
	</div>
	<div class="content">
		<div class="empty" style="display:none">
			<img src="${host.img}/images/coupon/img_empty.png">
			<div class="desc">暂无优惠券</div>
		</div>
		<div class="list">
		</div>
	</div>

</body>

</html>