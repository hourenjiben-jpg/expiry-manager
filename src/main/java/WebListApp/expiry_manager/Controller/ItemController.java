package WebListApp.expiry_manager.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import WebListApp.expiry_manager.model.Item;
import WebListApp.expiry_manager.Service.ItemService;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;

/**
 * 【機器・アイテム管理コントローラー】
 * ブラウザからのWebリクエスト（GET/POST）を受け取り、
 * ItemService を呼び出してビジネスロジックを実行後、表示するHTML（ビュー）を決定します。
 */
@Controller
public class ItemController {
    
    // コントローラーが直接リポジトリを触らず、ビジネスロジックはすべて Service に委任する
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * 一覧画面表示（並び替え・カテゴリ絞り込み対応）
     */
    @GetMapping("/items")
    public String listItems(
            @RequestParam(value = "sort", required = false, defaultValue = "expiry") String sort,
            @RequestParam(value = "category", required = false) String category,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String username = userDetails.getUsername();

        // ログインユーザー専用のデータ一覧を取得
        List<Item> items = itemService.findItems(username, category, sort);
        model.addAttribute("items", items);

        // 期限切れ・期限間近の判定マップを計算して画面へ渡す
        var statusMaps = itemService.getExpiryStatusMaps(items);
        model.addAttribute("expiredMap", statusMaps.get("expired"));
        model.addAttribute("nearMap", statusMaps.get("near"));
        
        // ログインユーザーが使用しているカテゴリ一覧（ドロップダウン用）を取得
        model.addAttribute("categories", itemService.findDistinctCategoriesByUsername(username));
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentCategory", category);

        return "items";
    }

    /**
     * 新規登録画面の表示
     */
    @GetMapping("/items/add")
    public String showAddForm(Model model) {
        model.addAttribute("item", new Item());
        return "item-form";
    }

    /**
     * 新規登録処理（バリデーション＋ユーザー紐付け保存）
     */
    @PostMapping("/items/add")
    public String addItem(
        @Valid @ModelAttribute Item item, 
        BindingResult result, 
        Model model, 
        @AuthenticationPrincipal UserDetails userDetails) {

            // 入力チェックエラー（@NotBlankや@NotNullの違反）がある場合はフォームへ押し戻す
        if (result.hasErrors()) {
            return "item-form";
        }

        // ログインユーザー名を渡して安全に保存
        itemService.saveItem(item, userDetails.getUsername());
        return "redirect:/items"; // 二重送信防止のためにリダイレクト
    }

    /**
     * 使用済み/未使用 トグル状態の切り替え処理
     */
    @PostMapping("/items/used/{id}")
    public String checkUsed(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        // IDだけでなくユーザー名も渡し、他人のデータを勝手に更新できないよう防衛
        itemService.toggleUsedStatus(id, userDetails.getUsername());
        return "redirect:/items";
    }

    /**
     * 編集画面の表示
     */
    @GetMapping("/items/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // 他人のデータIDを直入力された場合の不正アクセスを判定・防御
        Item item = itemService.findByIdAndUsername(id, userDetails.getUsername());
        if (item == null) {
            return "redirect:/items"; // 存在しない、または他人のデータなら一覧へ戻す
        }
        model.addAttribute("item", item);
        return "item-edit";
    }

    /**
     * 更新処理
     */
    @PostMapping("/items/edit")
    public String updateItem(
            @Valid @ModelAttribute Item item, 
            BindingResult result,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        if(result.hasErrors()) {
            return "item-edit";
        }
        
        // 保存時もログインユーザー名で所有権を厳しく検証
        itemService.updateItem(item, userDetails.getUsername());
        return "redirect:/items";
    }

    /**
     * 削除処理
     */
    @PostMapping("/items/delete/{id}")
    public String deleteItem(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
       // 所有者チェックを行った上で安全に削除
        itemService.deleteItem(id, userDetails.getUsername());
        return "redirect:/items";
    }

    /**
     * あいまいキーワード検索処理
     */
    @GetMapping("/items/search")
    public String searchItems(
            @RequestParam(value = "keyword", required = false) String keyword, 
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String username = userDetails.getUsername();
        
        // ユーザー専用の検索結果を取得（他人のデータは一切混ざらない）
        List<Item> items = itemService.searchItemsByKeywordAndUsername(keyword, username);
        
        var statusMaps = itemService.getExpiryStatusMaps(items);
        model.addAttribute("expiredMap", statusMaps.get("expired"));
        model.addAttribute("nearMap", statusMaps.get("near"));

        model.addAttribute("items", items);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categories", itemService.findDistinctCategoriesByUsername(username));
        
        return "items";
    }
}