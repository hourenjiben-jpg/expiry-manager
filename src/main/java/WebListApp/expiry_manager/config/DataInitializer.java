package WebListApp.expiry_manager.config;

import WebListApp.expiry_manager.model.User;
import WebListApp.expiry_manager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 【初期データ投入コンポーネント】
 * Spring Boot アプリの起動直後に CommandLineRunner 経由で自動実行されます。
 * H2などのメモリDBや、環境構築直後の空DBに対してテスト用ユーザーを用意するために使用します。
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * アプリ起動完了時に自動実行されるメソッド
     */
    @Override
    public void run(String... args) throws Exception {
        // 1. users テーブルのレコード数をチェック（0件＝初回起動時のみ実行）
        if (userRepository.count() == 0) {
            // 2. 初期管理者/デモユーザーの生成
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
