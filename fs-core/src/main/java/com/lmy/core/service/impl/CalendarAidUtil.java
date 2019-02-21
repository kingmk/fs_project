package com.lmy.core.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmy.core.model.FsCalendar;
import com.lmy.core.model.FsCalendarUser;
import com.lmy.core.model.FsCalendarUserinfo;

public class CalendarAidUtil {
	private static final Logger logger = Logger.getLogger(CalendarAidUtil.class);

	public static final SimpleDateFormat dateFormatFull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat dateFormatTimeShort = new SimpleDateFormat("HH:mm");
	public static final SimpleDateFormat dateFormatDateShort = new SimpleDateFormat("MM-dd");
	
	private static final String[] calendarHeavens = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
	private static final String[] calendarEarthes = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
	private static final String[] calendarEvents = {"嫁娶","移徙","出行","动土","开市","立券","交易","纳财","会友","理发","订婚","扫舍","赴任","求医","祈福","安葬"};
	private static final String[] calendarSolarTerms = {"立春","雨水","惊蛰","春分","清明","谷雨","立夏","小满","芒种","夏至","小暑","大暑","立秋","处暑","白露","秋分","寒露","霜降","立冬","小雪","大雪","冬至","小寒","大寒"};
	private static final String[] calendarConstels = {"角","亢","氐","房","心","尾","箕","斗","牛","女","虚","危","室","壁","奎","娄","胃","昴","毕","觜","参","井","鬼","柳","星","张","翼","轸"};
	private static final String[] calendarGuards = {"建","除","满","平","定","执","破","危","成","收","开","闭"};
	private static final String[] calendarLucks = {"小吉","吉","大吉","平","小凶","凶","大凶"};
	private static final String[] calendarEvils = {"冲鼠","冲牛","冲虎","冲兔","冲龙","冲蛇","冲马","冲羊","冲猴","冲鸡","冲狗","冲猪"};
	
	private static final Long customEvents = -84952406l; // for ["嫁娶", "移徙", "出行", "动土", "开市", "立券", "纳财", "订婚", "赴任", "求医"] 
	
	private static final String[][] hourHETable = {{"甲子","丙子","戊子","庚子","壬子"},{"乙丑","丁丑","己丑","辛丑","癸丑"},{"丙寅","戊寅","庚寅","壬寅","甲寅"},{"丁卯","己卯","辛卯","癸卯","乙卯"},{"戊辰","庚辰","壬辰","甲辰","丙辰"},{"己巳","辛巳","癸巳","乙巳","丁巳"},{"庚午","壬午","甲午","丙午","戊午"},{"辛未","癸未","乙未","丁未","己未"},{"壬申","甲申","丙申","戊申","庚申"},{"癸酉","乙酉","丁酉","己酉","辛酉"},{"甲戌","丙戌","戊戌","庚戌","壬戌"},{"乙亥","丁亥","己亥","辛亥","癸亥"}};
	private static final String[][] lifeEarthTable = {{"卯","寅","丑","子","亥","戌","酉","申","未","午","巳","辰"},{"寅","丑","子","亥","戌","酉","申","未","午","巳","辰","卯"},{"丑","子","亥","戌","酉","申","未","午","巳","辰","卯","寅"},{"子","亥","戌","酉","申","未","午","巳","辰","卯","寅","丑"},{"亥","戌","酉","申","未","午","巳","辰","卯","寅","丑","子"},{"戌","酉","申","未","午","巳","辰","卯","寅","丑","子","亥"},{"酉","申","未","午","巳","辰","卯","寅","丑","子","亥","戌"},{"申","未","午","巳","辰","卯","寅","丑","子","亥","戌","酉"},{"未","午","巳","辰","卯","寅","丑","子","亥","戌","酉","申"},{"午","巳","辰","卯","寅","丑","子","亥","戌","酉","申","未"},{"巳","辰","卯","寅","丑","子","亥","戌","酉","申","未","午"},{"辰","卯","寅","丑","子","亥","戌","酉","申","未","午","巳"}};
	
