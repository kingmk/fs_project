package test.com.lmy.core.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.unitils.spring.annotation.SpringBean;

import com.lmy.core.dao.FsUsrDao;
import com.lmy.core.dao.FsZxCateDao;
import com.lmy.core.model.FsUsr;
import com.lmy.core.model.FsZxCate;

import test.com.lmy.core.base.BaseTestCase;

public class TestFsUsrDao extends BaseTestCase {

	@SpringBean("fsUsrDao")
	FsUsrDao fsUsrDao;
	
	@SpringBean("fsZxCateDao")
	FsZxCateDao fsZxCateDao;
	
	public void test_insert(){
		
		Date now = new Date();
		List<FsUsr> list = new ArrayList<FsUsr>();
		for(int i=0;i<100;i++){
			FsUsr usr1 = new FsUsr();
			usr1.setBirthDate(null).setBirthYear(null).setFamilyRank(null).setMarriageStatus(null).setRealName(null).setWxOpenId("test_weixin_openid_"+10006+i)
			.setSex(null).setUsrCate(null).setUpdateTime(new Date() ).setCreateTime(now);
			list.add(usr1);
		}
		
		fsUsrDao.batchInsert(list);
		
	}
	
	
	public void test_findById(){
		Long id = 100000l;
		System.out.println(  fsUsrDao.findById(id) ) ;
	}
	
	
	public void test_initFsCate(){
		Date now = new Date();
		Long createUsrId = 10000l;
		Long suggestAmt = 1200l;
		Long sort = 10l;
		 FsZxCate top1 = new FsZxCate();
		 FsZxCate top2 = new FsZxCate();
		 FsZxCate top3 = new FsZxCate();
		 FsZxCate top4 = new FsZxCate();
		 FsZxCate top5 = new FsZxCate();
		 top1.setLevel(1l).setName("预测").setDescription(null).setSuggestAmt(suggestAmt).setSort(sort).setStatus("EFFECT").setUsrDefined("N").setCreateUsrId(createUsrId).setUpdateTime(now).setCreateTime(now);
		 top1.setId(10000l);
		 
		 top2.setLevel(1l).setName("择吉").setDescription(null).setSuggestAmt(suggestAmt).setSort(sort).setStatus("EFFECT").setUsrDefined("N").setCreateUsrId(createUsrId).setUpdateTime(now).setCreateTime(now);
		 top2.setId(10001l);
		 
		 top3.setLevel(1l).setName("起名").setDescription(null).setSuggestAmt(suggestAmt).setSort(sort).setStatus("EFFECT").setUsrDefined("N").setCreateUsrId(createUsrId).setUpdateTime(now).setCreateTime(now);
		 top3.setId(10003l);
		 
		 top4.setLevel(1l).setName("堪舆").setDescription(null).setSuggestAmt(suggestAmt).setSort(sort).setStatus("EFFECT").setUsrDefined("N").setCreateUsrId(createUsrId).setUpdateTime(now).setCreateTime(now);
		 top4.setId(10004l);
		 
		 top5.setLevel(1l).setName("用户自定义").setDescription(null).setSuggestAmt(suggestAmt).setSort(sort).setStatus("EFFECT").setUsrDefined("N").setCreateUsrId(createUsrId).setUpdateTime(now).setCreateTime(now);
		 top5.setId(30000l);
		 
		 List<FsZxCate> list = new ArrayList<FsZxCate>();
		 list.add(top1);
		 list.add(top2);
		 list.add(top3);
		 list.add(top4);
		 list.add(top5);
		 FsZxCate cate1_1 = new FsZxCate();
		 FsZxCate cate1_2 = new FsZxCate();
		 FsZxCate cate1_3 = new FsZxCate();
		 FsZxCate cate1_4 = new FsZxCate();
		 
		 cate1_1.setCreateUsrId(createUsrId).setDescription("流年运势 一句话描述").setLevel(2l).setName("流年运势").setParentId(top1.getId()).setParentName(top1.getName()).setSort(sort).setStatus("EFFECT").setSuggestAmt(suggestAmt)
		 .setUpdateTime(now).setUsrDefined("N").setCreateTime(now);

		 cate1_2.setCreateUsrId(createUsrId).setDescription("婚恋感情 一句话描述").setLevel(2l).setName("婚恋感情").setParentId(top1.getId()).setParentName(top1.getName()).setSort(sort).setStatus("EFFECT").setSuggestAmt(suggestAmt)
		 .setUpdateTime(now).setUsrDefined("N").setCreateTime(now);
		 
		 cate1_3.setCreateUsrId(createUsrId).setDescription("健康事业财运 一句话描述").setLevel(2l).setName("健康事业财运").setParentId(top1.getId()).setParentName(top1.getName()).setSort(sort).setStatus("EFFECT").setSuggestAmt(suggestAmt)
		 .setUpdateTime(now).setUsrDefined("N").setCreateTime(now);
		 
		 cate1_4.setCreateUsrId(createUsrId).setDescription("命运祥批 一句话描述").setLevel(2l).setName("命运祥批").setParentId(top1.getId()).setParentName(top1.getName()).setSort(sort).setStatus("EFFECT").setSuggestAmt(suggestAmt)
		 .setUpdateTime(now).setUsrDefined("N").setCreateTime(now);
		 
		 list.add(cate1_1);
		 list.add(cate1_2);
		 list.add(cate1_3);
		 list.add(cate1_4);
		 
		 
		 FsZxCate cate2_1 = new FsZxCate();
		 FsZxCate cate2_2 = new FsZxCate();
		 FsZxCate cate2_3 = new FsZxCate();
		 FsZxCate cate2_4 = new FsZxCate();
		 cate2_1.setCreateUsrId(createUsrId).setDescription("结婚吉日 一句话描述").setLevel(2l).setName("结婚吉日").setParentId(top2.getId()).setParentName(top2.getName()).setSort(sort).setStatus("EFFECT").setSuggestAmt(suggestAmt)
		 .setUpdateTime(now).setUsrDefined("N").setCreateTime(now);
		 
		 cate2_2.setCreateUsrId(createUsrId).setDescription("择日生子 一句话描述").setLevel(2l).setName("择日生子").setParentId(top2.getId()).setParentName(top2.getName()).setSort(sort).setStatus("EFFECT").setSuggestAmt(suggestAmt)
		 .setUpdateTime(now).setUsrDefined("N").setCreateTime(now);
		 
		 cate2_3.setCreateUsrId(createUsrId).setDescription("开张开市 一句话描述").setLevel(2l).setName("开张开市").setParentId(top2.getId()).setParentName(top2.getName()).setSort(sort).setStatus("EFFECT").setSuggestAmt(suggestAmt)
		 .setUpdateTime(now).setUsrDefined("N").setCreateTime(now);
		 
		 cate2_4.setCreateUsrId(createUsrId).setDescription("乔迁择日").setLevel(2l).setName("乔迁择日").setParentId(top2.getId()).setParentName(top2.getName()).setSort(sort).setStatus("EFFECT").setSuggestAmt(suggestAmt)
		 .setUpdateTime(now).setUsrDefined("N").setCreateTime(now);
		 
		 list.add(cate2_1);
		 list.add(cate2_2);
		 list.add(cate2_3);
		 list.add(cate2_4);
		 FsZxCate cate3_1 = new FsZxCate();
		 FsZxCate cate3_2 = new FsZxCate();
		 FsZxCate cate3_3 = new FsZxCate();
		 
		 cate3_1.setCreateUsrId(createUsrId).setDescription("个人改名 一句话描述").setLevel(2l).setName("个人改名").setParentId(top3.getId()).setParentName(top3.getName()).setSort(sort).setStatus("EFFECT").setSuggestAmt(suggestAmt)
		 .setUpdateTime(now).setUsrDefined("N").setCreateTime(now);

		 cate3_2.setCreateUsrId(createUsrId).setDescription("个人起名 一句话描述").setLevel(2l).setName("个人起名").setParentId(top3.getId()).setParentName(top3.getName()).setSort(sort).setStatus("EFFECT").setSuggestAmt(suggestAmt)
		 .setUpdateTime(now).setUsrDefined("N").setCreateTime(now);

		 
		 cate3_3.setCreateUsrId(createUsrId).setDescription("公司起名 一句话描述").setLevel(2l).setName("公司起名").setParentId(top3.getId()).setParentName(top3.getName()).setSort(sort).setStatus("EFFECT").setSuggestAmt(suggestAmt)
		 .setUpdateTime(now).setUsrDefined("N").setCreateTime(now);
		 list.add(cate3_1);
		 list.add(cate3_2);
		 list.add(cate3_3);
		 
		 FsZxCate cate4_1 = new FsZxCate();
		 FsZxCate cate4_2 = new FsZxCate();
		 cate4_1.setCreateUsrId(createUsrId).setDescription("办公风水 一句话描述").setLevel(2l).setName("办公风水").setParentId(top4.getId()).setParentName(top4.getName()).setSort(sort).setStatus("EFFECT").setSuggestAmt(suggestAmt)
		 .setUpdateTime(now).setUsrDefined("N").setCreateTime(now);
		 cate4_2.setCreateUsrId(createUsrId).setDescription("住宅风水 一句话描述").setLevel(2l).setName("住宅风水").setParentId(top4.getId()).setParentName(top4.getName()).setSort(sort).setStatus("EFFECT").setSuggestAmt(suggestAmt)
		 .setUpdateTime(now).setUsrDefined("N").setCreateTime(now);
		 
		 list.add(cate4_1);
		 list.add(cate4_2);
		 fsZxCateDao.batchInsert(list);
		 
	}
	
	
}
