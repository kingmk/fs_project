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
<link rel="stylesheet" href="${host.css}/css/user_rating.css?${host.version}1.0.1">
<link rel="stylesheet" href="${host.css}/css/masterhome.css?${host.version}3.2">
<script src="${host.js}/js/components.js?${host.version}"></script>
<style>
/*.fixed{position:fixed;top:0;left:0;right:0}
.center-fixed{padding-top:4.5rem}*/
.header .header-bottom .header-bottom-item .item-num{color: #d88c3a;}

</style>


<script>
var isFollowed  = '${result.body.isFollowed}';
var isZxCateId = '${zxCateId}';
var a = ${resultStr};
var wxconfig = ${wxconfig};
var isRegistered = "${result.body.isRegistered}";
// var curCateStr = "${result.body.curCateStr}";
$(function(){
	$(".button.service").click(function() {
		history.back();
	});
	loadUsrCommentsIntro();
	initFollow();
	var mt = $(".header").height();
	$("body").css("margin-top", mt+"px");


	$('.center-nav .nav-item').on('click', function(e){
		var domClick = $(this);
		var domContent = $("."+domClick.attr('id'));
		domContent.siblings().hide();
		domContent.show();
		domClick.siblings().removeClass("selected");
		domClick.addClass("selected");
	})

	$("#linkTo").on('click',function(){
		location.href='${host.base}/usr/master/evaluate_summary?masterUsrId=${result.body.masterUsrId}'
	})
});

function goCateDetail(cateId) {
	console.log(cateId);
}

function initFollow() {
	//取消关注事件
	$(".btn-follow").on("click", function(){
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
			<div class="logo-box">
				<img id="headImgUrl" class="header-top-img" src="${result.body.masterHeadImgUrl}" >
				<#if result.body.isFollowed == 'Y'>
				<div class="btn-follow followed">已关注</div>
				<#else>
				<div class="btn-follow">+关注</div>
				</#if>
			</div>
			<div class="intro-box">
				<div class="nickname">${result.body.masterNickName}</div>
				<div class="numbers">
					<div class="number-item">
						<div class="number-title">总解答数</div>
						<div class="number-count">${result.body.orderTotalNum}</div>
					</div>
					<div class="number-item">
						<div class="number-title">粉丝数</div>
						<div class="number-count">${result.body.fansTotalNum}</div>
					</div>
				</div>
			</div>
		</div>
		<div class="center-nav">
			<div class="nav-item selected" id="teacher-briefs">老师介绍</div>
			<div class="nav-item" id="user-comments">总体评价(${result.body.countEvaluate})</div>
			<div class="nav-item" id="service-intros">热门咨询</div>
		</div>
	</div>
	<div class="content">

		<!--老师介绍-->
		<div class="teacher-briefs">
			<div class="briefs-item">
				<div class="briefs-item-tit"><span class="icon-squre"></span><span>所学门派</span></div>
				<div class="briefs-item-text">${result.body.school}</div>
			</div>
			<div class="briefs-item">
				<div class="briefs-item-tit"><span class="icon-squre"></span><span>相关经历</span></div>
				<div class="briefs-item-text">${result.body.experience}</div>
			</div>
			<div class="briefs-item">
				<div class="briefs-item-tit"><span class="icon-squre"></span><span>所获成就</span></div>
				<div class="briefs-item-text">${result.body.achievement}</div>
			</div>
			<div class="briefs-item">
				<div class="briefs-item-tit"><span class="icon-squre"></span><span>擅长领域</span></div>
				<div class="briefs-item-text">${result.body.goodAt}</div>
			</div>
		</div>
		
		<!--用户评价-->
		<div class="user-comments" style="display:none;">
			<div class="user-comments-content">
				<div class="score-title">来自 ${result.body.countEvaluate} 位用户的评价<i class="tit-arr"></i></div>
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
			<div id="evaluate_list">
			</div>
			<div class="user-comments-tit" id='linkTo'><span>查看全部的评价 (${result.body.countEvaluate})</span><span class="tit-arr"></span></div>
		</div>
		<!--热门咨询-->
		<div class="service-intros clearfix" style="display:none;">
			<#list result.body.services as service>
				<div class="service-intro-item" onclick="goCateDetail(${service.cateId})">${service.cateName}(${service.countOrder})<br>${funUtils.formatNumber(service.price/100,"###,##0.00","--")}元<#if service.cateId==1>起</#if></div>
			</#list>
		</div>

	</div>
	<div class="button service fix">返回</div>

<script>
	$("#responseSpeed").star({type: 'show', tip: '${result.body.respSpeedAvgScore}'})
	$("#professionalLevel").star({type: 'show', tip: '${result.body.majorLevelAvgScore}'})
	$("#serviceSAttitude").star({type: 'show', tip: '${result.body.serviceAttitudeAvgScore}'})
</script>
</body>
</html>