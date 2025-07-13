package mini.minishop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createdDate;
    private LocalDateTime canceledDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @OneToMany(mappedBy = "order")
    private List<OrderGoods> orderGoods = new ArrayList<>();
}
