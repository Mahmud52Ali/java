package coursehub.generics;

public final class Result<T> {
    private final T value;
    private final String error;

    private Result(T value, String error) {
        if ((value == null) == (error == null)) {
            throw new IllegalArgumentException(
                    "Result должен содержать либо значение, либо ошибку"
            );
        }

        if (error != null && error.isBlank()) {
            throw new IllegalArgumentException(
                    "Сообщение ошибки не должно быть пустым"
            );
        }

        this.value = value;
        this.error = error;
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(value, null);
    }

    public static <T> Result<T> failure(String error) {
        return new Result<>(null, error);
    }

    public boolean isSuccess() {
        return error == null;
    }

    public T value() {
        if (!isSuccess()) {
            throw new IllegalStateException(
                    "У результата с ошибкой нет значения: " + error
            );
        }

        return value;
    }

    public String error() {
        if (isSuccess()) {
            throw new IllegalStateException(
                    "У успешного результата нет ошибки"
            );
        }

        return error;
    }
}
