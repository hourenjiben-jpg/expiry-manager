package WebListApp.expiry_manager.Service;

import org.springframework.stereotype.Service;
import WebListApp.expiry_manager.model.Item;
import WebListApp.expiry_manager.model.User;
import WebListApp.expiry_manager.repository.ItemRepository;
import WebListApp.expiry_manager.repository.UserRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    // ユーザー名で絞り込み
    public List<Item> getAllItemsByUsername(String username) {
        return itemRepository.findByUserUsername(username);
    }

    // カテゴリで検索
    public List<Item> findByCategoryAndUsername(String category, String username) {
        if (category == null || category.isEmpty() || "all".equals(category)) {
            return itemRepository.findByUserUsername(username);
        }
        return itemRepository.findByCategoryAndUserUsername(category, username);
    }

    public List<Item> findByCategory(String category) {
        if (category == null || category.isEmpty() || "all".equals(category)) {
            return itemRepository.findAll();
        }
        return itemRepository.findByCategory(category);
    }

    // 期限状態のマップを作成（ビジネスロジックの核）
    public Map<String, Map<Long, Boolean>> getExpiryStatusMaps(List<Item> items) {
        Map<Long, Boolean> expiredMap = new HashMap<>();
        Map<Long, Boolean> nearMap = new HashMap<>();
        LocalDate now = LocalDate.now();

        for (Item i : items) {
            LocalDate date = i.getExpiryDate();
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

    // 保存・更新・削除などの橋渡し
    public void saveItem(Item item, String username) {

        //　ユーザー名からUserUserエンティティを探す
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        // アイテムにユーザーをセットする
        item.setUser(user);

        //　通知日数のデフォルト設定
        if(item.getNotificationDays() == null) {
            item.setNotificationDays(3); 
        }

        //　保存
        itemRepository.save(item);
    }

    public Item findById(Long id) { return itemRepository.findById(id).orElse(null); }
    public void delete(Long id) { itemRepository.deleteById(id); }
}
