package WebListApp.expiry_manager.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 【ログイン画面表示用コントローラー】
 * ユーザーがログイン画面（/login）にアクセスした際に、
 * 対応するHTMLテンプレート（login.html）を表示する役割を担います。
 */
@Controller
public class LoginController {
    /**
     * ログイン画面のGETリクエスト処理
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
