package mini.minishop.domain.order;

import static jakarta.persistence.CascadeType.ALL;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
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
import mini.minishop.domain.ordergoods.OrderGoods;
import mini.minishop.domain.user.User;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue
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

    // 하나의 주문에 여러 가지 상품이 있을 수 있다고 정의했지만,
    // 하나의 주문에 하나의 상품만 있어야 한다고 변경해야 할 것 같음.
    @OneToMany(mappedBy = "order", cascade = ALL)
    private List<OrderGoods> orderGoods = new ArrayList<>();

    @Builder
    public Order(OrderStatus status, User user, Delivery delivery) {
        this.status = status;
        this.user = user;
        this.delivery = delivery;
    }

    public void cancel() {
        if (delivery.getStatus() != DeliveryStatus.BEFORE_DELIVERY) {
            throw new IllegalStateException("배송이 진행 중이거나, 이미 배송 완료된 상품은 취소가 불가능합니다.");
        }

        this.status = OrderStatus.CANCELED_ORDER;
        this.canceledDate = LocalDateTime.now();
    }
}
