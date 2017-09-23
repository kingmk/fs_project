package com.lmy.core.dao ;
import org.springframework.stereotype.Component;
import com.lmy.core.model.FsWeixinMsgPushRecord;
@Component
public class FsWeixinMsgPushRecordDao extends GenericDAOImpl<FsWeixinMsgPushRecord> {
	@Override
	public String getNameSpace() {
		return "fs_weixin_msg_push_record";
	}
}