	private static final String dayAvgDiff = "{'01-01':-3.32,'01-02':-3.79,'01-03':-4.25,'01-04':-4.71,'01-05':-5.17,'01-06':-5.61,'01-07':-6.05,'01-08':-6.48,'01-09':-6.9,'01-10':-7.31,'01-11':-7.71,'01-12':-8.11,'01-13':-8.49,'01-14':-8.86,'01-15':-9.22,'01-16':-9.58,'01-17':-9.92,'01-18':-10.25,'01-19':-10.56,'01-20':-10.87,'01-21':-11.16,'01-22':-11.44,'01-23':-11.71,'01-24':-11.96,'01-25':-12.2,'01-26':-12.43,'01-27':-12.65,'01-28':-12.85,'01-29':-13.04,'01-30':-13.21,'01-31':-13.37,'02-01':-13.52,'02-02':-13.65,'02-03':-13.77,'02-04':-13.88,'02-05':-13.97,'02-06':-14.05,'02-07':-14.11,'02-08':-14.16,'02-09':-14.2,'02-10':-14.23,'02-11':-14.24,'02-12':-14.24,'02-13':-14.22,'02-14':-14.2,'02-15':-14.16,'02-16':-14.11,'02-17':-14.04,'02-18':-13.97,'02-19':-13.88,'02-20':-13.78,'02-21':-13.67,'02-22':-13.55,'02-23':-13.42,'02-24':-13.28,'02-25':-13.13,'02-26':-12.97,'02-27':-12.8,'02-28':-12.62,'02-29':-12.5,'03-01':-12.43,'03-02':-12.23,'03-03':-12.03,'03-04':-11.82,'03-05':-11.6,'03-06':-11.37,'03-07':-11.13,'03-08':-10.89,'03-09':-10.64,'03-10':-10.39,'03-11':-10.13,'03-12':-9.87,'03-13':-9.6,'03-14':-9.32,'03-15':-9.05,'03-16':-8.76,'03-17':-8.48,'03-18':-8.19,'03-19':-7.9,'03-20':-7.61,'03-21':-7.31,'03-22':-7.01,'03-23':-6.71,'03-24':-6.41,'03-25':-6.11,'03-26':-5.81,'03-27':-5.51,'03-28':-5.2,'03-29':-4.9,'03-30':-4.6,'03-31':-4.3,'04-01':-4,'04-02':-3.71,'04-03':-3.42,'04-04':-3.12,'04-05':-2.83,'04-06':-2.55,'04-07':-2.27,'04-08':-1.99,'04-09':-1.71,'04-10':-1.44,'04-11':-1.18,'04-12':-0.91,'04-13':-0.66,'04-14':-0.41,'04-15':-0.16,'04-16':0.08,'04-17':0.32,'04-18':0.54,'04-19':0.76,'04-20':0.98,'04-21':1.19,'04-22':1.39,'04-23':1.58,'04-24':1.76,'04-25':1.94,'04-26':2.11,'04-27':2.27,'04-28':2.43,'04-29':2.57,'04-30':2.71,'05-01':2.83,'05-02':2.95,'05-03':3.06,'05-04':3.16,'05-05':3.25,'05-06':3.33,'05-07':3.41,'05-08':3.47,'05-09':3.52,'05-10':3.57,'05-11':3.6,'05-12':3.63,'05-13':3.64,'05-14':3.65,'05-15':3.65,'05-16':3.63,'05-17':3.61,'05-18':3.58,'05-19':3.54,'05-20':3.49,'05-21':3.43,'05-22':3.36,'05-23':3.29,'05-24':3.2,'05-25':3.11,'05-26':3,'05-27':2.9,'05-28':2.78,'05-29':2.65,'05-30':2.52,'05-31':2.38,'06-01':2.23,'06-02':2.08,'06-03':1.92,'06-04':1.75,'06-05':1.58,'06-06':1.4,'06-07':1.22,'06-08':1.03,'06-09':0.84,'06-10':0.65,'06-11':0.45,'06-12':0.24,'06-13':0.04,'06-14':-0.17,'06-15':-0.39,'06-16':-0.6,'06-17':-0.81,'06-18':-1.03,'06-19':-1.25,'06-20':-1.47,'06-21':-1.68,'06-22':-1.9,'06-23':-2.12,'06-24':-2.33,'06-25':-2.55,'06-26':-2.76,'06-27':-2.97,'06-28':-3.17,'06-29':-3.38,'06-30':-3.58,'07-01':-3.77,'07-02':-3.97,'07-03':-4.16,'07-04':-4.34,'07-05':-4.52,'07-06':-4.69,'07-07':-4.86,'07-08':-5.01,'07-09':-5.17,'07-10':-5.32,'07-11':-5.46,'07-12':-5.59,'07-13':-5.71,'07-14':-5.83,'07-15':-5.94,'07-16':-6.04,'07-17':-6.13,'07-18':-6.21,'07-19':-6.29,'07-20':-6.35,'07-21':-6.41,'07-22':-6.45,'07-23':-6.49,'07-24':-6.52,'07-25':-6.54,'07-26':-6.54,'07-27':-6.54,'07-28':-6.53,'07-29':-6.5,'07-30':-6.47,'07-31':-6.43,'08-01':-6.38,'08-02':-6.31,'08-03':-6.24,'08-04':-6.16,'08-05':-6.06,'08-06':-5.96,'08-07':-5.85,'08-08':-5.72,'08-09':-5.59,'08-10':-5.45,'08-11':-5.3,'08-12':-5.13,'08-13':-4.96,'08-14':-4.78,'08-15':-4.59,'08-16':-4.4,'08-17':-4.19,'08-18':-3.98,'08-19':-3.75,'08-20':-3.52,'08-21':-3.28,'08-22':-3.03,'08-23':-2.78,'08-24':-2.52,'08-25':-2.25,'08-26':-1.97,'08-27':-1.69,'08-28':-1.4,'08-29':-1.1,'08-30':-0.8,'08-31':-0.5,'09-01':-0.18,'09-02':0.13,'09-03':0.45,'09-04':0.78,'09-05':1.11,'09-06':1.44,'09-07':1.78,'09-08':2.12,'09-09':2.47,'09-10':2.81,'09-11':3.16,'09-12':3.51,'09-13':3.87,'09-14':4.22,'09-15':4.58,'09-16':4.93,'09-17':5.29,'09-18':5.64,'09-19':6,'09-20':6.36,'09-21':6.71,'09-22':7.07,'09-23':7.42,'09-24':7.77,'09-25':8.12,'09-26':8.47,'09-27':8.81,'09-28':9.15,'09-29':9.49,'09-30':9.82,'10-01':10.15,'10-02':10.47,'10-03':10.79,'10-04':11.11,'10-05':11.41,'10-06':11.72,'10-07':12.01,'10-08':12.3,'10-09':12.58,'10-10':12.86,'10-11':13.13,'10-12':13.39,'10-13':13.64,'10-14':13.88,'10-15':14.12,'10-16':14.34,'10-17':14.55,'10-18':14.76,'10-19':14.96,'10-20':15.14,'10-21':15.31,'10-22':15.48,'10-23':15.63,'10-24':15.77,'10-25':15.9,'10-26':16.01,'10-27':16.12,'10-28':16.21,'10-29':16.29,'10-30':16.35,'10-31':16.41,'11-01':16.44,'11-02':16.47,'11-03':16.48,'11-04':16.48,'11-05':16.46,'11-06':16.43,'11-07':16.39,'11-08':16.33,'11-09':16.26,'11-10':16.18,'11-11':16.07,'11-12':15.96,'11-13':15.83,'11-14':15.69,'11-15':15.53,'11-16':15.36,'11-17':15.17,'11-18':14.97,'11-19':14.76,'11-20':14.53,'11-21':14.29,'11-22':14.03,'11-23':13.76,'11-24':13.48,'11-25':13.19,'11-26':12.88,'11-27':12.56,'11-28':12.23,'11-29':11.89,'11-30':11.53,'12-01':11.17,'12-02':10.79,'12-03':10.4,'12-04':10.01,'12-05':9.6,'12-06':9.19,'12-07':8.76,'12-08':8.33,'12-09':7.89,'12-10':7.44,'12-11':6.99,'12-12':6.53,'12-13':6.06,'12-14':5.59,'12-15':5.11,'12-16':4.63,'12-17':4.15,'12-18':3.66,'12-19':3.17,'12-20':2.68,'12-21':2.18,'12-22':1.69,'12-23':1.19,'12-24':0.69,'12-25':0.2,'12-26':-0.29,'12-27':-0.79,'12-28':-1.28,'12-29':-1.76,'12-30':-2.25,'12-31':-2.73}";
	
