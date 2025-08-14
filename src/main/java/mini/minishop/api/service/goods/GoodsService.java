package mini.minishop.api.service.goods;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mini.minishop.api.controller.goods.request.CreateGoodsRequest;
import mini.minishop.api.controller.goods.request.UpdateGoodsRequest;
import mini.minishop.api.service.goods.response.FindGoodsResponse;
import mini.minishop.api.service.goods.response.UpdateGoodsResponse;
import mini.minishop.domain.goods.Goods;
import mini.minishop.domain.goods.GoodsRepository;
import mini.minishop.domain.user.User;
import mini.minishop.exception.BusinessException;
import mini.minishop.exception.goods.GoodsErrorCode;
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

    public FindGoodsResponse findGoods(Long goodsId) {
        Optional<Goods> findGoods = goodsRepository.findById(goodsId);
        Goods goods = findGoods.orElseThrow(
                () -> new BusinessException(GoodsErrorCode.GOODS_NOT_FOUND)
        );

        return FindGoodsResponse.of(goods);
    }

    @Transactional
    public List<FindGoodsResponse> searchGoodsByName(String name) {
        return goodsRepository.findByNameContaining(name);
    }

    @Transactional
    public UpdateGoodsResponse updateGoods(Long goodsId, Long userId, UpdateGoodsRequest request) {
        Goods findGoods = goodsRepository.findById(goodsId)
                .orElseThrow(() -> new BusinessException(GoodsErrorCode.GOODS_NOT_FOUND));

        if (!userId.equals(findGoods.getUser().getId())) {
            throw new BusinessException(GoodsErrorCode.NO_PERMISSION_MODIFY_GOODS);
        }

        findGoods.update(
                request.getGoodsName(),
                request.getPrice(),
                request.getInventoryQuantity()
        );

        return new UpdateGoodsResponse(findGoods);
    }
}
