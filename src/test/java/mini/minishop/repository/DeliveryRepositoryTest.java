package mini.minishop.repository;

import jakarta.transaction.Transactional;
import mini.minishop.domain.delivery.Delivery;
import mini.minishop.domain.delivery.DeliveryRepository;
import mini.minishop.domain.delivery.DeliveryStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
class DeliveryRepositoryTest {

    @Autowired
    DeliveryRepository deliveryRepository;

    @Test
    @Transactional
    @Rollback(false)
    void saveDelivery() {
        Delivery delivery = Delivery.builder()
                .status(DeliveryStatus.BEFORE_DELIVERY)
                .address("서울")
                .build();

        Delivery savedDelivery = deliveryRepository.save(delivery);

        Assertions.assertThat(savedDelivery.getId()).isEqualTo(delivery.getId());
    }
}