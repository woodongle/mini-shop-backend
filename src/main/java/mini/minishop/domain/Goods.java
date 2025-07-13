package mini.minishop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Goods {

    @Id
    @GeneratedValue
    @Column(name = "goods_id")
    private Long id;

    private String name;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    private int inventoryQuantity;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
