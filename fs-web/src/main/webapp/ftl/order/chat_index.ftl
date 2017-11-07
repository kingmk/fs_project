<#import "/common/host.ftl" as host>
<html>
<title>咨询${body.goodsName}</title>
<head>
<script src="${host.js}/js/rem.js?${host.version}"></script>
<script src="${host.js}/js/jquery-1.11.3.min.js"></script>
<script src="${host.js}/js/iscroll-probe.js?${host.version}"></script>
<script src="${host.js}/js/common.js?${host.version}"></script>
<link rel="stylesheet" href="${host.css}/css/liaotian.css?${host.version}">
<link rel="stylesheet" href="${host.css}/css/bgmask.css?${host.version}">
<script type="text/javascript" src="${host.js}/js/Math.uuid.js?${host.version}"></script>
<script type="text/javascript">

var lastTime = '${body.chatServiceSurplusSec}';
var myScroll = null;
var isScrollDown = false;
var isScrollUp = false;
var isUpdating = false;
var secondUpadte = 3000;
var minChatId = Number.MAX_VALUE;
var maxChatId = -1;

var orderExtraInfo = <#if body.orderExtraInfo ??>true<#else>false</#if>;

var overscroll = function(el) {
	el.addEventListener('touchstart', function() {
		var top = el.scrollTop
		,totalScroll = el.scrollHeight
		,currentScroll = top + el.offsetHeight;
		if(top === 0) {
			el.scrollTop = 1;
		} else if(currentScroll === totalScroll) {
			el.scrollTop = top - 1;
		}
	});

	el.addEventListener('touchmove', function(evt) {
		if(el.offsetHeight < el.scrollHeight){
			evt._isScroller = true;
		}
	});
}

$(function() {
	initHead();

	overscroll(document.querySelector('.speak-box'));
	overscroll(document.querySelector('.bgmask-body'));

	document.body.addEventListener('touchmove', function(evt) {
		if(!evt._isScroller) {
			evt.preventDefault();
		}
	});

	initScroll();

	loadAjax("next");
<#if body.isServiceEnd == "N">
	setInterval(function() {
		loadAjax('next');
	},secondUpadte);
	initMsgCtrl();
</#if>
})

function initHead() {
	if (<#if body.goodsName != "吉凶占卜">orderExtraInfo && </#if>"${body.isServiceEnd}" != "Y" && "${body.isWaitMasterService}" != "Y") {
		$("#lastTime").html(lastTime && lastTime!='0'?commonUtils.getTimeFix(lastTime):'0');
		var time = setInterval(function() {
		  $("#lastTime").html(lastTime  && lastTime!='0'?commonUtils.getTimeFix(lastTime--):'0');
		},1000);
		// setInterval(function(){
		// 	loadAjax(function(dom){
		// 		$("#list").append(dom);
		// 	},'next')
		// }, secondUpadte);
	}

<#if body.isMaster && body.isServiceEnd != "Y" && body.isWaitMasterService != "Y">
	$("#btnExtend").click(function(e){
		var domForm = $('<div class="form-hour-extend"></div>');
		domForm.append($('<div class="title">设置延长时间（小时）</div>'));
		domForm.append($('<div class="text">设置范围（1~48）</div>'));
		domForm.append($('<div class="input"><input id="hours" type="tel" value="24"/></div>'));
		domForm.append($('<div class="errinfo"></div>'));

		var mask = new Bgmsk({
			buttonTxt:"确认延时",
			type: "form",
			formContent: domForm,
			buttonFn: function() {
				extendService("${orderId}", "${chatSessionNo}");
			}
		});
		mask.init();
	});
<#elseif !body.isMaster && body.isServiceEnd != "Y" && body.isWaitMasterService != "Y">
	$("#preEnd").click(function(){
		var mask = new Bgmsk({
			title: "是否提前结束订单?", 
			text:"订单结束后，对话将立刻终止，<br/>如需再找老师咨询，需重新下单", 
			buttonTxt:"确认结束",
			buttonFn: function() {
				loading.addLoading('${host.img}/images/ajax-loader.gif');
				preEnd("${orderId}", "${chatSessionNo}");
			}
		});
		mask.init();
	})
</#if>

<#if body.isMaster && (body.contactCnt>1)>
	$(".master-contact").click(function() {
		location.href= "${host.base}/usr/master/order_list_nav?contactUsrId=${body.contactUsrId}&excludeOrderId=${body.orderId}";
	})
</#if>
<#if body.orderExtraInfo ??>
	initUserInfo();
</#if>
}

