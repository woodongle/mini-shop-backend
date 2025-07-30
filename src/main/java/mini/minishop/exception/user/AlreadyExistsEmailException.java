package mini.minishop.exception.user;

public class AlreadyExistsEmailException extends RuntimeException {
    public AlreadyExistsEmailException() {
        super(UserErrorCode.ALREADY_EXISTS_EMAIL.getMessage());
    }
}
