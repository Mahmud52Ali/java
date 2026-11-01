package coursehub;

import coursehub.demo.Task01DeterministicSorting;
import coursehub.demo.Task02FlatMapTopics;
import coursehub.demo.Task03ReduceIdentity;
import coursehub.demo.Task04CollectorsSummary;
import coursehub.demo.Task05OptionalResult;
import coursehub.demo.Task06CustomCollector;
import coursehub.demo.Task07OnePassReport;

public class Main {
    public static void main(String[] args) {
        Task01DeterministicSorting.run();
        Task02FlatMapTopics.run();
        Task03ReduceIdentity.run();
        Task04CollectorsSummary.run();
        Task05OptionalResult.run();
        Task06CustomCollector.run();
        Task07OnePassReport.run();
    }
}
