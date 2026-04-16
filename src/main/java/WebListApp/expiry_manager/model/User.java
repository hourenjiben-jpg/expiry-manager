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

@Entity
@Table(name = "users") // SQLの予約後を避けるためテーブル名を指定
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password; // 暗号化された文字が入る
    private String role;     //　USERやADMINなど

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // "ROLE_USER"などの権限をSpring Securityが理解できる形に変換
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override 
    public String getUsername() {
        return username;
    }

    @Override 
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() { return true;}

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

}
