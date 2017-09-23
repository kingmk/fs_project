 <!DOCTYPE html>
<html lang="en">
<#import "/common/host.ftl" as host>
<#import "/common/funUtils.ftl" as funUtils>
<head>
    <meta charset="UTF-8">
    <title>我的收入</title>
    <script src="${host.js}/js/rem.js?${host.version}"></script>
 	<script type="text/javascript" src="${host.js}/js/jquery-1.11.3.min.js"></script>
    <script src="${host.js}/js/jquery.tmpl.min.js"></script>
    <script src="${host.js}/js/common.js?${host.version}"></script>
    <script src="${host.js}/js/dropload.min.js"></script>
    <link rel="stylesheet" href="${host.css}/css/dropload.css">
    <link rel="stylesheet" href="${host.css}/css/my_income.css?${host.version}">
    <style>
    	.icon {display: block;}
    </style>
</head>

<body>
    <div class="content">
        <div class="list">
            <div class="list-body">
				<div class="list-item list-two-col">
					<div class="list-item-top">
						<div class="top-left">
							<span style="font-weight: bold;">
								代缴个税
							</span>
						</div>
						<div class="top-right" id='geshui'>

						</div>
					</div>
					<div class="list-item-bottom">
						<div class="bottom-left" id='geshuiData'>

						</div>
					</div>
				</div>
                <script id='incomelist' type="text/x-jquery-tmpl">
                 <#noparse>
                    <div class="list-item list-two-col">
                        <div class="list-item-top">
                            <div class="top-left" style="display: flex; display: -webkit-flex; align-items: center;"><span style="font-weight: bold;">${goodsName}</span> {{if status=='pay_succ' || status=='completed' || status=='settlementing' || status=='settlement_fail' || status=='refund_fail' }}
                                <span class='icon no-income'>未到账</span> {{else status=='refund_applied'}}
                                <span class='icon refunded'>退款申请中</span> {{else status=='refunded' || status=='refunding'}}
                                <span class='icon refunded'>已退款</span> {{else status=='settlemented'}}
                                <span class='icon has-income'>已到账</span> {{/if}}
                            </div>
                            <div class="top-right">税前收入 ¥ ${sqsrDesc}</div>
                        </div>
                        <div class="list-item-bottom">
                            <div class="bottom-left">${simpleOrderExtraInfoAndCateName}</div>
                            <div class="bottom-right">订单金额：${ddjeDesc}</div>
                        </div>
                        <div class="list-item-bottom">
                            <div class="bottom-left">${timeDesc}</div>
                            <div class="bottom-right">平台佣金：${ptyjDesc}</div>
                        </div>
                    </div>
                 </#noparse>
                </script>
            </div>
        </div>



    <script>


	$(function() {
		var page = 0;
		var size = 10;
 $('.content').dropload({
 	scrollArea: window,
 	domDown: {
 		domClass: 'dropload-down',
 		domRefresh: '<div class="dropload-refresh"></div>',
 		domLoad: '<div class="dropload-load"><span class="loading"></span>加载中</div>',
 		domNoData: '<div class="dropload-noData">到底了</div>'
 	},
 	loadDownFn: function(me) {
 		$.ajax({
 			type: 'POST',
 			url: "${host.base}/usr/master/my_bill_detail_query_ajax",
 			data: {
 				currentPage: page,
 				perPageNum: size,
 				orderSettlementId: '${orderSettlementId!""}'
 			},
 			dataType: 'json',
 			success: function(data) {
 				page = page + 1;
 				if (data.head.code == '1000' && page == 1) {
 					me.lock();
 					me.noData();
 					me.resetload();
 					$(".dropload-down").hide();
 					$(".list-header").hide();
                    $(".list").hide();
 					$("body").emptyBox({
 						iconImg: '${host.img}/images/no_income.png',
 						title: '暂无收入明细'
 					})
 				} else if (data.head.code == "1000" && page != 1) {
 					me.lock();
 					me.noData();
 					me.resetload();
 				}
 				//系统错误
 				else if (data.head.code == "9999") {
 					mAlert.addAlert(data.head.msg);
                    me.resetload();
 				} else {
 					$("#geshui").html(data.body.gsDetail.djgsDesc);
 					$("#geshuiData").html(data.body.gsDetail.jszqDesc );
 					$("#incomelist").tmpl(data.body.data).appendTo(".list-body");
 					// 每次数据插入，必须重置
 					me.resetload();
 				}
 			},
 			error: function(xhr, type) {
 				// alert('Ajax error!');
 				// 即使加载出错，也得重置
 				me.resetload();
 			}
 		})
 	},
 	threshold: 50
 	})
	})

    </script>
</body>

</html>
