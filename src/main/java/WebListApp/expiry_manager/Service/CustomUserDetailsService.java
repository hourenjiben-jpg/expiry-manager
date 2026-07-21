package WebListApp.expiry_manager.Service;

import WebListApp.expiry_manager.repository.UserRepository;
import WebListApp.expiry_manager.model.User;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 【Spring Security 専用 ユーザー詳細取得サービス】
 * UserDetailsService インターフェースを実装し、
 * ログイン時に Spring Security が DB からユーザー情報を安全に読み出すための bridge（架け橋）となります。
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * ログイン画面で入力された username をもとに DB を検索し、
     * Spring Security が理解できる UserDetails オブジェクトを作成して返します。
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. DBからユーザーを取得
        // 存在しない場合は、Spring Security 専用の UsernameNotFoundException 例外を投げて認証失敗にする
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
