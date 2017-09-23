package com.lmy.common.model.base;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base64;

import com.lmy.common.utils.HexUtil;
/**
 * 2015-11-13<br>
 * 产生/获得 RSA 公 私 钥
 * @author fidel
 */
public class FsRsaKey {
	
	private KeyPair keyPair = null;
	/**
	 * 2048 长度
	 * @throws NoSuchAlgorithmException
	 */
	public FsRsaKey() throws NoSuchAlgorithmException{
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA",
				new org.bouncycastle.jce.provider.BouncyCastleProvider());
		final int KEY_SIZE = 2048;// 这个值关系到块加密的大小，可以更改，但是不要太大，否则效率会低
		keyPairGen.initialize(KEY_SIZE, new SecureRandom());
		keyPair = keyPairGen.generateKeyPair();
	}
	public FsRsaKey(int size) throws NoSuchAlgorithmException{
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA",
				new org.bouncycastle.jce.provider.BouncyCastleProvider());
		final int KEY_SIZE = size;// 这个值关系到块加密的大小，可以更改，但是不要太大，否则效率会低
		keyPairGen.initialize(KEY_SIZE, new SecureRandom());
		 keyPair = keyPairGen.generateKeyPair();
	}
	/**
	 * 获得base4 公钥
	 * @return
	 */
	public String getPublicBase64(){
		 byte [] t = 	Base64.encodeBase64(keyPair.getPublic().getEncoded());
		 return org.apache.commons.codec.binary.StringUtils.newString(t, "utf-8");
	}
	/**
	 * 获得16进制公钥
	 * @return
	 */
	public String getPublicHex(){
		return HexUtil.toHexString(keyPair.getPublic().getEncoded());
	}	
	/**
	 * 获得 base4 私钥
	 * @return
	 */
	public String getPrivateBase64(){
		 byte [] t = 	Base64.encodeBase64(keyPair.getPrivate().getEncoded());
		 return org.apache.commons.codec.binary.StringUtils.newString(t, "utf-8");
	}
	/**
	 * 获得 16进制 私钥
	 * @return
	 */
	public String getPrivateHex(){
		return	HexUtil.toHexString(keyPair.getPrivate().getEncoded());
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		FsRsaKey  rsaKey = new FsRsaKey();
		
		System.out.println("公钥:" + rsaKey.getPublicBase64());
		//System.out.println(rsaKey.getPublicHex());
		
		System.out.println("私钥:"+rsaKey.getPrivateBase64());
		//System.out.println(rsaKey.getPrivateHex());
		
	}
}	
