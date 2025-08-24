package mini.minishop.api.controller.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import mini.minishop.api.service.user.request.CreateUserServiceRequest;

@Getter
@Setter
public class CreateUserRequest {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @Builder
    public CreateUserRequest(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public CreateUserServiceRequest toServiceRequest() {
        return CreateUserServiceRequest.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();
    }
}