<#if body.isServiceEnd == "N">
function initMsgCtrl() {
	overscroll(document.querySelector('#content'));
	overscroll(document.querySelector('#content'));
	$("#content").focus(function() {
		$("#content").addClass("focus");
	});
	$("#content").blur(function() {
		$("#content").removeClass("focus");
	});
	$("#content").on("keydown",function(e) {
		if(e.keyCode == 13) {
			$("#sentTextMsgBtn").trigger("mousedown");
			var f = function() {
				$("#content").val("");
			}
			setTimeout(f, 100);
		}
	});
	$("#sentTextMsgBtn").on("mousedown",function(){
		if ($.trim($("#content").val()).length == 0) {
			return;
		}
		chatSubmit("text");
	});
}

function chatSubmit(msgType,dataURL,imgData) {
	var domSend = $(".wenwen-send");
	if (domSend.hasClass("disabled")) {
		return;
	};
	domSend.addClass("disabled");
	var clientUniqueNo = Math.uuid();
	$("#msgType").val(msgType);
	$("#clientUniqueNo").val(clientUniqueNo);
	var form = new FormData($("#chatForm")[0]);
	handBeforeChatSubmit(clientUniqueNo, msgType, dataURL, imgData);
	$.ajax({
		type: "POST",
		url: "${host.base}/order/chat_submit",
		data:form,
		dataType: "json",
		cache: false,
		processData: false,
		contentType: false,
		success: function(data){
			handAfterChatSubmit(data,clientUniqueNo,imgData);
			domSend.removeClass("disabled");
		},
		error: function(res){
			domSend.removeClass("disabled");
		}
	});

}

//图片上传事件
function fileUpload(self) {
	var $file = $(self);
	var fileObj = $file[0];
	var windowURL = window.URL || window.webkitURL;
	var dataURL;
	if (fileObj && fileObj.files && fileObj.files[0]) {
		dataURL = windowURL.createObjectURL(fileObj.files[0]);
	} else {
		dataURL = $file.val();
	}
	var img = new Image();
	img.src = dataURL;
	var imgData = {};
	img.onload = function(){
		imgData = adjustImageSize({w: img.width, h:img.height});
		chatSubmit('img', dataURL, imgData);
	}
}

function handBeforeChatSubmit(clientUniqueNo, msgType, dataURL, imgData){
	var dom = $('<div id="test1" clientuniqueno="'+clientUniqueNo+'"></div>');
	var domSpeak = $('<div class="speak-right clearfix"></div>');
	domSpeak.append($('<div class="heard-img"><img src="${usrImgUrl}"/></div>'));
	var domText = $('<div class="speak-text"></div>');
	if(msgType =="text"){
		domText.append($('<p>'+ commonUtils.hTMLEncode($("#content").val())+'</p>')); 
		$("#content").val('');
	}else{
		domText.append($('<img src="' + dataURL + '" style="max-width: 24rem; float:right; width:'+imgData.w+'rem; height:'+imgData.h+'rem" onclick="showImg(\''+dataURL+'\')">'));
	}
	domSpeak.append(domText);
	dom.append(domSpeak);
	$("#list").append(dom);
	var hWrapper = $(".speak-window").height();
	var hBox = $(".speak-box").outerHeight();
	myScroll.refresh();
	myScroll.scrollTo(0, Math.min(hWrapper-hBox, myScroll.maxScrollY), 0);
}

function handAfterChatSubmit(data, clientUniqueNo){
	if(data.head.code == "0000"){
		var dom = $("#chatListDiv").find("div[clientUniqueNo='"+clientUniqueNo+"']");
		maxChatId = data.body.id;
		dom.attr("id", data.body.id);
		if (dom.length > 1) {
			for (var i = dom.length-1; i >=1; i--) {
				$(dom[i]).remove();
			};
		};
	}else{
		var dom = $("#chatListDiv").find("div[clientUniqueNo='"+clientUniqueNo+"']")	;
		console.log(dom);
		<#--TODO 发送失败-->
		dom.find(".speak-text p").addClass("disabled");
		dom.attr("id", (new Date()).getTime());
		mAlert.addAlert(data.head.msg, 3500);
	}
}

</#if>


