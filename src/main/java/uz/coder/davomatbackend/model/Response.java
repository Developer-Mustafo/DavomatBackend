package uz.coder.davomatbackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response <T>{
    private final int code;
    private T data;
    private String message;

    public Response(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public Response(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }
}