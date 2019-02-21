<#import "/common/host.ftl" as host>
<#import "/common/funUtils.ftl" as funUtils>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>老师主页</title>
<script src="${host.js}/js/rem.js?${host.version}"></script>
<script src="${host.js}/js/jquery-1.11.3.min.js"></script>
<script src="${host.js}/js/common.js?${host.version}"></script>
<script src="http://res.wx.qq.com/open/js/jweixin-1.2.0.js"></script>
<link rel="stylesheet" href="${host.css}/css/star.css?${host.version}">
<link rel="stylesheet" href="${host.css}/css/user_rating.css?${host.version}2">
<link rel="stylesheet" href="${host.css}/css/teacher_home.css?${host.version}4">
<script src="${host.js}/js/components.js?${host.version}"></script>
<style>
.fixed{position:fixed;top:0;left:0;right:0}
.center-fixed{padding-top:4.5rem}
.img-logo-box{width:9rem;height:9rem;border-radius:50%;overflow:hidden;position:relative;margin:0 auto}
.img-logo-box img{position:absolute;z-index:9}
.header .header-bottom .header-bottom-item .item-num{color: #d88c3a;}
.center .center-nav .nav-item.actived:after{background: #d88c3a;}

</style>


<script>
var isFollowed  = '${result.body.isFollowed}';
var isZxCateId = '${zxCateId}';
var a = ${resultStr};
var wxconfig = ${wxconfig};
var isRegistered = "${result.body.isRegistered}";
$(function(){
	window.scrollTo(0,0);
	$(".button.service").click(clickBtnService);

	$(".button.share").click(function() {
		var domMask = $('<div class="m-alert" id="share_bg"><img src="${host.img}/images/bg_share.png" style="width:100%;"></div>');
		domMask.click(function() {
			domMask.remove();
		})
		$("body").append(domMask);
	});
	loadServiceIntro();
	loadUsrCommentsIntro();
	initFollow();
	window.scrollTo(0,0);
	$('.center-nav .nav-item').on('click', function(e){
		$('.center-nav').addClass('fixed')
		$('.center').addClass('center-fixed');
		var top = $("."+$(this).attr('id')).offset().top;
		$('html, body').animate({scrollTop:top-$('.center-nav').height()}, 'slow');
	})

	$("#linkTo").on('click',function(){
		location.href='${host.base}/usr/master/evaluate_summary?masterUsrId=${result.body.masterUsrId}'
	})

	initWxShare();
});

function clickBtnService() {
	var domBtn = $(".button.service");
	if (domBtn.hasClass("disabled")) {
		return;
	} else if (domBtn.hasClass("reserve")){
		var masterInfoId = "${result.body.masterInfoId}";

		if (isRegistered == "N") {
			$.bgmask({
				title: "请先注册",
				text: "只有注册后才可以预约老师",
				type: "normal",
				buttonTxt: "去注册",
				buttonFn: function() {
					location.href = "${host.base}/usr/register/mobile_nav?backUrl="+encodeURIComponent(location.href);
				}
			})
		} else {
			$.bgmask({
				title: "确认预约${result.body.masterNickName}老师？",
				text: "预约成功后，系统会通过短信方式通知您何时可以去咨询老师",
				type: "normal",
				buttonTxt: "确认预约",
				buttonFn: function() {
					reserveMaster(masterInfoId);
				}
			})
		}

	} else {
	<#if zxCateId ??>
		location.href = "${host.base}/order/confirm_nav?masterServiceCateId=${result.body.curServiceCateInfo.id}&masterInfoId=${result.body.masterInfoId}";
	<#else>
		$("#service-intros").click();
		mAlert.addAlert("请选择服务");
	</#if>
	}

}

function reserveMaster(masterInfoId) {
	var domBtn = $(".button.service");
	$.ajax({
		url: "${host.base}/usr/search/reserve_master",
		method : 'POST',
		data: {
			masterInfoId : masterInfoId,
		} ,
		dataType: "json",
		success: function(data){
			if (data.head.code != "0000") {
				mAlert.addAlert(data.head.msg);
			} else {
				mAlert.addAlert("预约成功，老师恢复服务时会通过短信告知您", 3000);
				domBtn.removeClass("reserve");
				domBtn.addClass("disabled");
				domBtn.html("已预约，请耐心等待");
			}
		},
		error: function(res){
		}
	})
}


window.onscroll = function(e){
	var navHeight = $('.center-nav').height();
	var briefsTop = $('.teacher-briefs').offset().top;
	var introsTop = $('.service-intros').offset().top;
	var commentsTop =  $('.user-comments').offset().top;
	isClick = false;
	if($('body').scrollTop() > briefsTop-navHeight-10){
		$('.center-nav').addClass('fixed')
		$('.center').addClass('center-fixed');
	}else{
		$('.center-nav').removeClass('fixed')
		$('.center').removeClass('center-fixed');
	}
	if($('body').scrollTop() <= (introsTop-navHeight-2) && $('body').scrollTop() >= (briefsTop-navHeight-10) ){
		$("#teacher-briefs").addClass('actived').siblings().removeClass('actived');
	}else if($('body').scrollTop() <= (commentsTop-navHeight-2) && $('body').scrollTop() >= (introsTop-navHeight-10)){
		$("#service-intros").addClass('actived').siblings().removeClass('actived');
	}else if($('body').scrollTop() >= (commentsTop-navHeight-10)){
		$("#user-comments").addClass('actived').siblings().removeClass('actived');
	}
}

function initFollow() {
	//取消关注事件
	$(".btn").on("click", function(){
		if(isFollowed == 'Y'){
			$.ajax({
				url: '${host.base}/usr/common/follow_cancel',
				type: 'POST',
				dataType: 'json',
				data: {masterUsrId: '${result.body.masterUsrId}'},
			})
			.done(function(data) {
				if(data.head.code == '0000'){
					mAlert.addAlert('取消关注',600);
					setTimeout(function(){
						location.reload();
					},1000)

				}else{
					mAlert.addAlert(data.head.msg);
				}
			})
			.fail(function() {
				console.log("error");
			})
		}else{
			$.ajax({
				url: '${host.base}/usr/common/follow',
				type: 'POST',
				dataType: 'json',
				data: {focusUsrId: '${result.body.masterUsrId}'},
			})
			.done(function(data) {
				if(data.head.code == '0000'){
					mAlert.addAlert('关注成功', 600);
					setTimeout(function(){
						location.reload();
					},1000)
				}else{
					mAlert.addAlert(data.head.msg);
				}
			})
			.fail(function() {
				console.log("error");
			})
		}
	})
}

function loadServiceIntro(){
	$.post("${host.base}/usr/search/master_detail_service_ajax_html_fragment?masterUsrId=${result.body.masterUsrId}&zxCateId=${zxCateId}",function(data){
			$(".service-intros").empty();
			var dom = $('<div></div>');
			dom.append(data);
			<#if result.body.serviceStatus != "ING">
			dom.find(".item-right").remove();
			</#if>
			$(".service-intros").append(dom);
	});
}
var _currentPage = 0;
var hadNext = "true";
function loadUsrCommentsIntro(){
	if(hadNext !="true"){
		return ;
	}
	$.ajax({
		url: "${host.base}/usr/search/master_detail_evaluate_list_ajax_html_fragment",
		method : 'POST',
	    data: {
	    	masterUsrId:  ${result.body.masterUsrId},
	    	currentPage : _currentPage,
	    	perPageNum : 10
	    	} ,
	    dataType: "html",
	    success: function(data){
			// console.log(data);
			if(data ==""){
				hadNext = "false";
			}
			_currentPage = _currentPage + 1;
			$("#evaluate_list").append(data);
	    },
	    error: function(res){
	    }
	});
}

function initWxShare() {
	if (!wx) {
		return;
	};
	wx.config({
		appId: wxconfig.appid,
		timestamp: wxconfig.timestamp,
		nonceStr: wxconfig.noncestr,
		signature: wxconfig.signature,
		jsApiList: ["onMenuShareTimeline","onMenuShareAppMessage"]
	});
	wx.ready(function() {
		var image = "${result.body.masterHeadImgUrl}";
		var link = "${host.base}/enter/weixin?_goTo="+encodeURIComponent("/usr/search/master_detail?masterInfoId=${result.body.masterInfoId}");

		wx.onMenuShareTimeline({
			title:"我分享出来，就是要准得你吓一跳", 
			link:link, 
			imgUrl: image,
			success:function(){
				$("#share_bg").remove();
			}, 
			cancel:function(){
			}
		})
		wx.onMenuShareAppMessage({
			title:"我分享出来，就是要准得你吓一跳", 
			desc: "你的命超乎你的想象",
			link:link, 
			imgUrl: image,
			success:function(){
				$("#share_bg").remove();
			}, 
			cancel:function(){
			}
		})
	})
}
</script>

<style>
	.head-icon-sex{background:url(${host.img}/images/sex_icon.png) no-repeat; background-size: 1.6rem auto;padding-left: 2.1rem;height:1.6rem; display:inline-block;line-height: 1.6rem;}
	.head-icon-sex.M{background-position: 0 0;}
	.head-icon-sex.F{background-position: 0 -1.6rem;}
</style>
</head>
<body id="user-teacher-home">
<div class="header">
	<div class="header-top">
	<div class="img-logo-box"><img id="headImgUrl" class="header-top-img" src="${result.body.masterHeadImgUrl}"  onload="commonUtils.adjustHeadImg(this, 9, 'headImgUrl')"></div>
		<h3 class="header-top-h3"><span class="head-icon-sex ${result.body.sex}">${result.body.masterNickName}</span></h3>
		<ul class="hreader-top-nav">
		<#if result.body.workYearStr??>
			<li>${result.body.workYearStr} 年经验</li>
		</#if>
		<#if result.body.isCertificated == 'Y'>
			<li>实名认证</li>
		</#if>
		<#if result.body.isSignOther == 'N'>
			<li>独家合作</li>
		</#if>
		<#if result.body.isTranSecuried == 'Y'>
			<li>交易担保</li>
		</#if>
		</ul>
	</div>
	<div class="header-bottom">
		<div class="header-bottom-item">
			<div class="item-tip">总评分</div>
			<div class="item-num">${result.body.scoreDesc}</div>
		</div>
		<div class="header-bottom-item">
			<div class="item-tip">解答数</div>
			<div class="item-num">${result.body.orderTotalNum}</div>
		</div>
		<div class="header-bottom-item">
			<div class="item-tip">粉丝数</div>
			<div class="item-num">${result.body.fansTotalNum}</div>
		</div>
		<div class="header-bottom-item">
			<#if result.body.isFollowed == 'Y'>
			<div class="btn not-heart">已关注</div>
			<#else>
			<div class="btn heart">关注</div>
			</#if>
		</div>
	</div>
</div>
<div class="center">
	<div class="center-nav">
		<div class="nav-item actived"  id="teacher-briefs">老师介绍</div>
		<div class="nav-item"  id="service-intros">服务介绍</div>
		<div class="nav-item"  id="user-comments">用户评价(${result.body.evaluateTotal})</div>
	</div>
	<!--老师介绍-->
	<div class="teacher-briefs">
		<div class="briefs-item">
			<div class="briefs-item-tit"><span>所学门派</span></div>
			<div class="briefs-item-text">${result.body.school}</div>
		</div>
		<div class="briefs-item">
			<div class="briefs-item-tit"><span>相关经历</span></div>
			<div class="briefs-item-text">${result.body.experience}</div>
		</div>
		<div class="briefs-item">
			<div class="briefs-item-tit"><span>所获成就</span></div>
			<div class="briefs-item-text">${result.body.achievement}</div>
		</div>
		<div class="briefs-item">
			<div class="briefs-item-tit"><span>擅长领域</span></div>
			<div class="briefs-item-text">${result.body.goodAt}</div>
		</div>
	</div>
<#--服务项目 begin -->
	<div class="service-intros">
	</div>
<#--服务项目 end -->
<#--用户评价 begin -->
	<div class="user-comments">
		<div class="user-comments-tit" onclick="location.href='${host.base}/usr/master/evaluate_summary?masterUsrId=${result.body.masterUsrId}'">用户评价 (${result.body.evaluateTotal})<i class="tit-arr"></i></div>
		<div class="user-comments-content">
			<div class="score-title">来自 ${result.body.evaluateTotal} 位用户的评价<i class="tit-arr"></i></div>
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
		<#--TODO-->
		<div id="evaluate_list">
		</div>
		<div class="user-comments-tit more" id='linkTo'>查看全部的评价 (${result.body.evaluateTotal})<i class="tit-arr"></i></div>
	</div>
</div>
<#--用户评价 end -->
<div class="footer">
	因风水老师擅长术数及风格各有特点，故所有观点仅作参考。
</div>
<div class="button fix share"><img src="${host.img}/images/icon_share.png">分享</div>
<#if result.body.serviceStatus=="ING">
<div class="button service fix">
	<#if zxCateId ??>
	咨询&nbsp;${result.body.curServiceCateInfo.name}&nbsp;¥ ${funUtils.formatNumber(result.body.curServiceCateInfo.amt/100,"###,##0.00","--")}
	<#else>
	请选择服务
	</#if>
</div>
<#elseif result.body.serviceStatus=="NOTING">
	<#if result.body.hasReserved=="Y">
<div class="button service fix disabled">已预约，请耐心等待</div>
	<#else>
<div class="button service fix reserve">老师比较忙，点此预约</div>
	</#if>
<#elseif result.body.serviceStatus=="FIRED">
<div class="button service fix disabled">老师已与平台解约</div>
<#else>
<div class="button service fix disabled">老师暂时无法接单</div>
</#if>

<script>
	$("#responseSpeed").star({type: 'show', tip: '${result.body.respSpeedAvgScore}'})
	$("#professionalLevel").star({type: 'show', tip: '${result.body.majorLevelAvgScore}'})
	$("#serviceSAttitude").star({type: 'show', tip: '${result.body.serviceAttitudeAvgScore}'})
</script>
</body>
</html>