package WebListApp.expiry_manager.Service;

import WebListApp.expiry_manager.model.User;
import WebListApp.expiry_manager.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // データベースからユーザーを探す
        User user = userRepository.findByUsername(username)
             .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + username));

        // Spring Securityが理解できる形式(UserDetails)に変換して返す
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            Collections.emptyList() // 本来はここに権限(ROLE_USERなど)を入れる
        );
    }
    

}
