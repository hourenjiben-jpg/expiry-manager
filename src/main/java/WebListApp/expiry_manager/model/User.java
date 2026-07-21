package WebListApp.expiry_manager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 【ユーザーエンティティ兼認証モデル】
 * データベースの「users」テーブルに対応するエンティティです。
 * Spring Securityの「UserDetails」を implements することで、
 * このクラス自体をSpring Securityの認証・認可情報として直接使用できるようにしています。
 */
@Entity
@Table(name = "users") // SQLの予約語との衝突を防ぐため、明示的にテーブル名を「users」に指定
public class User implements UserDetails {

    // 1. データベースのカラム定義
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // ログイン用ID（ユーザー名）
    private String password; // 暗号化された文字が入る
    private String role;     //　USERやADMINなど

    // 2. Spring Security (UserDetails) の必須実装メソッド
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(new SimpleGrantedAuthority(role));
    }

    /**
     * ログイン識別子（ユーザー名）を返します。
     */
    @Override 
    public String getUsername() {
        return username;
    }

    /**
     * 暗号化されたパスワードを返します。
     */
    @Override 
    public String getPassword() {
        return password;
    }
    
    /**
     * アカウントの有効期限が切れていないかを返します。（true = 期限切れでない）
     */
    @Override
    public boolean isAccountNonExpired() { return true;}

    /**
     * アカウントがロックされていないかを返します。（true = ロックされていない）
     */
    @Override
    public boolean isAccountNonLocked() { return true; }

    /**
     * パスワード（資格情報）の有効期限が切れていないかを返します。（true = 期限切れでない）
     */
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    /**
     * アカウント自体が有効（有効化）されているかを返します。（true = 有効）
     */
    @Override
    public boolean isEnabled() { return true; }

    // 3. ゲッター・セッター（通常のデータアクセスメソッド）
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

}
