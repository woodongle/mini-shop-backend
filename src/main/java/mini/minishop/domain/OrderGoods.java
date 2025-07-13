package mini.minishop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OrderGoods {

    @Id
    @GeneratedValue
    @Column(name = "order_goods_id")
    private Long id;

    private int quantity;

    @Column(precision = 11, scale = 2)
    private BigDecimal paymentAmount;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "goods_id")
    private Goods goods;
}
