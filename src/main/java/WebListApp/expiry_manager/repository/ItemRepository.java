                package WebListApp.expiry_manager.repository;

                import org.springframework.data.jpa.repository.JpaRepository;  // こいつのおかげでCRUD操作を使えるようになる
                import org.springframework.data.jpa.repository.Query;
                import WebListApp.expiry_manager.model.Item;  // Entityクラスの Itemをインポート
                import java.util.List;

                public interface ItemRepository extends JpaRepository<Item, Long> {
                    List<Item> findByNameContainingIgnoreCase(String keyword);

                

                    // 全てのカテゴリーを取得
                    @Query("SELECT DISTINCT i.category FROM Item i WHERE i.category IS NOT NULL ORDER BY i.category")
                    List<String> findAllDistinctCategories();

                    // カテゴリーでフィルター
                    List<Item> findByCategory(String category);
                   
               
                    
                    
                }
                // ItemRepositoryはjavaのオブジェクト指向の世界とデータベースのSQLの世界を結びつける重要な橋渡し役を果たす
                // CRUD操作（データの作成・読み取り・更新・削除）を専門におこうなう
                // JpaRepository<Item, Long>こいつを継承するだけで更新・検索・全権取得・削除という基本的な機能を実装してくれる



