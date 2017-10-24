 <#import "/common/host.ftl" as host>
<html>
<head>
 <meta charset="UTF-8">
    <title>${order.goodsName}</title>
    <script src="${host.js}/js/rem.js?${host.version}"></script>
    <script src="${host.js}/js/jquery-1.11.3.min.js"></script>
    <script src="${host.js}/js/jquery.tmpl.min.js"></script>
    <script src="${host.js}/js/mobiscroll.custom-3.0.0-beta2.min.js"></script>
    <script src="${host.js}/js/common.js?${host.version}"></script>
    <script src="${host.js}/js/cityDate.js?${host.version}"></script>
    <link rel="stylesheet" href="${host.css}/css/mobiscroll.custom-3.0.0-beta2.min.css">
    <link rel="stylesheet" href="${host.css}/css/pay_result.css?${host.version}">
    <style>
    .error{border: 1px solid #f00;}
    </style>
 <script>
 $(function(){
    $("input").on('input propertychange', function(){
        if($(this).val()){
            $(this).parents(".list-control").removeClass('error');
        }
    })
 	$("#submit").on('click', submit);

 	$("#isSelf1, #isSelf2").click(function(){
 		var _thisId = $(this).prop("id");
        var _otherId = _thisId == 'isSelf1'?'isSelf2':'isSelf1';
 		if($("#"+_thisId).hasClass('open1')){
 			$('#'+_otherId).removeClass('open1').addClass('close1')
 		}
 	})
 })

 function convertBirthTimeText(birthTime) {
    var birthTimeText = "";
    if (birthTime == "不清楚") {
        birthTimeText = "不清楚";
    } else if (birthTime.indexOf("不清楚") == "3") {
        birthTimeText = birthTime.substr(0,2)+":00之后";
    } else {
        birthTimeText = birthTime;
    }
    return birthTimeText;
 }

 function submit(){
 	var formArr1 = $("#form1").serializeArray();
 	var formArr2 = $("#form2").serializeArray();
 	var formArr3 = $("#form3").serializeArray();
 	var data1 = {};
 	var data2 = {};
 	var data3 = {};

	formArr1.map(function(item,index){
        var value = item.value;
        if (value == "placeholder") {
            value = "";
        };
        data1[item.name] = value;
    })

	formArr2.map(function(item,index){
        var value = item.value;
        if (value == "placeholder") {
            value = "";
        };
        data2[item.name] = value;
    })

	formArr3.map(function(item,index){
        var value = item.value;
        if (value == "placeholder") {
            value = "";
        };
        data3[item.name] = value;
    })
    var postArr = [];
    <#--全年运势, 健康旺衰, 事业财运, 学业预测, 命运祥批, 堪舆类-->
    <#if order.zxCateId==100000 || order.zxCateId==100016 || order.zxCateId==100002 || order.zxCateId==100017 || order.zxCateId==100003 || (order.zxCateId>=100018 && order.zxCateId<=100023)>
        if($("#isSelf1").hasClass('open1')){
            data1['isSelf'] = 'Y';
        }else{
            data1['isSelf'] = 'N';
        }
        data1['birthTimeText'] = convertBirthTimeText(data1['birthTime']);
        data1['birthDate'] = data1['birth'];
        data1['birthTimeType']= 'min';
        $.map(data1, function(item, index) {
            if(index != 'englishName') {
                if(!item || item == 'placeholder'){
                    $("input[name='"+ index +"']").parents('.list-control').addClass('error');
                }else{
                    $("input[name='"+ index +"']").parents('.list-control').removeClass('error');
                }
            }
        });
        if($(".error").length>0){
            mAlert.addAlert('您有未填写信息,请填写完整！',2000)
            return;
        }else{postArr.push(data1);}

    <#--个人改名-->
    <#elseif order.zxCateId=100008>
        if($("#isSelf1").hasClass('open1')){
            data1['isSelf'] = 'Y';
        }else{
            data1['isSelf'] = 'N';
        }
        data1['birthTimeText'] = convertBirthTimeText(data1['birthTime']);
        data1['birthDate'] = data1['birth'];
        data1['birthTimeType']= 'min';
        $.map(data1, function(item, index) {
            if((!item || item == 'placeholder') && index!='onceName'){
                $("input[name='"+ index +"']").parents('.list-control').addClass('error');
            }else{
                $("input[name='"+ index +"']").parents('.list-control').removeClass('error');
            }

        });
        if($(".error").length>0){
            mAlert.addAlert('您有未填写信息,请填写完整！',2000)
            return;
        }else{postArr.push(data1);}
    <#--个人起名-->
    <#elseif order.zxCateId=100009>
        data1['birthDate'] = data1['birth'];
        data1['birthTimeType']= 'min';
        data1['birthTimeText'] = convertBirthTimeText(data1['birthTime']);
        $.map(data1, function(item, index) {
            if((!item || item == 'placeholder') && index != "brotherName" && index != 'sisterName'){
                $("input[name='"+ index +"']").parents('.list-control').addClass('error');
            }else{
                $("input[name='"+ index +"']").parents('.list-control').removeClass('error');
            }
        });
        if($(".error").length>0){
            mAlert.addAlert('您有未填写信息,请填写完整！',2000)
            return;
        }else{postArr.push(data1);}
    <#--企业起名-->
    <#elseif order.zxCateId=100010 || order.zxCateId=100006>
        if($("#isSelf2").hasClass('open1')){
            data2['isSelf'] = 'Y';
        }else{
            data2['isSelf'] = 'N';
        }
        data2['birthTimeText'] = convertBirthTimeText(data2['birthTime']);
        data2['birthDate'] = data2['birth'];
        data2['birthTimeType']= 'min';
        c = $.extend(data1,data2);
        $.map(c, function(item, index) {
            if((!item || item == 'placeholder') <#if order.zxCateId==100010>&& index != "curComName"</#if>){
                $("input[name='"+ index +"']").parents('.list-control').addClass('error');
            }else{
                $("input[name='"+ index +"']").parents('.list-control').removeClass('error');
            }
        });
        if($(".error").length>0){
            mAlert.addAlert('您有未填写信息,请填写完整！',2000)
            return;
        }else{ postArr.push(c);}
    <#--结婚吉日，择吉生产，婚恋感情-->
    <#elseif order.zxCateId=100004 || order.zxCateId=100005 || order.zxCateId==100001>
        if($("#isSelf1").hasClass('open1')){
            data1['isSelf'] = 'Y';
        }else{
            data1['isSelf'] = 'N';
        }
        data1['birthTimeText'] = convertBirthTimeText(data1['birthTime']);
        data1['birthDate'] = data1['birth'];
        data1['birthTimeType']= 'min';
        if($("#isSelf2").hasClass('open1')){
            data2['isSelf'] = 'Y';
        }else{
            data2['isSelf'] = 'N';
        }
        data2['birthTimeText'] = convertBirthTimeText(data2['birthTime']);
        data2['birthDate'] = data2['birth'];
        data2['birthTimeType']= 'min';
        data1['sex'] = 'M';
        data2['sex'] = 'F'
        data1
        <#if order.zxCateId=100005>
        data1['isFather'] = 'Y';
        data1['isMather'] = 'N';
        data2['isFather'] = 'N';
        data2['isMather'] = 'Y';
        <#elseif order.zxCateId=100004>
        data1['isFiance'] = 'Y' ;
        data1['isFiancee'] = 'N';
        data2['isFiance'] = 'Y';
        data2['isFiancee'] = 'N';
        </#if>
        <#if order.zxCateId==100001>
         $.map(data1, function(item, index) {
            if( (!item || item == 'placeholder') && index != "englishName"){
                $("#form1 input[name='"+ index +"']").parents('.list-control').addClass('error');
            }else{
                $("#form1 input[name='"+ index +"']").parents('.list-control').removeClass('error');
            }
        });
        $.map(data2, function(item, index) {
            if((!item || item == 'placeholder') && index != "englishName"){
                $("#form2 input[name='"+ index +"']").parents('.list-control').addClass('error');
            }else{
                $("#form2 input[name='"+ index +"']").parents('.list-control').removeClass('error');
            }
        });
        if($("#form1 .error").length>0 && $("#form2 .error").length>0){
            mAlert.addAlert('双方信息至少有一方是完整的',2000);
            return;
        }else{postArr.push(data1); postArr.push(data2);}
        <#else>
        data1 = $.extend(data1,data3);
        data2 = $.extend(data2,data3);
        $.map(data1, function(item, index) {
            if( (!item || item == 'placeholder') && index != "englishName" && index != 'expectMarriageDateBegin' && index != 'expectMarriageDateEnd'){
                $("#form1 input[name='"+ index +"']").parents('.list-control').addClass('error');
            }else{
                $("#form1 input[name='"+ index +"']").parents('.list-control').removeClass('error');
            }
        });
        $.map(data2, function(item, index) {
            if((!item || item == 'placeholder') && index != "englishName" && index != 'expectMarriageDateBegin' && index != 'expectMarriageDateEnd'){
                $("#form2 input[name='"+ index +"']").parents('.list-control').addClass('error');
            }else{
                $("#form2 input[name='"+ index +"']").parents('.list-control').removeClass('error');
            }
        });
        if($("#form1 .error").length>0 || $("#form2 .error").length>0  || $("#form3 .error").length>0){
            mAlert.addAlert('您有未填写信息,请填写完整！',2000)
            return;
        }else{postArr.push(data1); postArr.push(data2);}
        </#if>
    <#--乔迁择日-->
    <#elseif order.zxCateId==100007>
        if($("#isSelf1").hasClass('open1')){
            data2['isSelf'] = 'Y';
        }else{
            data2['isSelf'] = 'N';
        }
        data2['birthTimeText'] = convertBirthTimeText(data2['birthTime']);
        data2['birthDate'] = data2['birth'];
        data2['birthTimeType']= 'min';
        data2['isOwner']= 'Y';
        data2['isSpouse']= 'N';
        if($("#isSelf2").hasClass('open1')){
            data3['isSelf'] = 'Y';
        }else{
            data3['isSelf'] = 'N';
        }
        data3['birthTimeText'] = convertBirthTimeText(data3['birthTime']);
        data3['birthDate'] = data3['birth'];
        data3['birthTimeType']= 'min';
        data3['isOwner']= 'N';
        data3['isSpouse']= 'Y';
        var a = $.extend(data2,data1);
        var b = $.extend(data3,data1);
        $.map(a, function(item, index) {
            if(　!item && (index == 'newAddress' ||index == 'completedTime'　)){
                $("#form1 input[name='"+ index +"']").parents('.list-control').addClass('error');
            }else{
                $("#form1 input[name='"+ index +"']").parents('.list-control').removeClass('error');
            }
            if( (!item || item == 'placeholder') && index == 'expectMoveDateBegin' && index == 'expectMoveDateEnd' && index != 'englishName'){
                $("#form2 input[name='"+ index +"']").parents('.list-control').addClass('error');
            }else{
                $("#form2 input[name='"+ index +"']").parents('.list-control').removeClass('error');
            }

        });
        $.map(b, function(item, index) {
            if(!item && (index == 'newAddress' ||index == 'completedTime'　)){
                $("#form1 input[name='"+ index +"']").parents('.list-control').addClass('error');
            }else{
                $("#form1 input[name='"+ index +"']").parents('.list-control').removeClass('error');
            }
            if(!item || item == 'placeholder'){
                delete b[index];
            }
        });
        if($("#form1 .error").length>0 || $("#form2 .error").length>0 || $("#form3 .error").length>0){
            mAlert.addAlert('您有未填写信息,请填写完整！',2000)
            return;
        }else{postArr.push(a); postArr.push(b);}
    </#if>

    $.ajax({
	    type: "POST",
	    url: "${host.base}/order/supply_zx_usrinfo",
	    data: {
	    	 	orderId:  "${order.id}" ,
	    		chatSessionNo:  "${order.chatSessionNo}" ,
	    		data: JSON.stringify(postArr)
	    		}  ,
	    dataType: "json",
	    success: function(data){
            if(data.head.code == '0000'){
                mAlert.addAlert('填写成功');
                setTimeout(function(){
                    location.href = '${host.base}/order/chat_index?chatSessionNo=${order.chatSessionNo}&orderId=${order.id}&afterCommUsrSupplyInfo=true'
                },2000)
            }else{
                mAlert.addAlert(data.head.msg);
            }

	    },
	    error: function(res){
	    }
	});
 }
 </script>
</head>
<body>
 <#noparse>
 <script id='formItem' type="text/x-jquery-tmpl">
    <div class="list-item">
        <div class="list-label" style=${style}>${label}</div>
        <div class="list-control">
        	{{if type != 'isRadio'}}
            <input placeholder="${placeholder}" type="text" class='${className}' value="${value}" name='${name}'>
        	{{/if}}
        </div>
        {{if hasArr}}
        <div class="list-arrow">
        {{/if}}
        {{if type == 'isRadio'}}
        <div class="radio-box-parent {{if value=='Y'}} open1{{else}}close1{{/if}}" id='${id}'>
            <div class="radio-box-child open2"></div>
        </div>
        {{/if}}
        </div>
    </div>
</script>
 </#noparse>
 <div class="apply-card">
    <div class="apply-title">对话前请填写大师需要的信息</div>
    <div class="apply-tips">请确保所填信息的准确性，以免影响到您的测试结果</div>
</div>
 <div class="content" style="padding-bottom: 6rem;">
    <form id="form1">
	    <div class="list form">
	        <div class="list-header">咨询人信息</div>
	        <div class="list-body">
	        </div>
	    </div>
    </form>
     <form id="form2" style="display: none">
	    <div class="list form">
	        <div class="list-header">咨询人信息</div>
	        <div class="list-body">
	        </div>
	    </div>
    </form>
    <form id="form3" style="display: none">
    <div class="list form">
        <div class="list-header">其它信息</div>
        <div class="list-body">
        </div>
	</div>
	</form>

</div>
<div class="button fix" style="margin-top: 3rem; z-index:9;" id='submit'>提交，开始对话</div>

<script>
	<#--全年运势, 健康旺衰, 事业财运, 学业预测, 命运祥批, 堪舆类-->
	<#if order.zxCateId==100000 || order.zxCateId==100016 || order.zxCateId==100002 || order.zxCateId==100017 || order.zxCateId==100003 || (order.zxCateId>=100018 && order.zxCateId<=100023)>
		var arr = [
		{type:'isRadio',label: '是否本人',value:'Y',id :'isSelf1'},
		{type:'input',className:'',name:'realName',label: '中文姓名',value:'${usr.realName}',placeholder:'必填',hasArr:false},
		{type:'input',className:'',name:'englishName',label: '英文姓名',value:'${usr.englishName}',placeholder:'非必填',hasArr:false},
 		{type: 'select',className: 'birth',name: 'birth',placeholder:'必填',label: '阳历生日',value: '${usrBirthDate}',hasArr: true},
 		{type: 'select',className: 'birthTime',name: 'birthTime',placeholder:'必填',label: '出生时间',value: '<#if usr.birthTime??>${usr.birthTime}<#else>不清楚</#if>',hasArr: true},
 		{type: 'select',className: 'birthCity',name: 'birthAddress',placeholder:'必填',label: '出生地',value: '<#if usr.birthAddress??>${usr.birthAddress}<#else>placeholder</#if>',hasArr: true},
 		{type: 'select',className: 'sex',name: 'sex',placeholder:'必填',label: '性别',value: '<#if usr.sex??>${usr.sex}<#else>M</#if>',hasArr: true}
        <#if order.zxCateId==100000 || order.zxCateId==100002 || order.zxCateId==100003>
 		,{type: 'select',className: 'marriage',name: 'marriageStatus',placeholder:'必填',label: '婚姻状况',value: '<#if usr.marriageStatus?? >${usr.familyRank}<#else>single</#if>',hasArr: true},
 		{type: 'select',className: 'familyRank',name: 'familyRank',placeholder:'必填',label: '家中排行',value: '<#if usr.familyRank?? >${usr.familyRank}<#else>1</#if>',hasArr: true}
        </#if>
 		]
 		$("#formItem").tmpl(arr).appendTo("#form1 .list-body");
 	<#--个人改名-->
	<#elseif order.zxCateId==100008>
		var arr = [
		{type:'isRadio',label: '是否本人',value:'Y',id :'isSelf1'},
		{type:'input',className:'',name:'realName',label: '现用名',value:'${usr.realName}',placeholder:'必填',hasArr:false},
		{type:'input',className:'',name:'onceName',label: '曾用名',value:'',placeholder:'非必填',hasArr:false},
 		{type: 'select',className: 'birth',name: 'birth',placeholder:'必填',label: '阳历生日',value: '${usrBirthDate}',hasArr: true},
 		{type: 'select',className: 'birthTime',name: 'birthTime',placeholder:'必填',label: '出生时间',value: '<#if usr.birthTime??>${usr.birthTime}<#else>不清楚</#if>',hasArr: true},
 		{type: 'select',className: 'birthCity',name: 'birthAddress',placeholder:'必填',label: '出生地',value: '<#if usr.birthAddress??>${usr.birthAddress}<#else>placeholder</#if>',hasArr: true},
 		{type: 'select',className: 'sex',name: 'sex',placeholder:'必填',label: '性别',value: '<#if usr.sex??>${usr.sex}<#else>M</#if>',hasArr: true},
 		{type: 'select',className: 'marriage',name: 'marriageStatus',placeholder:'必填',label: '婚姻状况',value: '<#if usr.marriageStatus?? >${usr.familyRank}<#else>single</#if>',hasArr: true},
 		{type: 'select',className: 'familyRank',name: 'familyRank',placeholder:'必填',label: '家中排行',value: '<#if usr.familyRank?? >${usr.familyRank}<#else>1</#if>',hasArr: true}
 		]
 		$("#form1 .list-header").html('改名人信息');
 		$("#formItem").tmpl(arr).appendTo("#form1 .list-body");
 	<#--个人起名-->
	<#elseif order.zxCateId==100009>
		var arr = [
 		{type: 'select',className: 'birth',name: 'birth',placeholder:'必填',label: '阳历生日',value: '',hasArr: true},
 		{type: 'select',className: 'birthTime',name: 'birthTime',placeholder:'必填',label: '出生时间',value: '不清楚',hasArr: true},
 		{type: 'select',className: 'birthCity',name: 'birthAddress',placeholder:'必填',label: '出生地',value: 'placeholder',hasArr: true},
 		{type: 'select',className: 'sex',name: 'sex',placeholder:'必填',label: '性别',value: 'M',hasArr: true},
 		{type: 'select',className: 'familyRank',name: 'familyRank',placeholder:'必填',label: '家中排行',value: '1',hasArr: true},
 		{type:'input',className:'',name:'brotherName',label: '哥哥姓名',value:'',placeholder:'非必填',hasArr:false},
		{type:'input',className:'',name:'sisterName',label: '姐姐姓名',value:'',placeholder:'非必填',hasArr:false},
 		]
 		$("#form1 .list-header").html('起名人信息');
 		$("#formItem").tmpl(arr).appendTo("#form1 .list-body");
    <#--企业起名-->
    <#elseif order.zxCateId==100010 || order.zxCateId==100006>
        var arr1= [

        {type:'input',className:'',name:'curComName',label: '企业现用名',value:'',placeholder:'<#if order.zxCateId == 100010>非必填<#else>必填</#if>',hasArr:false},
        {type:'input',className:'',name:'industry',label: '所处行业',value:'',placeholder:'必填',hasArr:false},
        {type:'input',className:'',name:'scopeOfBusiness',label: '经营范围',value:'',placeholder:'必填',hasArr:false},
        {type: 'select',className: 'birthCity',name: 'comAddress',placeholder:'必填',label: '所在地',value: 'placeholder',hasArr: true},
        ]
        <#if order.zxCateId=100010>
            arr1.splice(1,0,{type: 'select',className: 'birth',name: 'establishedDate',placeholder:'必填',label: '成立时间',value: '',hasArr: true})
        </#if>
        var arr2 = [
        {type:'isRadio',label: '是否本人', value:'Y' , id:'isSelf2'},
        {type:'input',className:'',name:'realName',label: '中文姓名',value:'${usr.realName}',placeholder:'必填',hasArr:false},
        {type: 'select',className: 'birth',name: 'birth',placeholder:'必填',label: '阳历生日',value: '${usrBirthDate}',hasArr: true},
        {type: 'select',className: 'birthTime',name: 'birthTime',placeholder:'必填',label: '出生时间',value: '<#if usr.birthTime??>${usr.birthTime}<#else>不清楚</#if>',hasArr: true},
        {type: 'select',className: 'birthCity',name: 'birthAddress',placeholder:'必填',label: '出生地',value: '<#if usr.birthAddress??>${usr.birthAddress}<#else>placeholder</#if>',hasArr: true},
        {type: 'select',className: 'sex',name: 'sex',placeholder:'必填',label: '性别',value: '<#if usr.sex??>${usr.sex}<#else>M</#if>',hasArr: true}]
        $("#formItem").tmpl(arr1).appendTo("#form1 .list-body");
        $("#formItem").tmpl(arr2).appendTo("#form2 .list-body");
        $("#form1 .list-header").html('企业信息');
        $("#form2 .list-header").html('企业主信息');
        $("#form2").show();
	<#--结婚吉日-- 择吉生产 -- 婚恋感情100001--->
	<#elseif order.zxCateId==100004 || order.zxCateId==100005 || order.zxCateId==100001 >
	var arr1 = [
		{type:'isRadio',label: '是否本人', value:'<#if usr.sex?? && usr.sex=="F">N<#else>Y</#if>' , id:'isSelf1'},
		{type:'input',className:'',name:'realName',label: '中文姓名',value:'<#if usr.sex?? && usr.sex=="F"><#else>${usr.realName}</#if>',placeholder:'必填',hasArr:false},
		{type:'input',className:'',name:'englishName',label: '英文姓名',value:'<#if usr.sex?? && usr.sex=="F"><#else>${usr.englishName}</#if>',placeholder:'非必填',hasArr:false},
 		{type: 'select',className: 'birth',name: 'birth',placeholder:'必填',label: '阳历生日',value: '<#if usr.sex?? && usr.sex=="F"><#else>${usrBirthDate}</#if>',hasArr: true},
 		{type: 'select',className: 'birthTime',name: 'birthTime',placeholder:'必填',label: '出生时间',value: '<#if usr.sex?? && usr.sex=="F">不清楚<#else><#if usr.birthTime??>${usr.birthTime}<#else>不清楚</#if></#if>',hasArr: true},
 		{type: 'select',className: 'birthCity',name: 'birthAddress',placeholder:'必填',label: '出生地',value: '<#if usr.sex?? && usr.sex=="F"><#else><#if usr.birthAddress??>${usr.birthAddress}<#else>placeholder</#if></#if>',hasArr: true}
	]
	var arr2 = [
		{type:'isRadio',label: '是否本人', value:'<#if usr.sex?? && usr.sex=="F">Y<#else>N</#if>' , id:'isSelf2'},
		{type:'input',className:'',name:'realName',label: '中文姓名',value:'<#if usr.sex?? && usr.sex=="M"><#else>${usr.realName}</#if>',placeholder:'必填',hasArr:false},
        {type:'input',className:'',name:'englishName',label: '英文姓名',value:'<#if usr.sex?? && usr.sex=="M"><#else>${usr.englishName}</#if>',placeholder:'非必填',hasArr:false},
        {type: 'select',className: 'birth',name: 'birth',placeholder:'必填',label: '阳历生日',value: '<#if usr.sex?? && usr.sex=="M"><#else>${usrBirthDate}</#if>',hasArr: true},
        {type: 'select',className: 'birthTime',name: 'birthTime',placeholder:'必填',label: '出生时间',value: '<#if usr.sex?? && usr.sex=="M">不清楚<#else><#if usr.birthTime??>${usr.birthTime}<#else>不清楚</#if></#if>',hasArr: true},
        {type: 'select',className: 'birthCity',name: 'birthAddress',placeholder:'必填',label: '出生地',value: '<#if usr.sex?? && usr.sex=="M"><#else><#if usr.birthAddress??>${usr.birthAddress}<#else>placeholder</#if></#if>',hasArr: true}
	]
        <#--结婚吉日-->
        <#if order.zxCateId=100004>
        var arr3 = [
            {type: 'select',className: 'expectDate',name: 'expectMarriageDateBegin',placeholder:'非必填',label: '期望结婚时间',value: '',hasArr: true},
            {type: 'select',className: 'expectDate',style:'text-align:center;',name: 'expectMarriageDateEnd', placeholder:'非必填', label: '至',value: '',hasArr: true},
        ]
        $("#form1 .list-header").html('未婚夫信息');
        $("#form2 .list-header").html('未婚妻信息');
        <#--择日生子-->
        <#elseif order.zxCateId=100005>
        var arr3 = [
            {type: 'select',className: 'number',name: 'fetusNum',placeholder:'必填',label: '第几胎',value: '1',hasArr: true},
        ]
        $("#form1 .list-header").html('父亲信息');
        $("#form2 .list-header").html('母亲信息');
        $("#form3 .list-header").html('子女信息');
        </#if>
    <#if order.zxCateId==100001>
    $("#form1 .list-header").html('男朋友信息');
    $("#form2 .list-header").html('女朋友信息');
    $("#formItem").tmpl(arr1).appendTo("#form1 .list-body");
    $("#formItem").tmpl(arr2).appendTo("#form2 .list-body");
    $("#form2").show();
    <#else>
    $("#formItem").tmpl(arr1).appendTo("#form1 .list-body");
    $("#formItem").tmpl(arr2).appendTo("#form2 .list-body");
    $("#formItem").tmpl(arr3).appendTo("#form3 .list-body");
    $("#form2").show();
    $("#form3").show();
    </#if>
    <#--乔迁择日-->
    <#elseif order.zxCateId=100007>
     var arr1 = [
        {type:'isRadio',label: '是否本人', value:'Y' , id:'isSelf1'},
        {type:'input',className:'',name:'realName',label: '中文姓名',value:'${usr.realName}',placeholder:'必填',hasArr:false},
        {type:'input',className:'',name:'englishName',label: '英文姓名',value:'${usr.englishName}',placeholder:'非必填',hasArr:false},
        {type: 'select',className: 'birth',name: 'birth',placeholder:'必填',label: '阳历生日',value: '${usrBirthDate}',hasArr: true},
        {type: 'select',className: 'birthTime',name: 'birthTime',placeholder:'必填',label: '出生时间',value: '<#if usr.birthTime??>${usr.birthTime}<#else>不清楚</#if>',hasArr: true},
        {type: 'select',className: 'birthCity',name: 'birthAddress',placeholder:'必填',label: '出生地',value: '<#if usr.birthAddress??>${usr.birthAddress}<#else>placeholder</#if>',hasArr: true},
        {type: 'select',className: 'sex',name: 'sex',placeholder:'必填',label: '性别',value: '<#if usr.sex??>${usr.sex}<#else>M</#if>',hasArr: true}
    ]
    var arr2 = [
        {type:'isRadio',label: '是否本人', value:'' , id:'isSelf2'},
        {type:'input',className:'',name:'realName',label: '中文姓名',value:'',placeholder:'必填',hasArr:false},
        {type:'input',className:'',name:'englishName',label: '英文姓名',value:'',placeholder:'非必填',hasArr:false},
        {type: 'select',className: 'birth',name: 'birth',placeholder:'必填',label: '阳历生日',value: '',hasArr: true},
        {type: 'select',className: 'birthTime',name: 'birthTime',placeholder:'必填',label: '出生时间',value: '不清楚',hasArr: true},
        {type: 'select',className: 'birthCity',name: 'birthAddress',placeholder:'必填',label: '出生地',value: 'placeholder',hasArr: true},
        {type: 'select',className: 'sex',name: 'sex',placeholder:'必填',label: '性别',value: 'F',hasArr: true}
    ]
    var arr3 = [
            {type: 'input',className: '',name: 'newAddress',placeholder:'必填',label: '新宅地址',value: '',hasArr: false},
            {type: 'select',className: 'birth',name: 'completedTime',placeholder:'必填',label: '新宅落成时间',value: '',hasArr: true},
            {type: 'select',className: 'expectDate',name: 'expectMoveDateBegin',placeholder:'必填',label: '期望乔迁时间',value: '',hasArr: true},
            {type: 'select',className: 'expectDate',name: 'expectMoveDateEnd', style:'text-align:center;', placeholder:'必填',label: '至',value: '',hasArr: true},
        ]
    $("#form1 .list-header").html('新宅信息');
    $("#form2 .list-header").html('主人信息');
    $("#form3 .list-header").html('伴侣信息');
    $("#formItem").tmpl(arr3).appendTo("#form1 .list-body");
    $("#formItem").tmpl(arr1).appendTo("#form2 .list-body");
    $("#formItem").tmpl(arr2).appendTo("#form3 .list-body");
    $("#form2").show();
    $("#form3").show();
    </#if>
</script>
<#-- <h2> 支付成功</h2>
	<a href="#" id="btn">提交并开始对话${order.id} </a><br>
<a href="${host.base}/order/chat_index?chatSessionNo=${order.chatSessionNo}&orderId=${order.id}" > 前往 对话页chatSessionNo:${order.chatSessionNo}  orderId:${order.id} </a><br> -->

<script src="../../static/js/form.js"></script>
</body>
</html>
