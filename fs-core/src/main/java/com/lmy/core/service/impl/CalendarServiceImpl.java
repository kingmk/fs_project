package com.lmy.core.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.JsonUtils;
import com.lmy.core.dao.FsCalendarDao;
import com.lmy.core.dao.FsCalendarSolarDao;
import com.lmy.core.dao.FsCalendarUserDao;
import com.lmy.core.dao.FsCalendarUserinfoDao;
import com.lmy.core.model.FsCalendar;
import com.lmy.core.model.FsCalendarSolar;
import com.lmy.core.model.FsCalendarUser;
import com.lmy.core.model.FsCalendarUserinfo;

@Service
public class CalendarServiceImpl {
	private static final Logger logger = Logger.getLogger(CalendarServiceImpl.class);
	@Autowired
	private FsCalendarDao fsCalendarDao;
	@Autowired
	private FsCalendarSolarDao fsCalendarSolarDao;
	@Autowired
	private FsCalendarUserDao fsCalendarUserDao;
	@Autowired
	private FsCalendarUserinfoDao fsCalendarUserinfoDao;

	public void randomCreateCalendar(Date dateStart, Date dateEnd) {
		
		long startStamp = dateStart.getTime();
		long endStamp = dateEnd.getTime();
		long curStamp = startStamp;
		Date now = new Date();
		do {
			Date dateCur = new Date(curStamp);
			FsCalendar fsCalendar = CalendarAidUtil.randomCalendar(dateCur);
			fsCalendar.setCreateTime(now);
			logger.info("to insert: "+ fsCalendar);
			fsCalendarDao.insert(fsCalendar);
			curStamp += 3600*24*1000;
		} while (curStamp < endStamp);
	}
	
	public List<FsCalendar> getCommonCalendars(Date dateStart, Date dateEnd) {
		return fsCalendarDao.getCalendarsByPeriod(0, null, dateStart, dateEnd);
	}
	
	public JSONObject getCustomMonthCalendars(String openId, Long infoId, Integer year, Integer month) {
		FsCalendarUser fsUser = fsCalendarUserDao.findByOpenId(openId);
		if (fsUser == null) {
			return JsonUtils.commonJsonReturn("0010", "参数错误：用户不存在");
		}

		FsCalendarUserinfo fsInfo = fsCalendarUserinfoDao.findById(infoId);
		if (fsInfo == null) {
			return JsonUtils.commonJsonReturn("0011", "参数错误：用户信息不存在");
		}
		if (!fsInfo.getUserId().equals(fsUser.getId())) {
			logger.info("fail to get month info, info's userId="+fsInfo.getUserId()+" not equal with user's id="+fsUser.getId());
			return JsonUtils.commonJsonReturn("0012", "参数错误：用户信息不属于当前用户");
		}
		
		return getCustomMonthCalendars(year, month, fsInfo);
	}
	
	
	public JSONObject getCustomMonthCalendars(int year, int month, FsCalendarUserinfo fsInfo) {
		Date datePeriod[] = null;
		try {
			datePeriod = CalendarAidUtil.getMonthPeriod(year, month);
		} catch (ParseException e) {
			logger.info("fail to calculate the period of "+year+"-"+month);
			return null;
		}

		List<FsCalendar> fsCalendarList = fsCalendarDao.getCalendarsByPeriod(0, null, datePeriod[0], datePeriod[1]);
		List<FsCalendarSolar> fsSolarList = fsCalendarSolarDao.getSolarByPeriod(datePeriod[0], datePeriod[1]);
		Map<String,	FsCalendarSolar> solarMap = mapSolarListByShowDate(fsSolarList);
		JSONArray jArr = new JSONArray();
		Map<String, Integer> dateIdxMap = new HashMap<String, Integer>();
		int i = 0;
		for (FsCalendar fsCalendar: fsCalendarList) {
			String dateStr = CalendarAidUtil.dateFormat1.format(fsCalendar.getDate());
			JSONObject jCalendar = wrapFsCalendar(fsCalendar, fsInfo);
			if (dateIdxMap.containsKey(dateStr)) {
				int idx = dateIdxMap.get(dateStr);
				wrapSolarInfo(jCalendar, solarMap);
				jArr.getJSONObject(idx).put("extraInfo", jCalendar);
			} else {
				wrapSolarInfo(jCalendar, solarMap);
				jArr.add(jCalendar);
				dateIdxMap.put(dateStr, i);
				
				i++;
			}
		}
		
		JSONObject result = JsonUtils.commonJsonReturn();
		JSONObject body = new JSONObject();
		body.put("list", jArr);
		result.put("body", body);
		return result;
	}
	
	
	public JSONObject getCommonYearCalendar(int year) {
		Date datePeriod[] = null;
		try {
			datePeriod = CalendarAidUtil.getYearPeriod(year);
			logger.info("===== year period: "+ datePeriod[0].toString()+ " to "+datePeriod[1].toString());
		} catch (ParseException e) {
			logger.info("fail to calculate the period of "+year);
			return null;
		}
		
		List<FsCalendar> fsCalendarList = fsCalendarDao.getCalendarsByPeriod(0, null, datePeriod[0], datePeriod[1]);
		List<FsCalendarSolar> fsSolarList = fsCalendarSolarDao.getSolarByPeriod(datePeriod[0], datePeriod[1]);
		Map<String,	FsCalendarSolar> solarMap = mapSolarListByShowDate(fsSolarList);
		
		JSONArray jMonthArr = new JSONArray();
		JSONArray jDayArr = new JSONArray();
		jMonthArr.add(jDayArr);
		
		int idxM = 0;
		int idxD = 0;
		Map<String, Integer> dateIdxMap = new HashMap<String, Integer>();
		Calendar calendar = Calendar.getInstance();
		for (FsCalendar fsCalendar: fsCalendarList) {
			Date d = fsCalendar.getDate();
			calendar.setTime(d);
			int month = calendar.get(Calendar.MONTH)+1;
			if (month > idxM+1) {
				// if a new month begins, new array for days in the month should be created
				jDayArr = new JSONArray();
				jMonthArr.add(jDayArr);
				dateIdxMap = new HashMap<String, Integer>();
				idxD = 0;
				idxM ++;
			} 
			String dateStr = CalendarAidUtil.dateFormat1.format(fsCalendar.getDate());
			JSONObject jCalendar = wrapFsCalendar(fsCalendar, null);
			if (dateIdxMap.containsKey(dateStr)) {
				int idx = dateIdxMap.get(dateStr);
				wrapSolarInfo(jCalendar, solarMap);
				jDayArr.getJSONObject(idx).put("extraInfo", jCalendar);
			} else {
				wrapSolarInfo(jCalendar, solarMap);
				jDayArr.add(jCalendar);
				dateIdxMap.put(dateStr, idxD);
				
				idxD++;
			}
		}
		
		JSONObject result = JsonUtils.commonJsonReturn();
		JSONObject body = new JSONObject();
		body.put("list", jMonthArr);
		result.put("body", body);
		return result;
	}
	
