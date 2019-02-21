 <#import "/common/host.ftl" as host>
<html>
<head>
<meta charset="UTF-8">
<script src="${host.js}/js/rem.js?${host.version}"></script>
<script src="${host.js}/js/jquery-1.11.3.min.js"></script>
<script src="${host.js}/js/jquery.tmpl.min.js"></script>
<script src="${host.js}/js/common.js?${host.version}2"></script>

<link rel="stylesheet" href="${host.css}/css/error.css?${host.version}">
<title>错误</title>
</head>
<body>
	<div class="error">
		<img class="error-icon" src="${host.img}/images/error.png">
		<div class="error-msg">${error_msg}</div>
	</div>
</body>
</html>