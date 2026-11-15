package coursehub;

import coursehub.demo.Task01LostIncrements;
import coursehub.demo.Task02StopVisibility;
import coursehub.demo.Task03SessionRegistry;
import coursehub.demo.Task04ProducerConsumer;
import coursehub.demo.Task05DeterministicRace;
import coursehub.demo.Task06CourseEnrollment;

public final class Main {
	public static void main(String[] args) throws Exception {
		Task01LostIncrements.run();
		Task02StopVisibility.run();
		Task03SessionRegistry.run();
		Task04ProducerConsumer.run();
		Task05DeterministicRace.run();
		Task06CourseEnrollment.run();
	}
}
