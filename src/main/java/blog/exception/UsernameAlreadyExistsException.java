package blog.exception;

public class UsernameAlreadyExistsException extends RuntimeException {

    private final String username;

    public UsernameAlreadyExistsException(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
