package WebListApp.expiry_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 【アプリケーションメイン起動クラス】
 * Spring Boot アプリのスタート地点（エントリーポイント）です。
 */
@SpringBootApplication
@EnableScheduling
public class ExpiryManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpiryManagerApplication.class, args);
	}
}




