package WebListApp.expiry_manager.repository;

import WebListApp.expiry_manager.model.Item;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // ユーザー専用の全件取得
    List<Item> findByUserUsername(String username, Sort sort);

    // カテゴリとユーザーでの絞り込み取得
    List<Item> findByCategoryAndUserUsername(String category, String username, Sort sort);

    // キーワード検索（前回直したもの）
    List<Item> findByNameContainingIgnoreCaseAndUserUsername(String keyword, String username, Sort sort);

    // 重複のないカテゴリー一覧を取得（Serviceの44行目で使用）
    @Query("SELECT DISTINCT i.category FROM Item i WHERE i.user.username = :username")
    List<String> findAllDistinctCategoriesByUsername(@Param("username") String username);
}