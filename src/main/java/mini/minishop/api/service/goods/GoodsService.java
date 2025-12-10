package mini.minishop.api.service.goods;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mini.minishop.api.service.goods.request.CreateGoodsServiceRequest;
import mini.minishop.api.service.goods.request.UpdateGoodsServiceRequest;
import mini.minishop.api.service.goods.response.CreateGoodsResponse;
import mini.minishop.api.service.goods.response.FindGoodsResponse;
import mini.minishop.api.service.goods.response.UpdateGoodsResponse;
import mini.minishop.api.service.user.UserService;
import mini.minishop.domain.goods.Goods;
import mini.minishop.domain.goods.GoodsRepository;
import mini.minishop.domain.user.User;
import mini.minishop.exception.BusinessException;
import mini.minishop.exception.goods.GoodsErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoodsService {

    private final GoodsRepository goodsRepository;
    private final UserService userService;

    @Transactional
    public CreateGoodsResponse createGoods(CreateGoodsServiceRequest request, String userEmail) {
        User currentUser = userService.findUserByUserEmail(userEmail);

        Goods goods = request.toEntity(currentUser);

        Goods savedGoods = goodsRepository.save(goods);

        return CreateGoodsResponse.builder()
                .goodsId(savedGoods.getId())
                .goodsName(savedGoods.getName())
                .price(savedGoods.getPrice())
                .inventoryQuantity(savedGoods.getInventoryQuantity())
                .createDate(savedGoods.getCreatedDate())
                .build();
    }

    public List<FindGoodsResponse> findGoods() {
        List<Goods> findGoods = goodsRepository.findAll();

        return findGoods.stream()
                .map(FindGoodsResponse::of)
                .toList();
    }

    public FindGoodsResponse findGoodsByGoodsId(Long goodsId) {
        return FindGoodsResponse.of(findGoodsEntityByGoodsId(goodsId));
    }

    public Goods findGoodsEntityByGoodsId(Long goodsId) {
        Optional<Goods> findGoods = goodsRepository.findById(goodsId);

        return findGoods.orElseThrow(
                () -> new BusinessException(GoodsErrorCode.GOODS_NOT_FOUND)
        );
    }

    @Transactional
    public Page<FindGoodsResponse> searchGoodsByName(String name, int page, int size) {
        return goodsRepository.findByNameContaining(name, PageRequest.of(page, size));
    }

    @Transactional
    public UpdateGoodsResponse updateGoods(Long goodsId, String userEmail, UpdateGoodsServiceRequest request) {
        Goods findGoods = goodsRepository.findById(goodsId)
                .orElseThrow(() -> new BusinessException(GoodsErrorCode.GOODS_NOT_FOUND));

        Long currentUserId = userService.findUserByUserEmail(userEmail).getId();

        if (!currentUserId.equals(findGoods.getUser().getId())) {
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
