package mini.minishop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AutoCloseable.class)
public class Goods extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "goods_id")
    private Long id;

    private String name;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    private int inventoryQuantity;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Goods(String name, BigDecimal price, int inventoryQuantity) {
        this.name = name;
        this.price = price;
        this.inventoryQuantity = inventoryQuantity;
    }
}
