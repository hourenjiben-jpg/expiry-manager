package WebListApp.expiry_manager.repository;

import WebListApp.expiry_manager.model.Item;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * 【リポジトリインターフェース】
 * Item（品目/機器）テーブルに対するデータベース操作（CRUDや検索）を担当します。
 * Spring Data JPA の「JpaRepository」を継承することで、基本的なSQLを自動生成させています。
 */
public interface ItemRepository extends JpaRepository<Item, Long> {

    // ユーザーの全データを並び替え順を指定して取得
    List<Item> findByUserUsername(String username, Sort sort);

    // カテゴリとユーザーでの絞り込み取得
    List<Item> findByCategoryAndUserUsername(String category, String username, Sort sort);

    // キーワードによるあいまい検索 + ユーザー絞り込み
    List<Item> findByNameContainingIgnoreCaseAndUserUsername(String keyword, String username, Sort sort);

    // 重複のないカテゴリー一覧を取得（
    @Query("SELECT DISTINCT i.category FROM Item i WHERE i.user.username = :username")
    List<String> findAllDistinctCategoriesByUsername(@Param("username") String username);
}