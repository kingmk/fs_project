<#import "/common/host.ftl" as host>
<#import "/common/funUtils.ftl" as funUtils>
<html>
<head>
<meta charset="UTF-8">
<title>设置服务</title>
<script src="${host.js}/js/rem.js?${host.version}"></script>
<script type="text/javascript" src="${host.js}/js/jquery-1.11.3.min.js"></script>
<script>
	var hostBase = "${host.base}";
</script>
<script src="${host.js}/js/common.js?${host.version}"></script>
<link rel="stylesheet" href="${host.css}/css/select_service.css">
<script src="${host.js}/js/select_service.js?${host.version}"></script>
<style>
.hide {display: none;}
.select {position: initial!important;}
</style>
<script>
$(function(){
    var isInit = commonUtils.getUrlParam('isInit');
    if(!isInit){
        $("#submit").html('保存并返回');
        $("#topTips").hide();
        $("title").html('我的服务项目');
    }
})
</script>
</head>

<body id="select-service">
    <div class="tips" id='topTips'>
        请设置提供的服务
    </div>
    <#-- 前往 服务设置 页 -->
    <form id="selectForm">
        <div class="select-list">
        <#list serviceCateList as item>
        	<#--2017/05/24 19:19 确认 堪舆 类不在这里显示-->
        	<#if !(item.fsZxCateParentId==10005)>
            <div class="select-list-item" data-fsxcatecd= '${item.fsZxCateId}' data-fsmasterinfoid='${item.fsMasterInfoId}'' data-id='${item.id}'>
                <div class="select <#if item.id ??>on</#if>">
                    <input type="checkbox" name="checkbox" class="checkbox" <#if item.id ??>checked="checked"</#if>
                <#if item.id ??> hadSet="Y" <#else> hadSet="N" </#if> />
                </div>
                <#if item.fsZxCateParentId=='10000'>
                    <#assign clstype="prediction">
                <#elseif item.fsZxCateParentId=='10001'>
                    <#assign clstype="luckyday">
                <#elseif item.fsZxCateParentId=='10003'>
                    <#assign clstype="naming">
                <#else>
                    <#assign clstype="other">
                </#if>
                <div class="content">
                    <div class="content-img ${clstype}">${item.fsZxCateParentName}</div>
                    <div class="text-box">
                        <div class="text-title">${item.name}</div>
                        <div class="text-tips">建议价：¥${funUtils.formatNumber(item.suggestAmt/100,"###,##0.00","--")}</div>
                    </div>
                </div>
                <div class="input-box <#if !item.id ??>hide</#if>">
                    <input class="input" name="amt" type="text" placeholder="输入价格" value='<#if item.id ??>${funUtils.formatNumber(item.amt/100,"###,##0.00","--")}</#if>' />
                </div>
            </div>
            </#if>
        </#list>
        </div>
    </form>
    <div class="footer">
        注：服务費的30%会作为平台佣金有平台收取
    </div>
    <div class="button fix" id="submit">完成，进入管理后台</div>
</body>

</html>
