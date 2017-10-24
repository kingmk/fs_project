 <#import "/common/host.ftl" as host>
<html>
<title>咨询${body.goodsName}</title>
<head>
<script src="${host.js}/js/rem.js?${host.version}"></script>
<script src="${host.js}/js/jquery-1.11.3.min.js"></script>
<script src="${host.js}/js/iscroll-probe.js?${host.version}"></script>
<script src="${host.js}/js/common.js?${host.version}"></script>
<link rel="stylesheet" href="${host.css}/css/bgmask.css?${host.version}">
<link rel="stylesheet" href="${host.css}/css/liaotian.css?${host.version}">
<script type="text/javascript" src="${host.js}/js/Math.uuid.js?${host.version}"></script>
<style>
#bgk-img{z-index:999;}
.speak-window.chatting {bottom:8rem;}
.speak-box {position: absolute; }
.speak-box #list {position: relative;}
.speak-box .speak-text {word-wrap: break-word; display: block;}
.speak-right .speak-text {float: none; padding-right: 0; margin-right: 6rem; }
.speak-right .speak-text  p {float: right;}
.speak-left .speak-text {float: none; padding-left: 0; margin-left: 6rem; }
.speak-left .speak-text  p {float: left;}
.speak-right .speak-text  p:after{content: ''; position: absolute; display: block; top: 1rem; right: -0.9rem;width: 1rem; height: 1rem; background: url('${host.img}/images/master_chat.png') no-repeat; background-size: 100% 100%;}
.speak-left .speak-text  p:after{content: ''; position: absolute; display: block; top: 1rem; left: -0.9rem;width: 1rem; height: 1rem; background: url('${host.img}/images/none_chat.png') no-repeat; background-size: 100% 100%;}
.pullinfo {height: 2rem; line-height: 2rem; text-align: center; font-size: 1.2rem; position: absolute; width: 100%;}
#pulldown.pullinfo {top: -2rem;}
#pullup.pullinfo {bottom: -2rem;}
.tmphdr {position: absolute; opacity: 0; top: 0; width: 100%;}
.wait-evaluate{background:#ccc!important; color: #fff!important; border: 0.1rem solid #ddd;}
#content.focus {height: 7rem; margin-bottom: 2rem; margin-top: 1rem;}
</style>
<script type="text/javascript">
var excludeChatIds = new Array();
var lastTime = '${body.chatServiceSurplusSec}';
var myScroll = null;
var isScrollDown = false;
var isScrollUp = false;
var isUpdating = false;
var secondUpadte = 3000;
var isChatSubmit = false;
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

$(function(){
	overscroll(document.querySelector('.speak-box'));
	overscroll(document.querySelector('.bgmask-body'));
	if($("#content").length>0){
	overscroll(document.querySelector('#content'));
	overscroll(document.querySelector('#content'));
	$("#content").focus(function() {
		$("#content").addClass("focus");
		$(".speak-window").addClass("chatting");
	});
	$("#content").blur(function() {
		$("#content").removeClass("focus");
		$(".speak-window").removeClass("chatting");
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
	}

	document.body.addEventListener('touchmove', function(evt) {
		if(!evt._isScroller) {
			evt.preventDefault();
		}
	});

	if (<#if body.goodsName != "吉凶占卜">orderExtraInfo && </#if>"${body.isServiceEnd}" != "Y" && "${body.isWaitMasterService}" != "Y") {
		$("#lastTime").html(lastTime && lastTime!='0'?commonUtils.getTimeFix(lastTime):'0');
		var time = setInterval(function() {
		  $("#lastTime").html(lastTime  && lastTime!='0'?commonUtils.getTimeFix(lastTime--):'0');
		},1000);
		setInterval(function(){
			paramsMana();
			loadAjax(function(dom){
				$("#list").append(dom);
			},'next')
		}, secondUpadte);
	}

	if ("${body.isServiceEnd}" != "Y" && "${body.isWaitMasterService}" != "Y") {
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
		})
	}


	loadAjax(function(dom){
		$("#list").append(dom);
		initScroll();
	},'next')


	if ($(".order-completed").length > 0) {
		$(".speak-window").css('bottom',"12rem");
	}

	$("#sentTextMsgBtn").on("mousedown",function(){
		if ($.trim($("#content").val()).length == 0) {
			return;
		}
		chatSubmit("text");
	});
	$("#userInfo").on('click', showUserInfo)
	$(".bgmask-close").on('click', function(){
	$("#userForm").hide();$("body").removeClass('hidden');});
	$("#userForm").on('click', function(){
	$("#userForm").hide();$("body").removeClass('hidden');});
	$(".user-data-wrapper").on('click', function(e){
		e.stopPropagation;});
});

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

//展示用户信息
function showUserInfo(){
	if(!orderExtraInfo){
		return;
	}
	$("#userForm").show();
	$("body").addClass('hidden');
}
//聊天发送
function chatSubmit(msgType,dataURL,imgData){
	if (isChatSubmit) {
		return;
	}
	isChatSubmit = true;
	var clientUniqueNo = Math.uuid();
	$("#msgType").val( msgType );
	$("#clientUniqueNo").val( clientUniqueNo );
	var form = new FormData( $("#chatForm")[0] );
	handBeforeChatSubmit(clientUniqueNo,dataURL,imgData);
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
			isChatSubmit = false;
		},
		error: function(res){
			isChatSubmit = false;
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
		chatSubmit('img', dataURL,imgData);
	}
}

