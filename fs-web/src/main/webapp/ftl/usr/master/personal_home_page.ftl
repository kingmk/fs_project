 <#import "/common/host.ftl" as host>
 <#import "/common/funUtils.ftl" as funUtils>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>预览个人主页</title>
	<script src="${host.js}/js/rem.js?${host.version}"></script>
    <script src="${host.js}/js/jquery-1.11.3.min.js"></script>
    <script src="${host.js}/js/common.js?${host.version}"></script>
	<link rel="stylesheet" href="${host.css}/css/teacher_home.css?${host.version}">
	<link rel="stylesheet" href="${host.css}/css/star.css?${host.version}">
	<script src="${host.js}/js/components.js?${host.version}"></script>
	<style>
		.fixed {
			position: fixed;
			top: 0;
			left: 0;
			right: 0;
		}
		.center-fixed{
			padding-top: 4.5rem;
		}
		.mb30{
			margin-bottom: 30rem;
		}
		.center .service-intros .service-intros-list .service-intros-item{justify-content: inherit;}


.img-logo-box{width: 9rem; height: 9rem ;border-radius: 50%; border: 0.1rem solid #fff; overflow: hidden ;position: relative; margin: 0 auto;}
.img-logo-box img{position: absolute; z-index: 9;}
.briefs-item-tit span{background-image: url(${host.img}/images/dot.png)!important;}
	</style>

<script>
var isClick = false;
$(function(){
	window.scrollTo(0,0);
	$(".user-comments-tit").click(function(){
		location.href = "${host.base}/usr/master/evaluate_summary";
	});
	$('.center-nav .nav-item').on('click', function(e){
		$('.center-nav').addClass('fixed')
		$('.center').addClass('center-fixed');
		var top = $("."+$(this).attr('id')).offset().top;
		$('html, body').animate({scrollTop:top-$('.center-nav').height()}, 'slow');
	})
});
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

</script>
<style>
	.head-icon-sex{background:url(${host.img}/images/sex_icon.png) no-repeat; background-size: 1.6rem auto; padding-left: 2.2rem; height:1.6rem; display:inline-block;line-height: 1.6rem;}
	.head-icon-sex.M{background-position: 0 0;}
	.head-icon-sex.F{background-position: 0 -1.6rem;}
</style>
</head>
<body ms-controller='teacherHome'>
<div class="header">
	<div class="header-top">
		<div class="img-logo-box"><img id="headImgUrl" class="header-top-img" src="${result.body.materHeadImgUrl}" style="width:100%; left:0; top: 0; padding: 0; margin:0;"></div>
		<h3 class="header-top-h3"><span><i class="head-icon-sex ${result.body.sex}"></i><b>${result.body.masterName}</b></span></h3>
		<ul class="hreader-top-nav">
			<li>实名认证</li>
			<li>独家合作</li>
			<li>交易担保</li>
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
	</div>
</div>
<div class="center">
	<div class="center-nav">
		<div class="nav-item actived" id='teacher-briefs'>老师介绍</div>
		<div class="nav-item" id='service-intros'>服务介绍</div>
		<div class="nav-item" id='user-comments'>用户评价</div>
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
	<!--服务项目-->
	<div class="service-intros">
		<div class="service-intros-tit">服务项目 (${result.body.serviceCateSize})</div>
		<ul class="service-intros-list">
			<#if result.body.serviceCateList ?? >
				<#list  result.body.serviceCateList as item>
					<li class="service-intros-item">
						<div class="item-logo <#if item.cateParentId=='10000'>
							prediction
						<#elseif item.cateParentId=='10001'>
							luckyday
						<#elseif item.cateParentId=='10003'>
							naming
						<#else>
							other
						</#if>">${item.cateParentName}</div>
						<div class="item-center">
							<div class="item-center-top">
								<span class="item-center-title">${item.cateName}</span>
								<span class="item-center-price">¥${funUtils.formatNumber(item.amt/100,"###,##0.00","--")} </span>
							</div>
							<div class="item-center-bottom">已售<span>${item.serllerNum}</span>份</div>
						</div>
					</li>
				</#list>
			</#if>
		</ul>
	</div>
	<!--用户评价-->
	<div class="user-comments">
		<div class="user-comments-tit">用户评价 (${result.body.evaluateTotal})<i class="tit-arr"></i></div>
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
</div>
<div class="footer  mb30">
	因风水老师擅长术数及风格各有特点，故所有观点仅作参考。
</div>
<div class="button fix" onclick="location.href='${host.base}/usr/master/account'">返回</div>
<script>
	$("#responseSpeed").star({type: 'show', tip: '${result.body.respSpeedAvgScore}'})
	$("#professionalLevel").star({type: 'show', tip: '${result.body.majorLevelAvgScore}'})
	$("#serviceSAttitude").star({type: 'show', tip: '${result.body.serviceAttitudeAvgScore}'})
</script>
</body>
</html>