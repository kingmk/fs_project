package com.lmy.core.model.dto;

import java.io.Serializable;
/**
 * @author fidel
 * @since 2017/07/28
 */
@SuppressWarnings("serial")
public class MasterShortDto1 implements Serializable {
	private Long usrId;
	private String serviceStatus;
	private Double numberSortField1 =0d;
	public Long getUsrId() {
		return usrId;
	}
	public void setUsrId(Long usrId) {
		this.usrId = usrId;
	}
	public String getServiceStatus() {
		return serviceStatus;
	}
	public void setServiceStatus(String serviceStatus) {
		this.serviceStatus = serviceStatus;
	}
	public Double getNumberSortField1() {
		return numberSortField1;
	}
	public void setNumberSortField1(Double numberSortField1) {
		this.numberSortField1 = numberSortField1;
	}
	
}
