package mini.minishop.exception.user;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND.getMessage());
    }
}