	private static final Long dateBaseLong = -696157200000l; 
	private static final Long dayMillLong = 86400000l;
	
	private static JSONObject jDayAvgDiff = null;
	
	private static String randomCalendarItem(String[] itemList, int prob) {
		int size = itemList.length;
		int rand = (int)(Math.random() * 100);
		if (rand>prob) {
			return null;
		}
		rand = (int)(Math.random() * 1000);
		return itemList[(rand%size)];
	}
	
	private static Long randomCalendarEvents() {
		long rand = (long)(Math.random()*4294967295l);
		return Long.valueOf(rand);
	}
	
	public static FsCalendar randomCalendar(Date date) {
		FsCalendar fsCalendar = new FsCalendar();
		fsCalendar.setType(0);
		fsCalendar.setUserId(null);
		fsCalendar.setDate(date);
		fsCalendar.setLunarDate(date);
		fsCalendar.setYearHeaven(randomCalendarItem(calendarHeavens, 100));
		fsCalendar.setYearEarth(randomCalendarItem(calendarEarthes, 100));
		fsCalendar.setMonthHeaven(randomCalendarItem(calendarHeavens, 100));
		fsCalendar.setMonthEarth(randomCalendarItem(calendarEarthes, 100));
		fsCalendar.setDayHeaven(randomCalendarItem(calendarHeavens, 100));
		fsCalendar.setDayEarth(randomCalendarItem(calendarEarthes, 100));
		fsCalendar.setConstel(randomCalendarItem(calendarConstels, 80));
		fsCalendar.setGuard(randomCalendarItem(calendarGuards, 60));
		fsCalendar.setLuck(randomCalendarItem(calendarLucks, 100));
		fsCalendar.setLuckV1(0);
		fsCalendar.setLuckV2(0);
		fsCalendar.setEvil(randomCalendarItem(calendarEvils, 60));
		fsCalendar.setSolar(randomCalendarItem(calendarSolarTerms, 10));
		fsCalendar.setHoliday("");
		fsCalendar.setEventsValue(randomCalendarEvents());
		return fsCalendar;
	}
	
