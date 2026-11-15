package coursehub.session;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionRegistry {
	private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

	public Session createIfAbsent(String userId) {
		if (userId == null || userId.isBlank()) {
			throw new IllegalArgumentException("Нужен ID пользователя");
		}
		return sessions.computeIfAbsent(userId, id -> new Session(id, UUID.randomUUID().toString()));
	}

	public boolean remove(Session session) {
		return sessions.remove(session.userId(), session);
	}

	public int size() {
		return sessions.size();
	}
}
