package test.com.lmy.core.service;
import org.junit.Test;
import com.lmy.common.redis.RedisClient;
import junit.framework.TestCase;

public class TestRedisClient extends TestCase {
	@Test
	public void test_del(){
		RedisClient.delete("fs_login_test_weixin_openid_30001" , "test_weixin_openid_10001");
	}
	
	public static void main(String[] args) {
		RedisClient.set("key_test", "just a test", 1000000);
		
		String t = (String)RedisClient.get("key_test");
		System.out.println("key value is :"+t);
		
	}
}
