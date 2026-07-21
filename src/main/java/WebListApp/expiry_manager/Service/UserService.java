package WebListApp.expiry_manager.Service;

import WebListApp.expiry_manager.model.User;
import WebListApp.expiry_manager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 【ユーザー管理 サービス層】
 * 新規アカウントの登録や、パスワードの安全な暗号化などのビジネスロジックを担当します。
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 新規ユーザーを登録します。
     */
    public void registerUser(String username, String password) {
        // ユーザー名がすでに存在するかチェック
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("このユーザー名はすでに使用されています。");
        }
        
        // 2. 新しい User オブジェクトを作成して値をセット
        User user = new User();
        user.setUsername(username);
        // 3. パスワードのハッシュ化（生パスワードを平文のまま保存するのは絶対NGなため、BCrypt等で不可逆暗号化）
        user.setPassword(passwordEncoder.encode(password));
        // 4. デフォルト権限（一般ユーザー）を付与
        user.setRole("ROLE_USER");
        // 5. DB（users テーブル）へ保存
        userRepository.save(user);
    }
    
}
