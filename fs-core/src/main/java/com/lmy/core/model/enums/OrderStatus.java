package com.lmy.core.model.enums;

import com.lmy.common.model.base.StrEnum;

public enum OrderStatus implements StrEnum{
	
	/**支付确认中**/										init("init","支付中"),
	/**未支付|交易关闭**/								close("close","未支付"),
	/**支付成功**/											pay_succ("pay_succ","支付成功"),
	/**支付失败**/											pay_fail("pay_fail","支付失败"),
	/**已申请退款待审批中**/							refund_applied("refund_applied","已申请退款待审批中"),
	/**退款中(等待微信最终结果,一般可认为退款成功)**/										refunding("refunding","退款中(等待微信最终结果,一般可认为退款成功)"),
	/**已退款,退款成功**/								refunded("refunded","已退款"),
	/**退款失败**/											refund_fail("refund_fail","退款失败"),
	/**已支付且24小时内有回复**/					completed("completed","已完成"),
	/**结算中（打款给老师）**/						settlementing("settlementing","结算中"),
	/**已结算（打款给老师）**/						settlemented("settlemented","已结算"),
	/**结算失败（打款给老师）**/					settlement_fail("settlement_fail","结算失败");
	
	
	private  String strValue ;
	private String desc;
	OrderStatus(String value,String desc){
		this.strValue = value;
		this.desc = desc;
	}
	@Override
	public String getDesc() {
		return desc;
	}
	@Override
	public String getStrValue() {
		return this.strValue;
	}
	public static OrderStatus value(String strValue){
		OrderStatus  [] jtb   = OrderStatus.values();
		for(OrderStatus  _jtb : jtb){
			if(_jtb.getStrValue().equals(strValue)){
				return _jtb;
			}
		}
		throw new IllegalArgumentException("unknow strValue "+strValue);
	}
}
