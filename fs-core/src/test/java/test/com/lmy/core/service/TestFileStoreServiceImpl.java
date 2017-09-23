package test.com.lmy.core.service;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.unitils.spring.annotation.SpringBean;
import com.lmy.core.service.impl.FileStoreServiceImpl;
import test.com.lmy.core.base.BaseTestCase;

public class TestFileStoreServiceImpl extends BaseTestCase {

	@SpringBean("fileStoreServiceImpl")
	FileStoreServiceImpl fileStoreServiceImpl;
	
	public void test_fileStore() throws Exception{
		String host = "106.14.203.236";
		int port = 22;
		String userName = "webadmin";
		String password= "webaDmin%)0518";
		String path = "/data/filedata0/filedata0";
		String fileName = "1.png";
		InputStream input = new FileInputStream(new File("D:/fidel/2017/1.png"));
		fileStoreServiceImpl.sftpUpload(host, port, userName, password, path, fileName, input);
	}
}
