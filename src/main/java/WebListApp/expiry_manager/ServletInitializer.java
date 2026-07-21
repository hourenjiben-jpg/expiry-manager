package WebListApp.expiry_manager;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 【外部Webサーバー（Tomcat等）用 初期化クラス】
 * アプリを WAR パッケージとしてビルドし、外部の Servlet サーバーにデプロイして動かす際に、
 * メインの Spring Boot 設定（ExpiryManagerApplication）を読み込ませるための接続役を果たします。
 */
public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ExpiryManagerApplication.class);
	}

}
