<#import "/common/host.ftl" as host>
<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">
<script src="${host.js}/js/rem.js?${host.version}"></script>
<script src="${host.js}/js/jquery-1.11.3.min.js"></script>
<script src="${host.js}/js/common.js?${host.version}"></script>
<script src="${host.js}/js/components.js?${host.version}"></script>
<link rel="stylesheet" href="${host.css}/css/user_rating_star.css?${host.version}">
<title>回复用户评价</title>

<script type="text/javascript">
$(function() {

    $("#editWord").on('input propertychange', function() {
         if ($(this).val().length > 200) {
            $(this).val( $(this).val().substring(0,200));
        }
        $(this).siblings('.tips').html('还可填写' + (200 - $(this).val().length) + '字');
    })

    $("#btnReply").click(commitReply);

})

function commitReply() {
	var domBtn = $("#btnReply");
	if (domBtn.hasClass("disabled")) {
		return;
	};
	domBtn.addClass("disabled");
	var rlt = true;
	var replyWord = $("#editWord").val();
	if (replyWord.length == 0) {
		mAlert.addAlert("回复内容不可为空", 1000);
		rlt = false;
	} else if (replyWord.length >200) {
		mAlert.addAlert("回复内容不可超过200字", 1000);
		rlt = false;
	}

	if (!rlt) {
		domBtn.removeClass("disabled");
		return;
	}

	$.ajax({
		type: "POST",
		url: "${host.base}/usr/master/evaluate_reply_ajax",
		data: {
			orderId:  "${result.body.orderId}" ,
			replyWord: replyWord
		},
		dataType: "json",
		success: function(data){
			if (data.head.code == "0000") {
				mAlert.addAlert("回复成功");
				setTimeout(function() {
					location.href = "${host.base}/usr/master/evaluate_single_detail?orderId=${result.body.orderId}"
				}, 1500)
			} else {
				mAlert.addAlert(data.head.msg);
				domBtn.removeClass("disabled");
			}
		},
		error: function(res){
			mAlert.addAlert("网络异常，请稍后再试");
			domBtn.removeClass("disabled");
		}
	});
}
</script>
</head>

<body style='background: #fff;'>
	<div class="user-comments-content">
        <div class="header">
            <img src="${result.body.buyUsrHeadImgUrl}" />
            <div class="title">${result.body.buyUsrName}</div>
        </div>
	</div>
	<div class="edit-text-box">
        <div class="edit-text">
            <textarea id='editWord' placeholder='请输入您的回复内容'></textarea>
            <span class="tips">还可填写200字</span>
        </div>
    </div>
    <div class="button-box">
        <div class="button" id='btnReply'>确认回复</div>
    </div>
    <div class="evaluate-reply-tip">回复提交后不可再修改，对客户的评论仅可做一次回复</div>
</body>
</html>