<#if body.orderExtraInfo ??>
function initUserInfo() {
	$("#userInfo").on('click', function() {
		if(!orderExtraInfo){
			return;
		}
		$("#userForm").show();
		$("body").addClass('hidden');
	});
	$(".bgmask-close").on('click', function(){
		$("#userForm").hide();$("body").removeClass('hidden');
	});
	$("#userForm").on('click', function(){
		$("#userForm").hide();$("body").removeClass('hidden');
	});
	$(".user-data-wrapper").on('click', function(e){
		e.stopPropagation;
	});
}
</#if>

<#if body.isMaster>
function extendService(orderId, chatSessionNo) {
	var r = /^[1-9]+[0-9]*]*$/;
	var hours = parseInt($("#hours").val());
	if (!r.test(hours) || hours < 0 || hours > 48) {
		mAlert.addAlert("请输入1-48的整数");
		return;
	};

	loading.addLoading('${host.img}/images/ajax-loader.gif');
	$.ajax({
		type: "POST",
		url: "${host.base}/order/extend_chat",
		data: {
			orderId: orderId,
			chatSessionNo: chatSessionNo,
			hours: hours
		},
		dataType: "json",
		success: function(data) {
			if (data.head.code == "0000") {
				mAlert.addAlert("本次服务已延期", 2000);
				if (orderExtraInfo && "${body.isServiceEnd}" != "Y" && "${body.isWaitMasterService}" != "Y") {
					lastTime = lastTime+hours*3600;
					$("#lastTime").html(commonUtils.getTimeFix(lastTime));
				}

				loading.removeLoading();
			} else {
				mAlert.addAlert(data.head.msg);
				loading.removeLoading();
			}

		},
		error: function(res) {
			loading.removeLoading();
			mAlert.addAlert("系统暂时异常，请刷新后再试");
		}
	})
}

function masterToEvaluate(orderId){
	location.href = "${host.base}/usr/master/evaluate_single_detail?orderId="+orderId;
}
<#else>
function preEnd(orderId, chatSessionNo) {
	$.ajax({
		type: "POST",
		url: "${host.base}/order/pre_end_chat",
		data: {
			orderId: orderId,
			chatSessionNo: chatSessionNo
		},
		dataType: "json",
		success: function(data) {
			if (data.head.code == "0000") {
				mAlert.addAlert("本次服务已结束，感谢您的支持", 3000);
				var f = function() {
					userToEvaluate(orderId);
				}
		    	setTimeout(f, 2500);
			} else {
				mAlert.addAlert(data.head.msg);
				loading.removeLoading();
			}
		},
		error: function(res) {
			loading.removeLoading();
			mAlert.addAlert("系统暂时异常，请刷新后再试");
		}
	});
}

//去评价
function userToEvaluate(orderId) {
	location.href = "${host.base}/order/evaluate/common_view?orderId=" + orderId
}
</#if>


function initScroll() {
	var hWrapper = $(".speak-window").height();
	var hBox = $(".speak-box").outerHeight();
	myScroll = new IScroll(".speak-window", {
		scrollbars: true,
		mouseWheel: false,
		preventDefault:false,
		interactiveScrollbars: true,
		shrinkScrollbars: 'scale',
		fadeScrollbars: true,
		scrollY:true,
		probeType: 2,
		bindToWrapper:true,
		startY: Math.min(hWrapper-hBox, 0)
	});
	myScroll.on("scroll", function() {
		if (isUpdating) {
			return;
		};
		if (this.y > 30) {//下拉刷新操作
			isScrollDown = true;
			$("#pulldown").html("松手获取较早消息");
		} else {
			isScrollDown = false;
			$("#pulldown").html("下拉刷新");
		}

		if (this.y < (this.maxScrollY-40)) {
			isScrollUp = true;
			$("#pullup").html("松手获取最新消息");
		} else {
			isScrollUp = false;
			$("#pullup").html("加载更多");
		}
	});

	myScroll.on("scrollEnd",function(){
		if (isScrollUp) {
			// pullUpAction();
		} else if (isScrollDown) {
			pullDownAction();
		}
	});

	// document.addEventListener('touchmove', function (e) { e.preventDefault(); }, false);
}

function pullDownAction() {
	if (isUpdating) {
		return;
	};
	isUpdating = true;
	isScrollDown = false;
 	loadAjax("prev");
}


function loadAjax(type) {
	var gtChatId = (maxChatId != -1)?""+maxChatId: "";
	var ltChatId = (minChatId != Number.MAX_VALUE)?""+minChatId: "";
	if (type == "next") {
		ltChatId = "";
	} else {
		gtChatId = "";
	}
	$.ajax({
		type: "POST",
		url: "${host.base}/order/chat_query_html_fragment_ajax",
		data:{
			chatSessionNo:"${chatSessionNo}",
			orderId:${orderId},
			gtChatId: gtChatId,
			ltChatId: ltChatId
	    },
		dataType: "json",
		success: function(data){
			// console.log(data);
			updateChatList(data, type);
		},
		error: function(res){
		}
	});
}

