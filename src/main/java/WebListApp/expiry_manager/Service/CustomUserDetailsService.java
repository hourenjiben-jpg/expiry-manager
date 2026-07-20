package WebListApp.expiry_manager.Service;

import WebListApp.expiry_manager.repository.UserRepository;
import WebListApp.expiry_manager.model.User;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. DBからユーザーを取得
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 2. Spring Security用のUserDetailsオブジェクトを生成して返す
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            AuthorityUtils.createAuthorityList("ROLE_USER") // 空文字ではなく、明確な権限文字列を入れる！
        );
    }
    

}
