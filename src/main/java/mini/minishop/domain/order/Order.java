package mini.minishop.domain.order;

import static jakarta.persistence.CascadeType.ALL;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mini.minishop.domain.BaseTimeEntity;
import mini.minishop.domain.delivery.Delivery;
import mini.minishop.domain.delivery.DeliveryStatus;
import mini.minishop.domain.goods.Goods;
import mini.minishop.domain.ordergoods.OrderGoods;
import mini.minishop.domain.user.User;
import mini.minishop.exception.BusinessException;
import mini.minishop.exception.delivery.DeliveryErrorCode;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime canceledDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY, cascade = ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @OneToMany(mappedBy = "order", cascade = ALL)
    private List<OrderGoods> orderGoods = new ArrayList<>();

    @Builder
    public Order(OrderStatus status, User user, Delivery delivery) {
        this.status = status;
        this.user = user;
        this.delivery = delivery;
    }

    public void addOrderGoods(Goods goods, int quantity) {
        goods.deductInventoryQuantity(quantity);

        OrderGoods newOrderGoods = OrderGoods.builder()
                .order(this)
                .goods(goods)
                .quantity(quantity)
                .build();

        this.orderGoods.add(newOrderGoods);
    }

    public void cancel() {
        if (delivery.getStatus() != DeliveryStatus.BEFORE_DELIVERY) {
            throw new BusinessException(DeliveryErrorCode.DELIVERY_CANCEL_NOT_ALLOWED);
        }

        this.status = OrderStatus.CANCELED_ORDER;
        this.canceledDate = LocalDateTime.now();
    }
}
