<#import "/common/host.ftl" as host>
<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">
<script src="${host.js}/js/rem.js?${host.version}"></script>
<script src="${host.js}/js/jquery-1.11.3.min.js"></script>
<script src="${host.js}/js/common.js?${host.version}"></script>
<script src="${host.js}/js/components.js?${host.version}"></script>
<link rel="stylesheet" href="${host.css}/css/star.css?${host.version}">
<link rel="stylesheet" href="${host.css}/css/user_rating_star.css?${host.version}1">
<title>用户评价</title>

<script type="text/javascript">
var backUrl = commonUtils.getUrlParam('backUrl');
$(function(){
    $("#back").on('click',function(){
        if(backUrl){
            location.href = '${host.base}'+backUrl;
        }else{
            location.href = "${host.base}/order/chat_index?chatSessionNo=${result.body.chatSessionNo}&orderId=${result.body.orderId}";
        }
    });

    <#if result.body.hadEvaluate=="N">
    $("#responseSpeed").star({
        type: 'score'
    })
    $("#professionalLevel").star({
        type: 'score',
    })
    $("#serviceSAttitude").star({
        type: 'score',
    })

    $(".anonymous .checkbox").click(switchAnonymous);
    $(".anonymous .label").click(switchAnonymous);

    $("#editWord").on('input propertychange', function() {
         if ($(this).val().length > 200) {
            $(this).val( $(this).val().substring(0,200));
        }
        $(this).siblings('.tips').html('还可填写' + (200 - $(this).val().length) + '字');
    })
    $('#btnEvaluate').click(function(){
        if(!$("#responseSpeed").attr('tip') || !$("#professionalLevel").attr('tip') ||　!$("#serviceSAttitude").attr('tip')){
            mAlert.addAlert('评分不能为空');
            return;
        }
        if(!$("#editWord").val()){
            mAlert.addAlert('评价不能为空');
            return;
        }
        var domCheck = $(".anonymous .checkbox");
        var isAnonymous = 0;
        if (domCheck.hasClass("on")) {
            isAnonymous = 1;
        };
        $.ajax({
            type: "POST",
            url: "${host.base}/order/evaluate/common_ajax_submit",
            data: {
                orderId:  "${result.body.orderId}" ,
                respSpeed:  $("#responseSpeed").attr('tip')/2 ,
                majorLevel:   $("#professionalLevel").attr('tip')/2,
                serviceAttitude:  $("#serviceSAttitude").attr('tip')/2,
                evaluateWord:  $("#editWord").val(),
                isAnonymous: isAnonymous
            },
            dataType: "json",
            success: function(data){
                if(data.head.code=='0000'){
                    mAlert.addAlert('评价成功');
                    setTimeout(function(){
                        if(backUrl){
                            location.href = '${host.base}'+backUrl;
                        }else{
                            location.href = "${host.base}/order/chat_index?chatSessionNo=${result.body.chatSessionNo}&orderId=${result.body.orderId}";
                        }
                    },2000)
                }else{
                    mAlert.addAlert(data.head.msg);
                }
            },
            error: function(res){
            }
        });
    });
    <#else>
    $("#responseSpeed").star({
        type: 'show',
        tip: '${result.body.evaluateInfo.respSpeed* 2}'
    });
    $("#professionalLevel").star({
        type: 'show',
        tip: '${result.body.evaluateInfo.majorLevel* 2 }'
    });
    $("#serviceSAttitude").star({
        type: 'show',
        tip: '${result.body.evaluateInfo.serviceAttitude* 2}'
    });
    </#if>
})

function switchAnonymous() {
    var domCheck = $(".anonymous .checkbox");
    if (domCheck.hasClass("on")) {
        domCheck.removeClass("on");
    } else {
        domCheck.addClass("on");
    }
}

</script>

</head>

<body style='background: #fff; padding-bottom: 120px;'>
    <div class="user-comments-content">
        <div class="header">
            <img src="${result.body.masterHeadImgUrl}" />
            <div class="title">${result.body.masterName}</div>
        </div>
        <div class="score-box clearfix">
            <div class="score-item">
                <span class="score-item-tips">响应速度</span>
                <div id="responseSpeed" style="display: inline-block"></div>
            </div>
            <div class="score-item">
                <span class="score-item-tips">专业水平</span>
                <div id="professionalLevel" style="display: inline-block"></div>
            </div>
            <div class="score-item">
                <span class="score-item-tips">服务态度</span>
                <div id="serviceSAttitude" style="display: inline-block"></div>
            </div>
        </div>
    </div>
 <#if result.body.hadEvaluate=="N">
    <div class="edit-text-box">
        <div class="edit-text">
            <textarea id='editWord' placeholder='如果您喜欢大师，就表扬几句吧'></textarea>
            <span class="tips">还可填写200字</span>
        </div>
    </div>
    <div class="button-box">
        <div class="button" id='btnEvaluate'>发表评价</div>
    </div>
    <div class="anonymous">
        <div class="checkbox"></div>
        <div class="label">匿名评价</div>
    </div>
<#else>
    <div class="rating-content">
        <div class="rating-icon"><img src="${host.img}/images/icon_evaluate.png?${host.version}"/></div>
        <div class="text"><#if result.body.evaluateInfo ?? && result.body.evaluateInfo.evaluateWord ??>${result.body.evaluateInfo.evaluateWord?html}</#if></div>
    </div>
    <#if result.body.evaluateInfo?? && result.body.evaluateInfo.masterReplyWord??>
    <div class="rating-content">
        <div class="rating-icon"><img src="${host.img}/images/icon_evaluate_master_reply.png"/></div>
        <div class="text reply">老师回复：${result.body.evaluateInfo.masterReplyWord}</div>
    </div>
    </#if>
    <a class="fix button" id='back'>返回</a>
</#if>

</body>

</html>
