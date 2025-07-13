package mini.minishop.repository;

import jakarta.transaction.Transactional;
import mini.minishop.domain.Delivery;
import mini.minishop.domain.DeliveryStatus;
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
        Delivery delivery = new Delivery();
        delivery.setStatus(DeliveryStatus.BEFORE_DELIVERY);
        delivery.setAddress("서울");

        Delivery savedDelivery = deliveryRepository.save(delivery);

        Assertions.assertThat(savedDelivery.getId()).isEqualTo(delivery.getId());
    }
}