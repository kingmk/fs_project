 <#import "/common/host.ftl" as host>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no"/>
 <script type="text/javascript" src="${host.js}/js/jquery-1.11.3.min.js"></script>
</head>
<body>
微信 通知 
<#if isPerfectPersonalData ==false>
	<a href="${host.base}/usr/master/recruit/apply_info_supply_nav?masterInfoId${masterInfo.id}">完善个人资料</a>  
</#if>
</body>
</html>