	public static Date[] getMonthPeriod(int year, int month) throws ParseException {
		String strDate = ""+year+"-"+((month<10?"0"+month:month))+"-01 00:00:00";
		logger.info("=======get month: "+strDate+"======");
		Date dateStart = dateFormatFull.parse(strDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateStart);
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date dateEnd = calendar.getTime();
		
		Date result[] = {dateStart, dateEnd};
		
		return result;
	}

	
	public static Date[] getYearPeriod(int year) throws ParseException {
		String strDate = ""+year+"-01-01 00:00:00";
		Date dateStart = dateFormatFull.parse(strDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateStart);
		calendar.add(Calendar.YEAR, 1);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date dateEnd = calendar.getTime();
		
		Date result[] = {dateStart, dateEnd};
		
		return result;
	}
	
	
	// from a json array to the long value of events
	public static Long calcCalendarEvents(JSONArray jArrEvents) {
		Long val = 0l;
		for (int i = 0; i < jArrEvents.size(); i++) {
			val += jArrEvents.getLong(i)<<(i*2);
		}
		
		return val;
	}
	
	// from a long value to a map for {A00-A15} of events
	public static JSONObject parseCalendarEvents(Long events) {
		JSONObject rlt = new JSONObject();
		for (int i = 0; i < calendarEvents.length; i++) {
			String key = "A"+(i<10?"0"+i:i);
			int value = (int)((events & (3<<(i*2)))>>(i*2));
			rlt.put(key, value);
		}
		return rlt;
	}
	
	public static Long calcCustomEvents(FsCalendar fsCalendar, FsCalendarUserinfo fsInfo) {
		Long events = fsCalendar.getEventsValue();
		String[] excludeDayHeaven = {"庚","辛","壬","癸","甲","乙","甲丙","乙丁","丙戊","丁己"};
		String[] excludeDayEarth = {"午卯","未","申","酉子","戌辰","亥寅","子午","丑戌","寅巳","卯酉","辰丑","巳亥"};
		Boolean excluded = false;
		
		for (int i = 0; i < 10; i++) {
			String calendarHeaven = calendarHeavens[i];
			if ((fsInfo.getYearHeaven().equals(calendarHeaven) 
				|| fsInfo.getTaiHeaven().equals(calendarHeaven))
				&& excludeDayHeaven[i].indexOf(fsCalendar.getDayHeaven()) >= 0) {
				excluded = true;
				break;
			}
		}
		if (excluded) {
			return events&customEvents;
		}
		
		for (int i = 0; i < 12; i++) {
			String calendarEarth = calendarEarthes[i];
			if ((fsInfo.getYearEarth().equals(calendarEarth) 
				|| fsInfo.getTaiEarth().equals(calendarEarth)
				|| fsInfo.getLifeEarth().equals(calendarEarth))
				&& excludeDayEarth[i].indexOf(fsCalendar.getDayEarth()) >= 0) {
				excluded = true;
				break;
			}
		}
		if (excluded) {
			return events&customEvents;
		}
		
		return events;
	}
	
