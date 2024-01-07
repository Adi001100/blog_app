package blog.exception;

public class UserNotFoundByNameException extends RuntimeException {

    private String userName;

    public UserNotFoundByNameException(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