function handBeforeChatSubmit(clientUniqueNo,dataURL,imgData){
	var dom = '<div id="test1" clientuniqueno="'+clientUniqueNo+'">';
	dom =dom + '<div class="speak-right clearfix">' ;
	dom =dom +'		<div class="heard-img"><img src="${usrImgUrl}"/></div>';
	if($("#msgType").val() =="text"){
		<#--TODO需要判定提交状态状态 -->
		dom =dom +'<div class="speak-text"><p>'+ commonUtils.hTMLEncode($("#content").val())+'</p></div></div></div>';
		$("#content").val('');
		$("#list").append( $(dom));
	}else{
		dom = dom + '<div class="speak-text"><img src="' + dataURL + '" style="max-width: 24rem; float:right; width:'+imgData.w+'rem; height:'+imgData.h+'rem" onclick="showImg(\''+dataURL+'\')"></div></div></div>';
		$("#list").append($(dom));
	}
	var hWrapper = $(".speak-window").height();
	var hBox = $(".speak-box").outerHeight();
	myScroll.refresh();
	myScroll.scrollTo(0, Math.min(hWrapper-hBox, myScroll.maxScrollY), 0);
}

function handAfterChatSubmit( data , clientUniqueNo){
	if(data.head.code == "0000"){
		excludeChatIds.push( data.body.id );
		var dom = $("#chatListDiv").find("div[clientUniqueNo='"+clientUniqueNo+"']")	;
		dom.attr("id",data.body.id);
		dom.find(".right").removeClass("submitStautJudge");

	}else{
		var dom = $("#chatListDiv").find("div[clientUniqueNo='+clientUniqueNo+']")	;
		console.log(dom);
		<#--TODO 发送失败-->
	}
}


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
			pullUpAction();
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
 	paramsMana();
 	loadAjax(function(dom) {
		var domTmp = $('<div class="tmphdr"></div>');
		domTmp.append(dom);
		$("#list").prepend(domTmp);
		var domBox = $(".speak-box");
		domBox.css("bottom", myScroll.maxScrollY+"px");
		var hPrepend = domTmp.height();
		if (hPrepend == 0) {
			domTmp.remove();
			return;
		};

		$("#list").prepend(dom);
		domBox.css("bottom", "initial");
		domBox.css("top", (-hPrepend)+"px");

		// 根据后台返回的数据结构需要做调整，而且需要判断是否后台有返回
		// var hDeleta = $(dom[dom.length-2]).outerHeight();

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
 	})
}

function pullUpAction() {
	if (isUpdating) {
		return;
	};
	isUpdating = true;
	isScrollUp = false;
	var hWrapper = $(".speak-window").height();
	var hBox = $(".speak-box").outerHeight();
	// var hDelta = $(dom[0]).outerHeight();
	setTimeout(function() {
		myScroll.refresh();
		myScroll.scrollTo(0, Math.max(hWrapper-hBox, myScroll.maxScrollY), 0);
		setTimeout(function() {
			// myScroll.scrollBy(0, -hDelta, 100);
			setTimeout(function() {
				isUpdating = false;
			}, 105);
		}, 10)
	})
}

function loadAjax(callback,firefrom){
	var _gtChatId = $("#_gtChatId").val();
	var _ltChatId = $("#_ltChatId").val();
	if(firefrom=='next'){
		_ltChatId="";
	}else{
		_gtChatId = "";
	}
	var _excludeChatIds = "";
	if( !excludeChatIds.length==0 ){
		_excludeChatIds = JSON.stringify(excludeChatIds);
		_excludeChatIds = _excludeChatIds.replace("[","");
		_excludeChatIds = _excludeChatIds.replace("]","");
	}
	$.ajax({
		type: "POST",
		url: "${host.base}/order/chat_query_html_fragment_ajax",
		data:{
			chatSessionNo:"${chatSessionNo}",
			orderId:${orderId},
			gtChatId: _gtChatId,
			ltChatId:  _ltChatId,
			excludeChatIds : _excludeChatIds,
	    },
		dataType: "html",
		success: function(data){
			var doms = $(data);
			calculateImg(doms);
			callback(doms);
		},
		error: function(res){
		}
	});
}

