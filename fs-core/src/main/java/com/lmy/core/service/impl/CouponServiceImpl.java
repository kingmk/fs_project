package com.lmy.core.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.JsonUtils;
import com.lmy.core.dao.FsCouponInstanceDao;
import com.lmy.core.dao.FsCouponTemplateDao;
import com.lmy.core.dao.FsUsrDao;
import com.lmy.core.dao.FsZxCateDao;
import com.lmy.core.model.FsCouponInstance;
import com.lmy.core.model.FsCouponTemplate;
import com.lmy.core.model.FsUsr;
import com.lmy.core.model.FsZxCate;

@Service
public class CouponServiceImpl {
	private static final Logger logger = Logger.getLogger(CouponServiceImpl.class);
	@Autowired
	private FsUsrDao fsUsrDao;
	@Autowired
	private FsZxCateDao fsZxCateDao;
	@Autowired
	private FsCouponTemplateDao fsCouponTemplateDao;
	@Autowired
	private FsCouponInstanceDao fsCouponInstanceDao;
	@Autowired
	private org.springframework.transaction.support.TransactionTemplate fsTransactionTemplate;
	
	public JSONObject getCouponTmplDetail(Long userId, Long templateId) {
		FsCouponTemplate couponTemplate = fsCouponTemplateDao.findById(templateId);
		if (couponTemplate == null) {
			return JsonUtils.commonJsonReturn("0001", "查无此券");
		}
		FsUsr user = fsUsrDao.findById(userId);
		if (user == null) {
			return JsonUtils.commonJsonReturn("0002", "用户信息异常");
		}
		Map<Long, String> cateIdMap = getCateIdMap();
		JSONArray cateIds = JSONArray.parseArray(couponTemplate.getCategories());
		List<String> cateNameList = new ArrayList<>();
		String descCate = "";
		if (cateIds == null || cateIds.size() == 0) {
			descCate = "所有服务通用";
		} else {
			for (int i = 0; i < cateIds.size(); i++) {
				Long cateId = cateIds.getLong(i);
				cateNameList.add(cateIdMap.get(cateId));
			}
			descCate = "适用于"+StringUtils.join(cateNameList, "，");
		}
		String descFetchDate = "" 
				+CommonUtils.dateToString(couponTemplate.getFetchBeginTime(), CommonUtils.dateFormat4, null)+" 至 "
				+CommonUtils.dateToString(couponTemplate.getFetchEndTime(), CommonUtils.dateFormat4, null);
		
		String descValidDate = "";
		List<String> tmpList = new ArrayList<String>();
		if (couponTemplate.getUseDays() != null) {
			tmpList.add("领券后"+couponTemplate.getUseDays()+"天内使用");
		}
		if (couponTemplate.getLastUseTime() != null) {
			tmpList.add("最晚于"+CommonUtils.dateToString(couponTemplate.getLastUseTime(), CommonUtils.dateFormat4, null)+"前使用");
		}
		if (tmpList.size() > 0) {
			descValidDate = StringUtils.join(tmpList, " 并且 ");
		} else {
			descValidDate = "请于"+CommonUtils.dateToString(couponTemplate.getFetchEndTime(), CommonUtils.dateFormat4, null)+"前使用";
		}
		
		Long countUserFetch = fsCouponInstanceDao.countInstance(userId, templateId, null);
		
		String strButton = "立即领取";
		Date now = new Date();
		if (now.getTime() < couponTemplate.getFetchBeginTime().getTime()) {
			strButton = "还未开始，敬请期待！";
		} else if (now.getTime() >= couponTemplate.getFetchEndTime().getTime()) {
			strButton = "已过领取时间，下次赶早哦~";
		} else if (!couponTemplate.getStatus().equals(FsCouponTemplate.STATUS_VALID)) {
			strButton = "当前活动已下架，下次赶早哦~";
		} else if (couponTemplate.getUserLimit()!=null && couponTemplate.getUserLimit()<=countUserFetch) {
			strButton = "您已领取"+countUserFetch+"张，不能再领了~";
		} else if (couponTemplate.getTotalLimit() != null && couponTemplate.getTotalLimit() <= couponTemplate.getCountFetch()) {
			strButton = "券已领完，下次赶早哦~";
		} 
		
		JSONObject body = new JSONObject();
		body.put("id", couponTemplate.getId());
		body.put("name", couponTemplate.getName());
		body.put("discountAmt", couponTemplate.getDiscountAmt());
		body.put("descFetchDate", descFetchDate);
		body.put("descCate", descCate);
		body.put("descValidDate", descValidDate);
		body.put("rules", couponTemplate.getRules().replaceAll("[\n|\r]", "<br/>"));
		body.put("strButton", strButton);
		body.put("isRegistered", ((user != null && user.getRegisterMobile() != null)? "Y":"N"));

		JSONObject result = JsonUtils.commonJsonReturn(); 
		result.put("body", body);
		return result;
	}
	
