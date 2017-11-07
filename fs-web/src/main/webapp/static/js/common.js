var CommonUtils = function() {
	this.checkNotNull = function(val) {
		return val.trim().length > 0;
	};
	this.checkName = function(val) {
		return val.length > 1 && /^[\u4E00-\u9FA5]+$/.test(val)
	};
	this.checkCertNo = function(val) {
		return /(^[1-9][0-9Xx]{14,17}$)/.test(val)
	};
	this.checkMobile = function(val) {
		return /^1[34578]\d{9}$/.test(val);
	};
	this.numberCurrency  =   function(n) {    
		var  s  =  n.toString();    
		var  ss  =  s.split(".");    
		var  s1,  s2,  rs;    
		s1  =  ss[0];    
		s2  =  ss[1]  ?  ss[1]  :  "";    
		var  sign  =  "";    
		if  (s1[0]  ==  "+"  ||  s1[0]  ==  "-")  {        
			sign  =  s1[0];        
			s1  =  s1.substr(1);    
		};     
		var  len  =  s1.length;    
		if  (len  <=  3)  {        
			rs  =  s1;    
		} 
		else  {        
			var  r  =  len  %  3;        
			rs  =  r  >  0  ?  s1.slice(0,  r)  +  ","  +  s1.slice(r,  len).match(/\d{3}/g).join(",")  :  s1.slice(r,  len).match(/\d{3}/g).join(",");    
		}    
		if  (s2.length  >  0)  {        
			rs  +=  "."  +  s2    
		};    
		if  (sign  !=  "")  {        
			rs  =  sign  +  rs;    
		};    
		return  rs;
	}
	this.numberCurrencyFix = function(n)  {    
		var  s  =  numberCurrency(n);    
		var  ss  =  s.split(".");    
		if  (ss.length  ==  1)  {        
			return  s  +  ".00";    
		} 
		else  if  (ss[1].length  ==  1)  {        
			return  s  +  "0";    
		} 
		else  if  (ss[1].length  ==  0)  {        
			return  s  +  "00";    
		} 
		else  if  (ss[1].length  >  2)  {        
			return  ss[0]  +  "."  +  ss[1].substr(0,  2);    
		}    
		return  s;
	}

	this.hTMLEncode = function(html) {
		var temp = document.createElement("div");
		(temp.textContent != null) ? (temp.textContent = html) : (temp.innerText = html);
		var output = temp.innerHTML;
		temp = null;
		return output;
	}
	this.hTMLDecode = function(text) {
		var temp = document.createElement("div");
		temp.innerHTML = text;
		var output = temp.innerText || temp.textContent;
		temp = null;
		return output;
	}
	this.getUrlParam = function(name){
	    var url = window.location.href;
	    var params = url.substr(url.indexOf("?") + 1);
	    var paramsarr = params.split("&");
	    var paramsobj = {};
	    for (var i = 0; i < paramsarr.length; i++) {
	        var list = paramsarr[i].split("=");
	        paramsobj[list[0]] = list[1];
	    }
	    return paramsobj[name];
	}
	//给定秒数 格式化h小时m分s秒
	this.getTimeFix = function(time){
		if(time <=0){
			return;
		}else{
			var h =  parseInt(time/3600);
			var m = parseInt((time - h*3600)/60);
			var s = (time - h*3600) % 60;
			return h+'小时'+m+"分"+s+"秒";
		}
	}
	// 设置与当前日期间隔，并输出指定格式
	// parm1　　间隔日期
	// parm2　　日期分隔符
	this.setTime = function(p,bz){
		var preDate = new Date( (new Date()).getTime() +p*24*60*60*1000);
        var y = preDate.getFullYear();
        var m = preDate.getMonth()+1;
        var d = preDate.getDate();

        m = m<10?'0'+m:m;
        d = d<10?'0'+d:d;
        return y+bz+m+bz+d;
	}
	// 获取与当前日期间隔，并输出指定格式
	// parm1　　间隔日期
	// parm2　　日期分隔符
	this.getTime = function(p){
		var nowDate = new Date();
		var now = nowDate.getFullYear()+'/'+(nowDate.getMonth()+1)+'/'+nowDate.getDate();
		var d = new Date(now).getTime()-new Date(p).getTime();
		return (d/24/60/60/1000/365).toFixed(1);
	}

	this.adjustHeadImg = function(img, bound, domId) {
		var wreal = img.naturalWidth;
		var hreal = img.naturalHeight;
		var wshow = bound;
		var hshow = bound;
		var style = "";
		if (wreal < hreal) {
			hshow = wshow*hreal/wreal;
			style = style+"margin-top: -"+(hshow/2)+"rem; top: 50%; width: 100%; left:0; ";
		} else {
			wshow = hshow*wreal/hreal;
			style = style+"margin-left: -"+(wshow/2)+"rem; left: 50%; height: 100%; top: 0;";
		}
		$("#"+domId).attr("style", style);
	}

}
var commonUtils = new CommonUtils();
var InitUserFooter = function(opts) {
	this.domFooter = null;
	this.defaults = {
		itemList: [{
			className: 'consultation',
			title: '运势咨询',
			link: "/cate/introduce_nav"
		}, {
			className: 'teacher',
			title: '寻找老师',
			link: "/usr/search/master_nav"
		}, {
			className: 'order',
			title: '订单',
			link: "/usr/common/order_list_nav"
		}, {
			className: 'account',
			title: '我的',
			link: "/usr/common/my"
		}],
		activedIndex: 0,
		bubbleNum: 0
	};
	this.options = $.extend({}, this.defaults, opts);
	this.init = function() {
		var _this = this;
		var footer = $('<div class="footer"></div>');
		this.options.itemList.map(function(item, index) {
			var footerItem = $('<a class="footer-nav" href="' + item.link + '"><div class="icon ' + item.className + '"></div></div>');
			var title = $('<div class="title">' + item.title + '</div>');
			if (index == _this.options.activedIndex) {
				footerItem.addClass('actived')
			}
			footerItem.append(title);
			footer.append(footerItem);
		});
		this.domFooter = footer;
		$('body').append(footer);
	}
	this.updateUnread = function(unreadNum) {
		if (unreadNum > 0) {
			if(unreadNum > 99){
				unreadNum = 'N'
			}
			var domOrder = this.domFooter.find(".order");
			var domBubble = $('<div class="bubble" id="unReadChatMsgNum">' + unreadNum + '条信息</div>');
			domOrder.append(domBubble);
		}

	}
};

