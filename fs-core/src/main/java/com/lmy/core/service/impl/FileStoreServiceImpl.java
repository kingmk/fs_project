package com.lmy.core.service.impl;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.JsonUtils;
import com.lmy.common.model.base.StrEnum;
import com.lmy.core.dao.FsFileStoreDao;
import com.lmy.core.model.FsFileStore;
@Service
public class FileStoreServiceImpl {
	
	private static final Logger logger = Logger.getLogger(FileStoreServiceImpl.class);
	@Value("${fs.file.store.path.prefix}")
	private String sftpPathPrefix;
	@Value("${fs.file.store.sftp.service.host}")
	private String sftpServerHost;
	@Value("${fs.file.store.sftp.service.port}")
	private Integer sftpServerPort;	
	@Value("${encrypted.fs.file.store.sftp.account}")
	private String sftpServerAccount;	
	@Value("${encrypted.fs.file.store.sftp.passwd}")
	private String sftpServerPasswd;	
	@Value("${fs.file.http.server.url}")
	private String fsfileHttpServerUrl;
	@Autowired
	private FsFileStoreDao fsFileStoreDao;
	
	private final int cropbound = 500;
	/** 固定短路劲 **/
	private static final String shortSuffix = "/filedata0";
	public enum FileType implements StrEnum {
		/**头像 **/		HEADIMG("headimg","头像"),
		/**证件文件**/	AUDIT("audit","证件文件"),
		/**聊天文件**/ CHAT("chat","聊天文件"),
		/**其他文件 **/OTHER("file","其他文件");
		private  String strValue;
		private String desc;
		FileType(String strValue, String desc){
			this.strValue = strValue;
			this.desc = desc;
		}
		@Override
		public String getStrValue() {
			return this.strValue;
		}
		@Override
		public String getDesc() {
			return this.desc;
		}
	}
	
	private String getFileSuffixCatalog(FileType fileType){
		Date now = new Date();
		String tmp = "{0}/{1}/{2}";
		String middelCatalogName = CommonUtils.dateToString(now, "YYYYMM", "201712") + "/" + CommonUtils.dateToString(now, "dd", "01");
		return MessageFormat.format(tmp, shortSuffix ,	fileType.getStrValue(),	middelCatalogName);
	}
	private String getNewFileName(CommonsMultipartFile file ){
		String fileSuffixName = getFileSuffixName(file);
		return   UUID.randomUUID().toString().replaceAll("-", "") +"." + fileSuffixName;
	}
	/** 获取文件后缀 eg : png **/
	private String getFileSuffixName(CommonsMultipartFile file){
		String fileSuffixName = file.getOriginalFilename();
		int index = fileSuffixName.lastIndexOf(".");
		return fileSuffixName.substring(index+1);
	}
	
	private static Session sshSession;
	
	private  ChannelSftp connect(String host, int port, String userName, String password) throws JSchException {
		try{
			ChannelSftp sftp = null;
			JSch jsch = new JSch();
			if(sshSession == null){
				sshSession =  jsch.getSession(userName, host, port);
				sshSession.setPassword(password);
				Properties sshConfig = new Properties();
				sshConfig.put("StrictHostKeyChecking", "no");
				sshSession.setConfig(sshConfig);
				sshSession.connect();
			}
			com.jcraft.jsch.Channel channel = sshSession.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
			return sftp;
		}catch(Exception e){
			try{
				if(sshSession!=null){
					sshSession.disconnect();	
				}
			}catch(Exception e1){
				logger.error("", e1);
			}
			if(sshSession!=null){
				sshSession = null;	
			}
			logger.error("", e);
			return null;
		}
	}
	
	/**
	 * SFTP上传
	 * 
	 * @param host服务器IP或者域名
	 * @param port服务器端口
	 * @param userName用户名
	 * @param password密码
	 * @param path目录
	 * @param fileName文件名
	 * @param input输入流
	 * @return
	 * @throws JSchException
	 * @throws SftpException
	 */
	public boolean sftpUpload(String host, int port, String userName, String password, String path, String fileName, InputStream input) throws Exception {
		ChannelSftp sftp = null;
		try{
			sftp = connect(host, port, userName, password);
			createDirIFNotExistAndCd(path, sftp);
			sftp.put(input, fileName);
			return true;			
		}catch(Exception e){
			logger.error("", e);
			return false;
		}finally{
			if(sftp!=null){
				sftp.disconnect();
			}
		}
	}
	
	/** 
	  * 创建一个文件目录 
	  */  
	private void createDirIFNotExistAndCd(String createpath, ChannelSftp sftp)throws Exception {
		if (isDirExist(createpath, sftp)) {
			sftp.cd(createpath);
		}
		String pathArry[] = createpath.split("/");
		StringBuffer filePath = new StringBuffer("/");
		for (String path : pathArry) {
			if (path.equals("")) {
				continue;
			}
			filePath.append(path + "/");
			if (isDirExist(filePath.toString(), sftp)) {
				sftp.cd(filePath.toString());
			} else {
				// 建立目录
				sftp.mkdir(filePath.toString());
				// 进入并设置为当前目录
				sftp.cd(filePath.toString());
			}
		}
		sftp.cd(createpath);
	}  

	private boolean isDirExist(String directory, ChannelSftp sftp)throws Exception {
		boolean isDirExistFlag = false;
		try {
			SftpATTRS sftpATTRS = sftp.lstat(directory);
			isDirExistFlag = true;
			return sftpATTRS.isDir();
		} catch (Exception e) {
			if (e.getMessage().toLowerCase().equals("no such file")) {
				isDirExistFlag = false;
			}
		}
		return isDirExistFlag;
	} 
	
