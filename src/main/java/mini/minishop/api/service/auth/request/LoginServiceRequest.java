package mini.minishop.api.service.auth.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginServiceRequest {

    private String email;
    private String password;

    @Builder
    public LoginServiceRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
