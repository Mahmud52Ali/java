package coursehub.demo;

import coursehub.concurrent.ExecutorShutdown;
import coursehub.concurrent.NamedThreadFactory;
import coursehub.worker.PlainStopProbe;
import coursehub.worker.VolatileStopWorker;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class Task02StopVisibility {
	private Task02StopVisibility() {
	}

	public static void run() throws Exception {
		System.out.println("\nЗадание 2. Флаг остановки и видимость");
		String executable = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
		String java = Path.of(System.getProperty("java.home"), "bin", executable).toString();
		Process probe = new ProcessBuilder(java, "-Dfile.encoding=UTF-8", "-cp",
				System.getProperty("java.class.path"), PlainStopProbe.class.getName()).redirectErrorStream(true).start();
		try {
			if (probe.waitFor(3, TimeUnit.SECONDS)) {
				System.out.print(new String(probe.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
				if (probe.exitValue() != 0) {
					throw new IllegalStateException("Ошибка дочернего исследования visibility");
				}
			} else {
				System.out.println("Дочерняя JVM превысила лимит; выполняется принудительное завершение.");
			}
		} finally {
			probe.destroyForcibly();
			if (!probe.waitFor(2, TimeUnit.SECONDS)) {
				throw new IllegalStateException("Не удалось остановить дочернюю JVM");
			}
		}
		VolatileStopWorker worker = new VolatileStopWorker();
		var executor = Executors.newSingleThreadExecutor(new NamedThreadFactory("volatile-worker"));
		try {
			var result = executor.submit(worker);
			if (!worker.awaitStarted()) {
				throw new IllegalStateException("Worker не готов");
			}
			worker.requestStop();
			result.get(2, TimeUnit.SECONDS);
			System.out.println("volatile-флаг: worker завершился; итераций=" + worker.iterations());
		} finally {
			worker.requestStop();
			ExecutorShutdown.shutdown(executor, Duration.ofSeconds(1));
		}
	}
}
