package com.lmy.core.service.impl;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.JsonUtils;
import com.lmy.core.dao.FsMasterFansDao;
import com.lmy.core.dao.FsMasterInfoDao;
import com.lmy.core.model.FsMasterInfo;
@Service
public class FollowServiceImpl {
	private static final Logger logger = Logger.getLogger(FollowServiceImpl.class);
	@Autowired
	private FsMasterFansDao fsMasterFansDao;
	@Autowired
	private FsMasterInfoDao fsMasterInfoDao;
	public JSONObject findMyFollow(long followUsrId , int currentPage,int perPageNum){
		try{
			List<Long> masterUsrIds  = fsMasterFansDao.findCurFocusUsrIdsByFollowUsrId(followUsrId, currentPage, perPageNum);
			if(CollectionUtils.isEmpty(masterUsrIds)){
				return JsonUtils.commonJsonReturn("1000", "查无数据");
			}
			List<FsMasterInfo> masterInfoList = 	fsMasterInfoDao.findByUsrIds2(masterUsrIds, "approved", null);
			if(CollectionUtils.isEmpty(masterInfoList)){
				return JsonUtils.commonJsonReturn("1000", "数据错误|查无数据");
			}
			JSONArray dataList = new JSONArray();
			for(FsMasterInfo masterInfo :  masterInfoList){
				JSONObject dataOne = new JSONObject(true);
				dataOne.put("masterNickName",  masterInfo!=null ? ( StringUtils.isNotEmpty( masterInfo.getNickName() ) ? masterInfo.getNickName() : masterInfo.getName()  )  : "" );
				dataOne.put("masterHeadImgUrl",  masterInfo!=null ? ( masterInfo.getHeadImgUrl() )  : null );
				dataOne.put("masterInfoId", masterInfo.getId());
				dataOne.put("masterUsrId", masterInfo.getUsrId());
				dataOne.put("workBeginDate", masterInfo.getWorkDate());  // 工作起始日期
				dataOne.put("workYearStr",  UsrAidUtil.getWorkYearStr(masterInfo.getWorkDate()) );  // 从业年限
				dataOne.put("isTranSecuried", "Y"); //是否担保交易
				dataOne.put("isCertificated", masterInfo.getCertNo()!=null ? "Y" :"N");  //是否实行认证
				dataOne.put("isSignOther", masterInfo.getIsSignOther());  // 是否 在其他平台签约 Y(非独家);N(独家)
				dataList.add(dataOne);
			}
			JSONObject result = JsonUtils.commonJsonReturn();
			JsonUtils.setBody(result, "data", dataList);
			JsonUtils.setBody(result, "size", CollectionUtils.isEmpty(dataList) ? 0 : dataList.size());
			return result;
		}catch(Exception e){
			logger.error("followUsrId:"+followUsrId+"  ,currentPage:"+currentPage+", perPageNum:"+perPageNum,e);
			return JsonUtils.commonJsonReturn("9999","系统错误");
		}
	}
	/** 取消关注 **/
	public JSONObject followCancel(long loginUsrId , long focusUsrId){
		try{
			int effectNum = this.fsMasterFansDao.unfollow(null, loginUsrId, focusUsrId);	
			logger.info("loginUsrId:"+loginUsrId+"  ,focusUsrId:"+focusUsrId+",effectNum:"+effectNum);
			return JsonUtils.commonJsonReturn("0000","已取消关注");
		}catch(Exception e){
			logger.error("loginUsrId:"+loginUsrId+"  ,focusUsrId:"+focusUsrId,e);
			return JsonUtils.commonJsonReturn("9999","系统错误");
		}
	}
	
	/** 添加关注 **/
	public JSONObject followl(Long id, long loginUsrId , long focusUsrId){
		try{
			if(loginUsrId == focusUsrId){
				logger.info("不能关注自己个  id:"+id+"  ,loginUsrId:"+loginUsrId+",focusUsrId:"+focusUsrId);
				return JsonUtils.commonJsonReturn("0001","不能关注自己");
			}
			fsMasterFansDao.follow(id, loginUsrId, focusUsrId);
			return JsonUtils.commonJsonReturn();
		}catch(Exception e){
			logger.error("loginUsrId:"+loginUsrId+"  ,focusUsrId:"+focusUsrId,e);
			return JsonUtils.commonJsonReturn("9999","系统错误");
		}
	}
	
}
