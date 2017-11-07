 <#import "/common/host.ftl" as host>
<html>
<head>
<meta charset="UTF-8">
<script src="${host.js}/js/rem.js?${host.version}"></script>
<script src="${host.js}/js/jquery-1.11.3.min.js"></script>
<script src="${host.js}/js/jquery.tmpl.min.js"></script>
<script src="${host.js}/js/dropload.min.js"></script>
<script src="${host.js}/js/common.js?${host.version}"></script>
<script src="${host.js}/js/components.js?${host.version}"></script>
<link rel="stylesheet" href="${host.css}/css/dropload.css">
<link rel="stylesheet" href="${host.css}/css/star.css?${host.version}">
<link rel="stylesheet" href="${host.css}/css/user_rating.css?${host.version}1">
<title>用户评价</title>
<style>
.empty-icon{display: none;}
.empty{padding-top: 0;}
.empty-title{padding-top: 3.5rem!important;}
</style>

</head>
<body>
<div class="header">
    <div class="user-comments-content">
        <div class="score-title">来自 ${result.body.evaluateTotal} 位用户的评价</div>
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
</div>
<div class="content" id='contaner'>
  <div class="rating-content">
    <div class="rating-content-tit">全部评价（<span id='totalSize'>${result.body.evaluateTotal}</span>）<i class="tit-arr"></i></div>
  	<div class="rating-content-list">
      <#noparse>
      <script id='incomelist' type="text/x-jquery-tmpl">
      <div class="list-item">
        <div class="img-box"><img src="${buyUsrHeadImgUrl}" class="img"></div>
        <div class="list-item-content">
          <div class="name">${buyUsrName}</div>
          <div class="rating">${evaluateWord}</div>
          <div class="time">${evaluateTime}  ${goodsName} </div>
          {{if masterReplyWord}}
          <div class="reply-box">
            <div class="reply-content"><div class="reply-arrow"></div>老师回复：${masterReplyWord}</div>
          </div>
          {{/if}}
        </div>
      </div>
      </script>
      </#noparse>
    </div>
  </div>
</div>
<script>
<#if result.body.evaluateTotal == 0>
  $(".dropload-down").hide();
  $(".rating-content-tit").hide();
</#if>
var page = 0;
var size = 10;
$(function() {
  $("#responseSpeed").star({
    type: 'show',
    tip: '${result.body.respSpeedAvgScore}'
  });
  $("#professionalLevel").star({
    type: 'show',
    tip: '${result.body.majorLevelAvgScore }'
  });
  $("#serviceSAttitude").star({
    type: 'show',
    tip: '${result.body.serviceAttitudeAvgScore}'
  });
  $('#contaner').dropload({
    scrollArea: window,
    domDown: {
      domClass: 'dropload-down',
      domLoad: '<div class="dropload-load"><span class="loading"></span>加载中</div>',
      domNoData: '<div class="dropload-noData">到底了</div>'
    },
    loadDownFn: function(me){
    	loadFn(me)},
    threshold: 50
  })
})
function loadFn(me) {
  $.ajax({
    type: 'POST',
    url: '${host.base}/usr/master/evaluate_list',
    dataType: 'json',
    data: {
      masterUsrId: '${masterUsrId}',
      currentPage: page,
      perPageNum: size
    },
    success: function(data) {
      page = page + 1;
      if(data.head.code == '1000' && page ==1){
        me.lock();
        me.noData();
        me.resetload();
        $(".dropload-down").hide();
        $(".rating-content-tit").hide();
        $("body").emptyBox({iconImg:'${host.img}/images/no_rating.png',title:'暂无用户评价'})
      } else if (data.head.code == "1000" && page !=1) {
          me.lock();
          me.noData();
          me.resetload();
      } else if (data.head.code == "9999") {
        //系统错误
        mAlert.addAlert(data.head.msg);
      } else {
      	if(data.body.data.length < size){
          // 锁定
          me.lock();
          // 无数据
          me.noData();
          me.resetload();
      	}

        $("#incomelist").tmpl(data.body.data).appendTo(".rating-content-list");
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
}
</script>
</body>
</html>
