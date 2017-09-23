 <#import "/common/host.ftl" as host>
<#import "/common/funUtils.ftl" as funUtils>
<html>
<head>
 <meta charset="UTF-8">
	<title>个人资料</title>
	<script src="${host.js}/js/rem.js"></script>
    <script src="${host.js}/js/jquery-1.11.3.min.js"></script>
    <script src="${host.js}/js/common.js?${host.version}"></script>
     <script src="${host.js}/js/jquery.tmpl.min.js"></script>
    <script src="${host.js}/js/mobiscroll.custom-3.0.0-beta2.min.js"></script>
    <script src="${host.js}/js/cityDate.js?${host.version}"></script>
    <link rel="stylesheet" href="${host.css}/css/mobiscroll.custom-3.0.0-beta2.min.css">
    <link rel="stylesheet" href="${host.css}/css/user_info.css?${host.version}">
    <style>
        .error{
            border: 1px solid #f00;
        }
    </style>
  <script>
  var hostBase = "${host.base}";
  $(function(){
        $("#form input").on('input propertychange', function(){
            if($(this).val()){
                $(this).parents(".list-control").removeClass('error');
            }
        })
	  $("#confirmBtn").click(function(){
        var formArr = $("#form").serializeArray();
        var data = {};
        formArr.map(function(item,index){
            if(item.name!='englishName'){
                if(!item.value || item.value == 'placeholder'){
                    $("#form input[name='"+ item.name+"']").parents('.list-control').addClass('error');
                }else{
                    $("#form input[name='"+ item.name+"']").parents('.list-control').removeClass('error');
                }
            }
        })
        if($("#form .error").length>0){
            mAlert.addAlert('您有未填写信息,请填写完整！', 2000);
            return;
        }else{
            formArr.map(function(item,index){
                data[item.name] = item.value;
            })
        }
        data.birthYear = data['birth'].split('-')[0];
        data.birthDate = data['birth'].split('-')[1]+data['birth'].split('-')[2];
        data.birthTimeType = 'min';
	 	$.ajax({
		    type: "POST",
		    url: "${host.base}/usr/common/personal_data_submit",
		    dataType: "json",
			data: data,
            beforeSend: function(){
                loading.addLoading('${host.img}/images/ajax-loader.gif');
            },
		    success: function(data){
                loading.removeLoading();
                if(data.head.code == '0000'){
                    mAlert.addAlert('保存成功');
                    setTimeout(function(){
                        location.href = '${host.base}/usr/common/my';
                    },2000);
                }else{
                    mAlert.addAlert(data.head.msg);
                }

		    },
		    error: function(res){
		    }
	   });
	  });

  });
  </script>
</head>
<body>
<div class="content">
<form id='form'>
<div class="list form">
    <div class="list-body">
    <#noparse>
        <script id='formItem' type="text/x-jquery-tmpl">
            <div class="list-item">
            <div class="list-label">${label}</div>
            <div class="list-control">
                <input type="text" class='${className}' placeholder="${placeholder}" value="${value}" name='${name}'>
            </div>
            {{if hasArr}}
            <div class="list-arrow">
            {{/if}}
            </div>
            </div>
        </script>
    </#noparse>
    </div>
</div>
</form>
</div>

<div class="button-box" id='confirmBtn'>
    <div class="button">
        保存并返回
    </div>
</div>
<script>
console.log('${birthDate}')
var arr = [{
    type: 'input',
    className: '',
    name: 'realName',
    label: '中文姓名',
    value: '${loginUsr.realName}',
    placeholder:'请填写',
    hasArr: false
}, {
    type: 'input',
    className: '',
    name: 'englishName',
    label: '英文姓名',
      placeholder:'请填写',
    value: '${loginUsr.englishName}',
    hasArr: false
}, {
    type: 'select',//[input|select]
    className: 'birth',//[birth,birthTime,birthCity,sex,marriage]
    name: 'birth',//input的name
    label: '阳历生日',
    value: '${birthDate}'? '${birthDate}':'',//默认value
    hasArr: true //是否有右部箭头
}, {
    type: 'select',
    className: 'birthTime',
    name: 'birthTime',
    label: '出生时刻',
    value: '${loginUsr.birthTime}' ? '${loginUsr.birthTime}': '不清楚',
    hasArr: true //是否有右部箭头
}, {
    type: 'select',
    className: 'birthCity',
    name: 'birthAddress',
    label: '出生地',
    value: '${loginUsr.birthAddress}'?'${loginUsr.birthAddress}':'placeholder',
    hasArr: true //是否有右部箭头
}, {
    type: 'select',
    className: 'sex',
    name: 'sex',
    label: '性别',
    value: '${loginUsr.sex}'?'${loginUsr.sex}':'M',
    hasArr: true //是否有右部箭头
}, {
    type: 'select',
    className: 'marriage',
    name: 'marriageStatus',
    label: '婚姻状况',
    value: '${loginUsr.marriageStatus}'?'${loginUsr.marriageStatus}':'single',
    hasArr: true //是否有右部箭头
},{
    type: 'select',
    className: 'familyRank',
    name: 'familyRank',
    label: '家中排行',
    value: '${loginUsr.familyRank}'?'${loginUsr.familyRank}':'1',
    hasArr: true //是否有右部箭头
}];
$("#formItem").tmpl(arr).appendTo(".list-body");
</script>
 <script src="../../static/js/form.js"></script>

<#-- <div class="button-box"><div class="button">保存并返回</div></div>
<h2> 普通用户| 个人资料页</h2>
<div id="container">
	<div class="item">
		<form id="form">
			姓名:      <input type="text" name="realName" value="${loginUsr.realName}"> <br>
			昵称:      <input type="text" name="nickName" value="${loginUsr.nickName}"> <br>
			阳历生日: <input type="text" name="birthDate" value="${loginUsr.birthDate}"><br>
			性别: <input type="text" name="sex" value="${loginUsr.sex}">   <br>
 			婚姻状态: <input type="text" name="marriageStatus" value="${loginUsr.marriageStatus}"> <br>
			家中排行 <input type="text" name="familyRank" value="${loginUsr.familyRank}"> <br>
			<input type='button' value='保存' id = 'btnSave'>
		</form>
	</div>
<div> -->
</body>
</html>
