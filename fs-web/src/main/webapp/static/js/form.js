$('.radio-box-child').on('click', function () {
	var div2 = $(this);
	var div1 = $(this).parents('.radio-box-parent');
	var div1Class = div1.hasClass('close1') ? "open1" : "close1";
	var div2Class = div2.hasClass("close2") ? "open2" : "close2";
	div1.prop('class', div1Class).addClass('radio-box-parent');
	div2.prop('class', div2Class).addClass('radio-box-child');
})
var fontSize = $("html").css("font-size");
var hArr = ["不清楚","00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"];
var mArr = ["不清楚","00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60"];
$(function(){
mobiscroll.scroller('.birthTime', {
	theme: 'ios',
	display: 'bottom',
	lang: 'zh',
	palaceholder:'必填',
  width: 300 / 20 * parseFloat(fontSize),
  wheels: [
    [{
        circular: false,
        data: hArr,
        label: '时'
    }, {
        circular: false,
        data: mArr,
        label: '分'
    }]
  ],
  validate: function (event, inst) {
      var values = event.values;
			if(values[0]=='不清楚'){
				return {
					disabled :[
						[], mArr.slice(1,mArr.length)
					]
				}
			}else{
				return {
					disabled :[
						[],mArr.slice(0,1)
					]
				}
			}
  },
  showLabel: true,
  minWidth: 130,
  parseValue: function (val) {
  	arr = val.toString().split(":")
  	arr[0] = $.trim(arr[0]);
  	arr[1] = $.trim(arr[1]);
  	return [arr[0], arr[1]]
  },
  formatValue: function (data) {
  	if(data[0]=='不清楚'){
  		return '不清楚';
  	}else{
  		if(!data[1]){
  			return data[0] + ':不清楚';
  		}
  		if(!data[0]){
  			return '不清楚:' + data[1];
  		}
    	return  data[0] + ':' + data[1];
  	}
  },
	rows: 5,
	height: 68 / 20 * parseFloat(fontSize),
});
var now = new Date();
var maxDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());
mobiscroll.date('.birth', {
	theme: 'ios',
	display: 'bottom',
	lang: 'zh',
	height: 68 / 20 * parseFloat(fontSize),
	dateFormat: 'yy-mm-dd',
	dateWheels:'yy mm dd',
	showLabel: true,
	max: maxDate
});

var minExpectDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());
var maxExpectDate = new Date(now.getFullYear()+10, now.getMonth(), now.getDate());
mobiscroll.date('.expectDate', {
	theme: 'ios',
	display: 'bottom',
	lang: 'zh',
	height: 68 / 20 * parseFloat(fontSize),
	dateFormat: 'yy-mm-dd',
	dateWheels:'yy mm dd',
	showLabel: true,
	min: minExpectDate,
	max: maxExpectDate
});
mobiscroll.select('.birthCity', {
	theme: 'ios',
	display: 'bottom',
	lang: 'zh',
	group: {header: false,groupWheel: true,},
	headerText: '选择现居住地',
	width: [60, 235],
	data: cityDate,
	height: 68 / 20 * parseFloat(fontSize),
	rows: 3,
});
mobiscroll.select('.sex', {
	theme: 'ios',
	display: 'bottom',
	lang: 'zh',
	data: [{
		text: '男',
		value: 'M',
		valueText: '男'
	},{
		text: '女',
		value: 'F',
		valueText:'女'
	},{
		text: '其它',
		value: 'O',
		valueText:'其它'
	}],
	height: 68 / 20 * parseFloat(fontSize),
	rows: 2
});
mobiscroll.select('.marriage', {
	theme: 'ios',
	display: 'bottom',
	lang: 'zh',
	data: [{
		text: '单身',
		value: 'single',
		valueText: '单身'
	},{
		text: '已婚',
		value: 'married',
		valueText:'已婚'
	},{
		text: '离异',
		value: 'divorce',
		valueText:'离异'
	},{
		text: '丧偶',
		value: 'widowed',
		valueText:'丧偶'
	},{
		text: '再婚',
		value: 'remarriage',
		valueText:'再婚'
	}],
	height: 68 / 20 * parseFloat(fontSize),
	rows: 2
});
mobiscroll.select('.familyRank', {
	theme: 'ios',
	display: 'bottom',
	lang: 'zh',
	data: [{
		text: '1',
		value: '1',
		valueText: '1'
	},{
		text: '2',
		value: '2',
		valueText:'2'
	},{
		text: '3',
		value: '3',
		valueText:'3'
	},{
		text: '4',
		value: '4',
		valueText: '4'
	},{
		text: '5',
		value: '5',
		valueText:'5'
	},{
		text: '6',
		value: '6',
		valueText:'6'
	},{
		text: '7',
		value: '7',
		valueText: '7'
	},{
		text: '8',
		value: '8',
		valueText:'8'
	},{
		text: '9',
		value: '9',
		valueText:'9'
	},{
		text: '10',
		value: '10',
		valueText:'10'
	}],
	height: 68 / 20 * parseFloat(fontSize),
	rows: 2
});

mobiscroll.select('.number', {
	theme: 'ios',
	display: 'bottom',
	lang: 'zh',
	data:  [{
		text: '1',
		value: '1',
		valueText: '1'
	},{
		text: '2',
		value: '2',
		valueText:'2'
	},{
		text: '3',
		value: '3',
		valueText:'3'
	},{
		text: '4',
		value: '4',
		valueText: '4'
	},{
		text: '5',
		value: '5',
		valueText:'5'
	},{
		text: '6',
		value: '6',
		valueText:'6'
	},{
		text: '7',
		value: '7',
		valueText: '7'
	},{
		text: '8',
		value: '8',
		valueText:'8'
	},{
		text: '9',
		value: '9',
		valueText:'9'
	},{
		text: '10',
		value: '10',
		valueText:'10'
	}],
	height: 68 / 20 * parseFloat(fontSize),
	rows: 2
});

})