function updateChatList(data, type) {
	var chatList = data.chatList;
	if (!chatList || chatList.length == 0) {
		return;
	}
	var domTmpList = $('<div></div>');
	for (var i = 0; i < chatList.length; i++) {
		var chat = chatList[i];
		var domCheck = $("#list").find("div[clientUniqueNo='"+chat.clientUniqueNo+"']");
		if (domCheck.length>0) {
			continue;
		};

		var domChat = $('<div id="'+chat.id+'" clientUniqueNo="'+chat.clientUniqueNo+'"></div>');
		var domSpeak = $('<div class="clearfix"></div>');
		if (chat.sentUsrId == data.loginUsrId) {
			domSpeak.addClass("speak-right");
		} else {
			domSpeak.addClass("speak-left");
		}
		domSpeak.append('<div class="heard-img"><img src="'+chat.sendtUsrHeadImgUrl+'"/></div>');
		var domText = $('<div class="speak-text"></div>');
		if (chat.msgType == "text") {
			domText.append('<p>'+chat.content+'</p>');
		} else if (chat.msgType == "img") {
			var sizeReal = adjustImageSize({w: parseInt(chat.width), h: parseInt(chat.height)});
			domText.append('<img class="speak-img" style="width: '+sizeReal.w+'rem; height: '+sizeReal.h+'rem" onclick="showImg(\''+chat.content+'\')" src="'+chat.content+'">');
		}
		domSpeak.append(domText);
		domChat.append(domSpeak);
		domTmpList.append(domChat);
	};
	var domList = $("#list");
	if (type == "next") {
		domList.append(domTmpList.children());
		if (maxChatId<data.curMaxId) {
			maxChatId = data.curMaxId;
		};
		if (minChatId == Number.MAX_VALUE) {
			minChatId = data.curMinId;
		};
		var hWrapper = $(".speak-window").height();
		var hBox = $(".speak-box").outerHeight();
		myScroll.refresh();
		myScroll.scrollTo(0, Math.min(hWrapper-hBox, myScroll.maxScrollY), 0);
	} else {
		domTmpList.addClass("tmphdr");
		$("#list").prepend(domTmpList);
		var domBox = $(".speak-box");
		domBox.css("bottom", myScroll.maxScrollY+"px");
		var hPrepend = domTmpList.height();
		if (hPrepend == 0) {
			domTmpList.remove();
			return;
		};
		$("#list").prepend(domTmpList.children());
		domBox.css("bottom", "initial");
		domBox.css("top", (-hPrepend)+"px");
		setTimeout(function() {
			myScroll.refresh();
			domBox.css("top", "initial");
			myScroll.scrollTo(0, Math.min(-hPrepend+80, 0), 0);
			setTimeout(function() {
				// myScroll.scrollBy(0, hDeleta, 100);
				setTimeout(function() {
					isUpdating = false;
				}, 105);
			}, 10)
		})

		// domList.prepend(domTmpList.children());
		if (minChatId>data.curMinId) {
			minChatId = data.curMinId;
		};
		if (maxChatId == -1) {
			maxChatId = data.maxChatId;
		}
	}
}

function adjustImageSize(size) {
	var max_w = 24;
	var wreal = size.w;
	var hreal = size.h;
	if (size.w > max_w) {
		wreal = max_w;
		hreal = wreal * size.h / size.w;
	}
	return {w: wreal, h: hreal}
}

//点击图片放大
function showImg(url){
	var img = $('<img style="width:100%;" src="'+url+'"/>');
	$("#bgk-img .bgk-img-wrapper").empty();
	$("#bgk-img .bgk-img-wrapper").append(img);
	$("#bgk-img").show();
	$("#bgk-img").on('click', function(){
		$("#bgk-img").hide();
	})
}

</script>

</head>

<body>
<#if body.isMaster && (body.contactCnt>1)>
<div class="master-contact">
	<span class="icon-arrow"></span>
	<span class="icon-contact"></span>
	<span class="contact-info">该客户还向您咨询过${body.contactCnt-1}次</span>
</div>

</#if>

