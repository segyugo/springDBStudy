package hello.jdbc.repository;

public class MySqlException extends RuntimeException{
    public MySqlException() {
    }

    public MySqlException(String message) {
        super(message);
    }

    public MySqlException(String message, Throwable cause) {
        super(message, cause);
    }

    public MySqlException(Throwable cause) {
        super(cause);
    }
}
