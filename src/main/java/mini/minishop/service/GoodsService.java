package mini.minishop.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mini.minishop.domain.Goods;
import mini.minishop.domain.User;
import mini.minishop.dto.goods.CreateGoodsRequest;
import mini.minishop.dto.goods.FindGoodsResponse;
import mini.minishop.dto.goods.UpdateGoodsRequest;
import mini.minishop.dto.goods.UpdateGoodsResponse;
import mini.minishop.error.GoodsErrorCode;
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

    public FindGoodsResponse findGoods(Long goodsId) {
        Optional<Goods> findGoods = goodsRepository.findById(goodsId);
        Goods goods = findGoods.orElseThrow(
                () -> new IllegalArgumentException(GoodsErrorCode.GOODS_NOT_FOUND.getMessage())
        );

        return FindGoodsResponse.of(goods);
    }

    @Transactional
    public List<FindGoodsResponse> searchGoodsByName(String name) {
        return goodsRepository.findByNameContaining(name);
    }

    @Transactional
    public UpdateGoodsResponse updateGoods(Long goodsId, Long userId, UpdateGoodsRequest request)
            throws AccessDeniedException {
        Goods findGoods = goodsRepository.findById(goodsId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        if (!userId.equals(findGoods.getUser().getId())) {
            throw new AccessDeniedException("상품을 수정할 권한이 없습니다.");
        }

        findGoods.update(
                request.getGoodsName(),
                request.getPrice(),
                request.getInventoryQuantity()
        );

        return new UpdateGoodsResponse(findGoods);
    }
}
