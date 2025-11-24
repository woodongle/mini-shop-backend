package mini.minishop.api.controller;

import mini.minishop.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public ApiResponse<String> home() {
        return ApiResponse.ok("Minishop이 시작되었습니다!");
    }
}
