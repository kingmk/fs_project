<#import "/common/host.ftl" as host>
<#import "/common/funUtils.ftl" as funUtils>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>我的关注</title>
     <meta name="viewport" />
    <script src="${host.js}/js/rem.js?${host.version}"></script>
    <script src="${host.js}/js/jquery-1.11.3.min.js"></script>
    <script src="${host.js}/js/common.js?${host.version}"></script>
    <script src="${host.js}/js/jquery.tmpl.min.js"></script>
    <script src="${host.js}/js/dropload.min.js"></script>
    <link rel="stylesheet" type="text/css" href="${host.css}/css/dropload.css">
    <link rel="stylesheet" href="${host.css}/css/heart.css?${host.version}">
    <script>
    var perPageNum = 10;
    var currentPage = 0;
    $(function(){
      // loadList(function(){});
      $('.center').dropload({
        scrollArea: window,
        domDown: {
          domClass: 'dropload-down',
          domLoad: '<div class="dropload-load"><span class="loading"></span>加载中</div>',
          domNoData: '<div class="dropload-noData">到底了</div>'
        },
        loadDownFn  : function(me){
            loadList(function(data){
              currentPage ++;
              if(data.head.code == '1000' && currentPage ==1){
                me.lock();
                me.noData();
                me.resetload();
                $(".dropload-down").hide();
                $("body").emptyBox({iconImg:'${host.img}/images/no_search.png?${host.version}',title:'没有关注的大师'})
              }else if (data.head.code == "1000" && currentPage !=1) {
                  me.lock();
                  me.noData();
                  me.resetload();
              }else if(data.body.data.length > 0){
                   $("#itemTmpl").tmpl(data.body.data).appendTo(".list");
                  if(data.body.data.length<perPageNum){
                    me.lock();
                    me.noData();
                  }
                   me.resetload();
              }else{
                  me.lock();
                  me.noData();
                  me.resetload();
              }

            },function(){
                // alert('Ajax error!');
                me.resetload();
            })
        },
        threshold: 50
    })
  })
    function loadList(callback,errorback){
      $.ajax({
        url: '${host.base}/usr/common/follow_list_ajax_query',
        type: 'POST',
        dataType: 'json',
        data: {
          currentPage:currentPage,
          perPageNum:perPageNum
        },
      })
      .done(function(data) {
        callback(data);
      })
      .fail(function() {
        // console.log("error");
        return errorback?errorback():'';
      })
      .always(function() {
        // console.log("complete");
      });

    }

     function cancelFollow(el){
        var _this = el;
        var $el = $(el);
        if($el.prop('uFollow')){
          $.ajax({
            url: '${host.base}/usr/common/follow',
            type: 'POST',
            dataType: 'json',
            data: {focusUsrId: $el.data('id')},
          })
          .done(function(data) {
            if(data.head.code == '0000'){
              mAlert.addAlert("添加关注成功",1000);
              $el.text("取消关注");
              $el.prop('uFollow',false);
            }else{
              mAlert.addAlert(data.head.msg);
            }
          })
          .fail(function() {
            console.log("error");
          })
        }else{
          $.ajax({
            type: "POST",
            url: '${host.base}/usr/common/follow_cancel',
            dataType: "json",
            data:{masterUsrId: $el.data('id')},
            success: function(data){
              if(data.head.code=="0000"){
                mAlert.addAlert("取消关注成功",1000);
                $el.text("添加关注");
                $el.prop('uFollow',true);
              }else{
                mAlert.addAlert(data.head.msg);
              }
            },
            error: function(res){
            }
          });
        }

    }

 function goToMasterDetail( masterInfoId ){
 	location.href = "${host.base}/usr/search/master_detail?masterInfoId="+masterInfoId;
 }
    </script>
</head>
<body>
 <#noparse>
<script id='itemTmpl' type="text/x-jquery-tmpl">
	 <div class="item">
		<div class="left" onclick="goToMasterDetail(${masterInfoId})">
			<img src="${masterHeadImgUrl}">
			<div class="info">
				<div class="name">${masterNickName}</div>
				<ul class="incoming-box">
					{{if !!workYearStr}}
              <li class="red">${workYearStr} 年经验</li>
          {{/if}}
          {{if isCertificated=='Y'}}
              <li class="yellow">实名认证</li>
          {{/if}}
         	{{if isSignOther=='N'}}
               <li class="green">独家合作</li>
          {{/if}}
          {{if isTranSecuried='Y'}}
               <li class="blue">担保交易</li>
          {{/if}}
	      </ul>
			</div>
		</div>
		<div class="right">
			<div class="cancel-follow" onclick="cancelFollow(this)" data-id = '${masterUsrId}'>取消关注</div>
		</div>
	</div>
</script>
</#noparse>
<div class="center">
<div class="list">
</div>
</div>

</body>
</html>
