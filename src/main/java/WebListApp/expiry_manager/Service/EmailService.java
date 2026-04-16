package WebListApp.expiry_manager.Service;

import WebListApp.expiry_manager.repository.ItemRepository;
import WebListApp.expiry_manager.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.time.LocalDate;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ItemRepository itemRepository;

    public void sendSimpleEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@expirymanager.com");  //　送信元
        message.setTo(toEmail); // 宛先
        message.setSubject(subject); // 件名
        message.setText(body); // 本文

        mailSender.send(message);
        System.out.println("メールを送信しました!");
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void sendExpiryReminder() {
        // パターン
        int[] notificationPatterns = {0, 1, 3, 7};
        
        for (int days : notificationPatterns) {
            LocalDate targetDate = LocalDate.now().plusDays(days);

        //　データベースからその通知設定かつその期限日のものを取得
        List<Item> items = itemRepository.findByNotificationDaysAndExpiryDate(days, targetDate);

        for (Item item : items) {
            String timingMessage = (days == 0) ? "本日" : days + "日前";

            try {
            sendSimpleEmail(
                "user@example.com",
                "【" + timingMessage + "通知】" + item.getName(),
                item.getName() + " の期限が " + timingMessage + " (" + targetDate + ") です！ "
            );
            System.out.println("通知送信成功:  " + item.getName() + " (" + timingMessage + ")");
          } catch (Exception e) {
            System.err.println("通知送信失敗: " + item.getName() + " - " + e.getMessage());
          }
         }   
        }
    }
}
