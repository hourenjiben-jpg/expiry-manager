package WebListApp.expiry_manager.Controller;

import WebListApp.expiry_manager.Service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    // 登録画面を表示
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    // 登録画面を実行
    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password, Model model) {
        try {
            userService.registerUser(username, password);
            return "redirect:/login?register_success"; //　終わったログイン画面へ
        } catch (RuntimeException e) {
            //　エラーが発生したらメッセージ画面に渡して、登録画面を表示
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
        
    }
}
