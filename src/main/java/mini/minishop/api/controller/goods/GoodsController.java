package mini.minishop.api.controller.goods;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mini.minishop.api.controller.goods.request.CreateGoodsRequest;
import mini.minishop.api.controller.goods.request.UpdateGoodsRequest;
import mini.minishop.api.service.goods.GoodsService;
import mini.minishop.api.service.goods.response.FindGoodsResponse;
import mini.minishop.api.service.goods.response.UpdateGoodsResponse;
import mini.minishop.api.service.user.UserService;
import mini.minishop.domain.goods.Goods;
import mini.minishop.domain.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/goods")
public class GoodsController {

    private final GoodsService goodsService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<String> createGoods(@RequestBody @Valid CreateGoodsRequest request,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findUser(userDetails.getUsername());
        Goods goods = goodsService.createGoods(request.toServiceRequest(), user);

        return new ResponseEntity<>("상품 등록이 완료되었습니다.", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FindGoodsResponse>> findGoods() {
        List<FindGoodsResponse> goods = goodsService.findGoods();

        return ResponseEntity.ok(goods);
    }

    @GetMapping("/{goodsId}")
    public ResponseEntity<FindGoodsResponse> findGoods(@PathVariable Long goodsId) {
        FindGoodsResponse goods = goodsService.findGoods(goodsId);

        return ResponseEntity.ok(goods);
    }

    @GetMapping("/search")
    public ResponseEntity<List<FindGoodsResponse>> searchGoodsByName(@RequestParam(name = "name") String name) {
        List<FindGoodsResponse> findGoods = goodsService.searchGoodsByName(name);

        return ResponseEntity.ok(findGoods);
    }

    @PatchMapping("/{goodsId}")
    public ResponseEntity<UpdateGoodsResponse> updateGoods(@PathVariable Long goodsId,
                                                           @Valid @RequestBody UpdateGoodsRequest request,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findUser(userDetails.getUsername());
        UpdateGoodsResponse response = goodsService.updateGoods(goodsId, user.getId(), request.toServiceRequest());

        return ResponseEntity.ok(response);
    }
}
