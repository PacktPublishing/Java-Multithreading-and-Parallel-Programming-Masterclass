import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                3,
                5,
                1,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(3)
        );

        threadPoolExecutor.prestartAllCoreThreads();

        threadPoolExecutor.execute(() -> System.out.println("Task 1"));
        threadPoolExecutor.execute(() -> System.out.println("Task 2"));

        System.out.println("Pool size: " + threadPoolExecutor.getPoolSize());

        threadPoolExecutor.shutdown();
        threadPoolExecutor.awaitTermination(3, TimeUnit.SECONDS);

//        Future<Integer> future = threadPoolExecutor.submit(new CallableTask());
//
//        // Do other stuff
//        Integer result = future.get();

    }

    static class CallableTask implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            // Do some work
            return 4;
        }
    }
}
