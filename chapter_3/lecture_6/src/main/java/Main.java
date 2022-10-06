import java.util.ArrayList;
import java.util.List;

public class Main {

    private static int globalCounter = 0;

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();

        ThreadGroup group = new ThreadGroup("Group1");

        for (int i = 1; i<=1000; i++) {
            Thread t = new Thread(group, new MyThread());
            t.start();
            threads.add(t);
        }

        group.interrupt();

        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println("Total = " + globalCounter);
    }

    static class MyThread implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(99999);
            } catch (InterruptedException e) {

            }
            globalCounter++;

//            int localCounter = globalCounter;
//            localCounter = localCounter + 1;
//            globalCounter = localCounter;
        }
    }

//    private static ThreadLocal<String> threadLocal = ThreadLocal.withInitial(() -> "initialValue");
//
//    public static void main(String[] args) {
//        Thread t1 = new Thread(new MyThread());
//        Thread t2 = new Thread(new MyThread());
//
//        t1.start();
//        t2.start();
//    }
//
//    static class MyThread implements Runnable {
//        @Override
//        public void run() {
//            int counter = 0;
//
//            threadLocal.set("myValue");
//            threadLocal.get();
//        }
//    }
}