	public JSONObject getSolarByYear(int year) {
		Date datePeriod[] = null;
		try {
			datePeriod = CalendarAidUtil.getYearPeriod(year);
			logger.info("===== year period: "+ datePeriod[0].toString()+ " to "+datePeriod[1].toString());
		} catch (ParseException e) {
			logger.info("fail to calculate the period of "+year);
			return null;
		}
		List<FsCalendarSolar> fsSolarList = fsCalendarSolarDao.getSolarByPeriod(datePeriod[0], datePeriod[1]);
		JSONArray jArr = new JSONArray();
		for (FsCalendarSolar fsSolar: fsSolarList) {
			jArr.add(JSON.parseObject(fsSolar.toString()));
		}

		JSONObject result = JsonUtils.commonJsonReturn();
		JSONObject body = new JSONObject();
		body.put("list", jArr);
		result.put("body", body);
		return result;
	}
	
	private Map<String, FsCalendarSolar> mapSolarListByShowDate(List<FsCalendarSolar> solarList) {
		Map<String,	FsCalendarSolar> solarMap = new HashMap<>();
		for (FsCalendarSolar fsSolar: solarList) {
			solarMap.put(CalendarAidUtil.dateFormat1.format(fsSolar.getShowDate()), fsSolar);
		}
		return solarMap;
	}
	
	private void wrapSolarInfo(JSONObject jCalendar, Map<String, FsCalendarSolar> solarMap) {
		String dateStr = jCalendar.getString("date").substring(0, 10);
		if (solarMap.containsKey(dateStr)) {
			FsCalendarSolar fsSolar = solarMap.get(dateStr);
			jCalendar.put("solar", fsSolar.getName());
			
			String solarTimeStr = CalendarAidUtil.dateFormatFull.format(fsSolar.getTime());
			String showTimeStr = CalendarAidUtil.dateFormatFull.format(fsSolar.getShowDate());
			if (solarTimeStr.substring(0, 10).equals(showTimeStr.substring(0, 10))) {
				jCalendar.put("solar_time", solarTimeStr.substring(11, 16));
			} else {
				jCalendar.put("solar_time", ""+Integer.parseInt(solarTimeStr.substring(8, 10))+"日"
						+solarTimeStr.substring(11, 16));
			}
		}
	}
	
