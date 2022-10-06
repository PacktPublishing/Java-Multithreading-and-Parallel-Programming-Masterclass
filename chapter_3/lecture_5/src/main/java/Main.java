import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(new CustomThreadGroup("group1"), new MyThread(1), "Thread1");

        Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable exception) -> {
            System.out.println(exception.getMessage());
        });

//        thread1.setUncaughtExceptionHandler((Thread t, Throwable exception) -> {
//            System.out.println(exception.getMessage());
//        });

        thread1.start();
        thread1.join();
    }

    static class CustomThreadGroup extends ThreadGroup {

        public CustomThreadGroup(String name) {
            super(name);
        }

        public CustomThreadGroup(ThreadGroup parent, String name) {
            super(parent, name);
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            super.uncaughtException(t, e);
            System.out.println(e.getMessage());
        }
    }

    static class MyThread implements Runnable {
        private final int numberOfSeconds;

        public MyThread(int numberOfSeconds) {
            this.numberOfSeconds = numberOfSeconds;
        }

        @Override
        public void run() {
            for(int i = 0; i < numberOfSeconds; i++) {
                try {
                    System.out.println("Sleeping for 1s, thread: " + Thread.currentThread().getName());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            List<String> list = null;
            list.size();

            System.out.println("Result: " + (4 / 0));
        }
    }
}
