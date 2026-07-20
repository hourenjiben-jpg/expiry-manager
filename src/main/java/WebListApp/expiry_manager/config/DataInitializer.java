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
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setRole("ROLE_USER");

            userRepository.save(admin);
        
            // パスワードを生でログに出すのは開発環境のみの想定
            System.out.println("[INFO] データベースが空のため、初期デモユーザー(admin)を作成しました。");
        }
    }
    
}