	public synchronized JSONObject fetchCoupon(final Long userId, final Long templateId) {

		final FsCouponTemplate couponTemplate = fsCouponTemplateDao.findById(templateId);
		if (couponTemplate == null) {
			return JsonUtils.commonJsonReturn("0001", "查无此券");
		}
		FsUsr user = fsUsrDao.findById(userId);
		if (user == null) {
			return JsonUtils.commonJsonReturn("0002", "用户信息异常");
		}

		final Date now = new Date();
		if (now.getTime() < couponTemplate.getFetchBeginTime().getTime()) {
			return JsonUtils.commonJsonReturn("0010", "还未开始，敬请期待！");
		}
		if (now.getTime() >= couponTemplate.getFetchEndTime().getTime()) {
			return JsonUtils.commonJsonReturn("0011", "已过领取时间，下次赶早哦~");
		}
		if (!couponTemplate.getStatus().equals(FsCouponTemplate.STATUS_VALID)) {
			return JsonUtils.commonJsonReturn("0012", "当前活动已下架，下次赶早哦~");
		}
		if (couponTemplate.getUserLimit() != null) {
			Long countUserFetch = fsCouponInstanceDao.countInstance(userId, templateId, null);
			if (countUserFetch >= couponTemplate.getUserLimit()) {
				return JsonUtils.commonJsonReturn("0013", "您已领取"+countUserFetch+"张，不能再领了~");
			}
		}

		final Long countFetch = fsCouponInstanceDao.countInstance(null, templateId, null);
		if (couponTemplate.getTotalLimit() != null) {
			if (countFetch >= couponTemplate.getTotalLimit()) {
				return JsonUtils.commonJsonReturn("0014", "券已领完，下次赶早哦~");
			}
		}
		
		Long countUserUsable = fsCouponInstanceDao.countInstance(userId, null, "Y");
		if (countUserUsable >= 30) {
			return JsonUtils.commonJsonReturn("0015", "不要太贪心哦，您已经有很多优惠券了，请先使用已有的优惠券哦~");
		}
		
		try{
			return 
			fsTransactionTemplate.execute(new TransactionCallback<JSONObject>() {
				@Override
				public JSONObject doInTransaction(TransactionStatus status) {
					Date lastUseTime = (couponTemplate.getLastUseTime()!=null)?
						couponTemplate.getLastUseTime():couponTemplate.getFetchEndTime();
					
					if (couponTemplate.getUseDays() != null) {
						Date tmp = new Date(now.getTime()+couponTemplate.getUseDays()*3600000*24);
						if (tmp.getTime()<lastUseTime.getTime()) {
							lastUseTime = tmp;
						}
					}
					FsCouponInstance couponInstance = new FsCouponInstance();
					couponInstance.setCreateTime(now);
					
					couponInstance.setName(couponTemplate.getName()).setUsrId(userId).setTemplateId(templateId)
						.setLastUseTime(lastUseTime).setType(couponTemplate.getType())
						.setCategories(couponTemplate.getCategories()).setPayAmtMin(couponTemplate.getPayAmtMin())
						.setDiscountAmt(couponTemplate.getDiscountAmt())
						.setStatus(FsCouponInstance.STATUS_UNUSED);
					FsCouponTemplate couponTmplUpdate = new FsCouponTemplate();
					couponTmplUpdate.setId(templateId);
					couponTmplUpdate.setCountFetch(countFetch+1).setUpdateTime(now);
					
					fsCouponInstanceDao.insert(couponInstance);
					fsCouponTemplateDao.update(couponTmplUpdate);
					
					JSONObject result = JsonUtils.commonJsonReturn();
					return result;
				}
			});
		}catch(Exception e){
			logger.error("=====fetch coupon failed, templateId:"+templateId+" =====",e);
			return JsonUtils.commonJsonReturn("9999", "系统错误");
		}
	}
	
	public JSONObject userCouponsNav(Long userId) {
		FsUsr user = fsUsrDao.findById(userId);
		if (user == null) {
			return JsonUtils.commonJsonReturn("0001", "用户信息异常");
		}
		
		Long countUsable = fsCouponInstanceDao.countInstance(userId, null, "Y");

		JSONObject body = new JSONObject();
		body.put("total", countUsable);
		
		JSONObject result = JsonUtils.commonJsonReturn();
		result.put("body", body);
		return result;
	}
	