<div class="speak-header">
	<div class="speak-header-img">
		<img class="img" src="${body.contactUsrHeadImgUrl}">
		<div class="time">
		<#if body.isServiceEnd == "Y">
			<b id='lastTime'>本次服务已结束</b>
		<#elseif !(body.orderExtraInfo??) && body.goodsName != "吉凶占卜">
			<b id='lastTime'>用户信息未提供，可提醒用户</b>
		<#elseif body.isWaitMasterService == "Y" && body.isMaster>
			<b id='lastTime'>请尽快回复用户</b>
		<#elseif body.isWaitMasterService == "Y" && !body.isMaster>
			<b id='lastTime'>等待老师接单</b>
		<#else>
			<span class="lastTimeTitle">本次服务剩余：</span><br/><b id='lastTime'></b>
		</#if>
		</div>
	</div>

<#if (body.orderExtraInfo??)&& body.goodsName != "吉凶占卜">
	<div class="speak-header-btn" id='userInfo'><#if body.isMaster>用户信息<#else>我的信息</#if></div>
</#if>

<#if body.isWaitMasterService != "Y" && body.isServiceEnd != "Y">
	<#if body.isMaster>
	<div class="speak-header-btn btn-service" id='btnExtend'>延长服务</div>
	<#else>
	<div class="speak-header-btn btn-service" id='preEnd'>结束订单</div>
	</#if>
</#if>
</div>
<div class="speak-window <#if body.isMaster && (body.contactCnt>1)>pos2</#if> <#if body.isServiceEnd=='Y'>service-end</#if>">
	<div class="speak-box" id ="chatListDiv">
		<div id="pulldown" class="pullinfo">下拉刷新</div>
		<div id='list'></div>
	</div>
</div>

<#if body.isServiceEnd =="N">
<form id="chatForm">
	<div class="wenwe-footer clearfix">
		<div class="wenwen-btn" id='imgbox'>
			 <input type="file" accept="image/*" id="img"  name="img" value="" onchange="fileUpload(this)" class='wenwen-imginput'>
		</div>
		<div class="wenwen-text">
			<textarea class="write-box" id="content" name ="content"></textarea>
		</div>
		<div class="wenwen-send" id='sentTextMsgBtn'>发送</div>
		<div style="opacity:0;" class="clear"></div>
	</div>
	<input type="hidden" value="${orderId}" id="orderId" name="orderId">
	<input type="hidden" value="${chatSessionNo}" id="chatSessionNo" name="chatSessionNo">
	<input type="hidden" value="text" id="msgType" name="msgType">
	<input type="hidden" value="Y" id="isEscape" name="isEscape">
	<input type = "hidden" value = "" id = "clientUniqueNo" name ="clientUniqueNo">
</form>
<#else>
<div class="order-completed">
	<#if body.hadRefund == "N">
	<div class="title">订单已完成</div>
	<div class="time">完成时间：${body.completedTime}<#if !body.isMaster && body.isCanRefund == "Y"><a style="color: #d88c3a!important" href="${host.base}/order/common_refund_nav?orderId=${body.orderId}">&nbsp;我要退款</a></#if></div>
	<#elseif body.orderStatus == "refunded">
	<div class="title">订单已退款</div>
	<div class="time">退款时间：${body.refundConfirmTime}</div>
	<#elseif body.orderStatus == "refund_applied" || body.orderStatus=="refunding">
	<div class="title">订单退款中</div>
	<div class="time">退款申请时间：${body.refundApplyTime}</div>
	<#elseif body.orderStatus == "refund_fail">
	<div class="title">退款失败</div>
	<div class="time">退款时间：${body.refundConfirmTime}</div>
	</#if>
	<#if body.hadEvaluate == 'Y' && body.isMaster>
		<div class="btn" onclick="masterToEvaluate(${body.orderId})">查看用户评价</div>
	<#elseif body.hadEvaluate == 'Y' && !body.isMaster>
		<div class="btn see-evaluate" onclick="userToEvaluate(${body.orderId})">查看我的评价</div>
	<#elseif body.hadEvaluate == 'N' && body.isMaster>
		<div class="btn wait-evaluate">等待用户评价</div>
	<#elseif body.hadEvaluate == 'N' && !body.isMaster>
		<div class="btn" onclick="userToEvaluate(${body.orderId})">去评价</div>
	</#if>
</div>
</#if>

<div id='bgk-img'>
	<div class="bgk-img-wrapper"></div>
</div>
<div class="user-data-bgk bgmask" id='userForm'>
	<#include "/order/chat_order_extra_info.ftl">
</div>

</body>