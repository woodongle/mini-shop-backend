package mini.minishop.api.service.user.response;

import lombok.Getter;
import lombok.Setter;
import mini.minishop.domain.user.User;

@Getter
@Setter
public class FindUserResponse {
    private Long userId;
    private String username;
    private String userEmail;

    public static FindUserResponse of(User user) {
        return new FindUserResponse(user.getId(), user.getName(), user.getEmail());
    }

    private FindUserResponse(Long userId, String username, String userEmail) {
        this.userId = userId;
        this.username = username;
        this.userEmail = userEmail;
    }
}
