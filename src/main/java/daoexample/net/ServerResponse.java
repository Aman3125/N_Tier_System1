package daoexample.net;

// F11: All server replies use ServerResponse<T> carrying status, message and data.
public class ServerResponse<T> {

    private String status;
    private String message;
    private T data;

    public ServerResponse() {
    }

    public ServerResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ServerResponse<T> success(String message, T data) {
        return new ServerResponse<>("SUCCESS", message, data);
    }

    public static <T> ServerResponse<T> error(String message) {
        return new ServerResponse<>("ERROR", message, null);
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
