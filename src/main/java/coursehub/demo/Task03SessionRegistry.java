package coursehub.demo;

import coursehub.concurrent.ConcurrentBatch;
import coursehub.session.SessionRegistry;

public final class Task03SessionRegistry {
	private Task03SessionRegistry() {
	}

	public static void run() throws Exception {
		System.out.println("\nЗадание 3. Потокобезопасный реестр");
		SessionRegistry registry = new SessionRegistry();
		var results = ConcurrentBatch.run(8, 100, index -> registry.createIfAbsent("student-1"));
		System.out.println("Запросов=100, сессий=" + registry.size()
				+ ", все получили один объект=" + results.stream().allMatch(session -> session == results.getFirst()));
	}
}
