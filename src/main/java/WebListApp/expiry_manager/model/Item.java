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

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "品名を入力して下さい")
    @Size(max = 50, message = "品名は５０文字で入力して下さい")
    private String name;

    @NotBlank(message = "カテゴリを選択して下さい")
    private String category;

    @NotNull(message = "期限を入力して下さい")
    private LocalDate expiryDate;

    @Min(value = 0, message = "価格は０円以上にして下さい")
    private int price;

    private boolean used;

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

}
