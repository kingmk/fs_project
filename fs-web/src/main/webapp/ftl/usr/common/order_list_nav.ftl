 <#import "/common/host.ftl" as host>
  <#import "/common/funUtils.ftl" as funUtils>
<html>
<head>
   <meta charset="UTF-8">
    <title>我的订单</title>
    <script src="${host.js}/js/rem.js?${host.version}"></script>
    <script src="${host.js}/js/jquery-1.11.3.min.js"></script>
    <script src="${host.js}/js/jquery.tmpl.min.js"></script>
    <script src="${host.js}/js/dropload.min.js"></script>
    <script src="${host.js}/js/common.js?${host.version}"></script>
    <link rel="stylesheet" type="text/css" href="${host.css}/css/dropload.css">
    <link rel="stylesheet" href="${host.css}/css/my_order.css?${host.version}">


</head>
<body>
<div class="content">
    <div class="lists">
    <#noparse>
        <script id='itemTmpl' type="text/x-jquery-tmpl">
        		<div class="item">
                <div class='top'  onclick="goOrderChatIndex('/order/chat_index?chatSessionNo=${chatSessionNo}&orderId=${orderId}')">
                    <div class='left-box'>
                        <div class="left"><img src="${masterHeadImgUrl}"></div>
                        <div class="center">
                            <div class="title">${masterNickName}</div>
                            <div class="price">${goodsName} | ¥ ${payRmbAmtDesc}</div>
                            <div class="text">{{if lastReplyInfo != null }}
                                {{if lastReplyInfo.msgType == 'text'}}
                                    ${lastReplyInfo.content}
                                {{else}}
                                    [图片]
                                {{/if}}
                            {{/if}}</div>
                        </div>
                    </div>
                    <div class="right">
                        <div class="top-time">${lastChatTimeStr}</div>
                        {{if unReadNum >0}}
                        	 <div class="bubbel num">{{if unReadNum > 99}}...{{else}}${unReadNum}{{/if}}</div>
                        {{else}}
						<div class="bubbel ${status} wait${isWaitMasterService}">
	                        {{if status == 'completed' || status == 'settlementing' || status == 'settlemented' || status == 'settlement_fail'}}已结束
	                        {{else status == 'pay_succ' && isWaitMasterService == "Y"}}待接单
                            {{else status == 'pay_succ' && isWaitMasterService == "N"}}服务中
	                        {{else status == 'refunded'}}已退款
	                        {{else status == 'refund_fail'}}退款失败
	                        {{else status == 'refund_applied' || status == 'refunding'  }}退款申请中
	                        {{/if}}
                        </div>
                        {{/if}}
                    </div>
                </div>
                <div class="bottom">
                    <div class="bottom-time">下单时间：${createTime}</div>
                    {{if isCanEvaluate =='Y' && hadEvaluate=="N" }}
                    	<div class="bottom-button go-evaluate" onclick="goToorderEvaluate(${orderId})">去评价</div>
                    {{/if}}
                    {{if hadEvaluate =='Y' }}
                    	<div class="bottom-button see-evaluate" onclick="goToorderEvaluate(${orderId})">查看评价</div>
                    {{/if}}
                </div>
			</div>
        </script>
        </#noparse>
    </div>
</div>
<script>
var page = 0;
var size = 10;
$(function(){
	$.initUserFooter({activedIndex:2});
    chartUnreadNum('${host.base}');
	$('.content').dropload({
	    scrollArea: window,
	    domDown: {
	      domClass: 'dropload-down',
	      domLoad: '<div class="dropload-load"><span class="loading"></span>加载中</div>',
	      domNoData: '<div class="dropload-noData">到底了</div>'
	    },
	    loadDownFn  : function(me){
            $.ajax({
                type: 'POST',
                url: '${host.base}/usr/common/order_list_ajax_query',
                dataType: 'json',
                data: {
				  currentPage  : page,
				  perPageNum   : size
				},
                success: function(data){
                	page ++;
                	if(data.head.code == '1000' && page ==1){
                        me.lock();
                        me.noData();
                        me.resetload();
                        $(".dropload-down").hide();
                        $("body").emptyBox({iconImg:'${host.img}/images/no_order.png',title:'暂无订单'})
                    }else if (data.head.code == "1000" && page !=1) {
                        me.lock();
                        me.noData();
                        me.resetload();
                    }else if(data.body.data.length > 0){
                    	 $("#itemTmpl").tmpl(data.body.data).appendTo(".lists");
                    // 如果没有数据
                    }else{
                        // 锁定
                        me.lock();
                        // 无数据
                        me.noData();
                    }
                     me.resetload();
                },
                error: function(xhr, type){
                    // alert('Ajax error!');
                    // 即使加载出错，也得重置
                    me.resetload();
                }
            });
	    },
	    threshold: 50
  })
})
function goToorderEvaluate(orderId){
	location.href = "${host.base}/order/evaluate/common_view?orderId="+orderId+"&backUrl="+'/usr/common/order_list_nav';
}
function goOrderChatIndex(url){
	location.href = "${host.base}"+url;
}
</script>
</body>
</html>
