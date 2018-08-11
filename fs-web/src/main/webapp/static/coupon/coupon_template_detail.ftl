<#import "/common/host.ftl" as host>
<#import "/common/funUtils.ftl" as funUtils>
<html>
<head>
<meta charset="UTF-8">
<title>领取优惠券</title>
<script src="${host.js}/js/rem.js?${host.version}"></script>
<script src="${host.js}/js/jquery-1.11.3.min.js"></script>
<script src="${host.js}/js/jquery.tmpl.min.js"></script>
<script src="${host.js}/js/common.js?${host.version}2"></script>
<link rel="stylesheet" href="${host.css}/css/coupon.css?${host.version}10">

<script type="text/javascript">
var isRegistered = "${result.body.isRegistered}";
$(document).ready(function() {

    $("#btnFetch").click(clickFetch);
    $("#btnHome").click(function() {
       location.href = "http://www.leimenyi.com.cn/enter/weixin?_goTo=/cate/introduce_nav";
    })
})

function clickFetch() {
    var domBtn = $("#btnFetch");
    if (domBtn.hasClass("disabled")) {
        return;
    };

    if (isRegistered == "N") {
        $.bgmask({
            title: "请先注册",
            text: "只有注册后才可以领取",
            type: "normal",
            buttonTxt: "去注册",
            buttonFn: function() {
                location.href = "${host.base}/usr/register/mobile_nav?backUrl="+encodeURIComponent(location.href);
            }
        })
        return;
    };
    $.ajax({
        url: "${host.base}/coupon/fetch_ajax",
        method: "POST",
        data: {
            templateId: ${result.body.id}
        },
        dataType: "json",
        success: function(data) {
            if (data.head.code != "0000") {
                mAlert.addAlert(data.head.msg);
                var f = function() {
                    location.reload();
                }
                setTimeout(f, 1500);
            } else {

                $.bgmask({
                    title: "领取成功",
                    text: "您领到了一张优惠券，快在去个人中心看看吧~",
                    type: "normal",
                    buttonTxt: "去个人中心",
                    hasClose: false,
                    canRemove: false,
                    buttonFn: function() {
                        location.href = "${host.base}/usr/common/my";
                    }
                })

            }
        },
        error: function(res) {

        }
    })
}
</script>
</head>

<body class="coupon-tmpl">
    <div class="top-block">
        <div class="name">${result.body.name}</div>
        <div class="price"><span class="sign">￥</span>${funUtils.formatNumber(result.body.discountAmt/100,"###,##0.00","--")}</div>
        <div class="fetch-time">${result.body.descFetchDate}</div>
    </div>
    <div class="intro">
        <div class="intro-title">适用范围</div>
        <div class="intro-desc">${result.body.descCate}</div>
        <div class="intro-title">使用有效期</div>
        <div class="intro-desc">${result.body.descValidDate}</div>
        <div class="intro-title">详细规则</div>
        <div class="intro-desc">${result.body.rules}</div>
        <div id="btnFetch" class="button<#if result.body.strButton!="立即领取"> disabled</#if>">${result.body.strButton}</div>
        <div id="btnHome" class="button home">去雷门易测算</div>
    </div>
</body>
</html>