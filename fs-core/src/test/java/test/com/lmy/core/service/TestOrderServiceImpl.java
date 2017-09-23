package test.com.lmy.core.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.Bazi;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.LunarSolarConverter;
import com.lmy.core.dao.FsOrderDao;
import com.lmy.core.dao.FsPayRecordDao;
import com.lmy.core.dao.FsChatRecordDao;
import com.lmy.core.model.FsChatRecord;
import com.lmy.core.model.FsOrder;
import com.lmy.core.model.FsPayRecord;
import com.lmy.core.model.dto.FsChatRecordDto;
import com.lmy.core.service.impl.OrderServiceImpl;
import com.lmy.core.service.impl.WeiXinInterServiceImpl;

import test.com.lmy.core.base.BaseTestCase;

public class TestOrderServiceImpl extends BaseTestCase {

	@SpringBean("orderServiceImpl")
	OrderServiceImpl orderServiceImpl;
	@SpringBean("fsPayRecordDao")
	FsPayRecordDao fsPayRecordDao;
	@SpringBean("fsOrderDao")
	FsOrderDao fsOrderDao;
	
	@SpringBean("fsChatRecordDao")
	FsChatRecordDao fsChatRecordDao;
	
	@Test
	public void test_zxOrderHandWeiXinNotify() throws InterruptedException{
		FsPayRecord record = fsPayRecordDao.findByOrderIdAndTradeType(100127l ,null ).get(0);
		String out_trade_no = record.getOutTradeNo();
		String bank_type = "CFT";
		String transaction_id = record.getOutTradeNo();
		boolean paySucc = true;
		JSONObject result = orderServiceImpl.zxOrderHandWeiXinNotify(out_trade_no, bank_type, transaction_id, paySucc);
		System.out.println(result);
		Thread.sleep(6 * 1000);
	}
	
	@Test
	public void test_chatData(){
		List<FsChatRecordDto> list = fsChatRecordDao.findChatRecord(null	, null, null, null, null, 0, 200, null);
		if(CollectionUtils.isEmpty(list)){
			return;
		}
		Map<Long,FsOrder> orderIdBeanMap = new HashMap<Long,FsOrder>();
		for(FsChatRecordDto bean : list ){
			FsOrder order =getOrder(bean.getOrderId(), orderIdBeanMap);
			FsChatRecord beanForUpdate = new FsChatRecord();
			beanForUpdate.setId( bean.getId() );
			beanForUpdate.setOrderId(bean.getOrderId());
			beanForUpdate.setSentIsMaster(bean.getSentUsrId().equals( order.getSellerUsrId() )?"Y":"N"   );
			this.fsChatRecordDao.update(beanForUpdate);
		}
	}
	private FsOrder getOrder(Long orderId,Map<Long,FsOrder> orderIdBeanMap){
		if(orderIdBeanMap.get(orderId)!=null){
			return orderIdBeanMap.get(orderId);
		}else{
			FsOrder order = fsOrderDao.findById( orderId);
			orderIdBeanMap.put(orderId, order);
			return order;
		}
	}
	
	
	@Test
	public void test_transfers1(){
		
		FsPayRecord record = fsPayRecordDao.findByOrderIdAndTradeType(100124l ,null ).get(0);
		record.setTotalFee(1l);
		JSONObject result = WeiXinInterServiceImpl.transfers1(record);
		System.out.println(result);
		
	}
	
	@Test
	public void test_bazi() throws Exception{
		
		FsOrder order = 	fsOrderDao.findById(100122L);
		if(StringUtils.isBlank(order.getOrderExtraInfo())){
			return;
		}
		JSONArray orderExtraInfoList = JSONArray.parseArray(order.getOrderExtraInfo());
		for(int i =0;i<orderExtraInfoList.size();i++){
			JSONObject info = orderExtraInfoList.getJSONObject(i);
			//计算八字
			if(StringUtils.isNotEmpty(	info.getString("birthDate")  )){
				Date _birthDate = null;
				if( StringUtils.isNotBlank(	info.getString("birthTime")   ) ){
					String birthDate = 	info.getString("birthDate")   + " "+ info.getString("birthTime").split("~")[0];
					_birthDate = CommonUtils.stringToDate(birthDate, CommonUtils.dateFormat4);
				}else{
					_birthDate = CommonUtils.stringToDate(info.getString("birthDate")   , CommonUtils.dateFormat1);
				}
				Bazi bazi = new Bazi(_birthDate);
				System.out.println( bazi.getbazi() );
				info.put("bazi",  bazi.getbazi() );
				Calendar cal = Calendar.getInstance();
				cal.setTime(_birthDate);
				//{1986,1,12,0}
				int obj []= LunarSolarConverter.solarToLunar(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH));
				cal.set(obj[0], obj[1]-1, obj[2]);
		        info.put("nlBirthDateDesc",  CommonUtils.dateToString(cal.getTime(), "yyyy 年 MM 月 dd 日", "") );
			}
		}
		System.out.println( orderExtraInfoList);
	}
	
}
