package blog.exception;

public class UserNotFoundByEmailException extends RuntimeException {

    private final String email;

    public UserNotFoundByEmailException(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
