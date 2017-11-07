<#if (body.size>0)>
	<#list body.chatList as item>
		<div id="${item.id}" clientUniqueNo="${item.clientUniqueNo}">
		<#if item.sentUsrId == loginUsrId>
		<div class="speak-right clearfix">
			<div class="heard-img"><img src="${item.sendtUsrHeadImgUrl}"/></div>
			<#if item.msgType="text">
				<div class="speak-text"><p>${item.content}</p></div>
			<#elseif item.msgType="img">
			<div class="speak-text"><img onclick="showImg('${item.content}')" src="${item.content}" class='speak-img' data-h="${item.height}" data-w="${item.width}"></div>
			</#if>
		</div>
		<#else>
		<div class="speak-left clearfix">
			<div class="heard-img"><img src="${item.sendtUsrHeadImgUrl}"/></div>
			<#if item.msgType="text">
				<div class="speak-text"><p>${item.content}</p></div>
			<#elseif item.msgType="img">
			<div class="speak-text">
				<img onclick="showImg('${item.content}')" src="${item.content}" class='speak-img' data-h="${item.height}" data-w="${item.width}">
			</div>
			</#if>
		</div>
		</#if>
		</div>
	</#list>
</#if>
<div id = "temporaryHidediv" style="display:none">
	<input type="hidden" id="_curMaxId" value = "${body.curMaxId}"/>
	<input type="hidden" id="_curMinId" value = "${body.curMinId}"/>
	<input type="hidden" id="_curSize" value = "${body.size}"/>
</div>