//加载图片高宽处理
function calculateImg(doms) {
	var domImgs = doms.find("img");
	for (var i = domImgs.length - 1; i >= 0; i--) {
		var domImg = $(domImgs[i]);
		var w = parseInt(domImg.attr("data-w"));
		var h = parseInt(domImg.attr("data-h"));
		var sizeReal = adjustImageSize({w:w, h:h});
		domImg.css("width", sizeReal.w + "rem");
		domImg.css("height", sizeReal.h + "rem");
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

//参数管理函数
function paramsMana(){
	var _curMaxId = $("#chatListDiv").find("#_curMaxId").val();
	var _curMinId = $("#chatListDiv").find("#_curMinId").val();
	if( _curMaxId !="" ){
		var _gtChatId = $("#_gtChatId").val();
		if(_gtChatId =="" || _gtChatId ==null  ||  _gtChatId<_curMaxId ){
			$("#_gtChatId").val( _curMaxId );
		}
	}
	if( _curMinId !="" ){
		var _ltChatId = $("#_ltChatId").val();
		if(_ltChatId =="" || _ltChatId ==null  ||  _ltChatId>_curMinId ){
			$("#_ltChatId").val( _curMinId );
		}
	}
	$("#chatListDiv").find("#temporaryHidediv").remove();
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
function viewEvaluateSingle(orderId){
	location.href = "${host.base}/usr/master/evaluate_single_detail?orderId="+orderId;
}
</script>

</head>
<body>

<div class="speak-header">
	<div class="speak-header-img">
		<img class="img" src="${body.buyUsrHeadImgUrl}">
		<div class="time" >
			<#if body.isServiceEnd == "Y">
			<b id='lastTime'>本次服务已结束</b>
			<#elseif !(body.orderExtraInfo??) && body.goodsName != "吉凶占卜">
			<b id='lastTime'>用户信息未提供，可提醒用户</b>
			<#elseif body.isWaitMasterService == "Y">
			<b id='lastTime'>请尽快回复用户</b>
			<#elseif body.isWaitMasterService != "Y">
			<span class="lastTimeTitle">本次服务剩余：</span><br/><b id='lastTime'></b>
			</#if>
		</div>
	</div>
	<#if (body.orderExtraInfo??)&& body.goodsName != "吉凶占卜">
	<div class="speak-header-btn" id='userInfo'>用户信息</div>
	</#if>
	<#if body.isWaitMasterService != "Y" && body.isServiceEnd != "Y">
	<div class="speak-header-btn btn-service" id='btnExtend'>延长服务</div>
	</#if>
</div>
<div class="speak-window">
<div class="speak-box" id ="chatListDiv">
	<div id="pulldown" class="pullinfo">下拉刷新</div>
	<div id='list'></div>
	<div>
		<input type="hidden" id="_gtChatId" value = "">
		<input type="hidden" id="_ltChatId" value = "">
		<input type="hidden" id="currentPage" value = "0">
		<input type="hidden" id="perPageNum" value = "5">
	</div>
</div>
</div>

<#if body.isServiceEnd =="Y" && body.hadRefund == 'N'>
<div class="order-completed">
	<div class="title">订单已完成</div>
	<div class="time">完成时间：${body.completedTime}</div>
	<#if body.hadEvaluate == 'Y'>
		<div class="btn" onclick="viewEvaluateSingle(${body.orderId})">查看用户评价</div>
	<#else>
		<div class="btn wait-evaluate">等待用户评价</div>
	</#if>
</div>
<#elseif body.isServiceEnd =="Y" &&  body.hadRefund == 'Y'>
<div class="order-completed">

	<#if body.orderStatus=='refunded'>
		<div class="title">订单已退款</div>
		<div class="time">退款时间：${body.refundConfirmTime}</div>
	<#elseif body.orderStatus=='refund_applied' || body.orderStatus=='refunding'>
		<div class="title">订单退款中</div>
		<div class="time">退款申请时间：${body.refundApplyTime}</div>
	<#elseif body.orderStatus=='refund_fail' >
		<div class="title">退款失败</div>
		<div class="time">退款时间：${body.refundConfirmTime}</div>
	<#else>
	</#if>

	<#if body.hadEvaluate == 'Y'>
		<div class="btn" onclick="viewEvaluateSingle(${body.orderId})">查看用户评价</div>
	<#else>
		<div class="btn wait-evaluate">等待用户评价</div>
	</#if>
</div>
</#if>

<#if body.isServiceEnd =="N">
<form id = "chatForm">
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
<form>
<#-- <div  style="margin-bottom:15px;margin-top:15px;dispay:none">
	<form id = "chatForm">
		<textarea value="test" id="content" name ="content" rows="3" cols="40"></textarea>
		<input type="file" value="test" id="img" name="img"> <br>
		<input type="button" value="sentTextMsgBtn" id = "sentTextMsgBtn">
		<input type="button" value="sentImgMsgBtn" id = "sentImgMsgBtn">
		<input type="hidden" value="${orderId}" id="orderId" name="orderId">
		<input type="hidden" value="${chatSessionNo}" id="chatSessionNo" name="chatSessionNo">
		<input type="hidden" value="text" id="msgType" name="msgType">
		<input type="hidden" value="Y" id="isEscape" name="isEscape">
		<input type = "hidden" value = "" id = "clientUniqueNo" name ="clientUniqueNo">

<div> -->

</#if>
</form>
<div id='bgk-img'>
	<div class="bgk-img-wrapper"></div>
</div>


<div class="user-data-bgk bgmask" id='userForm'>
	<#include "/order/chat_order_extra_info.ftl">
</div>
</body>
</html>
