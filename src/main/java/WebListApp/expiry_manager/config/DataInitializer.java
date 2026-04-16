package WebListApp.expiry_manager.config;

import WebListApp.expiry_manager.model.User;
import WebListApp.expiry_manager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // すでにユーザーがいるか確認
        if (userRepository.count() == 0) {
            // いなければ新ユーザー作成
            User admin = new User();
            admin.setUsername("admin");

            //　パスワードをしっかりBCryptで暗号化
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setRole("ROLE_USER");

            userRepository.save(admin);
            System.out.println("初期ユーザーを作成しました: admin / password");
        }
    }
    
}