	private JSONObject wrapFsCalendar(FsCalendar fsCalendar, FsCalendarUserinfo fsInfo) {
		JSONObject jCalendar = JSON.parseObject(fsCalendar.toString());
		Long customEventsValue = CalendarAidUtil.calcCustomEvents(fsCalendar, fsInfo);
		jCalendar.put("customEventsValue", customEventsValue);
		jCalendar.put("events", CalendarAidUtil.parseCalendarEvents(customEventsValue));
//		jCalendar.put("strLunar", CalendarAidUtil.parseLunarDate(fsCalendar.getLunarDate()));
		
		return jCalendar;
	}
	
	private FsCalendar parseFsCalendar(JSONObject jCalendar) throws ParseException {
		FsCalendar fsCalendar = new FsCalendar();

		fsCalendar.setType(0);
		fsCalendar.setUserId(null);
		fsCalendar.setDate(CalendarAidUtil.dateFormatFull.parse(jCalendar.getString("date")));
		fsCalendar.setLunarStr(jCalendar.getString("moonstr"));
		fsCalendar.setYearHeaven(jCalendar.getString("year_heaven"));
		fsCalendar.setYearEarth(jCalendar.getString("year_earth"));
		fsCalendar.setMonthHeaven(jCalendar.getString("month_heaven"));
		fsCalendar.setMonthEarth(jCalendar.getString("month_earth"));
		fsCalendar.setDayHeaven(jCalendar.getString("day_heaven"));
		fsCalendar.setDayEarth(jCalendar.getString("day_earth"));
		fsCalendar.setGuard(jCalendar.getString("guard"));
		fsCalendar.setLuck(jCalendar.getString("luck"));
		fsCalendar.setLuckV1(jCalendar.getInteger("luck_v1"));
		fsCalendar.setLuckV2(jCalendar.getInteger("luck_v2"));
		fsCalendar.setEvil(jCalendar.getString("evil"));
		fsCalendar.setHoliday(jCalendar.getString("holiday"));
		fsCalendar.setEventsValue(CalendarAidUtil.calcCalendarEvents(jCalendar.getJSONArray("events")));
		
		return fsCalendar;
	}
	
	
	public JSONObject saveCalendar(String s) {
		JSONArray jArrData = (JSONArray) JSONArray.parse(s);
		List<FsCalendar> fsCalendarList = new ArrayList<>();
		Date now = new Date();
		for (int i = 0; i < jArrData.size(); i++) {
			JSONObject jData = jArrData.getJSONObject(i);
			try {
				FsCalendar fsCalendar = parseFsCalendar(jData);
				fsCalendar.setCreateTime(now);
//				fsCalendarDao.insert(fsCalendar);
				fsCalendarList.add(fsCalendar);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		fsCalendarDao.batchInsert(fsCalendarList);

		JSONObject result = JsonUtils.commonJsonReturn();
		return result;
	}
	
	public JSONObject saveSolar(String s) {
		if (s == null) {
			s = "[{'time':'2019-01-05 23:39:00','name':'小寒','showDate':'2019-01-06 00:00:00'},{'time':'2019-01-20 17:00:00','name':'大寒','showDate':'2019-01-20 00:00:00'},{'time':'2019-02-04 11:14:00','name':'立春','showDate':'2019-02-04 00:00:00'},{'time':'2019-02-19 07:04:00','name':'雨水','showDate':'2019-02-19 00:00:00'},{'time':'2019-03-06 05:10:00','name':'惊蛰','showDate':'2019-03-06 00:00:00'},{'time':'2019-03-21 05:58:00','name':'春分','showDate':'2019-03-21 00:00:00'},{'time':'2019-04-05 09:51:00','name':'清明','showDate':'2019-04-05 00:00:00'},{'time':'2019-04-20 16:55:00','name':'谷雨','showDate':'2019-04-20 00:00:00'},{'time':'2019-05-06 03:03:00','name':'立夏','showDate':'2019-05-06 00:00:00'},{'time':'2019-05-21 15:59:00','name':'小满','showDate':'2019-05-21 00:00:00'},{'time':'2019-06-06 07:06:00','name':'芒种','showDate':'2019-06-06 00:00:00'},{'time':'2019-06-21 23:54:00','name':'夏至','showDate':'2019-06-22 00:00:00'},{'time':'2019-07-07 17:21:00','name':'小暑','showDate':'2019-07-07 00:00:00'},{'time':'2019-07-23 10:50:00','name':'大暑','showDate':'2019-07-23 00:00:00'},{'time':'2019-08-08 03:13:00','name':'立秋','showDate':'2019-08-08 00:00:00'},{'time':'2019-08-23 18:02:00','name':'处暑','showDate':'2019-08-23 00:00:00'},{'time':'2019-09-08 06:17:00','name':'白露','showDate':'2019-09-08 00:00:00'},{'time':'2019-09-23 15:50:00','name':'秋分','showDate':'2019-09-23 00:00:00'},{'time':'2019-10-08 22:06:00','name':'寒露','showDate':'2019-10-08 00:00:00'},{'time':'2019-10-24 01:20:00','name':'霜降','showDate':'2019-10-24 00:00:00'},{'time':'2019-11-08 01:24:00','name':'立冬','showDate':'2019-11-08 00:00:00'},{'time':'2019-11-22 22:59:00','name':'小雪','showDate':'2019-11-22 00:00:00'},{'time':'2019-12-07 18:18:00','name':'大雪','showDate':'2019-12-07 00:00:00'},{'time':'2019-12-22 12:19:00','name':'冬至','showDate':'2019-12-22 00:00:00'}]";
		}
		JSONArray jArrSolar = (JSONArray) JSONArray.parse(s);
		List<FsCalendarSolar> fsSolarList = new ArrayList<>();
		Date now = new Date();
		for (int i = 0; i < jArrSolar.size(); i++) {
			JSONObject jSolar = jArrSolar.getJSONObject(i);
			FsCalendarSolar fsSolar = new FsCalendarSolar();
			fsSolar.setName(jSolar.getString("name"));
			try {
				fsSolar.setTime(CalendarAidUtil.dateFormatFull.parse(jSolar.getString("time")));
				fsSolar.setShowDate(CalendarAidUtil.dateFormatFull.parse(jSolar.getString("showDate")));
				fsSolar.setYearHeaven(jSolar.getString("year_heaven"));
				fsSolar.setYearEarth(jSolar.getString("year_earth"));
				fsSolar.setMonthHeaven(jSolar.getString("month_heaven"));
				fsSolar.setMonthEarth(jSolar.getString("month_earth"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			fsSolar.setCreateTime(now);
			fsSolarList.add(fsSolar);
		}
		fsCalendarSolarDao.batchInsert(fsSolarList);
		
		return JsonUtils.commonJsonReturn();
	}
	
	public JSONObject calUserHeavenEarth(FsCalendarUserinfo fsUser) {
		Date birthTime = fsUser.getBirthTime();
		
		// get year, month h & e from solar infos
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(birthTime);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		calendar.add(Calendar.MONTH, -1);
		Date dStart = calendar.getTime();
		
		List<FsCalendarSolar> fsSolarList = fsCalendarSolarDao.getSolarByPeriod(dStart, birthTime);
		FsCalendarSolar fsSolar = fsSolarList.get(fsSolarList.size()-1);
		
		fsUser.setYearHeaven(fsSolar.getYearHeaven());
		fsUser.setYearEarth(fsSolar.getYearEarth());
		fsUser.setMonthHeaven(fsSolar.getMonthHeaven());
		fsUser.setMonthEarth(fsSolar.getMonthEarth());
		
		// get day h & e 
		String dayHE = CalendarAidUtil.calcDayHeavenEarth(birthTime);
		fsUser.setDayHeaven(dayHE.substring(0, 1));
		fsUser.setDayEarth(dayHE.substring(1, 2));
		
		// get hour h & e
		String hourHE = CalendarAidUtil.calcHourHeavenEarth(hour, dayHE);
		fsUser.setHourHeaven(hourHE.substring(0, 1));
		fsUser.setHourEarth(hourHE.substring(1, 2));
		
		// get tai h & e
		String taiHE = CalendarAidUtil.calcTaiHeavenEarth(fsUser.getMonthHeaven(), fsUser.getMonthEarth());
		fsUser.setTaiHeaven(taiHE.substring(0, 1));
		fsUser.setTaiEarth(taiHE.substring(1, 2));
		
		String lifeEarth = CalendarAidUtil.calcLifeEarth(hour, fsSolar.getName());
		fsUser.setLifeEarth(lifeEarth);

		JSONObject result = JsonUtils.commonJsonReturn();
		result.put("body", fsUser);
		return result;
	}
	

	public static void main(String[] args) {
//		CalendarServiceImpl serviceImpl = new CalendarServiceImpl();
//		serviceImpl.saveCalendar();
		
//		Date d = new Date();
//		String s = CalendarAidUtil.dateFormatFull.format(d);
//		System.out.println(s);
		
		try {
			Date d = CalendarAidUtil.dateFormatFull.parse("1947-12-10 23:00:00");
			
			Date d2 = new Date(24*3600000l);
//			String s = CalendarAidUtil.dateFormatFull.format(d);
			System.out.println(d2.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		String[]calendarHeavens = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
		System.out.println(calendarHeavens.length);
	}
}