	public JSONObject getUserCoupons(Long userId, String usable, int page, int pagesize) {
		FsUsr user = fsUsrDao.findById(userId);
		if (user == null) {
			return JsonUtils.commonJsonReturn("0001", "用户信息异常");
		}
		
		List<FsCouponInstance> couponList = fsCouponInstanceDao.findByUser(userId, usable, page, pagesize);

		Map<Long, String> cateIdMap = getCateIdMap();
		JSONArray jCoupons = new JSONArray();
		for (FsCouponInstance coupon : couponList) {
			JSONObject jCoupon = wrapCouponInstance(coupon, cateIdMap, false);
			jCoupons.add(jCoupon);
		}
		
		Long total = fsCouponInstanceDao.countInstance(userId, null, usable);
		
		JSONObject body = new JSONObject();
		body.put("list", jCoupons);
		body.put("total", total);
		
		JSONObject result = JsonUtils.commonJsonReturn();
		result.put("body", body);
		
		return result;
	}
	
	public JSONObject getUserCouponDetail(Long userId, Long couponId) {
		FsUsr user = fsUsrDao.findById(userId);
		if (user == null) {
			return JsonUtils.commonJsonReturn("0001", "用户信息异常");
		}
		FsCouponInstance coupon = fsCouponInstanceDao.findById(couponId);
		if (coupon == null) {
			return JsonUtils.commonJsonReturn("0002", "券不存在");
		}
		if (coupon.getUsrId() != userId) {
			return JsonUtils.commonJsonReturn("0003", "不是当前用户的券");
		}
		FsCouponTemplate couponTemplate = fsCouponTemplateDao.findById(coupon.getTemplateId());
		if (couponTemplate == null) {
			return JsonUtils.commonJsonReturn("0004", "数据异常");
		}
		
		Map<Long, String> cateIdMap = getCateIdMap();
		JSONObject body = wrapCouponInstance(coupon, cateIdMap, true);
		body.put("rules", couponTemplate.getRules().replaceAll("[\n|\r]", "<br/>"));
		
		JSONObject result = JsonUtils.commonJsonReturn();
		result.put("body", body);
		
		return result;
	}
	
	public JSONObject getCouponsForOrder(Long userId, final Long orderPayAmt, final Long cateId) {
		FsUsr user = fsUsrDao.findById(userId);
		if (user == null) {
			return JsonUtils.commonJsonReturn("0001", "用户信息异常");
		}
		if (orderPayAmt == null || cateId == null) {
			return JsonUtils.commonJsonReturn("0002", "参数异常");
		}
		List<FsCouponInstance> couponList = fsCouponInstanceDao.findByUser(userId, "Y", null, null);
		Collections.sort(couponList, new Comparator<FsCouponInstance>() {
			@Override
			public int compare(FsCouponInstance c1, FsCouponInstance c2) {
				if ((c1.getPayAmtMin()<=orderPayAmt 
					&& (c1.getCategories()== null || c1.getCategories().contains("\""+cateId+"\"")))
					&& (c2.getPayAmtMin()>orderPayAmt 
					|| (c2.getCategories() != null && !c2.getCategories().contains("\""+cateId+"\"")))
					) {
					// c1 can be used but c2 cannot be used
					return -1;
				} else if ((c2.getPayAmtMin()<=orderPayAmt 
						&& (c2.getCategories() == null || c2.getCategories().contains("\""+cateId+"\"")))
						&& (c1.getPayAmtMin()>orderPayAmt 
						|| (c1.getCategories() != null && !c1.getCategories().contains("\""+cateId+"\"")))
					) {
					// c1 cannot be used but c2 can be used
					return 1;
				} else if ((c1.getPayAmtMin()<=orderPayAmt 
						&& (c1.getCategories()== null || c1.getCategories().contains("\""+cateId+"\"")))
						&& (c2.getPayAmtMin()<=orderPayAmt 
						&& (c2.getCategories()== null || c2.getCategories().contains("\""+cateId+"\"")))
					) {
					// c1 and c2 all can be used
					if (c1.getDiscountAmt() > c2.getDiscountAmt()) {
						return -1;
					} else if (c1.getDiscountAmt() < c2.getDiscountAmt()) {
						return 1;
					} else {
						if (c1.getLastUseTime().getTime() < c2.getLastUseTime().getTime()) {
							return -1;
						} else if (c1.getLastUseTime().getTime() > c2.getLastUseTime().getTime()) {
							return 1;
						} else {
							JSONArray cateIds1 = JSONArray.parseArray(c1.getCategories());
							if (cateIds1 == null) {
								cateIds1 = new JSONArray();
							}
							JSONArray cateIds2 = JSONArray.parseArray(c2.getCategories());
							if (cateIds2 == null) {
								cateIds2 = new JSONArray();
							}
							if ((cateIds1.size() == 0 && cateIds2.size()>0) 
								|| (cateIds1.size()>cateIds2.size())) {
								// if c1 can be used in more categories
								return 1;
							} else if ((cateIds2.size() == 0 && cateIds1.size()>0) || cateIds2.size()>cateIds1.size()) {
								// if c2 can be used in more categories
								return -1;
							} else {
								return (int) (c1.getId() - c2.getId());
							}
						}
					}
				} else {
					return (int) (c1.getId() - c2.getId());
				}
				
			}
		});

		Map<Long, String> cateIdMap = getCateIdMap();
		JSONArray jCoupons = new JSONArray();
		int countUsable = 0;
		for (FsCouponInstance coupon: couponList) {
			JSONObject jCoupon = wrapCouponInstance(coupon, cateIdMap, true);
			Boolean containsCate = (coupon.getCategories() == null 
					|| coupon.getCategories().contains("\""+cateId+"\""));
			if (coupon.getPayAmtMin() > orderPayAmt || !containsCate) {
				jCoupon.put("useForOrder", "N");
			} else {
				jCoupon.put("useForOrder", "Y");
				countUsable++;
			}
			
			jCoupon.put("containsCate", containsCate?"Y":"N");
			
			jCoupons.add(jCoupon);
		}
		JSONObject body = new JSONObject();
		body.put("list", jCoupons);
		body.put("countUsable", countUsable);

		JSONObject result = JsonUtils.commonJsonReturn();
		result.put("body", body);
		
		return result;
	}

