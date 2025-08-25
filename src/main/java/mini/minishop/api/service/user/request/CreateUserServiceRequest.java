package mini.minishop.api.service.user.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import mini.minishop.domain.user.User;

@Getter
@Setter
public class CreateUserServiceRequest {

    private String name;
    private String email;
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
