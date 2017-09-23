 <#import "/common/host.ftl" as host>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <script src="${host.js}/js/rem.js?${host.version}"></script>
    <script src="${host.js}/js/jquery-1.11.3.min.js"></script>
    <script src="${host.js}/js/common.js?${host.version}"></script>
    <link rel="stylesheet" href="${host.css}/css/user_rating_star.css?${host.version}">
    <title>用户投诉</title>
    <style>
      .tip-box{ margin-top: 7.5rem; width: 100%; }
      .text{font-size: 1.2rem; color: #999; line-height: 2rem; text-align: left;}
    </style>
    <script>
      $(function(){
        $("#edite").on('input propertychange', function() {
          if ($(this).val().length > 200) {
              $(this).val( $(this).val().substring(0,200));
          }
          $(this).siblings('.tips').html('还可填写' + (200 - $(this).val().length) + '字');
        })
        $("#submit").on("click", function(){
          if($("#edite").val().length==0){
            mAlert.addAlert('请填写退款原因');
            return;
          }
          $.ajax({
            url: '/order/common_refund_apply_ajax_submit',
            type: 'POST',
            dataType: 'JSON',
            data: {
              refundReason: $("#edite").val(),
              orderId: '${result.body.orderId}'
            },
          })
          .done(function(data) {
            if(data.head.code == '0000'){
              mAlert.addAlert('提交退款成功');
              setTimeout(function(){
                location.href = '${host.base}/order/chat_index?chatSessionNo=${result.body.chatSessionNo}&orderId=${result.body.orderId}';
              },2000)
            }else{
              mAlert.addAlert(data.head.msg);
            }
          })
          .fail(function() {
            mAlert.addAlert('请求失败');
          })

        })
      })
    </script>
<body style='background: #fff;'>
<div class="user-comments-content">
  <div class="header">
      <img src="${result.body.masterHeadImgUrl}" />
      <div class="title">${result.body.masterName}</div>
  </div>
  <div class="edit-text-box">
	  <div class="edit-text">
	  <textarea id='edite' placeholder='希望一切都是误会，请填写您的退款原因：'></textarea>
	  <span class="tips">还可填写200字</span>
	</div>
	<div class="button-box">
    <div class="button" id='submit'>提交退款申请</div>
 	</div>
	<div class="tip-box">
		<div class="text">·在服务时间结束后的第7个自然日22:00前可提出退款申请；</div>
		<div class="text">·提出退款申请后，请耐心等待审核结果； </div>
		<div class="text">·退款时请说明原因，一般过于简单的原因将不会被通过；</div>
	</div>
</body>
</html>
