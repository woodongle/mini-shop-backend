package mini.minishop.api.controller.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import mini.minishop.api.service.auth.request.LoginServiceRequest;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @Builder
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public LoginServiceRequest toServiceRequest() {
        return LoginServiceRequest.builder()
                .email(this.email)
                .password(this.password)
                .build();
    }
}
