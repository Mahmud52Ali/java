package coursehub.session;

import java.util.Objects;

public record Session(String userId, String token) {
	public Session {
		Objects.requireNonNull(token, "Токен не должен быть null");
		if (userId == null || userId.isBlank()) {
			throw new IllegalArgumentException("Нужен ID пользователя");
		}
	}
}
