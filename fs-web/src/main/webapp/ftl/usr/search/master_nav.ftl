<#import "/common/host.ftl" as host>
<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">
<title>寻找老师</title>
<script src="${host.js}/js/rem.js?${host.version}"></script>
<script src="${host.js}/js/jquery-1.11.3.min.js"></script>
<script src="${host.js}/js/jquery.tmpl.min.js"></script>
<script src="${host.js}/js/dropload.min.js"></script>
<script src="${host.js}/js/common.js?${host.version}"></script>
<link rel="stylesheet" type="text/css" href="${host.css}/css/dropload.css">
<link rel="stylesheet" href="${host.css}/css/find_teacher.css?${host.version}5">
<link rel="stylesheet" href="${host.css}/css/star.css?${host.version}">
<script src="${host.js}/js/components.js?${host.version}"></script>
<style>
    .hidden{overflow-y: hidden;}
    .content-item .mask{position: absolute; top: 0; bottom: 0; left: 0; right: 0; background-color: rgba(0,0,0,0.1);}
     .content .content-list .content-item.unavailable {color: #888!important;}
</style>
<script>
var cateData = [{"group":"预测","groupId":10000,"subItem":[{"id":100015, "text":"吉凶占卜"},{"id":100000, "text":"全年运势"},{"id":100001, "text":"婚恋感情"},{"id":100016, "text":"健康旺衰"},{"id":100002, "text":"事业财运"},{"id":100017, "text":"学业预测"},{"id":100003, "text":"八字详批"}]},{"group":"择吉","groupId":10001,"subItem":[{"id":100004, "text":"结婚吉日"},{"id":100006, "text":"开张开市"},{"id":100007, "text":"乔迁择日"},{"id":100005, "text":"择吉生产"}]},{"group":"起名","groupId":10003,"subItem":[{"id":100009,"text":"个人起名"},{"id":100010,"text":"公司起名"}]},{"group":"堪舆","groupId":10004,"subItem":[{"id":100018, "text":"远程公寓居家风水"},{"id":100019, "text":"远程复式、联排别墅居家风水"},{"id":100020, "text":"远程独立别墅居家风水"},{"id":100021, "text":"远程500平米以下办公风水"},{"id":100022, "text":"远程500-1000平米办公风水"},{"id":100023, "text":"远程1000平米以上办公风水"}]}];
var zxCateId = '${zxCateId}';
var isPlatRecomm = '${isPlatRecomm}';
var orderBy = '${orderBy}';
var currentPage = '${currentPage}';
var perPageNum = '${perPageNum}';
var isRegistered = "${isRegistered}";
var choseId = '';
var droploadParams = {};
$(function(){
    $.initUserFooter({activedIndex:1});
    chatUnreadNum('${host.base}');
    $("#selectItemTmpl").tmpl(cateData).appendTo(".select-mask-body");
    //判断有没有分类搜索 初始化
    $(".sub-class .select-item-li").map(function(index,item){
        if($(item).data('id') == zxCateId){
            $(".select-item-li").removeClass('actived');
            $(item).addClass('actived');
            $("#select .select").addClass('on');
        }
    })
    if(!zxCateId){
        // isPlatRecomm = 'Y';
        orderBy = 'orderNumDesc';
        $("#orderNum").addClass('on');
         $("#orderNum").find('.arr').addClass('bottom')
        $("#platRecomm").addClass('on');

    }
    $('.select-item-li').on('click', function(){
        choseId = $(this).data('id');
        $(".select-item-li").removeClass('actived');
        $(this).addClass('actived');
    })
    $("#select").on("click", function(){
        var domSelect = $(".select-mask");
        if (domSelect.is(":hidden")) {
            domSelect.show();
        } else {
            domSelect.hide();
            zxCateIdChoose();
        }
        // $("body").on("touchmove",function(e){
        //     e.preventDefault();
        // })
    })
    $("#selectConfirm").on("click", zxCateIdChoose);
    $("#selectClear").on("click",selectClear);
    $("#orderNum").on('click',orderNum);
    $("#price").on('click',price);
    $("#evaluateScore").on("click",evaluateScore);
    dropload();

});
function dropload(){
    $('.content').dropload({
        scrollArea: window,
        domDown: {
            domClass: 'dropload-down',
            domRefresh: '',
            domLoad: '<div class="dropload-load"><span class="loading"></span>加载中</div>',
            domNoData: '<div class="dropload-noData">到底了</div>'
        },
        loadDownFn: function(me) {
            loadData(isPlatRecomm, zxCateId, orderBy, currentPage, perPageNum, function(data){
                currentPage++;
                if (data.head.code == '1000' && currentPage == 1) {
                    me.lock();
                    me.noData();
                    $(".dropload-down").hide();
                    $(".content-list").html('')
                    $("body").emptyBox({
                        iconImg: '${host.img}/images/no_search.png',
                        title: '没有找到结果'
                    })
                    me.resetload();
                } else if (data.head.code == "1000" && currentPage != 1) {
                    me.lock();
                    me.noData();
                    me.resetload();
                } else if (data.body.data.length > 0) {
                    console.log(data.body.data);
                    $("#listTmpl").tmpl(data.body.data).appendTo(".content-list");
                } else {
                    // 锁定
                    me.lock();
                    // 无数据
                    me.noData();
                }
                me.resetload();
            })
        },
        threshold: 50
    })
}

function checkData(data){
    if(data.body.data &&　data.body.data.length > 0){
        var reserveMap = data.body.reserveMap;
        data.body.data.map(function(item,index){
            if(!zxCateId){
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
function loadData(isPlatRecomm, zxCateId, orderBy, currentPage, perPageNum , callback){
    if(!$(".select-mask").is(':hidden')){
        $(".select-mask").hide();
        // $("body").off("touchmove");
    }
    $.ajax({
        url: "${host.base}/usr/search/master_ajax_query",
        method : 'POST',
        data: {
        	isPlatRecomm:  isPlatRecomm,
        	zxCateId : zxCateId,
        	orderBy : orderBy,
        	currentPage: currentPage,
        	perPageNum:perPageNum
        } ,
        dataType: "json",
        success: function(data){
            data = checkData(data);
            callback(data);
        },
        error: function(res){
        }
    });
}

function orderNum(){
    var $child = $(this).find('.arr');
    var $siblings =  $(this).siblings('.orderby');
    $(this).addClass('on');
    $siblings.removeClass('on');
    $siblings.find(".arr").prop("class",'arr');
    if($child .hasClass('top')){
        orderBy = 'orderNumDesc';
        $child.addClass('bottom').removeClass('top');
    }else if($child .hasClass('bottom')){
        orderBy = 'orderNumAsc';
        $child.addClass('top').removeClass('bottom');
    }else{
        orderBy = 'orderNumDesc';
        $child.addClass('bottom').removeClass('top');
    }
    currentPage = 0;
    loadData(isPlatRecomm, zxCateId, orderBy,currentPage, perPageNum, function(data){
        currentPage ++;
        if(data.head.code == '1000'){
            $(".dropload-down").hide();
            $(".content-list").html('');
            $(".content-list").emptyBox({iconImg:'${host.img}/images/no_search.png',title:'没有找到结果'})
         }else{
             var dataList = data.body.data;

             $("#listTmpl").tmpl(dataList).appendTo(".content-list");
         }
    })
}
function price(){
    var $child = $(this).find('.arr');
    var $siblings =  $(this).siblings('.orderby');
    $(this).addClass('on');
    $siblings.removeClass('on');
    $siblings.find(".arr").prop("class",'arr');
    if($child .hasClass('top')){
        orderBy = 'priceDesc';
        $child.addClass('bottom').removeClass('top');
    }else if($child .hasClass('bottom')){
        orderBy = 'priceAsc';
        $child.addClass('top').removeClass('bottom');
    }else{
        orderBy = 'priceAsc';
        $child.addClass('top').removeClass('bottom');
    }
    currentPage = 0;
    loadData(isPlatRecomm, zxCateId, orderBy,currentPage, perPageNum, function(data){
        currentPage ++;
        if(data.head.code == '1000'){
            $(".dropload-down").hide();
            $(".content-list").html('');
            $(".content-list").emptyBox({iconImg:'${host.img}/images/no_search.png',title:'没有找到结果'})
         }else{
             var dataList = data.body.data;
             $(".content-list").html('')
             $("#listTmpl").tmpl(dataList).appendTo(".content-list");
         }
    })
}

function evaluateScore(){
    var $child = $(this).find('.arr');
    var $siblings =  $(this).siblings('.orderby');
    $(this).addClass('on');
    $siblings.removeClass('on');
    $siblings.find(".arr").prop("class",'arr');
    if($child .hasClass('top')){
        orderBy = 'evaluateScoreDesc';
        $child.addClass('bottom').removeClass('top');
    }else if($child .hasClass('bottom')){
        orderBy = 'evaluateScoreAsc';
        $child.addClass('top').removeClass('bottom');
    }else{
        orderBy = 'evaluateScoreDesc';
        $child.addClass('bottom').removeClass('top');
    }
    currentPage = 0 ;
    loadData(isPlatRecomm, zxCateId, orderBy,currentPage, perPageNum, function(data){
        currentPage ++;
        if(data.head.code == '1000'){
            $(".dropload-down").hide();
            $(".content-list").html('');
            $(".content-list").emptyBox({iconImg:'${host.img}/images/no_search.png',title:'没有找到结果'})
         }else{
             var dataList = data.body.data;
             $(".content-list").html('')
             $("#listTmpl").tmpl(dataList).appendTo(".content-list");
         }
    })
}

function zxCateIdChoose(){
    if($(".select-mask-body").find('.actived').length <= 0){
       selectClear();
    }else{
        if(choseId == ''){
            $(".select-mask").hide();
            // $("body").off("touchmove");
            // $(".select-mask-body").on("touchmove");
        }else{
            zxCateId = choseId;
            currentPage = 0 ;
            loadData(isPlatRecomm, zxCateId, orderBy,currentPage, perPageNum, function(data){
                    currentPage ++;
                if(data.head.code == '1000'){
                    $(".dropload-down").hide();
                    $(".content-list").html('');
                    $(".content-list").emptyBox({iconImg:'${host.img}/images/no_search.png',title:'没有找到结果'})
                }else{
                    var dataList = data.body.data;
                    $(".content-list").html('')
                    $("#listTmpl").tmpl(dataList).appendTo(".content-list");
                }
            });
            $("#select").addClass('on');
            $("#select").find('.select').addClass('on');
            $(".select-mask").hide();
            // $("body").off("touchmove");
            // $(".select-mask-body").on("touchmove");
        }
    }
}

function selectClear(){
    zxCateId = '';
    currentPage = 0 ;
    loadData(isPlatRecomm, zxCateId, orderBy,currentPage, perPageNum, function(data){
        currentPage ++;
        if(data.head.code == '1000'){
            $(".dropload-down").hide();
            $(".content-list").html('');
            $(".content-list").emptyBox({iconImg:'${host.img}/images/no_search.png',title:'没有找到结果'})
         }else{
             var dataList = data.body.data;
             $(".content-list").html('')
             $("#listTmpl").tmpl(dataList).appendTo(".content-list");
         }
    });
    $("#select").removeClass('on');
    $("#select").find('.select').removeClass('on');
    $(".select-item-li").removeClass('actived');
    $(".select-mask").hide();
    // $("body").off("touchmove")
}

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
 	location.href = "${host.base}/usr/search/master_detail?masterInfoId="+masterInfoId+"&zxCateId="+zxCateId;
}

</script>
</head>

<body id='consultation'>
<div class="header">
    <div class='header-list'>
        <#-- <div class="header-item" id='platRecomm'>全部</div> -->
        <div class="header-item orderby" id='orderNum'>热度<span class='arr'></span></div>
        <div class="header-item orderby" id='price'>价格<span class='arr'></span></div>
        <div class="header-item orderby" id='evaluateScore'>评分<span class='arr'></span></div>
        <div class="header-item" id='select'>筛选<span class='select'></span></div>
    </div>
    <div class="select-mask">
    <div class="select-mask-wrap">
    <div class='select-mask-body'>
    <#noparse>
    <script id='selectItemTmpl' type="text/x-jquery-tmpl">
        <div class="mask-item">
            <div class="main-class">${group}</div>
            <ul class="sub-class clearfix">
            {{each(i,item) subItem}}
                <li class='select-item-li' data-id='${item.id}'>${item.text}</li>
            {{/each}}
            </ul>
        </div>
     </script>
    </#noparse>
    </div>
     <div class="select-mask-footer">
        <div class="footer-item" id='selectClear'>重置</div>
        <div class="footer-item" id='selectConfirm'>确定</div>
    </div>
    </div>
    </div>
</div>
<div class="content" style="padding-bottom: 5rem;">
    <div class="content-list">
    </div>
</div>
 <#noparse>
    <script id='listTmpl' type="text/x-jquery-tmpl">
     <div class="content-item {{if serviceStatus!='ING'}}unavailable{{/if}}" onclick="goToMasterDetail(${masterInfoId})">
        <div class="left">
            <img class="img" src="${masterHeadImgUrl}">
            <div class="name">${masterNickName}</div>
            <div class="star">
                <div class="star_show"><p style="width: ${scoreDesc * 66 /2/20}rem"></p></div>
            </div>
        </div>
        <div class="right">
            <div class="price">{{if serviceStatus=="ING"}}<span class='small'>¥</span> ${amtDesc} {{if isCate}}<span class='small'>起</span>{{/if}}{{else}}<span class="busy">繁忙中</span>{{/if}}</div>
            <ul class="incoming-box">
                {{if isCertificated=='Y'}}
                    <li class="red">实名认证</li>
                {{/if}}
                {{if isTranSecuried='Y'}}
                    <li class="yellow">担保交易</li>
                {{/if}}
                {{if isSignOther=='N'}}
                     <li class="blue">独家合作</li>
                {{/if}}
            </ul>
            <div class="info">
               ${experience}
            </div>
        </div>
    {{if serviceStatus!="ING"}}
        <div class="mask">
        {{if reserved == false}}
            <span class="btn-reserve" onclick="clickReserve(${masterInfoId}, '${masterNickName}')" id="reserve-${masterInfoId}">预约服务</span>
        {{else}}
            <span class="btn-reserve disabled">已预约</span>
        {{/if}}
        </div>
    {{/if}}

    </div>
    </script>
    </#noparse>
</body>

</html>
