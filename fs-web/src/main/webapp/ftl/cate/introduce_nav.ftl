<#import "/common/host.ftl" as host>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>运势咨询</title>
    <script src="${host.js}/js/rem.js?${host.version}"></script>
    <script src="${host.js}/js/jquery-1.11.3.min.js"></script>
    <script src="${host.js}/js/common.js?${host.version}"></script>
    <script src="${host.js}/js/jquery.tmpl.min.js"></script>
    <link rel="stylesheet" href="${host.css}/css/consultation.css?${host.version}">
    <script>
        $(function(){
        $.initUserFooter({activedIndex:0,bubbleNum:0 });
        chartUnreadNum('${host.base}');
        })
    </script>
</head>

<body id='consultation'>
    <div class='content'>
    <#list zxCateList as item>
        <a class='card' href='${host.base}/cate/introduce_index?zxCateId=${item.id}'>
            <img src="${host.img}/images/indexImg/${item.id}.jpg?${host.version}">
            <#-- <div class="title">${item.name}</div>
            <div class='text'>${item.description}</div> -->
        </a>
    </#list>
    </div>
</body>

</html>
