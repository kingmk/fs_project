package com.lmy.common.component;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
public class EncryptPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer  {
	   private  static Logger logger = Logger.getLogger(EncryptPropertyPlaceholderConfigurer.class);

	 @Override
	protected String convertProperty(String propertyName, String propertyValue) {
		   logger.debug("propertyName="+propertyName+",propertyValue="+propertyValue);
		   String newPropertyValue = null;
		   if(propertyName!=null && propertyName.startsWith("encrypted.")){      //已经加密，进行解密
               try{
            	   newPropertyValue =  AES256Encryption.decrypt(propertyValue) ;//调用AES进行解密
	                if (newPropertyValue == null) {  //!=null说明正常
	                	  logger.error("Decrypt " + propertyName + "=" + propertyValue + " error!");
	                } 
               }catch(Exception e){
               	logger.error( "解密失败propertyName="+propertyName+",propertyValue="+propertyValue, e);
               }
		   }
		   return super.convertProperty(propertyName, newPropertyValue!=null ? newPropertyValue : propertyValue);
	}
	   
	   @Override
	protected String convertPropertyValue(String originalValue) {
		   logger.debug("originalValue="+originalValue);
		   return super.convertPropertyValue(originalValue);
	}

}
