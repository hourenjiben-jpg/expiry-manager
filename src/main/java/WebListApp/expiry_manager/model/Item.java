package WebListApp.expiry_manager.model;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class Item {

    // 1. 主キー（プライマリキー）と自動採番の設定
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDをデータベース側で自動連番（Auto Increment）させる設定
    private Long id;

    @NotBlank(message = "品名を入力して下さい")
    @Size(max = 50, message = "品名は５０文字以内で入力して下さい")
    // 品名
    private String name;

    @NotBlank(message = "カテゴリを選択して下さい")
    //　カテゴリ
    private String category;

    @NotNull(message = "期限を入力して下さい")
    // 消費期限
    private LocalDate expiryDate;

    @Min(value = 0, message = "価格は０円以上にして下さい")
    // 価格
    private int price;

    // 使用済み（真偽値）：true (使用済) / false (未使用)
    private boolean used;

    // 多対1のリレーション設定：「複数のItem（機器）」が「1人のUser（所有者/登録者）」に紐づく
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Getter/Setter

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean getUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

}
