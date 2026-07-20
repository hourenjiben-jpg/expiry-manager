package WebListApp.expiry_manager.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import WebListApp.expiry_manager.model.Item;
import WebListApp.expiry_manager.model.User;
import WebListApp.expiry_manager.repository.ItemRepository;
import WebListApp.expiry_manager.repository.UserRepository;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional // データの追加・更新・削除を行うため、クラス全体、または各メソッドにトランザクションを付与します
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    // 1. データ取得・絞り込み・ソート
    // 条件（カテゴリ、ソート）に合わせて、ログインユーザーのアイテム一覧を取得
    @Transactional(readOnly = true)
    public List<Item> findItems(String username, String category, String sortKey) {
        // 1. 画面からのソートキーを、Spring Data JPAのSortオブジェクトに変換
        Sort sort = resolveSort(sortKey);

        // 2. カテゴリ指定がない、または「all」の場合は全件取得（ユーザー限定）
        if (category == null || category.isEmpty() || "all".equals(category)) {
            return itemRepository.findByUserUsername(username, sort);
        }
        
        // 3. カテゴリが指定されている場合は、カテゴリとユーザーで絞り込んで取得
        return itemRepository.findByCategoryAndUserUsername(category, username, sort);
    }

    // ユーザーが登録した重複のないカテゴリー一覧を取得
    @Transactional(readOnly = true)
    public List<String> findDistinctCategoriesByUsername(String username) {
        return itemRepository.findAllDistinctCategoriesByUsername(username);
    }

    // ユーザー専用のキーワード検索
    @Transactional(readOnly = true)
    public List<Item> searchItemsByKeywordAndUsername(String keyword, String username) {
        Sort sort = Sort.by(Sort.Direction.ASC, "expiryDate"); // 検索時もデフォルトは期限順
        if (keyword == null || keyword.isEmpty()) {
            return itemRepository.findByUserUsername(username, sort);
        }
        return itemRepository.findByNameContainingIgnoreCaseAndUserUsername(keyword, username, sort);
    }

    // 2. セキュリティ担保付きのCUD操作（登録・更新・削除）
    // 新規登録
    public void saveItem(Item item, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        item.setUser(user);
        
        itemRepository.save(item);
    }

    // 編集画面用：指定されたIDが「本当に自分のデータか」を確認して取得
    @Transactional(readOnly = true)
    public Item findByIdAndUsername(Long id, String username) {
        Item item = itemRepository.findById(id).orElse(null);
        if (item != null && item.getUser().getUsername().equals(username)) {
            return item;
        }
        return null; // 他人のデータ、または存在しない場合はnullを返してコントローラー側でリダイレクトさせる
    }

    // 既存データの更新（所有者チェック付き）
    public void updateItem(Item updatedItem, String username) {
        Item existingItem = itemRepository.findById(updatedItem.getId()).orElse(null);
        
        // 防衛線：DBにデータが存在し、かつ登録者がログインユーザーと一致する場合のみ上書き
        if (existingItem != null && existingItem.getUser().getUsername().equals(username)) {
            // ユーザー情報が画面から送られてこない（nullになる）のを防ぐため、既存のユーザーを再セット
            updatedItem.setUser(existingItem.getUser());
            itemRepository.save(updatedItem);
        }
    }

    // 使用済み（used）の切り替え（所有者チェック付き）
    public void toggleUsedStatus(Long id, String username) {
        Item item = itemRepository.findById(id).orElse(null);
        if (item != null && item.getUser().getUsername().equals(username)) {
            item.setUsed(!item.getUsed());
            itemRepository.save(item); // @Transactionalがあるので自動保存されますが、明示的にも書けます
        }
    }

    // 削除（所有者チェック付き）
    public void deleteItem(Long id, String username) {
        Item item = itemRepository.findById(id).orElse(null);
        if (item != null && item.getUser().getUsername().equals(username)) {
            itemRepository.deleteById(id);
        }
    }

    // 3. ビジネスロジック（期限判定）
    @Transactional(readOnly = true)
    public Map<String, Map<Long, Boolean>> getExpiryStatusMaps(List<Item> items) {
        Map<Long, Boolean> expiredMap = new HashMap<>();
        Map<Long, Boolean> nearMap = new HashMap<>();
        LocalDate now = LocalDate.now();

        for (Item i : items) {
            LocalDate date = i.getExpiryDate();
            // ItemService内の期限判定ロジック（Java側で判定しているため、SQLでの絞り込みは不要）
            boolean isExpired = (date != null && (date.isBefore(now) || date.isEqual(now)));
            boolean isNear = (date != null && !isExpired && date.isBefore(now.plusDays(4)));

            expiredMap.put(i.getId(), isExpired);
            nearMap.put(i.getId(), isNear);
        }

        Map<String, Map<Long, Boolean>> result = new HashMap<>();
        result.put("expired", expiredMap);
        result.put("near", nearMap);
        return result;
    }

    // 4. ヘルパーメソッド（ソート文字列の解析）
    private Sort resolveSort(String sortKey) {
        if ("name".equals(sortKey)) {
            return Sort.by(Sort.Direction.ASC, "name");
        } else if ("price-asc".equals(sortKey)) {
            return Sort.by(Sort.Direction.ASC, "price");
        } else if ("price-desc".equals(sortKey)) {
            return Sort.by(Sort.Direction.DESC, "price");
        }
        // デフォルトは期限が近い順（昇順）
        return Sort.by(Sort.Direction.ASC, "expiryDate");
    }
}