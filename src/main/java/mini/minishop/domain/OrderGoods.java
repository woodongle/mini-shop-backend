package mini.minishop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    public OrderGoods(Order order, Goods goods, int quantity) {
        this.order = order;
        this.order.getOrderGoods().add(this);
        this.goods = goods;
        this.goods.deductInventoryQuantity(quantity);
        this.quantity = quantity;
        this.paymentAmount = calculatePaymentAmount();
    }

    private BigDecimal calculatePaymentAmount() {
        return goods.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
