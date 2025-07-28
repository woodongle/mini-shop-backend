package mini.minishop.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mini.minishop.domain.User;
import mini.minishop.dto.goods.CreateGoodsRequest;
import mini.minishop.dto.goods.FindGoodsResponse;
import mini.minishop.error.UserErrorCode;
import mini.minishop.service.GoodsService;
import mini.minishop.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
        User user = userService.findUser(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException(UserErrorCode.USER_NOT_FOUND.getMessage()));

        Long goodsId = goodsService.createGoods(request, user);

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
}
