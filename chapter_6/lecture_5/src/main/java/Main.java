import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                2,
                3,
                1,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(2),
                new ThreadPoolExecutor.DiscardPolicy()
        );

        threadPoolExecutor.submit(new SleepingTask(1));
        threadPoolExecutor.submit(new SleepingTask(2));

        System.out.println("[1] Pool size: " + threadPoolExecutor.getPoolSize());

        threadPoolExecutor.submit(new SleepingTask(3));
        threadPoolExecutor.submit(new SleepingTask(4));

        threadPoolExecutor.submit(new SleepingTask(5));
        System.out.println("[2] Pool size: " + threadPoolExecutor.getPoolSize());

        threadPoolExecutor.submit(new SleepingTask(6));
    }

    static class SleepingTask implements Runnable {

        private final int id;

        public SleepingTask(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(99999);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
