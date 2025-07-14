package mini.minishop.dto.user;

import lombok.Getter;
import mini.minishop.domain.User;

@Getter
public class FindUserResponse {

    private Long userId;
    private String username;
    private String userEmail;

    public FindUserResponse(User user) {
        this.userId = user.getId();
        this.username = user.getName();
        this.userEmail = user.getEmail();
    }
}
