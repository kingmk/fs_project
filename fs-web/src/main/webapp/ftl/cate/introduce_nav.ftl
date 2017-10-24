<#import "/common/host.ftl" as host>
<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">
<title>运势咨询</title>
<script src="${host.js}/js/rem.js?${host.version}"></script>
<script src="${host.js}/js/jquery-1.11.3.min.js"></script>
<script src="${host.js}/js/common.js?${host.version}"></script>
<script src="${host.js}/js/jquery.tmpl.min.js"></script>
<link rel="stylesheet" href="${host.css}/css/consultation.css?${host.version}">
<link rel="stylesheet" href="${host.css}/css/bgmask.css?${host.version}">
<style type="text/css">
.bgmask .bgmask-wrap {top: 8rem;}
.bgmask .bgmask-wrap .bgmask-body{padding-left: 1.5rem; padding-right: 1.5rem;}
.bgmask .bgmask-wrap .bgmask-body .title {line-height: 2.2rem;}
.bgmask .bgmask-wrap .bgmask-body .text {text-align: left;}
</style>
<script>
$(function(){
    $.initUserFooter({activedIndex:0,bubbleNum:0 });
    chartUnreadNum('${host.base}');
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
    <div class='content'>
    <#list zxCateList as item>
        <#if item.parentId == 10005>
        <a class='card' href='javascript:showOfflineNote()'>
            <img src="${host.img}/images/indexImg/${item.id}.jpg?${host.version}">
        </a>
        <#else>
        <a class='card' href='${host.base}/usr/search/master_nav?zxCateId=${item.id}'>
            <img src="${host.img}/images/indexImg/${item.id}.jpg?${host.version}">
        </a>
        </#if>
    </#list>
    </div>
</body>

</html>
