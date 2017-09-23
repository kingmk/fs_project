(function ($) {
    "use strict";

    var _defaults = {
        count: 60
    };

    var _SendCode = function ($dom, beforeFnc, fAjax, count) {
        this.interValObj; //timer变量，控制时间
        this.fnc = fAjax;
        this.beforeFnc = beforeFnc;
        this.$dom = $dom, this.count = $.isNumeric(count) && count > 0 ? count : _defaults.count; //间隔函数，1秒执行
        if (this.$dom.length === 0) {
            alert('cant find dom');
            return;
        }
        if (!$.isFunction(this.fnc) || !$.isFunction(this.beforeFnc)) {
            alert('fAjax need function');
            return;
        }
    };

    _SendCode.prototype.bind = function () {
        var $dom = this.$dom, $self = this;
        if ($dom.length === 0 || !$.isFunction(this.fnc)) {
            return;
        }
        $dom.on("click", function () {
            if(! $self.beforeFnc()){
                return;
            }
            if($dom.hasClass('disabled')){
                return;
            }
            var i = $self.count * 1;
            var interValObj = window.setInterval(function () {
                if (i === 0) {
                    window.clearInterval(interValObj); //停止计时器
                    $dom.removeClass('disabled').html("发送验证码");
                } else {
                    $dom.addClass('disabled');
                    $dom.html( i + "秒后获取");
                    i--;
                }
            }, 1000); //启动计时器，1秒执行一次
            $self.fnc();//向后台发送处理数据
        });
    }

    $.extend($.fn, {
        sendCode: function (beforeFn, fAjax, count) {
            new _SendCode(this, beforeFn, fAjax, count).bind();
        }
    });
    $.fn.sendCode.defaults = _defaults;
})(jQuery)
