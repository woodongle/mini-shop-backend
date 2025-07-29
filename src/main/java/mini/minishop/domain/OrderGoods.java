package mini.minishop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class OrderGoods {

    @Id
    @GeneratedValue
    @Column(name = "order_goods_id")
    private Long id;

    private int quantity;

    @Column(precision = 11, scale = 2)
    private BigDecimal paymentAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id")
    private Goods goods;

    @Builder
    public OrderGoods(int quantity, BigDecimal paymentAmount, Order order, Goods goods) {
        this.quantity = quantity;
        this.paymentAmount = paymentAmount;
        this.order = order;
        this.goods = goods;
    }
}
