package WebListApp.expiry_manager.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import WebListApp.expiry_manager.repository.ItemRepository;
import WebListApp.expiry_manager.model.Item;
import WebListApp.expiry_manager.Service.ItemService; // Serviceをインポート
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

import java.util.List;

@Controller
public class ItemController {
    
    // RepositoryではなくServiceを呼ぶ
    private final ItemService itemService;
    private final ItemRepository itemRepository;

    public ItemController(ItemService itemService, ItemRepository itemRepository) {
        this.itemService = itemService;
        this.itemRepository = itemRepository;
    }

    @GetMapping("/items")
    public String listItems(
            @RequestParam(value = "sort", required = false, defaultValue = "expiry") String sort,
            @RequestParam(value = "category", required = false) String category,
            Model model) {
        
        // Serviceにカテゴリ絞り込みを任せる
        List<Item> items = itemService.findByCategory(category);

        // ソートロジック (ここにも本来はService内でDBソートするのが理想)
        sortItems(items, sort);

        model.addAttribute("items", items);

        //　期限判定マップをServiceから受け取る
        var statusMaps = itemService.getExpiryStatusMaps(items);
        model.addAttribute("expiredMap", statusMaps.get("expired"));
        model.addAttribute("nearMap", statusMaps.get("near"));
        
        model.addAttribute("categories", itemRepository.findAllDistinctCategories());
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentCategory", category);

        return "items";
    }

    @GetMapping("/message")
    public String showMessage(Model model) {
        model.addAttribute("message", "ようこそ消費期限リストへ");
        model.addAttribute("title", "消費期限リスト");
        return "message";
    }

    @GetMapping("/items/add")
    public String showAddForm(Model model) {
        model.addAttribute("item", new Item());
        return "item-form";
    }

    @PostMapping("/items/add")
    public String addItem(@Valid @ModelAttribute Item item, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "item-form";
        }

        itemService.save(item);
        return "redirect:/items";
    }

    @PostMapping("/items/used/{id}")
    public String checkUsed(@PathVariable Long id) {
        Item item = itemRepository.findById(id).orElse(null);
        
        if (item == null) {
            return "redirect:/items";
        }
        
        item.setUsed(!item.getUsed());
        itemRepository.save(item);
        
        return "redirect:/items";
    }

    @GetMapping("/items/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Item item = itemRepository.findById(id).orElse(null);
        if (item == null) {
            return "redirect:/items";
        }
        model.addAttribute("item", item);
        return "item-edit";
    }

    @PostMapping("/items/edit")
    public String updateItem(@Valid @ModelAttribute Item item, BindingResult result) {
        if(result.hasErrors()) {
            return "item-edit";
        }
        itemRepository.save(item);
        return "redirect:/items";
    }

    @PostMapping("/items/delete/{id}")
    public String deleteItem(@PathVariable Long id) {
        itemRepository.deleteById(id);
        return "redirect:/items";
    }

    @GetMapping("/items/search")
    public String searchItems(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<Item> items;
        
        if(keyword == null || keyword.isEmpty()) {
            items = itemRepository.findAll();
        } else {
            items = itemRepository.findByNameContainingIgnoreCase(keyword);
        }
        
        // Serviceで色判定
        var statusMaps = itemService.getExpiryStatusMaps(items);
        model.addAttribute("expiredMap", statusMaps.get("expired"));
        model.addAttribute("nearMap", statusMaps.get("near"));

        model.addAttribute("items", items);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categories", itemRepository.findAllDistinctCategories());
        
        return "items";
    }

    // ソート用プライベートメソッド
    private void sortItems(List<Item> items, String sort) {
        if("name". equals(sort)) {
            items.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
            } else if ("price-asc".equals(sort)) {
                items.sort((a, b) -> Integer.compare(a.getPrice(), b.getPrice()));
            } else if ("price-desc".equals(sort)) {
                items.sort((a, b) -> Integer.compare(b.getPrice(), a.getPrice()));
            } else {
                items.sort((a, b) -> {
                    if (a.getExpiryDate() == null) return 1;
                    if (b.getExpiryDate() == null) return -1;
                    return a.getExpiryDate(). compareTo(b.getExpiryDate());
                });
            }
        }
    }
