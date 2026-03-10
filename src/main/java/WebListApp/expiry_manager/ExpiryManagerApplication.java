package WebListApp.expiry_manager;

import WebListApp.expiry_manager.model.User;
import WebListApp.expiry_manager.repository.UserRepository;

import java.beans.JavaBean;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ExpiryManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpiryManagerApplication.class, args);
	}


//  アプリ起動時に実行される特別な命令（魔法のコード）
@Bean
CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    return args -> {
		// すでにユーザーがいる場合は何もしない（二重登録防止）
		if (userRepository.count() == 0) {
			User user = new User();
			user.setUsername("admin"); 

			//　パスワードを「生」のまま入れず、暗号化（ハッシュ化）して保存
			user.setPassword(passwordEncoder.encode("password"));

			user.setrole("ROLE_USER");

			userRepository.save(user);
			System.out.println("⭐︎テストユーザーを作成しました: admin / password");
		}
	};
}
}




