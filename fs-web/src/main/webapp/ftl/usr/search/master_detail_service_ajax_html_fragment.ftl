 <#import "/common/host.ftl" as host>
 <#import "/common/funUtils.ftl" as funUtils>
<div class="service-intros-tit">服务项目 (${result.body.serviceCateSize})</div>
<ul class="service-intros-list">
	<#if result.body.serviceCateList ?? >
		<#list  result.body.serviceCateList as item>
			<li class="service-intros-item">
				<div class="item-left">
				<div class="item-logo
				<#if item.cateParentId=='10000'>
					prediction
				<#elseif item.cateParentId=='10001'>
					luckyday
				<#elseif item.cateParentId=='10003'>
					naming
				<#elseif item.cateParentId=='10004'>
					kanyu
				<#else>
					other
				</#if>">${item.cateParentName}</div>
				<div class="item-center">
					<div class="item-center-top">
						<span class="item-center-title">${item.cateName}</span>
					</div>
					<div class="item-center-bottom">
						<span class="item-center-price">¥${funUtils.formatNumber(item.amt/100,"###,##0.00","--")} </span>
					</div>
					<!-- <div class="item-center-bottom">已售<span>${item.serllerNum}</span>份</div> -->
				</div></div>
				<#-- <#if !(zxCateId ??)> -->
				<div class="item-right">
					<a href='${host.base}/order/confirm_nav?masterServiceCateId=${item.id}&masterInfoId=${item.masterInfoId}'>立即咨询</a>
				</div>
				<#-- </#if> -->
			</li>
		</#list>
	</#if>
</ul>