var Bgmsk = function(opts) {
	this.defaults = {
		hasClose: true,
		hasHeader: false,
		headerTxt:'',
		title: '我是title信息',
		text: '我是text信息',
		type: 'normal', //body里面的配置【normal|form】
		buttonTxt: '立即开启',
		formContent: null //body里面的form ->$(dom)
	};
	this.options = $.extend({}, this.defaults, opts);
	this.init = function() {
		var _this = this;
		var bgmask = $('<div class="bgmask"></div>');
		var bgmaskWrap = $('<div class="bgmask-wrap"></div>');
		var bgmaskHeader = $('<div class="bgmask-header">'+_this.options.headerTxt+'</div>')
		var bgmaskClose = $('<div class="bgmask-close"></div>');
		var bgmaskBody = $('<div class="bgmask-body"></div>');
		var bgmaskFooter = $('<div class="bgmask-footer"></div>');
		var footerBtn = $('<div class="button">' + this.options.buttonTxt + '</div>');
		this.bgmsk = bgmask;
		bgmaskFooter.append(footerBtn);
		if (this.options.hasClose) {
			bgmaskWrap.append(bgmaskClose);
		}
		if(this.options.hasHeader){
			bgmaskWrap.append(bgmaskHeader);
		}
		bgmaskWrap.append(bgmaskBody);
		if (this.options.type == 'normal') {
			this.initNormal(bgmaskBody);
		}else if (this.options.type == 'form'){
			this.initForm(bgmaskBody);
		}
		bgmaskWrap.append(bgmaskFooter);
		bgmask.append(bgmaskWrap);
		bgmask.remove();
		$('body').append(bgmask);

		bgmaskClose.on('click', function() {
			bgmask.remove()
		});
		bgmask.on("click", function() {
			bgmask.remove()
		});
		bgmaskWrap.on("click", function(e) {
			e.stopPropagation()
		});
		footerBtn.on('click', function(e) {
			if (!!_this.options.buttonFn && typeof _this.options.buttonFn == 'function') {
				_this.options.buttonFn();
				setTimeout(function() {
					bgmask.remove()
				}, 300)
			} else {
				bgmask.remove();
			}
		});
	};
	this.initNormal = function(parent) {
		var title = $('<div class="title">' + this.options.title + '</div>');
		var text = $('<div class="text">' + this.options.text + '</div>');
		parent.append(title).append(text);
	};
	this.initForm = function(parent){
		parent.append(this.options.formContent);
	}
};

