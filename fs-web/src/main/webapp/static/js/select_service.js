$(function() {
    $("#selectForm :checkbox[name='checkbox']").on('change', checkBoxChange);
    $("#submit").on("click", submit)
});

function checkBoxChange() {
    var $el = $(this);
    if ($el.is(':checked')) {
        $el.parents('.select').addClass('on');
        $el.parents('.select-list-item').find('.input-box').removeClass('hide');
    } else {
        $el.parent('.select').removeClass('on');
        $el.parents('.select-list-item').find('.input-box').addClass('hide');
        $el.parents('.select-list-item').find(':input').val('')
    }
}
var isClick = false;
function submit() {
    if(isClick){
        return;
    }
    isClick = true;
    var arr = [];
    var flag = true;
    $("#selectForm :checkbox").filter(':checked').parents(".select-list-item").map(function(index, el) {
        var $el = $(el);
        var amt = $el.find(':input[name="amt"]').val().replace(/,/g,'');
        if(!/^\d+(\.\d{1,2})?$/.test(amt*1) ||　amt =='' || amt*1 <= 0){
            mAlert.addAlert('请输入正确的价格');
            flag = false;
            return;
        }
        var elParams = {
            amt: ($el.find(':input[name="amt"]').val()*100),
            fsZxCateId: $el.data("fsxcatecd"),
            fsMasterInfoId: $el.data("fsmasterinfoid"),
            status: "ON"
        };
        if ($el.data("id")) {
            elParams.id = $el.data("id")
        }
        arr.push(elParams)
    })
    if(!flag){
        return;
    }
    $("#selectForm :checkbox[hadSet='Y']").map(function(index, el) {
        if (!$(el).is(":checked")) {
            var $this = $(el).parents(".select-list-item");
            arr.push({
                amt: $this.find(':input[name="amt"]').val(),
                fsZxCateId: $this.data("fsxcatecd"),
                fsMasterInfoId: $this.data("fsmasterinfoid"),
                status: "OFF",
                id: $this.data("id")
            });

        }
    })

    $.ajax({
        type: "POST",
        url: hostBase+ "/usr/master/recruit/service_cate_config_submit",
        data: {
            data: JSON.stringify(arr)
        }, //将对象序列化成JSON字符串
        success: function(data) {
            var data = JSON.parse(data);
            isClick = false;
            if(data.head.code == '0000'){
                mAlert.addAlert('保存成功');
                setTimeout(function(){
                    location.href = hostBase + '/usr/master/account';
                },1000)
            }else{
                 mAlert.addAlert(data.head.msg);
            }

        },
        error: function(res) {}
    }); // $.ajax
}
