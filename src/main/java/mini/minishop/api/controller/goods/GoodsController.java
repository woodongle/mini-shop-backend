package mini.minishop.api.controller.goods;

import static org.springframework.http.HttpStatus.CREATED;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mini.minishop.api.ApiResponse;
import mini.minishop.api.controller.goods.request.CreateGoodsRequest;
import mini.minishop.api.controller.goods.request.UpdateGoodsRequest;
import mini.minishop.api.service.goods.GoodsService;
import mini.minishop.api.service.goods.response.CreateGoodsResponse;
import mini.minishop.api.service.goods.response.FindGoodsResponse;
import mini.minishop.api.service.goods.response.UpdateGoodsResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/goods")
public class GoodsController {

    private final GoodsService goodsService;

    @ResponseStatus(CREATED)
    @PostMapping
    public ApiResponse<CreateGoodsResponse> createGoods(@RequestBody @Valid CreateGoodsRequest request,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        CreateGoodsResponse response = goodsService.createGoods(request.toServiceRequest(), userEmail);

        return ApiResponse.created("상품 등록이 완료되었습니다.", response);
    }

    @GetMapping
    public ApiResponse<List<FindGoodsResponse>> findGoods() {
        List<FindGoodsResponse> response = goodsService.findGoods();

        return ApiResponse.ok(response);
    }

    @GetMapping("/{goodsId}")
    public ApiResponse<FindGoodsResponse> findGoodsByGoodsId(@PathVariable Long goodsId) {
        FindGoodsResponse response = goodsService.findGoodsByGoodsId(goodsId);

        return ApiResponse.ok(response);
    }

    @GetMapping("/search")
    public ApiResponse<List<FindGoodsResponse>> searchGoodsByName(
            @RequestParam(name = "name") String name) {
        List<FindGoodsResponse> response = goodsService.searchGoodsByName(name);

        return ApiResponse.ok(response);
    }

    @PatchMapping("/{goodsId}")
    public ApiResponse<UpdateGoodsResponse> updateGoods(@PathVariable Long goodsId,
                                                        @Valid @RequestBody UpdateGoodsRequest request,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        UpdateGoodsResponse response = goodsService.updateGoods(goodsId, userEmail, request.toServiceRequest());

        return ApiResponse.ok(response);
    }
}
