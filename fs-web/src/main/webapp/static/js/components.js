function Star(ele, opts) {
    this.$element = ele;
    this.defaults = {
        type: 'show', //展示评分[show]，评分[score]
        tip: '5', //分数[type='show']时用到
        percent: '10' //分数制
    };
    this.options = $.extend({}, this.defaults, opts);
}
var starProto = Star.prototype;
starProto._init = function() {
    if (this.options.type == 'show') {
        this._initShow();
    } else if (this.options.type == 'score') {
        this._initScore();
    }
};
starProto._initShow = function() {
    var starBox = $('<div class="star_show"></div>');
    var star = $('<p></p>');
    starBox.append(star);
    var www = this.options.tip * 3.3 / (this.options.percent / 5);
    star.css('width',www+'rem');
    this.$element.append(starBox);
};
starProto._initScore = function() {
    var _this = this;
    var scoreBox = $('<p class="star_score"></p>');
    for (var i = 0; i < 5; i++) {
        var scoreItem = $('<span></span>');
        scoreItem.on("click", function() {
            $(this).addClass("clibg").siblings().removeClass("clibg");
            _this.$element.attr('tip', ($(this).index() + 1) * (_this.options.percent / 5))
        })
        scoreBox.append(scoreItem)
    }
    this.$element.append(scoreBox);

};

(function($) {
    $.fn.star = function(opts) {
        return new Star($(this), opts)._init();
    }
})(jQuery);