var EmptyBox = function(ele,opts){
	this.$element = ele;
	this.defaults = {
		iconImg:'aa',
		title:'我是文本title'
	};
	this.options = $.extend({}, this.defaults, opts);
	this.init = function(){
		var _this = this;
		var empty = $('<div class="empty"></div>');
		var emptyIcon = $('<div class="empty-icon"><img src="'+_this.options.iconImg+'"></div>');
		var emptyTitle = $('<div class="empty-title">'+_this.options.title+'</div>');
		empty.append(emptyIcon).append(emptyTitle);
		$("body").find(".empty").remove();
		_this.$element.append(empty);
	}
}
//查询未读数目
function chatUnreadNum(hostbase) {
	var unReadNum = 0;
	$.ajax({
		type: "POST",
		url: hostbase + "/order/chat_unread_num_query",
		dataType: "json",
		data: {},
		success: function(data) {
			if (data.head.code == "0000") {
				unreadNum = data.body.unReadNum;

				if (unreadNum > 0) {
					var domOrder = $(".footer .order");
					var domBubble = $('<div class="bubble" id="unReadChatMsgNum">' + unreadNum + '条信息</div>');
					domOrder.append(domBubble);
				};
			}
		},
		error: function(res) {}
	});
	return unReadNum;
}

function appendChats(data, domParent) {
	var chatList = data.chatList;
	var loginUsrId = data.loginUsrId;
	if (chatList) {
		for (var i = 0; i < chatList.length; i++) {
			var chat = chatList[i];
			if (domParent.find("div[clientUniqueNo='"+chat.clientUniqueNo+"']")) {
				continue;
			};
			var domChat = $('<div id="'+chat.id+'" clientUniqueNo="'+chat.clientUniqueNo+'"></div>');
			var domSpeak = $('<div class="clearfix"></div>');
			if (loginUsrId == chat.sentUsrId) {
				domSpeak.addClass("speak-right");
			} else {
				domSpeak.addClass("speak-left");
			}
			domSpeak.append('<div class="heard-img"><img src="'+chat.sendtUsrHeadImgUrl+'"/></div>');
			if (chat.msgType=="text") {
				domSpeak.append('<div class="speak-text"><p>'+chat.content+'</p></div>');
			} else {
				var w = chat.width;
				var h = chat.height;
				var sizeReal = adjustImageSize({w:w, h:h});

				domSpeak.append('<div class="speak-text"><img onclick="showImg(\''+chat.content+'\')" src="'+chat.content+'" class="speak-img" data-h="'+chat.height+'" data-w="'+chat.width+'" style="width:'+sizeReal.w+'rem; height: '+sizeReal.h+'"></div>');
			}
			domChat.append(domSpeak);
			domParent.append(domChat);
		};
	}
	var domHide = $('<div id = "temporaryHidediv" style="display:none"><input type="hidden" id="_curMaxId" value = "'+data.curMaxId+'"/><input type="hidden" id="_curMinId" value = "'+data.curMinId+'"/><input type="hidden" id="_curSize" value = "'+data.size+'"/></div>');
	domParent.append(domHide);
}

function adjustImageSize(size) {
	var max_w = 24;
	var wreal = size.w;
	var hreal = size.h;
	if (size.w > max_w) {
		wreal = max_w;
		hreal = wreal * size.h / size.w;
	}
	return {w: wreal, h: hreal}
}

var InitLoading = function(opts){
	this.initLoading = function(imgsrc){
		if( this.loadingWapper) {
			return;
		}
		this.loadingWapper = $('<div class="m-loading"></div>');
		var loadingImg = $('<img src="'+imgsrc+'"/>');
		this.loadingWapper.append(loadingImg);
	}
	this.addLoading = function(imgsrc){
		this.initLoading(imgsrc);
		$("body").append(this.loadingWapper);
	}
	this.removeLoading = function(){
		// this.initLoading();
		this.loadingWapper.remove();
	}
}
var InitAlert = function(){
	this.initAlert = function(msg){
		$(".m-alert").remove();
		this.alertWapper = $('<div class="m-alert"></div>');
		var alertMsg = $('<div class="alert-msg">'+ msg +'</div>');
		this.alertWapper.append(alertMsg);
	}
	this.addAlert = function(msg,time){
		var time = time?time:'2000'
		this.initAlert(msg);
		var _this = this;
		$("body").append(this.alertWapper);
		$(".alert-msg").css("margin-top", ($(".m-alert").height() - $(".alert-msg").height()) / 2);
    $(".alert-msg").css("margin-left", ($(".m-alert").width() -$(".alert-msg").outerWidth()) / 2);
		setTimeout(function(){
			_this.removeAlert();
		},time)
	}
	this.removeAlert = function(){
		this.alertWapper.remove();
	}
}
var loading = new InitLoading();
var mAlert = new InitAlert();
!(function($) {
	$.extend({
		initUserFooter: function(opts) {
			return new InitUserFooter(opts).init();
		},
		bgmask: function(opts) {
			return new Bgmsk(opts).init();
		}
	})
	$.fn.emptyBox = function(opts) {
        return new EmptyBox($(this), opts).init();
  }
})(jQuery);