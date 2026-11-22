package coursehub;

import coursehub.async.demo.Task01CallableFuture;
import coursehub.async.demo.Task02CompletablePipeline;
import coursehub.async.demo.Task03AllOfResults;
import coursehub.async.demo.Task04ExecutorChoice;
import coursehub.async.demo.Task05ThreadExperiment;
import coursehub.async.demo.Task06SharedDeadline;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws Exception {
        Task01CallableFuture.run();
        Task02CompletablePipeline.run();
        Task03AllOfResults.run();
        Task04ExecutorChoice.run();
        Task05ThreadExperiment.run();
        Task06SharedDeadline.run();
    }
}
