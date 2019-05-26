<#import "/common/host.ftl" as host>
<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">
<title>运势咨询</title>
<script src="${host.js}/js/rem.js?${host.version}"></script>
<script src="${host.js}/js/jquery-1.11.3.min.js"></script>
<script src="${host.js}/js/common.js?${host.version}2"></script>
<script src="${host.js}/js/jquery.tmpl.min.js"></script>
<script src="${host.js}/js/swiper.jquery.min.js?t=1"></script>
<link rel="stylesheet" href="${host.css}/css/consultation.css?${host.version}8">
<link rel="stylesheet" href="${host.css}/css/bgmask.css?${host.version}">
<link rel="stylesheet" href="${host.css}/css/swiper.min.css">
<style type="text/css">
.bgmask .bgmask-wrap {top: 8rem;}
.bgmask .bgmask-wrap .bgmask-body{padding-left: 1.5rem; padding-right: 1.5rem;}
.bgmask .bgmask-wrap .bgmask-body .title {line-height: 2.2rem;}
.bgmask .bgmask-wrap .bgmask-body .text {text-align: left;}
</style>
<script>
<#if (banners?size>0) >
var banners = ${bannerStr};
var eSwiper = null;

function initBanners(banners){
    var images = [];
    for (var i = 0; i < banners.length; i++) {
        images.push(banners[i].imgurl);
    };
    var onclick = function(actIdx) {
        window.open(banners[actIdx].url);
    };
    eSwiper = new ESwiper(".banners", images, 240.0, onclick, {delay: 4000, disableOnInteraction:false}, true);
}

</#if>



$(function(){
    $.initUserFooter({activedIndex:0,bubbleNum:0 });
    chatUnreadNum('${host.base}');

    <#if (banners?size>0) >
    setTimeout("initBanners(banners)", 100);
    </#if>
})

function showOfflineNote() {
    $.bgmask({
        hasClose: false,
        hasHeader: false,
        title: "由于需要实地勘察，请发送邮件至service@billinn.cn进行咨询",
        text: "请您提供业主（居住者）的出生年月日时、房型图（面积、朝向）、建造时间，适合联系您的时间，将由客服与您联系。谢谢",
        type: "normal",
        buttonTxt: "我知道了"
    })
}

</script>
</head>

<body id='consultation'>
    <#if (banners?size>0) >
    <div class="banners"></div>
    </#if>
    <div class='content'>
    <#list zxCateList as item>
        <#if item.parentId == 10005>
        <a class='card' href='javascript:showOfflineNote()'>
            <img src="${host.img}/images/indexImg/${item.id}.jpg?${host.version}">
            <span class="separator"></span>
        </a>
        <#elseif item.id == 100024>
        <#elseif item.id == 100008>
        <#else>
        <a class='card' href='${host.base}/usr/search/master_nav?zxCateId=${item.id}'>
            <img src="${host.img}/images/indexImg/${item.id}.jpg?${host.version}">
            <span class="separator"></span>
        </a>
        </#if>
    </#list>
    </div>
</body>

</html>
