<#import "/common/host.ftl" as host>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>感谢您的申请</title>
    <script src="${host.js}/js/rem.js?${host.version}"></script>
    <script src="${host.js}/js/jquery-1.11.3.min.js"></script>
    <link rel="stylesheet" href="${host.css}/css/apply.css?${host.version}">
    <style>
        .apply-title{padding:1.5rem 0 2rem!important;}
    </style>
<script>
        $(function(){
            var isFirst = '${isFirstAfterApply}';
            var status = '${fsMasterInfo.auditStatus}'
            if(status == 'ing'){
                if(isFirst == 'Y'){
                    $("#applyStatus").addClass('apply-success');
                }else{
                    $("#applyStatus").addClass('apply-loading');
                    $(".apply-title").html('正在审核中');
                    $(".apply-tips").html('请保持您尾号${mobileEnd4}的手机畅通<br/>即将与您取得联系')
                }
            }else{
                $("#applyStatus").addClass('apply-loading');
            }
            $("#iKonw").on("click", function(){
               WeixinJSBridge.call('closeWindow');
            })
        })
    </script>
</head>
<body>
    <div class="apply-card">
        <div class="apply-icon" id='applyStatus'></div>
        <div class="apply-title">申请成功</div>
        <div class="apply-tips">小二将于2个工作日内与您联系<br/>
            请保持您尾号${mobileEnd4}的手机畅通</div>
    </div>
<div class="apply-button"><div class="button" id='iKonw'>我知道了</div></div>
</body>
</html>