package test.com.lmy.core.service;

import java.util.Date;

import org.unitils.spring.annotation.SpringBean;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.dao.FsUsrDao;
import com.lmy.core.model.FsMasterInfo;
import com.lmy.core.model.FsUsr;
import com.lmy.core.service.impl.AdminAuditServiceImpl;
import com.lmy.core.service.impl.MasterRecruitServiceImpl;

import test.com.lmy.core.base.BaseTestCase;

public class TestMasterRecruitServiceImpl extends BaseTestCase{
	
	@SpringBean("masterRecruitServiceImpl")
	MasterRecruitServiceImpl masterRecruitServiceImpl;
	@SpringBean("adminAuditServiceImpl")
	AdminAuditServiceImpl adminAuditServiceImpl;
	@SpringBean("fsUsrDao")
	FsUsrDao fsUsrDao;		
	
	public void test_applyMaster(){
		
		Date now = new Date();
		
		FsUsr usr = fsUsrDao.findById(100010l);
		
		FsMasterInfo bean = new FsMasterInfo();
		bean.setUsrId(usr.getId());
		bean.setAchievement("取得成就描述").setAuditStatus("ing").setBirthDate("0220").setBirthYear(1987).setWxOpenId(usr.getWxOpenId())
		.setCertImg1Url("http://imgtu.5011.net/uploads/content/20160818/6305861471485655.jpg")
		.setCertImg2Url("http://imgtu.5011.net/uploads/content/20160818/6305861471485655.jpg").setCertNo("362329198601120614");
		bean.setCertType("0").setContactMobile(usr.getRegisterMobile()).setCreateTime(now);
		bean.setEnglishName("jin").setExperience("民国时期军阀林立，各种势力与江湖帮派汇聚，一个忍受不了山中寂寞的小道士何安下偷偷下山，却阴差阳错地被卷入了乱世中的一场场阴谋。他周旋于军方、帮派和日本人之间，经历了一系列诡异奇幻的人物与事件，却在危难中慢慢悟出了武术的至理与境界，从而人生也随之改变")
		.setSchool("茅山派")  .setGoodAt("驱鬼捉妖")
		.setHeadImgUrl("http://10.10.17.135:8083/data0/headimg/201704/06/jin.png");
		bean.setIsFullTime("Y").setIsSignOther("N").setLiveAddress("茅山").setName("金瘸子").setNickName("金瘸子");
		bean.setProfession("捉妖师").setSex("M").setUpdateTime(now);
		
		JSONObject result = masterRecruitServiceImpl.applyMaster(bean);
		System.out.println(  result );
		}
	
	public void test_masterApplyAudit(){
		JSONObject result =adminAuditServiceImpl.masterApplyAudit(100041l, "approved", "approved");
		System.out.println(  result );
	}
}
