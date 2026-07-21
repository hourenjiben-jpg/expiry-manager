package WebListApp.expiry_manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 【Spring Security 全体設定クラス】
 * Webアプリ全体のアクセス制御（認可）、ログイン・ログアウト設定、
 * およびパスワードの暗号化方式を定義する「セキュリティの要」となるクラスです。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * HTTPリクエストに対するセキュリティフィルターチェーンを設定します。
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. アクセス権限（認可）の設定
            .authorizeHttpRequests(auth -> auth
                // 登録画面、ログイン画面、CSSなどの静的ファイルは未ログインでもアクセス可能にする
                .requestMatchers("/register", "/login", "/css/**").permitAll()
                // それ以外のすべてのURL（/items など）はログイン認証を必須にする
                .anyRequest().authenticated()
            )
            // 2. フォームログインの設定
            .formLogin(login -> login
                .loginPage("/login") // 自作のログインページを使う
                .defaultSuccessUrl("/items", true) //ログイン成功後の移動さき
                .permitAll()
            )
            // 3. ログアウトの設定
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

            return http.build();
    }

    /**
     * パスワード暗号化（ハッシュ化）のアルゴリズムを指定する Bean 定義。
     * 強力なハッシュ化アルゴリズムである BCrypt を採用しています。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); 
    }
}
