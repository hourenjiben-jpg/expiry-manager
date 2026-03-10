package WebListApp.expiry_manager.repository;

import WebListApp.expiry_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // ユーザー名で検索するためのメソッド（Spring Data JPAが中身を自動生成してくれます）
    Optional<User> findByUsername(String username);
    
}
