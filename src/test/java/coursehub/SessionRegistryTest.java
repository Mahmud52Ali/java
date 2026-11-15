package coursehub;

import coursehub.concurrent.ConcurrentBatch;
import coursehub.session.SessionRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

@Timeout(10)
class SessionRegistryTest {
	@Test
	void competingCreatorsReceiveTheSameSession() throws Exception {
		SessionRegistry registry = new SessionRegistry();
		var sessions = ConcurrentBatch.run(12, 300, index -> registry.createIfAbsent("student-42"));
		assertEquals(1, registry.size());
		sessions.forEach(session -> assertSame(sessions.getFirst(), session));
	}

	@Test
	void differentUsersGetIndependentSessions() throws Exception {
		SessionRegistry registry = new SessionRegistry();
		ConcurrentBatch.run(8, 200, index -> registry.createIfAbsent("student-" + index));
		assertEquals(200, registry.size());
	}

	@Test
	void staleLogoutDoesNotRemoveNewSession() {
		SessionRegistry registry = new SessionRegistry();
		var oldSession = registry.createIfAbsent("student-1");
		assertTrue(registry.remove(oldSession));
		var newSession = registry.createIfAbsent("student-1");
		assertFalse(registry.remove(oldSession));
		assertSame(newSession, registry.createIfAbsent("student-1"));
		assertEquals(1, registry.size());
	}

	@Test
	void invalidUserDoesNotChangeRegistry() {
		SessionRegistry registry = new SessionRegistry();
		assertThrows(IllegalArgumentException.class, () -> registry.createIfAbsent(" "));
		assertThrows(IllegalArgumentException.class, () -> registry.createIfAbsent(null));
		assertEquals(0, registry.size());
	}
}
