package WebListApp.expiry_manager.Controller;

import WebListApp.expiry_manager.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestEmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/test-email")
    public String sendTestEmail() {
        emailService.sendSimpleEmail("hourenjiben@gmail.com", "期限管理アプリより", "これはテストメールです");

        return "Mailtrapを確認して下さい";
    }
    
}
