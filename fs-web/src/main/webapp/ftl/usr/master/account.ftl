 <#import "/common/host.ftl" as host>
 <#import "/common/funUtils.ftl" as funUtils>
<!doctype html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>我的账户</title>
<script src="${host.js}/js/rem.js?${host.version}"></script>
<script src="${host.js}/js/jquery-1.11.3.min.js"></script>
<script src="${host.js}/js/common.js?${host.version}"></script>
<link rel="stylesheet" href="${host.css}/css/bgmask.css?${host.version}">
<link rel="stylesheet" href="${host.css}/css/my_account.css?${host.version}">
<style type="text/css">
.list .list-item .list-note {position: absolute; right: 3.5rem; font-size: 1.4rem; color: #fff; height: 2.4rem; line-height: 2.4rem; padding: 0 1rem; top: 1.3rem; background-color: #f42d47; border-radius: 1.3rem;}
</style>
</head>
<script>
$(function(){
	//设置click 事件 我的收入
	$("#myIncome").click(function(){
		location.href = "${host.base}/usr/master/my_income"
	});
	//设置click 事件 我的服务项目
	$("#myServiceCate").click(function(){
		location.href = "${host.base}/usr/master/recruit/service_cate_config_nav"
	});
	//设置 click 事件 我的资料
	$("#personal_data").click(function(){
		location.href = "${host.base}/usr/master/recruit/apply_info_supply_nav?masterInfoId=${result.body.masterInfoId}"
	});
	//设置 click 事件 我的银行卡
	$("#bank_card").click(function(){
		location.href = "${host.base}/usr/master/my_bankcard"
	});
	$("#usrEvalutate").click(function(){
		location.href = "${host.base}/usr/master/evaluate_summary"
	});
	
	//设置click 事件 预览个人主页
	$("#personal_home_page").click(function(){
		location.href = "${host.base}/usr/master/personal_home_page"
	});
	$("#order_list_nav").click(function(){
		location.href = "${host.base}/usr/master/order_list_nav"
	});

<#if result.body.serviceStatus="FORBID">
	$(".forbid-bar-btn").click(function() {
		$.bgmask({
			hasClose: false,
			hasHeader: false,
			title: "下线理由",
			text: "${result.body.forbidReason}",
			type: "normal",
			buttonTxt: "我知道了"
		})
	})
</#if>

});
var isClick = false;
$(function(){
	//是否完成了个人资料
	if( "${result.body.isPerfectPersonalData}" =="N" ){
		$.bgmask({
			hasClose:false,
			title:'您尚未配置初始化信息',
			text:'完成配置后即可接单',
			buttonTxt:'立即配置',
			buttonFn: function(){
				location.href="${host.base}/usr/master/recruit/apply_info_supply_nav?masterInfoId=${result.body.masterInfoId}?isInit=true";
			}
		});
		$(".bgmask").off('click');
		return;
	}
	//是否设置了服务项目
	if( "${result.body.isSetServiceList}" =="N" ){
		$.bgmask({
			hasClose:false,
			title:'您尚未设置服务项目',
			text:'完成配置后即可接单',
			buttonTxt:'立即设置',
			buttonFn: function(){
				location.href="${host.base}/usr/master/recruit/service_cate_config_nav?isInit=true";
			}
		});
		$(".bgmask").off('click');
		return;
	}
	$(".header-button").click(function(){
		var dom = $(this);
		var serviceStatus = dom.attr("serviceStatus");
		var _serviceStatus = "";
		var title = '';
		var text = '';
		var buttonTxt = '';
		if("ING" == serviceStatus){
			_serviceStatus = "NOTING";
			title = '您确定暂停接单吗？';
			text = '暂停后，平台将不再显示您的服务';
			buttonTxt = '暂停';
		}
		else{
			_serviceStatus = "ING";
			title = '确认开启接单？';
			text = '您的加入就是我的荣耀';
			buttonTxt = '开启';
		}
		$.bgmask({
			title:title,
			text:text,
			buttonTxt:buttonTxt,
			buttonFn: function(){
 				changeServiceStatus(_serviceStatus,dom);
			}
		})
	})

	//加载未读消息
	 $.ajax({
	    type: "POST",
	    url: "${host.base}/order/chat_unread_num_query",
	    dataType: "json",
		data:{
			isMaster:'Y'
		},
	    success: function(data){
			if(data.head.code=="0000" && data.body.unReadNum * 1 > 0){
				$("#unReadChatMsgNum").text(data.body.unReadNum +"条信息" );
				$("#unReadChatMsgNum").show();
			}
	    },
	    error: function(res){
	    }
	});
});

function changeServiceStatus(serviceStatus,dom){
	if(isClick){
		return;
	}
	isClick = true;
	var _serviceStatus = serviceStatus;
	$.ajax({
		type: "POST",
		url: "${host.base}/usr/master/recruit/update_service_status",
		dataType: "json",
		data:{
				masterInfoId :${result.body.masterInfoId},
				serviceStatus : _serviceStatus
		},
		success: function(data){
			isClick = false;
			if (data.head != "0000") {
				mAlert.addAlert(data.head.msg);
				return;
			}
			dom.attr("serviceStatus",_serviceStatus);
			if(_serviceStatus =="ING"){
				mAlert.addAlert('开启接单成功')
				$(".header-top").removeClass("start").addClass("stop");
				$(".name-box").find("p[class='status']").text("状态：接单中");
				dom.find("span").text("暂停接单");
			}else{
				mAlert.addAlert('暂停接单成功')
				$(".header-top").removeClass("stop").addClass("start");
				$(".name-box").find("p[class='status']").text("状态：暂停接单");
				dom.find("span").text("开起接单");
			}
		},
		error: function(res){}
		});
}
</script>

<style>
	.img-logo-box{
		width: 7.5rem;
    height: 7.5rem;
    border-radius: 50%;
    border: 0.1rem solid #fff;
    overflow: hidden;
		position: relative;
	}
	.img-logo-box img{position: absolute;
		z-index: 9;}
	.text-right{font-weight: bold!important;}
</style>
<body>
<#if result.body.serviceStatus="FORBID">
<div class="forbid-bar"><span class="forbid-bar-btn">下线理由</span>${result.body.forbidTimeStr}</div>
</#if>
<div class="header">
    <div class="header-top  <#if result.body.serviceStatus="ING">stop<#else>start</#if>">
        <div class="header-top-left">
            <div class="img-logo">
            		<div class="img-logo-box">
               	 <img id="headImgUrl" src="${ result.body.materHeadImgUrl}" style="width:100%;"/>
            		</div>
            </div>
            <div class="name-box">
                <p class="name">${result.body.masterNickName}</p>
                <p class="status">状态：<#if result.body.serviceStatus="ING">接单中<#elseif result.body.serviceStatus="NOTING">暂停接单<#elseif result.body.serviceStatus="FORBID">已下线</#if>  </p>
            </div>
        </div>
        <#if result.body.serviceStatus="ING">
        	  <div class="header-button" serviceStatus="${result.body.serviceStatus}">	<span>暂停接单</span></div>
        <#elseif result.body.serviceStatus="NOTING">
        	  <div class="header-button" serviceStatus="${result.body.serviceStatus}">	<span>开启接单</span></div>
        </#if>
    </div>
    <div class="header-bottom">
        <div class="income">
            <div class="income-time">到账税前收入</div>
            <div class="income-num">¥ ${funUtils.formatNumber(result.body.dzsqsr/100,"###,##0.00","--")}</div>
        </div>
        <div class="income">
            <div class="income-time">累计到账收入</div>
            <div class="income-num">¥ ${funUtils.formatNumber(result.body.ljdzsr/100,"###,##0.00","--")}</div>
        </div>
    </div>
</div>
<div class="content">
<div class="list">
    <div class="list-body">
        <div class="list-item" id = "myIncome">
            <div class="list-icon icon-income"></div>
            <div class="list-label">我的收入</div>
            <div class="list-arrow"></div>
        </div>
        <div class="list-item" id = "myServiceCate">
            <div class="list-icon icon-service"></div>
            <div class="list-label">我的服务项目</div>
            <div class="list-control">
                <div class="text-right">${result.body.currServiceCateNum}</div>
            </div>
            <div class="list-arrow"></div>
        </div>
        <div class="list-item" id = "personal_data">
            <div class="list-icon icon-data"></div>
            <div class="list-label">我的资料</div>
            <div class="list-arrow"></div>
        </div>
        <div class="list-item" id = "bank_card">
            <div class="list-icon icon-bank"></div>
            <div class="list-label">我的银行卡</div>
            <#if result.body.hasCard == 'false'><div class="list-note">请添加</div></#if>
            <div class="list-arrow"></div>
        </div>
        <div class="list-item" id = "usrEvalutate">
            <div class="list-icon icon-rating"></div>
            <div class="list-label" >用户评价</div>
            <div class="list-control">
                <div class="text-right"><#if result.body.scoreDesc == '0.00'><span style="color: #ccc!important;">暂无评分</span><#else>${result.body.scoreDesc}分</#if></div>
            </div>
            <div class="list-arrow"></div>
        </div>
    </div>
    <div class="list-body">
        <div class="list-item mt30" id = "personal_home_page">
            <div class="list-icon icon-home"></div>
            <div class="list-label">预览个人主页</div>
            <div class="list-arrow"></div>
        </div>
    </div>
</div>
</div>
<div class="footer">
    <div class="footer-nav actived">
        <div class="icon account"></div>
        <div class="title">我的账户</div>
    </div>
    <div class="footer-nav" id = "order_list_nav">
        <div class="icon order"></div>
        <div class="bubble" style="display:none" id = "unReadChatMsgNum"></div>
        <div class="title">我的订单</div>
    </div>
</div>
</body>
</html>