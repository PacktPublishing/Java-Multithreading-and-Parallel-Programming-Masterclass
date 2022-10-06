import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(3);

//        threadPoolExecutor.schedule(() -> System.out.println("Task"), 5, TimeUnit.SECONDS);
        ScheduledFuture<?> future = threadPoolExecutor.scheduleAtFixedRate(() -> System.out.println("Task"), 5, 1, TimeUnit.SECONDS);

        threadPoolExecutor.shutdown();
        threadPoolExecutor.shutdownNow();
        threadPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(true);
        threadPoolExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(true);

    }
}
