package WebListApp.expiry_manager.repository;

import WebListApp.expiry_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * 【ユーザーリポジトリインターフェース】
 * users テーブルに対するデータベース操作を担当します。
 * 主に Spring Security のログイン認証時や、ユーザー登録時の重複チェック等で使用されます。
 */
public interface UserRepository extends JpaRepository<User, Long> {
    // ユーザー名で検索するためのメソッド
    Optional<User> findByUsername(String username);
    
}