	public static String parseLunarDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH);
		String strDays[] = {"十","一","二","三","四","五","六","七","八","九"};
		String strMonthes[] = {"正月","二月","三月","四月","五月","六月","七月","八月","九月","十月","冬月","腊月"};
		String strUnit[] = {"初","十","廿","三"};
		
		String rlt = "";
		int a = day/10;
		int b = day%10;
		if (day == 1) {
			rlt = strMonthes[month];
		} else {
			rlt = strUnit[a]+strDays[b];
		}
		
		return rlt;
	}
	
	public static Boolean needRecalcUser(FsCalendarUserinfo userCurrent, FsCalendarUserinfo userUpdate) {
		Boolean result = false;
		Integer birthLongitude = userUpdate.getBirthLongitude();
		Date birthTime = userUpdate.getBirthTime();
		if ( (birthLongitude != null && !birthLongitude.equals(userCurrent.getBirthLongitude())) || 
				(birthTime != null && !birthTime.equals(userCurrent.getBirthTime()))) {
			result = true;
		}
		return result;
	}
	
	public static Date calcRealTime(Date date, double longitude) {
		if (jDayAvgDiff == null) {
			jDayAvgDiff = JSONObject.parseObject(dayAvgDiff);
		}
		String strDate = dateFormatDateShort.format(date);
		long time = date.getTime();
		long adjustAvgDiff = (long)(jDayAvgDiff.getDoubleValue(strDate)*60000.0);
		long adjustGeoDiff = (long)((longitude-120.0)*240000.0); // (longitude-120)*60/15 minutes
		
		return new Date(time+adjustAvgDiff+adjustGeoDiff);
	}
	
	public static String calcDayHeavenEarth(Date date) {
		
		Long diffTime = date.getTime() - dateBaseLong;
		Long diffDay = diffTime/dayMillLong;
		String s = calendarHeavens[(int)(diffDay%calendarHeavens.length)] 
				+ calendarEarthes[(int)(diffDay%calendarEarthes.length)];
		return s;
	}
	
	public static String calcHourHeavenEarth(int hour, String dayHeaven) {
		int idxHour = ((hour+1)%24)/2; // starts from 23:00
		
		int idxDay = 0;
		for (; idxDay<calendarHeavens.length; idxDay++) {
			if (calendarHeavens[idxDay].equals(dayHeaven)) {
				break;
			}
		}
		idxDay = idxDay % 5;
		
		
		return hourHETable[idxHour][idxDay];
	}
	
	public static String calcTaiHeavenEarth(String monthHeaven, String monthEarth) {
		int idxH = 0;
		int idxE = 0;
		for (; idxH < calendarHeavens.length; idxH++) {
			if (calendarHeavens[idxH].equals(monthHeaven)) {
				break;
			}
		}
		for (; idxE < calendarEarthes.length; idxE++) {
			if (calendarEarthes[idxE].equals(monthEarth)) {
				break;
			}
		}
		idxH = (idxH+1) % calendarHeavens.length;
		idxE = (idxE+3) % calendarEarthes.length;
		
		return calendarHeavens[idxH]+calendarEarthes[idxE];
	}
	
	public static String calcLifeEarth(int hour, String solar) {
		int idxHour = ((hour+1)%24)/2; // starts from 23:00
		int idxSolar = 0;
		for (; idxSolar < calendarSolarTerms.length; idxSolar++) {
			if (calendarSolarTerms[idxSolar].equals(solar)) {
				break;
			}
		}
		idxSolar = ((idxSolar+1)%24)/2; // starts from 大寒, only for half of the solar terms
		
		
		return lifeEarthTable[idxHour][idxSolar];
	}
	
	private static Long customEventsMask() {
		Boolean[] customMasks = {true,true,true,true,true,true,false,true,false,false,true,false,true,true,false,false};
		Long maskValue = 0l;
		for (int i = 0; i < customMasks.length; i++) {
			maskValue = maskValue+ (customMasks[i]?(2<<(i*2)):(3<<(i*2)));
		}
		return maskValue;
	}
	
	public static void main(String[] args) {
//		try {
//			Date d = CalendarAidUtil.dateFormatFull.parse("1982-08-13 17:00:00");
//			Date d2 = calcRealTime(d, 121.48461);
//			System.out.println(d2);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		JSONObject j = parseCalendarEvents(268435712l);
//		System.out.println(j);
//		Long maskValue = customEventsMask();
//		System.out.println(maskValue);
//		Long value = 1512133994l;
//		Long customValue = value&maskValue;
//		System.out.println(parseCalendarEvents(value));
//		
//		System.out.println(customValue);
//		System.out.println(parseCalendarEvents(customValue));
		
		FsCalendarUserinfo info = new FsCalendarUserinfo();
		info.setUserId(1923l);
		
		FsCalendarUser user = new FsCalendarUser();
		user.setId(1923l);
		Long a = 1923l;
		Long b = 1923l;
		System.out.println(info.getUserId().equals(user.getId()));
		
		System.out.println(UUID.randomUUID());
	}
}
