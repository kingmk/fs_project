package com.lmy.common.component;
import java.io.IOException;
import java.security.Key;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
public class AES256Encryption {
    private static Logger logger = Logger.getLogger(AES256Encryption.class);

    public static final String KEY_ALGORITHM = "AES";

    public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    public static byte[] encryptKey;
    static {
        try {
            encryptKey = AES256Encryption.getkeyByFile("/opt/p2pproEncrypt/encryptkey/aeskey");
           // encryptKey = Base64.decode(new String(encryptKey));
            encryptKey =  org.apache.commons.codec.binary.Base64.decodeBase64(new String(encryptKey))   ;  //Base64.decode(new String(encryptKey));
        } catch (IOException e) {
        	logger.error(e);
        }
    }

    public static byte[] getkeyByFile(String filename) throws IOException {
    	//TODO 从文件中读取
    	return "cADl9Xe3fYprHzygwf76gnP2ddSOpI2gUq0G+yGSL0w=".getBytes();
    }

    public static byte[] getkey() throws Exception {
        byte[] bb = new byte[] { 112, 0, -27, -11, 119, -73, 125, -118, 107, 31, 60, -96, -63, -2, -6, -126, 115, -10,
                117, -44, -114, -92, -115, -96, 82, -83, 6, -5, 33, -110, 47, 76 };
        return bb;
    }

    public static Key toKey(byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
        return secretKey;
    }

    /**
     * 数据库表中的数据加密使用该方法，返回加密后的字符串
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static String encrypt(String data) throws Exception {
    	//return Base64.encodeBytes(encrypt(data.getBytes(), encryptKey));
       // return Base64.encodeBytes(encrypt(data.getBytes(), encryptKey));
    	return  new String(org.apache.commons.codec.binary.Base64.encodeBase64(encrypt(data.getBytes(), encryptKey)));
    }

    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {

        Key k = toKey(key);
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
        cipher.init(Cipher.ENCRYPT_MODE, k);
        return cipher.doFinal(data);
    }

    /**
     * 数据库表中的数据解密使用该方法，返回解密后的字符串
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static String decrypt(String data) throws Exception {
        String fr = null;
        try {
            fr =   new String(org.apache.commons.codec.binary.Base64.encodeBase64(  org.apache.commons.codec.binary.Base64.decodeBase64(data) ));    //Base64.encodeBytes(Base64.decode(data));
        } catch (Exception e) {
            // System.out.println("没有加过密");
        }
        if (fr != null && fr.equals(data)) {
            //logger.debug("已经密了");

        } else {
            logger.debug("没有加过密");
            return data;
        }
        byte[] dataByte = org.apache.commons.codec.binary.Base64.decodeBase64(data);  //                Base64.decode(data);
        return new String(new String(decrypt(dataByte, encryptKey)));
    }

    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        Key k = toKey(key);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, k);
        return cipher.doFinal(data);
    }

    public static String printHexString(byte[] b) {
        String s = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            s += hex;
        }
        return s;
    }

    /**
     * 
     * @param b byte[]
     * @return String
     */
    public static String Bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex;
        }
        return ret;
    }
    
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        String str_sqlurl =  "jdbc:mysql://127.0.0.1:3306/fs_pro?autoReconnect=true&amp;useUnicode=true&amp;characterEncoding=utf8";
        String str_sqluser = "fs_pro";
        String str_sqlpwd = "mk_king813";
        try {
        	String encrp_sqlurl  = AES256Encryption.encrypt(str_sqlurl);
        	System.out.println("加密后sql url=\n"+encrp_sqlurl );
        	String encrp_sqluser  = AES256Encryption.encrypt(str_sqluser);
        	System.out.println("加密后sql user=\n"+encrp_sqluser );
        	String encrp_sqlpwd  = AES256Encryption.encrypt(str_sqlpwd);
        	System.out.println("加密后sql pwd=\n"+encrp_sqlpwd );
//            String data = AES256Encryption.decrypt(encrp);
//            System.out.println("解密后数据="+data);
        	
//        	encrp = "9vxCb169jIFbQ1f/5VsvJko/XGRdj5doc9MVGgWzUoMJ9N1gZK7Q5NcTGxemP6QqbeDzsh1N+Hf8pmjlT7LNr7rEO+CUrFvrwDqqIDBPAEE8GRTSYqUl3jZb5SIyLD+wTI42KvAuH7rPNKkxKdWZNw==";
        	String data = AES256Encryption.decrypt("xIpTbMuPNwE1p0GD3qpIjw==");
        	System.out.println("解密后数据="+data);
        	
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    
}
