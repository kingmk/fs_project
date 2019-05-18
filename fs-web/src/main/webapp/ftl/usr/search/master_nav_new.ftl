<#import "/common/host.ftl" as host>
<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">
<title><#if cateName??>${cateName}<#else>寻找老师</#if></title>
<script src="${host.js}/js/rem.js?${host.version}"></script>
<script src="${host.js}/js/jquery-1.11.3.min.js"></script>
<script src="${host.js}/js/jquery.tmpl.min.js"></script>
<script src="${host.js}/js/common.js?${host.version}"></script>
<link rel="stylesheet" href="${host.css}/css/searchlist.css?${host.version}4.4"></script>
<style>


</style>
<script>
var isPlatRecomm = '${isPlatRecomm}';
var isRegistered = "${isRegistered}";

var searchParams = {
    cateId : '${zxCateId}',
    orderBy: "defaultDesc",
    currentPage: 0,
    perPageNum: 100
}

$(function(){
    <#if zxCateId == 1>
    $.initUserFooter({activedIndex:1});
    </#if>
    searchMasters(function(data) {
        console.log(data);
    });


    <#if zxCateId !=1 >
    $(".orderby").click(function() {
        clickSortItem($(this));
    })
    </#if>
});

function orderStartsWith(orderType) {
    var reg = new RegExp("^"+orderType);
    return reg.test(searchParams.orderBy);
}

function checkData(data){
    if(data.body.data &&　data.body.data.length > 0){
        var reserveMap = data.body.reserveMap;
        data.body.data.map(function(item,index){
            item['order'] = searchParams.orderBy;
            if(searchParams.cateId != '1'){
                item['isCate'] = true;
            }else{
                item['isCate'] = false;
            }
            if (reserveMap[item.masterUsrId] == "Y") {
                item.reserved = true;
            } else {
                item.reserved = false;
            }
        })
    }
    return data;
}

function searchMasters(callback) {
    var url = "";
    if (orderStartsWith("default")) {
        url = "${host.base}/usr/search/master_ajax_sort_query";
    } else {
        url = "${host.base}/usr/search/master_ajax_query2";
    }
    $.ajax({
        url: url,
        method : 'POST',
        data: searchParams,
        dataType: "json",
        success: function(data){
            if (data.head.code == "1000") {
                $("body").emptyBox({
                    iconImg: '${host.img}/images/no_search.png',
                    title: '没有找到结果'
                });
                return;
            };
            data = checkData(data);
            var dataList = data.body.data;
            $(".content-list").html('')
            $("#listTmpl").tmpl(dataList).appendTo(".content-list");
            if (typeof(callback) == "function") {
                callback(data);
            }
        },
        error: function(res){
        }
    });
}


<#if zxCateId !=1 >

function clickSortItem(domClick) {
    var domId = domClick.attr("id");
    var domArr = domClick.find(".arr");
    var orderBy = domId;
    if (domClick.hasClass("selected")) {
        if (domArr.hasClass("desc")) {
            domArr.removeClass("desc");
            domArr.addClass("asc");
            orderBy += "Asc";
        } else {
            domArr.removeClass("asc");
            domArr.addClass("desc");
            orderBy += "Desc";
        }
    } else {
        var domCur = $(".orderby.selected");
        domCur.removeClass("selected");
        var domCurArr = domCur.find(".arr")
        domCurArr.removeClass("asc");
        domCurArr.removeClass("desc");
        domClick.addClass("selected");
        domArr.addClass("desc");
        orderBy += "Desc";
    }
    searchParams.orderBy = orderBy;
    searchMasters();

}


</#if>

function clickReserve(masterInfoId, masterNickName) {
    event.stopPropagation();
    if (isRegistered == "N") {
        $.bgmask({
            title: "请先注册",
            text: "只有注册后才可以预约老师",
            type: "normal",
            buttonTxt: "去注册",
            buttonFn: function() {
                location.href = "${host.base}/usr/register/mobile_nav?backUrl="+encodeURIComponent(location.href);
            }
        })
        return;
    };

    $.bgmask({
        title: "确认预约"+masterNickName+"老师？",
        text: "预约成功后，系统会通过短信方式通知您何时可以去咨询老师",
        type: "normal",
        buttonTxt: "确认预约",
        buttonFn: function() {
            reserveMaster(masterInfoId);
        }
    })
}

function reserveMaster(masterInfoId) {
    $.ajax({
        url: "${host.base}/usr/search/reserve_master",
        method : 'POST',
        data: {
            masterInfoId : masterInfoId,
        } ,
        dataType: "json",
        success: function(data){
            console.log(data);
            if (data.head.code != "0000") {
                mAlert.addAlert(data.head.msg);
            } else {
                mAlert.addAlert("预约成功，老师恢复服务时会通过短信告知您", 3000);
                var domReserveBtn = $('#reserve-'+masterInfoId);
                domReserveBtn.addClass("disabled");
                domReserveBtn.html("已预约");
            }
        },
        error: function(res){
        }
    })

}

function goToMasterDetail( masterInfoId ){
 	var url = "${host.base}/usr/search/master_detail_new?masterInfoId="+masterInfoId;

    if (searchParams.cateId != "1") {
        url += "&zxCateId="+searchParams.cateId;
    }
    location.href = url;
}

</script>
</head>

<body>

<#if zxCateId !=1 >
<div class="header">
    <div class='header-list clearfix'>
        <div class="header-item orderby selected" id='default'>综合<span class='arr desc'></span></div>
        <div class="header-item orderby" id='order'>销量<span class='arr'></span></div>
        <div class="header-item orderby" id='price'>价格<span class='arr'></span></div>
        <div class="header-item orderby" id='evaluate'>评分<span class='arr'></span></div>
    </div>
</div>

</#if>

<div class="content">
    <div class="content-list clearfix">
    </div>
</div>

<#noparse>
<script id='listTmpl' type="text/x-jquery-tmpl">
<div class="content-item {{if serviceStatus!='ING'}}unavailable{{/if}}" onclick="goToMasterDetail(${masterInfoId})">
    <img class="headimg" src="${masterHeadImgUrl}">
    <div class="name">${masterNickName}</div>
    <div class="price"><span class='small'>¥</span> ${amtDesc} {{if isCate}}<span class='small'>起</span>{{/if}}</div>
    {{if order == "evaluateDesc" || order == "evaluateAsc"}}
    <div class="evaluate"><span class="icon_star"></span>${scoreDesc}分</div>
    {{else}}
    <div class="ordernum">已解答：${countOrder}单</div>
    {{/if}}

{{if serviceStatus!="ING"}}
    {{if reserved == false}}
    <div class="reservetag">繁忙中</div>
    {{else}}
    <div class="reservetag reserved">已预约</div>
    {{/if}}
    </div>
{{/if}}

</div>
</script>
</#noparse>

</body>

</html>