	private Map<Long, String> getCateIdMap() {
		List<FsZxCate> cateList = fsZxCateDao.findZxCate1(null, null, 2l, "N", "EFFECT");
		Map<Long, String> cateIdMap = new HashMap<Long, String>();
		for (FsZxCate cate:cateList) {
			cateIdMap.put(cate.getId(), cate.getName());
		}
		return cateIdMap;
	}

	private JSONObject wrapCouponInstance(FsCouponInstance coupon,
			Map<Long, String> cateIdMap, Boolean isDetail) {
		JSONObject jCoupon = new JSONObject();
		
		JSONArray cateIds = JSONArray.parseArray(coupon.getCategories());
		List<String> cateNameList = new ArrayList<>();
		String descCate = "";
		if (cateIds == null || cateIds.size() == 0) {
			descCate = "平台通用";
		} else {
			for (int i = 0; i < cateIds.size(); i++) {
				Long cateId = cateIds.getLong(i);
				cateNameList.add(cateIdMap.get(cateId));
			}
			if (isDetail) {
				descCate = "限"+StringUtils.join(cateNameList, "、")+"项目使用";
			} else {
				descCate = "限"+StringUtils.join(cateNameList.subList(0, Math.min(2, cateNameList.size())), "、");
				if (cateNameList.size()>2) {
					descCate += "等"+cateNameList.size()+"个项目使用";
				} else {
					descCate += "使用";
				}
			}
		}
		Date now = new Date();
		String descStatus = "可使用";
		if (coupon.getStatus().equals(FsCouponInstance.STATUS_USED)) {
			descStatus = "已使用";
		} else if (now.getTime() >= coupon.getLastUseTime().getTime()) {
			descStatus = "已过期";
		}
		
		String descType = "现金券";

		jCoupon.put("id", coupon.getId());
		jCoupon.put("name", coupon.getName());
		jCoupon.put("descType", descType);
		jCoupon.put("payAmtMin", coupon.getPayAmtMin());
		jCoupon.put("descPayAmtMin", CommonUtils.numberFormat(coupon.getPayAmtMin()/100d, "###,##0.00", ""));
		jCoupon.put("discountAmt", coupon.getDiscountAmt());
		jCoupon.put("descDiscountAmt", CommonUtils.numberFormat(coupon.getDiscountAmt()/100d, "###,##0", ""));
		jCoupon.put("descLastUseTime", CommonUtils.dateToString(coupon.getLastUseTime(), CommonUtils.dateFormat4, null));
		if (coupon.getUsedTime()!= null) {
			jCoupon.put("descUsedTime", CommonUtils.dateToString(coupon.getUsedTime(), CommonUtils.dateFormat4, null));
		}
		jCoupon.put("descCate", descCate);
		jCoupon.put("descStatus", descStatus);
		return jCoupon;
	}

}
