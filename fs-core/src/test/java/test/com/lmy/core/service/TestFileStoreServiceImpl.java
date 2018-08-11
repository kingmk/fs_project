package test.com.lmy.core.service;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.unitils.spring.annotation.SpringBean;

import test.com.lmy.core.base.BaseTestCase;

import com.lmy.core.service.impl.FileStoreServiceImpl;

public class TestFileStoreServiceImpl extends BaseTestCase {

	@SpringBean("fileStoreServiceImpl")
	FileStoreServiceImpl fileStoreServiceImpl;
	
	public void test_fileStore() throws Exception{
		String host = "139.196.229.200";
		int port = 22;
		String userName = "fs_pro";
		String password= "mk_king813";
		String path = "/data/filedata0/filedata0";
		String fileName = "face.jpg";
		InputStream input = new FileInputStream(new File("/Users/yuxinjin/Desktop/face.jpg"));
		boolean rlt = fileStoreServiceImpl.sftpUpload(host, port, userName, password, path, fileName, input);
		System.out.println(rlt);
	}
}
