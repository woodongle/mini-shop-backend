package mini.minishop.api.service.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import mini.minishop.domain.user.User;

@Getter
@Setter
public class CreateUserServiceRequest {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @Builder
    public CreateUserServiceRequest(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User toEntity(String encryptedPassword) {
        return User.builder()
                .name(name)
                .email(email)
                .password(encryptedPassword)
                .build();
    }
}
