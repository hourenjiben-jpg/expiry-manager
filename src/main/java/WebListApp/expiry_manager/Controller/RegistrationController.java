package WebListApp.expiry_manager.Controller;

import WebListApp.expiry_manager.Service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

/**
 * 【新規ユーザー登録コントローラー】
 * 新規アカウント作成画面の表示と、フォームから送信されたユーザー情報の登録処理を担当します。
 */
@Controller
public class RegistrationController {

    // ユーザー登録のビジネスロジックを担当する Service を注入（コンストラクタインジェクション）
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 新規ユーザー登録画面の表示（GETリクエスト）
     */
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    /**
     * 新規ユーザー登録の実行処理（POSTリクエスト）
     */
    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password, Model model) {
        try {
            // Service側で重複チェック＆パスワード暗号化（BCrypt）を行ってDB保存
            userService.registerUser(username, password);
            return "redirect:/login?register_success"; //　終わったらログイン画面へ
        } catch (RuntimeException e) {
            // Service側で例外（例: 「そのユーザー名は既に存在します」など）が発生した場合の処理
            // エラーメッセージを Model に載せてユーザー登録画面に戻す
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
        
    }
}