	//private void fileSftpStore(CommonsMultipartFile file, String fileCatalog , String fileName) throws Exception{
		//if (file != null && file.getSize()>0) {
			//fileSftpStore(file.getInputStream(), fileCatalog, fileName);
		//}
	//}
	
	private void _fileSftpStore(InputStream inputStream, String fileCatalog , String fileName) throws Exception{
		try{
			if (inputStream != null) {
				sftpUpload(sftpServerHost, sftpServerPort, sftpServerAccount, sftpServerPasswd, fileCatalog, fileName, inputStream);
			}
			logger.info("文件存储成功:文件目录 "+fileCatalog+"/"+fileName);
		}catch(Exception e){
			logger.error("文件存储错误:"+fileCatalog+"/"+fileName, e);
			throw e;
		}
	}

	public FsFileStore fileStore(CommonsMultipartFile file, FileType fileType , Long uploadUsrId) {
		if(file == null || file.getSize()<1l){
			return null;
		}
		//文件后缀 eg :  png;jpeg
		String   suffixName = this.getFileSuffixName(file);
		Date now = new Date();
		String fileName = this.getNewFileName(file);
		String fileSuffixCatalog = this.getFileSuffixCatalog(fileType);
		FsFileStore fileStore = new FsFileStore();
		fileStore.setUpdateTime(now).setCreateTime(now);
		fileStore.setExpiryEndTime( DateUtils.addYears(now, 10) ).setFileName(fileName).setFilePathPrefix(sftpPathPrefix).setSuffixName(suffixName)
		.setFilePathSuffix(fileSuffixCatalog+"/"+fileName);
		
		InputStream is = null;
		BufferedImage imageDst = null;
		long size = file.getSize();
		if(fileIsImg(suffixName)){
			if (fileType.getStrValue().equals("headimg")) {
				try {
					imageDst = this.cropHeadImg(file.getInputStream());
				} catch (IOException e) {
					e.printStackTrace();
					imageDst = null;
				}
			}
			if (imageDst != null) {
				fileStore.setHeight(imageDst.getHeight()).setWidth(imageDst.getWidth());
//				System.out.println("image width:"+imageDst.getWidth()+", image height:"+imageDst.getHeight());
			} else {
				JSONObject imgSizeResult =  getImgHeightAndSize(file);
				if(JsonUtils.equalDefSuccCode(imgSizeResult)){
					//设置size
					fileStore.setHeight( (Integer)JsonUtils.getBodyValue(imgSizeResult, "height")  )
					.setWidth( (Integer)JsonUtils.getBodyValue(imgSizeResult, "width")  );
				}
			}
			
		}
		try {
			if (imageDst != null) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ImageIO.write((RenderedImage) imageDst, "jpg", bos);
				byte fileByte [] = bos.toByteArray();
				size = fileByte.length;
				is = new ByteArrayInputStream(fileByte);
			} else {
				is = file.getInputStream();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("文件size:"+size/1024+"kb");
		fileStore.setFileSize(size).setHttpsUrl(null).setHttpUrl(null)
		.setOriginalFileName(  file.getOriginalFilename()).setStatus("effect")
		.setUsrId(uploadUsrId).setHttpUrl( this.fsfileHttpServerUrl + fileStore.getFilePathSuffix()  );
		try{
			 this._fileSftpStore(is, sftpPathPrefix +fileSuffixCatalog, fileName);
			 this.fsFileStoreDao.insert(fileStore);
			 is.close();
			 return fileStore;
		}catch(Exception e){
			logger.error("文件存储出错:", e);
			return null;
		}
	}
	
	private JSONObject getImgHeightAndSize(CommonsMultipartFile file ){
		try{
			String suffixName = getFileSuffixName(file);
			if(fileIsImg(suffixName)){
				BufferedImage sourceImg =ImageIO.read(file.getInputStream());   
				JSONObject result = JsonUtils.commonJsonReturn();
				JsonUtils.setBody(result, "width", sourceImg.getWidth());
				JsonUtils.setBody(result, "height", sourceImg.getHeight());
				return result;
			}else{
				return JsonUtils.commonJsonReturn("0001","不是图片");
			}
		}catch(Exception e){
			logger.error("", e);
			return JsonUtils.commonJsonReturn("0001","不是图片");
		}
	}
	private boolean fileIsImg(String   suffixName){
		//常见图片格式
		String  [] imgsSuffixName = new String[]{"BMP","JPG","JPEG" ,"PNG","GIF" ,"PCX","TIFF","TGA","EXIF","FPX","SVG","PSD","CDR","PCD","DXF","UFO","EPS","HDRI","AI","RAW"};
		return ArrayUtils.contains(imgsSuffixName, suffixName.toUpperCase());
	}
	
	private BufferedImage cropHeadImg(InputStream fis) throws IOException {
		BufferedImage imageSrc = ImageIO.read(fis);
		int wSrc = imageSrc.getWidth();
		int hSrc = imageSrc.getHeight();
		int boundDst = cropbound;
		int xs0 = 0, ys0 =0, xs1 = wSrc, ys1 = hSrc;
		if (wSrc > hSrc) {
			boundDst = Math.min(hSrc, cropbound);
			xs0 = (wSrc-hSrc)/2;
			xs1 = xs0+hSrc;
		} else {
			boundDst = Math.min(wSrc, cropbound);
			ys0 = (hSrc-wSrc)/2;
			ys1 = ys0+wSrc;
		}
		BufferedImage imageDst = new BufferedImage(boundDst, boundDst, BufferedImage.TYPE_INT_RGB);
		imageDst.getGraphics().drawImage(imageSrc, 0, 0, boundDst, boundDst, xs0, ys0, xs1, ys1, null);
		return imageDst;
	}
	
}
