package WebListApp.expiry_manager.Service;

import WebListApp.expiry_manager.model.User;
import WebListApp.expiry_manager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(String username, String password) {
        // ユーザー名がすでに存在するかチェック
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("このユーザー名はすでに使用されています。");
        }
        
        User user = new User();
        user.setUsername(username);
        // パスワードを暗号化
        user.setPassword(passwordEncoder.encode(password));
        //　デフォルトの権限設定
        user.setRole("ROLE_USER");

        userRepository.save(user);
    }
    
}
