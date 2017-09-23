package com.lmy.common.component;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
public class IDCardUtil {

    final static Map<Integer, String> zoneNum = new HashMap<Integer, String>();
    final static Map<String, String> cityLevel = new HashMap<String, String>();
    final static Map<String, String> cityNameLevel = new HashMap<String, String>();
    
    static {
        zoneNum.put(11, "北京");
        zoneNum.put(12, "天津");
        zoneNum.put(13, "河北");
        zoneNum.put(14, "山西");
        zoneNum.put(15, "内蒙古");
        zoneNum.put(21, "辽宁");
        zoneNum.put(22, "吉林");
        zoneNum.put(23, "黑龙江");
        zoneNum.put(31, "上海");
        zoneNum.put(32, "江苏");
        zoneNum.put(33, "浙江");
        zoneNum.put(34, "安徽");
        zoneNum.put(35, "福建");
        zoneNum.put(36, "江西");
        zoneNum.put(37, "山东");
        zoneNum.put(41, "河南");
        zoneNum.put(42, "湖北");
        zoneNum.put(43, "湖南");
        zoneNum.put(44, "广东");
        zoneNum.put(45, "广西");
        zoneNum.put(46, "海南");
        zoneNum.put(50, "重庆");
        zoneNum.put(51, "四川");
        zoneNum.put(52, "贵州");
        zoneNum.put(53, "云南");
        zoneNum.put(54, "西藏");
        zoneNum.put(61, "陕西");
        zoneNum.put(62, "甘肃");
        zoneNum.put(63, "青海");
        zoneNum.put(64, "新疆");
        zoneNum.put(71, "台湾");
        zoneNum.put(81, "香港");
        zoneNum.put(82, "澳门");
        zoneNum.put(91, "外国");
        // ------一线城市-------------------------------begin //
        cityLevel.put("1101", "01,北京"); //北京 一线城市
        cityLevel.put("1102", "01,北京"); //北京 一线城市
        cityLevel.put("3101", "01,上海"); //上海 一线城市
        cityLevel.put("3102", "01,上海"); //上海 一线城市
        cityLevel.put("4401", "01,广州"); //广州 一线城市
        cityLevel.put("4403", "01,深圳"); //深圳 一线城市
        cityLevel.put("1201", "01,天津"); //天津 一线城市
        cityLevel.put("1202", "01,天津"); //天津 一线城市
        // ------一线城市---------------------------------end //
        // ------二线城市-------------------------------begin //
        cityLevel.put("3301", "02,杭州"); //杭州   二线城市
        cityLevel.put("3201", "02,南京"); //南京   二线城市
        cityLevel.put("3701", "02,济南"); //济南   二线城市
        cityLevel.put("5001", "02,重庆"); //重庆   二线城市
        cityLevel.put("5002", "02,重庆"); //重庆   二线城市
        cityLevel.put("5003", "02,重庆"); //重庆   二线城市
        cityLevel.put("3702", "02,青岛"); //青岛   二线城市
        cityLevel.put("2102", "02,大连"); //大连 	二线城市
        cityLevel.put("3302", "02,宁波"); //宁波 	二线城市
        cityLevel.put("3502", "02,厦门"); //厦门 	二线城市
        cityLevel.put("5101", "02,成都"); //成都 	二线城市
        cityLevel.put("4201", "02,武汉"); //武汉 	二线城市
        cityLevel.put("2301", "02,哈尔滨"); //哈尔滨 二线城市
        cityLevel.put("2101", "02,沈阳"); //沈阳 	二线城市
        cityLevel.put("6101", "02,西安"); //西安 	二线城市
        cityLevel.put("2201", "02,长春"); //长春 	二线城市
        cityLevel.put("4301", "02,长沙"); //长沙 	二线城市
        cityLevel.put("3501", "02,福州"); //福州 	二线城市
        cityLevel.put("4101", "02,郑州"); //郑州 	二线城市
        cityLevel.put("1301", "02,石家庄"); //石家庄 二线城市
        cityLevel.put("3205", "02,苏州"); //苏州 	二线城市
        cityLevel.put("4406", "02,佛山"); //佛山 	二线城市
        cityLevel.put("4419", "02,东莞"); //东莞 	二线城市
        cityLevel.put("3202", "02,无锡"); //无锡 	二线城市
        cityLevel.put("3706", "02,烟台"); //烟台 	二线城市
        cityLevel.put("1401", "02,太原"); //太原 	二线城市
        cityLevel.put("3401", "02,合肥"); //合肥 	二线城市
        cityLevel.put("3601", "02,南昌"); //南昌 	二线城市
        cityLevel.put("4501", "02,南宁"); //南宁 	二线城市
        cityLevel.put("5301", "02,昆明"); //昆明 	二线城市
        cityLevel.put("3303", "02,温州"); //温州 	二线城市
        cityLevel.put("3703", "02,淄博"); //淄博 	二线城市
        cityLevel.put("1302", "02,唐山"); //唐山 	二线城市
        // ------二线城市--------------------------------结束 //
        // ------三线城市------------------------------begin //
        cityLevel.put("6501", "03,乌鲁木齐"); //乌鲁木齐 		三线城市
        cityLevel.put("5201", "03,贵阳"); //贵阳 				三线城市
        cityLevel.put("4601", "03,海口"); //海口 				三线城市
        cityLevel.put("6201", "03,兰州"); //兰州 				三线城市
        cityLevel.put("6401", "03,银川"); //银川 				三线城市
        cityLevel.put("6301", "03,西宁"); //西宁 				三线城市
        cityLevel.put("1501", "03,呼和浩特"); // 呼和浩特		三线城市
        cityLevel.put("3505", "03,泉州"); //泉州					三线城市
        cityLevel.put("1502", "03,包头"); //包头 				三线城市
        cityLevel.put("3206", "03,南通"); //南通 				三线城市
        cityLevel.put("2306", "03,大庆"); //大庆 				三线城市
        cityLevel.put("3203", "03,徐州"); //徐州 				三线城市
        cityLevel.put("3707", "03,潍坊"); //潍坊 				三线城市
        cityLevel.put("3204", "03,常州"); //常州 				三线城市
        cityLevel.put("3306", "03,绍兴"); //绍兴 				三线城市
        cityLevel.put("3708", "03,济宁"); //济宁 				三线城市
        cityLevel.put("3209", "03,盐城"); //盐城 				三线城市
        cityLevel.put("1304", "03,邯郸"); //邯郸 				三线城市
        cityLevel.put("3713", "03,临沂"); //临沂 				三线城市
        cityLevel.put("4103", "03,洛阳"); //洛阳 				三线城市
        cityLevel.put("3705", "03,东营"); //东营 				三线城市
        cityLevel.put("3210", "03,扬州"); //扬州 				三线城市
        cityLevel.put("3310", "03,台州"); //台州 				三线城市
        cityLevel.put("3304", "03,嘉兴"); //嘉兴 				三线城市
        cityLevel.put("1309", "03,沧州"); //沧州 				三线城市
        cityLevel.put("6127", "03,榆林"); //榆林 				三线城市
        cityLevel.put("3212", "03,泰州"); //泰州 				三线城市
        cityLevel.put("3211", "03,镇江"); //镇江 				三线城市
        cityLevel.put("3205", "03,昆山"); //昆山 				三线城市
        cityLevel.put("3202", "03,江阴"); //江阴 				三线城市
        cityLevel.put("3205", "03,张家港"); //张家港 			三线城市
        cityLevel.put("3307", "03,义乌"); //义乌 				三线城市
        cityLevel.put("3307", "03,金华"); //金华 				三线城市 
        cityLevel.put("1306", "03,保定"); //保定 				三线城市
        cityLevel.put("2202", "03,吉林"); //吉林 				三线城市
        cityLevel.put("2103", "03,鞍山"); //鞍山         三线城市
        cityLevel.put("3709", "03,泰安"); //泰安 				三线城市
        cityLevel.put("4205", "03,宜昌"); //宜昌 				三线城市
        cityLevel.put("4420", "03,中山"); //中山 				三线城市
        cityLevel.put("4413", "03,惠州"); //惠州 				三线城市
        cityLevel.put("4113", "03,南阳"); //南阳 				三线城市
        cityLevel.put("3710", "03,威海"); //威海 				三线城市
        cityLevel.put("3714", "03,德州"); //德州 				三线城市
        cityLevel.put("4306", "03,岳阳"); //岳阳 				三线城市
        cityLevel.put("3715", "03,聊城"); //聊城 				三线城市
        cityLevel.put("4307", "03,常德"); //常德 				三线城市
        cityLevel.put("3506", "03,漳州"); //漳州 				三线城市
        cityLevel.put("3723", "03,滨州"); //滨州 				三线城市
        cityLevel.put("4409", "03,茂名"); //茂名 				三线城市
        cityLevel.put("3208", "03,淮安"); //淮安 				三线城市
        cityLevel.put("4407", "03,江门"); //江门 				三线城市
        cityLevel.put("3402", "03,芜湖"); //芜湖 				三线城市
        cityLevel.put("4408", "03,湛江"); //湛江 				三线城市
        cityLevel.put("1310", "03,廊坊"); //廊坊 				三线城市
        cityLevel.put("3729", "03,菏泽"); //菏泽 				三线城市
        cityLevel.put("4502", "03,柳州"); //柳州 				三线城市
        cityLevel.put("6103", "03,宝鸡"); //宝鸡 				三线城市
        cityLevel.put("4404", "03,珠海"); //珠海 				三线城市
        cityLevel.put("5107", "03,绵阳"); //绵阳 				三线城市
        // ------三线城市-------------------------------end//
        // ------四线城市-------------------------------begin//
        cityLevel.put("4302", "04,株洲"); //株洲 			四线城市
        cityLevel.put("3704", "04,枣庄"); //枣庄 			四线城市
        cityLevel.put("4110", "04,许昌"); //许昌 			四线城市
        cityLevel.put("1523", "04,通辽"); //通辽 			四线城市
        cityLevel.put("3305", "04,湖州"); //湖州 			四线城市
        cityLevel.put("4107", "04,新乡"); //新乡 			四线城市
        cityLevel.put("6104", "04,咸阳"); //咸阳 			四线城市
        cityLevel.put("2207", "04,松原"); //松原 			四线城市
        cityLevel.put("3207", "04,连云港"); //连云港     四线城市
        cityLevel.put("4105", "04,安阳"); //安阳 			四线城市
        cityLevel.put("4127", "04,周口"); //周口 			四线城市
        cityLevel.put("4108", "04,焦作"); //焦作 			四线城市
        cityLevel.put("1504", "04,赤峰"); //赤峰 			四线城市
        cityLevel.put("1305", "04,邢台"); //邢台 			四线城市
        cityLevel.put("4310", "04,郴州"); //郴州 			四线城市
        cityLevel.put("3213", "04,宿迁"); //宿迁 			四线城市
        cityLevel.put("3607", "04,赣州"); //赣州 			四线城市
        cityLevel.put("4104", "04,平顶山"); //平顶山 		四线城市
        cityLevel.put("4503", "04,桂林"); //桂林 			四线城市
        cityLevel.put("4412", "04,肇庆"); //肇庆 			四线城市
        cityLevel.put("5303", "04,曲靖"); //曲靖 			四线城市
        cityLevel.put("3604", "04,九江"); //九江 			四线城市
        cityLevel.put("4114", "04,商丘"); //商丘 			四线城市
        cityLevel.put("4405", "04,汕头"); //汕头 			四线城市
        cityLevel.put("4115", "04,信阳"); //信阳 			四线城市
        cityLevel.put("4128", "04,驻马店"); //驻马店 		四线城市
        cityLevel.put("2108", "04,营口"); //营口 			四线城市
        cityLevel.put("4452", "04,揭阳"); //揭阳 			四线城市
        cityLevel.put("3508", "04,龙岩"); //龙岩 			四线城市
        cityLevel.put("3408", "04,安庆"); //安庆 			四线城市
        cityLevel.put("3711", "04,日照"); //日照 			四线城市
        cityLevel.put("5203", "04,遵义"); //遵义 			四线城市
        cityLevel.put("3504", "04,三明"); //三明 			四线城市
        cityLevel.put("1521", "04,呼伦贝尔"); //呼伦贝尔   四线城市
        cityLevel.put("1404", "04,长治"); //长治 			四线城市
        cityLevel.put("4303", "04,湘潭"); //湘潭 			四线城市
        cityLevel.put("5106", "04,德阳"); //德阳 			四线城市
        cityLevel.put("5113", "04,南充"); //南充 			四线城市
        cityLevel.put("5111", "04,乐山"); //乐山 			四线城市
        cityLevel.put("2111", "04,盘锦"); //盘锦 			四线城市
        cityLevel.put("6106", "04,延安"); //延安 			四线城市
        cityLevel.put("3623", "04,上饶"); //上饶 			四线城市
        cityLevel.put("2107", "04,锦州"); //锦州 			四线城市
        cityLevel.put("3622", "04,宜春"); //宜春 			四线城市
        cityLevel.put("5115", "04,宜宾"); //宜宾 			四线城市
        cityLevel.put("1307", "04,张家口"); //张家口 		四线城市
        cityLevel.put("3405", "04,马鞍山"); //马鞍山 		四线城市
        cityLevel.put("2104", "04,抚顺"); //抚顺 			四线城市
        cityLevel.put("1426", "04,临汾"); //临汾				四线城市
        cityLevel.put("6105", "04,渭南"); //渭南 			四线城市
        cityLevel.put("4102", "04,开封"); //开封 			四线城市
        cityLevel.put("3503", "04,莆田"); //莆田 			四线城市
        cityLevel.put("4210", "04,荆州"); //荆州 			四线城市
        cityLevel.put("4211", "04,黄冈"); //黄冈 			四线城市
        cityLevel.put("2203", "04,四平"); //四平 			四线城市
        cityLevel.put("1308", "04,承德"); //承德 			四线城市
        cityLevel.put("2302", "04,齐齐哈尔"); //齐齐哈尔 	四线城市
        cityLevel.put("4112", "04,三门峡"); //三门峡 		四线城市
        cityLevel.put("1303", "04,秦皇岛"); //秦皇岛 		四线城市
        cityLevel.put("2105", "04,本溪"); //本溪 			四线城市
        cityLevel.put("4509", "04,玉林"); //玉林 			四线城市
        cityLevel.put("4209", "04,孝感"); //孝感 			四线城市
        cityLevel.put("2310", "04,牡丹江"); //牡丹江 		四线城市
        cityLevel.put("4208", "04,荆门"); //荆门 			四线城市
        cityLevel.put("3522", "04,宁德"); //宁德 			四线城市
        cityLevel.put("1427", "04,运城"); //运城 			四线城市
        cityLevel.put("2323", "04,绥化"); //绥化 			四线城市
        cityLevel.put("4311", "04,永州"); //永州 			四线城市
        cityLevel.put("4312", "04,怀化"); //怀化 			四线城市
        cityLevel.put("4202", "04,黄石"); //黄石 			四线城市
        cityLevel.put("5105", "04,泸州"); //泸州 			四线城市
        cityLevel.put("4418", "04,清远"); //清远 			四线城市
                                
        cityLevel.put("4305", "04,邵阳"); //邵阳 			四线城市
        cityLevel.put("1311", "04,衡水"); //衡水 			四线城市
        cityLevel.put("4309", "04,益阳"); //益阳 			四线城市
        cityLevel.put("2106", "04,丹东"); //丹东 			四线城市
        cityLevel.put("2112", "04,铁岭"); //铁岭 			四线城市
        cityLevel.put("1405", "04,晋城"); //晋城 			四线城市
        cityLevel.put("1406", "04,晋城"); //晋城 			四线城市
        cityLevel.put("3624", "04,吉安"); //吉安 			四线城市
        cityLevel.put("4325", "04,娄底"); //娄底 			四线城市
        cityLevel.put("5304", "04,玉溪"); //玉溪 			四线城市
        cityLevel.put("2110", "04,辽阳"); //辽阳 			四线城市
        cityLevel.put("3507", "04,南平"); //南平 			四线城市
        cityLevel.put("4109", "04,濮阳"); //濮阳 			四线城市
        cityLevel.put("1424", "04,晋中"); //晋中 			四线城市
        cityLevel.put("5104", "04,攀枝花"); //攀枝花     四线城市
        cityLevel.put("3308", "04,衢州"); //衢州 			四线城市
        cityLevel.put("5110", "04,内江"); //内江 			四线城市
        cityLevel.put("3411", "04,滁州"); //滁州 			四线城市
        cityLevel.put("3412", "04,阜阳"); //阜阳 			四线城市
        cityLevel.put("4203", "04,十堰"); //十堰 			四线城市
        cityLevel.put("1402", "04,大同"); //大同 			四线城市
        cityLevel.put("2113", "04,朝阳"); //朝阳 			四线城市
        cityLevel.put("3424", "04,六安"); //六安 			四线城市
        cityLevel.put("3413", "04,宿州"); //宿州 			四线城市
        cityLevel.put("2205", "04,通化"); //通化 			四线城市
        cityLevel.put("3403", "04,蚌埠"); //蚌埠 			四线城市
        cityLevel.put("4402", "04,韶关"); //韶关 			四线城市
        cityLevel.put("3325", "04,丽水"); //丽水 			四线城市
        cityLevel.put("5103", "04,自贡"); //自贡 			四线城市
        cityLevel.put("4417", "04,阳江"); //阳江 			四线城市
        cityLevel.put("5224", "04,毕节"); //毕节 			四线城市
     // ------四线城市-------------------------------end//
        
        cityNameLevel.put("北京",              "01");
        cityNameLevel.put("上海",              "01");
        cityNameLevel.put("广州",              "01");
        cityNameLevel.put("深圳",              "01");
        cityNameLevel.put("天津",              "01");
        cityNameLevel.put("天津",              "01");
                                                    
        cityNameLevel.put("杭州",              "02");
        cityNameLevel.put("南京",              "02");
        cityNameLevel.put("济南",              "02");
        cityNameLevel.put("重庆",              "02");
        cityNameLevel.put("重庆",              "02");
        cityNameLevel.put("重庆",              "02");
        cityNameLevel.put("青岛",              "02");
        cityNameLevel.put("大连",              "02");
        cityNameLevel.put("宁波",              "02");
        cityNameLevel.put("厦门",              "02");
        cityNameLevel.put("成都",              "02");
        cityNameLevel.put("武汉",              "02");
        cityNameLevel.put("哈尔滨",            "02");
        cityNameLevel.put("沈阳",              "02");
        cityNameLevel.put("西安",              "02");
        cityNameLevel.put("长春",              "02");
        cityNameLevel.put("长沙",              "02");
        cityNameLevel.put("福州",              "02");
        cityNameLevel.put("郑州",              "02");
        cityNameLevel.put("石家庄",            "02");
        cityNameLevel.put("苏州",              "02");
        cityNameLevel.put("佛山",              "02");
        cityNameLevel.put("东莞",              "02");
        cityNameLevel.put("无锡",              "02");
        cityNameLevel.put("烟台",              "02");
        cityNameLevel.put("太原",              "02");
        cityNameLevel.put("合肥",              "02");
        cityNameLevel.put("南昌",              "02");
        cityNameLevel.put("南宁",              "02");
        cityNameLevel.put("昆明",              "02");
        cityNameLevel.put("温州",              "02");
        cityNameLevel.put("淄博",              "02");
        cityNameLevel.put("唐山",              "02");
                                                    
        cityNameLevel.put("乌鲁木齐",          "03");
        cityNameLevel.put("贵阳",              "03");
        cityNameLevel.put("海口",              "03");
        cityNameLevel.put("兰州",              "03");
        cityNameLevel.put("银川",              "03");
        cityNameLevel.put("西宁",              "03");
        cityNameLevel.put("呼和浩特",          "03");
        cityNameLevel.put("泉州",              "03");
        cityNameLevel.put("包头",              "03");
        cityNameLevel.put("南通",              "03");
        cityNameLevel.put("大庆",              "03");
        cityNameLevel.put("徐州",              "03");
        cityNameLevel.put("潍坊",              "03");
        cityNameLevel.put("常州",              "03");
        cityNameLevel.put("绍兴",              "03");
        cityNameLevel.put("济宁",              "03");
        cityNameLevel.put("盐城",              "03");
        cityNameLevel.put("邯郸",              "03");
        cityNameLevel.put("临沂",              "03");
        cityNameLevel.put("洛阳",              "03");
        cityNameLevel.put("东营",              "03");
        cityNameLevel.put("扬州",              "03");
        cityNameLevel.put("台州",              "03");
        cityNameLevel.put("嘉兴",              "03");
        cityNameLevel.put("沧州",              "03");
        cityNameLevel.put("榆林",              "03");
        cityNameLevel.put("泰州",              "03");
        cityNameLevel.put("镇江",              "03");
        cityNameLevel.put("昆山",              "03");
        cityNameLevel.put("江阴",              "03");
        cityNameLevel.put("张家港",            "03");
        cityNameLevel.put("义乌",              "03");
        cityNameLevel.put("金华",              "03");
        cityNameLevel.put("保定",              "03");
        cityNameLevel.put("吉林",              "03");
        cityNameLevel.put("鞍山",              "03");
        cityNameLevel.put("泰安",              "03");
        cityNameLevel.put("宜昌",              "03");
        cityNameLevel.put("中山",              "03");
        cityNameLevel.put("惠州",              "03");
        cityNameLevel.put("南阳",              "03");
        cityNameLevel.put("威海",              "03");
        cityNameLevel.put("德州",              "03");
        cityNameLevel.put("岳阳",              "03");
        cityNameLevel.put("聊城",              "03");
        cityNameLevel.put("常德",              "03");
        cityNameLevel.put("漳州",              "03");
        cityNameLevel.put("滨州",              "03");
        cityNameLevel.put("茂名",              "03");
        cityNameLevel.put("淮安",              "03");
        cityNameLevel.put("江门",              "03");
        cityNameLevel.put("芜湖",              "03");
        cityNameLevel.put("湛江",              "03");
        cityNameLevel.put("廊坊",              "03");
        cityNameLevel.put("菏泽",              "03");
        cityNameLevel.put("柳州",              "03");
        cityNameLevel.put("宝鸡",              "03");
        cityNameLevel.put("珠海",              "03");
        cityNameLevel.put("绵阳",              "03");
                                                    
        cityNameLevel.put("株洲",              "04");
        cityNameLevel.put("枣庄",              "04");
        cityNameLevel.put("许昌",              "04");
        cityNameLevel.put("通辽",              "04");
        cityNameLevel.put("湖州",              "04");
        cityNameLevel.put("新乡",              "04");
        cityNameLevel.put("咸阳",              "04");
        cityNameLevel.put("松原",              "04");
        cityNameLevel.put("连云港",            "04");
        cityNameLevel.put("安阳",              "04");
        cityNameLevel.put("周口",              "04");
        cityNameLevel.put("焦作",              "04");
        cityNameLevel.put("赤峰",              "04");
        cityNameLevel.put("邢台",              "04");
        cityNameLevel.put("郴州",              "04");
        cityNameLevel.put("宿迁",              "04");
        cityNameLevel.put("赣州",              "04");
        cityNameLevel.put("平顶山",            "04");
        cityNameLevel.put("桂林",              "04");
        cityNameLevel.put("肇庆",              "04");
        cityNameLevel.put("曲靖",              "04");
        cityNameLevel.put("九江",              "04");
        cityNameLevel.put("商丘",              "04");
        cityNameLevel.put("汕头",              "04");
        cityNameLevel.put("信阳",              "04");
        cityNameLevel.put("驻马店",            "04");
        cityNameLevel.put("营口",              "04");
        cityNameLevel.put("揭阳",              "04");
        cityNameLevel.put("龙岩",              "04");
        cityNameLevel.put("安庆",              "04");
        cityNameLevel.put("日照",              "04");
        cityNameLevel.put("遵义",              "04");
        cityNameLevel.put("三明",              "04");
        cityNameLevel.put("呼伦贝尔",          "04");
        cityNameLevel.put("长治",              "04");
        cityNameLevel.put("湘潭",              "04");
        cityNameLevel.put("德阳",              "04");
        cityNameLevel.put("南充",              "04");
        cityNameLevel.put("乐山",              "04");
        cityNameLevel.put("盘锦",              "04");
        cityNameLevel.put("延安",              "04");
        cityNameLevel.put("上饶",              "04");
        cityNameLevel.put("锦州",              "04");
        cityNameLevel.put("宜春",              "04");
        cityNameLevel.put("宜宾",              "04");
        cityNameLevel.put("张家口",            "04");
        cityNameLevel.put("马鞍山",            "04");
        cityNameLevel.put("抚顺",              "04");
        cityNameLevel.put("临汾",              "04");
        cityNameLevel.put("渭南",              "04");
        cityNameLevel.put("开封",              "04");
        cityNameLevel.put("莆田",              "04");
        cityNameLevel.put("荆州",              "04");
        cityNameLevel.put("黄冈",              "04");
        cityNameLevel.put("四平",              "04");
        cityNameLevel.put("承德",              "04");
        cityNameLevel.put("齐齐哈尔",          "04");
        cityNameLevel.put("三门峡",            "04");
        cityNameLevel.put("秦皇岛",            "04");
        cityNameLevel.put("本溪",              "04");
        cityNameLevel.put("玉林",              "04");
        cityNameLevel.put("孝感",              "04");
        cityNameLevel.put("牡丹江",            "04");
        cityNameLevel.put("荆门",              "04");
        cityNameLevel.put("宁德",              "04");
        cityNameLevel.put("运城",              "04");
        cityNameLevel.put("绥化",              "04");
        cityNameLevel.put("永州",              "04");
        cityNameLevel.put("怀化",              "04");
        cityNameLevel.put("黄石",              "04");
        cityNameLevel.put("泸州",              "04");
        cityNameLevel.put("清远",              "04");
        cityNameLevel.put("邵阳",              "04");
        cityNameLevel.put("衡水",              "04");
        cityNameLevel.put("益阳",              "04");
        cityNameLevel.put("丹东",              "04");
        cityNameLevel.put("铁岭",              "04");
        cityNameLevel.put("晋城",              "04");
        cityNameLevel.put("晋城",              "04");
        cityNameLevel.put("吉安",              "04");
        cityNameLevel.put("娄底",              "04");
        cityNameLevel.put("玉溪",              "04");
        cityNameLevel.put("辽阳",              "04");
        cityNameLevel.put("南平",              "04");
        cityNameLevel.put("濮阳",              "04");
        cityNameLevel.put("晋中",              "04");
        cityNameLevel.put("攀枝花",            "04");
        cityNameLevel.put("衢州",              "04");
        cityNameLevel.put("内江",              "04");
        cityNameLevel.put("滁州",              "04");
        cityNameLevel.put("阜阳",              "04");
        cityNameLevel.put("十堰",              "04");
        cityNameLevel.put("大同",              "04");
        cityNameLevel.put("朝阳",              "04");
        cityNameLevel.put("六安",              "04");
        cityNameLevel.put("宿州",              "04");
        cityNameLevel.put("通化",              "04");
        cityNameLevel.put("蚌埠",              "04");
        cityNameLevel.put("韶关",              "04");
        cityNameLevel.put("丽水",              "04");
        cityNameLevel.put("自贡",              "04");
        cityNameLevel.put("阳江",              "04");
        cityNameLevel.put("毕节",              "04");
    }
    final static int[] PARITYBIT = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
    final static int[] POWER_LIST = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 
        5, 8, 4, 2};
     
    /**
     * 身份证验证
     *@param s  号码内容
     *@return 是否有效 null和"" 都是false 
     */
    public static boolean isIDCard(String certNo){
        if(certNo == null || (certNo.length() != 15 && certNo.length() != 18))
            return false;
        final char[] cs = certNo.toUpperCase().toCharArray();
        //校验位数
        int power = 0;
        for(int i=0; i<cs.length; i++){
            if(i==cs.length-1 && cs[i] == 'X')
                break;//最后一位可以 是X或x
            if(cs[i]<'0' || cs[i]>'9')
                return false;
            if(i < cs.length -1){
                power += (cs[i] - '0') * POWER_LIST[i];
            }
        }
         
        //校验区位码
        if(!zoneNum.containsKey(Integer.valueOf(certNo.substring(0,2)))){
            return false;
        }
         
        //校验年份
        String year = certNo.length() == 15 ? getIdcardCalendar() + certNo.substring(6,8) :certNo.substring(6, 10);
         
        final int iyear = Integer.parseInt(year);
        if(iyear < 1900 || iyear > Calendar.getInstance().get(Calendar.YEAR))
            return false;//1900年的PASS，超过今年的PASS
         
        //校验月份
        String month = certNo.length() == 15 ? certNo.substring(8, 10) : certNo.substring(10,12);
        final int imonth = Integer.parseInt(month);
        if(imonth <1 || imonth >12){
            return false;
        }
         
        //校验天数      
        String day = certNo.length() ==15 ? certNo.substring(10, 12) : certNo.substring(12, 14);
        final int iday = Integer.parseInt(day);
        if(iday < 1 || iday > 31)
            return false;       
         
        //校验"校验码"
        if(certNo.length() == 15)
            return true;
        return cs[cs.length -1 ] == PARITYBIT[power % 11];
    }
     
    private static int getIdcardCalendar() {        
         GregorianCalendar curDay = new GregorianCalendar();
         int curYear = curDay.get(Calendar.YEAR);
         int year2bit = Integer.parseInt(String.valueOf(curYear).substring(2));          
         return  year2bit;
    }     
     
    public static String getCityLevel(String certNo){
    	String num4 = certNo.substring(0,4);
    	String value = 	cityLevel.get(num4);
    	if(StringUtils.isEmpty(value)){
    		return "05";
    	}else{
    		return value.split(",")[0];
    	}
    }
    
    /**
     * if not found return null;
     * @param cityName
     * @return
     */
    public static String getCityLevelByCityName(String cityName){
    	String value = cityNameLevel.get(cityName);
    	if(StringUtils.isEmpty(value)){
    		value = "05";
    	}
    	return value;
    }
    
    /**
     * if not found return null
     * @param certNo
     * @return
     */
    public static String getCityName(String certNo){
    	String num4 = certNo.substring(0,4);
    	String value = 	cityLevel.get(num4);
    	if(StringUtils.isEmpty(value)){
    		return null;
    	}else{
    		return value.split(",")[1];
    	}
    }
     
	/**
	 * 从身份证号码 提前用户年龄 
	 * @param idNum
	 * @return
	 */
	public static Integer getAgeFromIdNum(String idNum){
 		if(StringUtils.isEmpty(idNum)){
			return null;
		}
		 if(! IDCardUtil.isIDCard(idNum)){
			 return null;
		 }
		 //出生年份
		 int birthYear =Integer.parseInt(getBirthYearIdNum(idNum)) ;
		 int birthMothDay =Integer.parseInt(getBirthMothDayIdNum(idNum) ) ;
		 Date now = new Date();
		 Calendar cal = Calendar.getInstance();
		 cal.setTime(now);
		 int nowYear = cal.get(Calendar.YEAR);
		 SimpleDateFormat mmdd = new SimpleDateFormat("MMdd");
		 int nowMothDay = Integer.parseInt( mmdd.format(now ));
		 int _d = 0;
		 if( birthMothDay >nowMothDay ){
			 _d  = _d  - 1;
		 }
		  //比较月日
		return  nowYear - birthYear + _d;
	}
	/**
	 * 
	 * @param idNum
	 * @return 出生月份 eg: 01  1月分 
	 */
	public static String getBirthMothDayIdNum(String idNum){
		return  idNum.substring(10, 14)  ;
	}
	/**
	 * 
	 * @param idNum
	 * @return 出生年份 eg: 1986  1986年
	 */
	public static String getBirthYearIdNum(String idNum){
		return  idNum.substring(6, 10) ;
	}
	/**
	 * 从身份证号码 提前性别
	 * @param idNum
	 * @return M男人;F女人;O其他
	 */
	public static String getSexFromIdNum(String idNum){
 		if(StringUtils.isEmpty(idNum)){
			return null;
		}
		 if(! IDCardUtil.isIDCard(idNum)){
			 return null;
		 }
		String sex_str = idNum.substring( idNum.length()-1 -1, idNum.length()-1);
		int sex= Integer.parseInt(sex_str);
        if(sex%2==0){
           return "F";
        }else{	
        	  return "M";
        }
	}
    
    public static void main(String[] args) {    
    	String certNo = "362329198601120613";
         boolean mark = isIDCard("362329198601120613");   
         System.out.println(mark  );
         String value = getCityLevel(certNo);
         System.out.println(value  );
         String cityName = getCityName(certNo)  ;
         System.out.println(cityName);
         System.out.println(getCityLevelByCityName(cityName));
    }
}
