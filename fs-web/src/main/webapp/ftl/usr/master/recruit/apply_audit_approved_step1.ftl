<#import "/common/host.ftl" as host>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>申请结果</title>
    <script src="${host.js}/js/rem.js?${host.version}"></script>
    <script src="${host.js}/js/jquery-1.11.3.min.js"></script>
    <link rel="stylesheet" href="${host.css}/css/apply.css?${host.version}">
</head>
<body style="background: #fff;">
<div class="apply-card">
    <div class="apply-icon apply-success"></div>
    <div class="apply-2line-title">${fsMasterInfo.name}<br/>恭喜您成为新晋风水师</div>
    <div class="apply-tips">完成以下步骤后开始接单</div>
</div>
<div class="apply-content">
    <dl class="step-bar">
        <div class="step-bar-line"></div>
        <div class="step-bar-item">
            <div class="step-bar-item-num">1</div>
            <div class="step-bar-item-tips">完善个人资料</div>
        </div>
        <div class="step-bar-item">
            <div class="step-bar-item-num">2</div>
            <div class="step-bar-item-tips">设置服务/价格</div>
        </div>
        <div class="step-bar-item">
            <div class="step-bar-item-num">3</div>
            <div class="step-bar-item-tips">开始接单</div>
        </div>
    </dl>
</div>
<div class="fix-button button" style="line-height: 6rem!important; height: 6rem!important;" onclick="location.href='${host.base}/usr/master/recruit/apply_info_supply_nav?masterInfoId=${fsMasterInfo.id}&isInit=true';">第一步完善个人资料</div>
</body>
</html>