package mini.minishop.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mini.minishop.domain.Goods;
import mini.minishop.domain.User;
import mini.minishop.dto.goods.CreateGoodsRequest;
import mini.minishop.dto.goods.FindGoodsResponse;
import mini.minishop.repository.GoodsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoodsService {

    private final GoodsRepository goodsRepository;

    @Transactional
    public Long createGoods(CreateGoodsRequest request, User user) {
        Goods goods = Goods.builder()
                .name(request.getName())
                .price(request.getPrice())
                .inventoryQuantity(request.getInventoryQuantity())
                .user(user)
                .build();

        Goods savedGoods = goodsRepository.save(goods);

        return savedGoods.getId();
    }

    public List<FindGoodsResponse> findGoods() {
        List<Goods> findGoods = goodsRepository.findAll();
        
        return findGoods.stream()
                .map(FindGoodsResponse::of)
                .toList();
    }
}
