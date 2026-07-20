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

@Controller
public class ItemController {
    
    // Repositoryは直接呼ばず、すべての窓口をServiceに一本化します
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/items")
    public String listItems(
            @RequestParam(value = "sort", required = false, defaultValue = "expiry") String sort,
            @RequestParam(value = "category", required = false) String category,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String username = userDetails.getUsername();

        // データの取得、絞り込み、並び替えまですべてService側（DB側）で完結させて綺麗に受け取る
        List<Item> items = itemService.findItems(username, category, sort);
        model.addAttribute("items", items);

        // 期限判定マップ
        var statusMaps = itemService.getExpiryStatusMaps(items);
        model.addAttribute("expiredMap", statusMaps.get("expired"));
        model.addAttribute("nearMap", statusMaps.get("near"));
        
        // カテゴリ一覧も「そのユーザーが登録したものだけ」をService経由で取得
        model.addAttribute("categories", itemService.findDistinctCategoriesByUsername(username));
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentCategory", category);

        return "items";
    }

    @GetMapping("/items/add")
    public String showAddForm(Model model) {
        model.addAttribute("item", new Item());
        return "item-form";
    }

    @PostMapping("/items/add")
    public String addItem(
        @Valid @ModelAttribute Item item, 
        BindingResult result, 
        Model model, 
        @AuthenticationPrincipal UserDetails userDetails) {

        if (result.hasErrors()) {
            return "item-form";
        }

        itemService.saveItem(item, userDetails.getUsername());
        return "redirect:/items";
    }

    // セキュリティ対策：IDだけでなく、必ず「誰のデータか」をServiceに渡して検証する
    @PostMapping("/items/used/{id}")
    public String checkUsed(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        // Service側で「自分のデータであれば状態を反転する」ロジックを徹底
        itemService.toggleUsedStatus(id, userDetails.getUsername());
        return "redirect:/items";
    }

    @GetMapping("/items/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // 他人の編集画面を覗き見されないように防御
        Item item = itemService.findByIdAndUsername(id, userDetails.getUsername());
        if (item == null) {
            return "redirect:/items"; // 存在しない、または他人のデータなら一覧へ戻す
        }
        model.addAttribute("item", item);
        return "item-edit";
    }

    @PostMapping("/items/edit")
    public String updateItem(
            @Valid @ModelAttribute Item item, 
            BindingResult result,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        if(result.hasErrors()) {
            return "item-edit";
        }
        
        // 保存する際も、乗っ取りを防ぐためにログインユーザー名で防衛線
        itemService.updateItem(item, userDetails.getUsername());
        return "redirect:/items";
    }

    @PostMapping("/items/delete/{id}")
    public String deleteItem(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        // 安全に削除を実行
        itemService.deleteItem(id, userDetails.getUsername());
        return "redirect:/items";
    }

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

    @GetMapping("/message")
    public String showMessage(Model model) {
        model.addAttribute("message", "ようこそ消費期限リストへ");
        model.addAttribute("title", "消費期限リスト");
        return "message";
    }
}