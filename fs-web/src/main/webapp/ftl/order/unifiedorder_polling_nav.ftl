 <#import "/common/host.ftl" as host>
<html>
<title>微信支付 结果轮询页</title>
 <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no"/>
<head>
 <script type="text/javascript" src="${host.js}/js/jquery-1.11.3.min.js"></script>
 <script>
 </script>
</head>
<body>
<h2> 微信支付 结果轮询页</h2>
<a href="${host.base}/order/order_pay_succ?orderId=${orderId}" > 前往支付成功页 模拟轮询成功 </a><br>
</body>
</html>
