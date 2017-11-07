<#import "/common/host.ftl" as host>
<#if result.head.code="0000">
<#list result.body.data as item>
<div class="rating-content">
	<div class="rating-content-list">
		<div class="list-item">
			<div class="img-box">
				<img src="${item.buyUsrHeadImgUrl}" class="img">
			</div>
			<div class="list-item-content">
				<div class="name">${item.buyUsrName}</div>
				<div class="rating">${item.evaluateWord}</div>
				<div class="time">${item.evaluateTime}  ${item.goodsName} </div>
				<#if item.masterReplyWord>
				<div class="reply-box" >
					<div class="reply-content"><div class="reply-arrow"></div>老师回复：${item.masterReplyWord}</div>
				</div>
				</#if>
			</div>
		</div>
	</div>
</div>
</#list>
</#if>