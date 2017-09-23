<#import "/common/host.ftl" as host>
<#import "/common/funUtils.ftl" as funUtils>
<!doctype html>
<html lang="en">
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
      .header .header-top .title{font-size: 1.6rem;}
      .header{height: 20rem;background: #fff;}
      .header .header-bottom{border: none!important;}
      .list-body .top-right{font-weight: bold;}
      .list-item-top{padding-bottom: 0.5rem!important;}
      .list-item-bottom{padding-bottom: 0!important;}
      .top-left{display: flex; align-items: center;}
      .top-left .icon{margin-left: 0.5rem!important; border-radius: 0.2rem!important;}
      .header .header-bottom .bottom-item .text{font-size: 1.8rem;}
    </style>
</head>
<body>
<div class="header">
    <div class="header-top">
        <div class="title">累计到账收入</div>
        <div class="income">
            <small>¥</small>
            <span style="font-weight: bold;">${funUtils.formatNumber(result.body.allAmt/100,"###,##0.00","--")}</span>
        </div>
    </div>
    <div class="header-bottom">
        <div class="bottom-item">
            <div class="title">未到账税前收入</div>
            <div class="text">
                <small>¥</small>
               ${funUtils.formatNumber(result.body.waitInComeAmt/100,"###,##0.00","--")}
            </div>
        </div>
        <div class="bottom-item">
            <div class="title">已退款</div>
            <div class="text">
                <small>¥</small>
              ${funUtils.formatNumber(result.body.refundedComeAmt/100,"###,##0.00","--")}
            </div>
        </div>
    </div>
</div>
<div class="content">
    <div class="list">
      <div class="list-header">月账单列表</div>
      <div class="card-list">
      <#noparse>
      <script  id='incomelist' type="text/x-jquery-tmpl">
        <div class="card-list-item">
            <div class="top">
                <div class="left">
                    <div class="data">${month}
                        <div class="small-text">${year}</div>
                    </div>
                   {{if hadSettle =="Y"}}
                   		<div class="top-left-button"> 已结算</div>
                    {{else}}
                    	<div class="top-left-button unbalanced"> 未结算</div>
                    {{/if}}
                </div>
                <div class="right">
                    <div class="pretax-income">
                        <div class="tips">税前收入：<span class="income">¥${sqsrDesc}</span></div>
                    </div>
                    <div class="income-detail">
                        <div class="tips">账单总额：<span class="income">¥${zdjeDesc}</span></div>
                        <div class="tips">平台佣金：<span class="income">¥${ptyjDesc}</span></div>
                        <div class="tips">代缴个税：<span class="income">${djgsDesc}</span></div>
                    </div>
                </div>
            </div>
            <div class="bottom" onclick="location.href='/usr/master/my_bill_detail_nav?orderSettlementId=${orderSettlementId}'" >查看明细</div>
        </div>
      </script>
      </#noparse>
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
		       url: "${host.base}/usr/master/my_bill_list_query_ajax",
		       data:{
		       			currentPage:page,
		       			perPageNum:size
		       		},
		       dataType: 'json',
		       success: function(data) {
		         page = page + 1;
		         if(data.head.code == '0000' && page ==1 && data.body.size==0){
		             me.lock();
		             me.noData();
		             me.resetload();
		             $(".dropload-down").hide();
		             $(".list-header").hide();
		             $("body").emptyBox({iconImg:'${host.img}/images/no_income.png',title:'暂无收入明细'})
		           }else if (data.head.code == "0000" && page !=1 && data.body.size==0 ) {
		               me.lock();
		               me.noData();
		               me.resetload();
		           }
		         //系统错误
		         else if(data.head.code =="9999"){
		           mAlert.addAlert(data.head.msg);
		         }else{
		             $("#incomelist").tmpl(data.body.data).appendTo(".card-list");
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