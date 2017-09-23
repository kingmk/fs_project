 <#import "/common/host.ftl" as host>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <script src="${host.js}/js/rem.js?${host.version}"></script>
    <script src="${host.js}/js/jquery-1.11.3.js"></script>
    <script src="${host.js}/js/components.js?${host.version}"></script>
    <link rel="stylesheet" href="${host.css}/css/star.css?${host.version}">
    <link rel="stylesheet" href="${host.css}/css/user_rating_star.css?${host.version}">
    <title>用户评价</title>
</head>

<body style='background: #fff;'>
    <div class="user-comments-content">
        <div class="header">
            <img src="${result.body.buyUsrHeadImgUrl}" />
            <div class="title">${result.body.buyUsrName}</div>
            <div class="text">${result.body.goodsName}|¥ ${result.body.payRmbAmtDesc}</div>
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
    <div class="rating-content">
        <div class="quote"><img src="${host.img}/images/quote.png"/></div>
        <div class="text">${result.body.evaluateWord!?html}</div>
    </div>
    <div class="fix button" id = "goOrderChatPage">返回订单详情</div>
<script>
    $(function(){
    	$("#goOrderChatPage").click(function(){
    			location.href = "${host.base}/order/chat_index?chatSessionNo=${result.body.chatSessionNo}&orderId=${result.body.orderId}";
    	});
	    $("#responseSpeed").star({
	        type: 'show',
	        tip:'${result.body.respSpeed}'
	    })
	    $("#professionalLevel").star({
	        type: 'show',
	        tip:'${result.body.majorLevel}'
	    })
	    $("#serviceSAttitude").star({
	       type: 'show',
	        tip:'${result.body.serviceAttitude}'
	    })
    });
</script>
</body>

</html>
