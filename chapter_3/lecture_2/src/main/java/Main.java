public class Main {
//    public static void main(String[] args) throws InterruptedException {
//        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
//
//        Thread thread1 = new Thread(() -> {
//            Thread currentThread = Thread.currentThread();
//            System.out.println(currentThread.getName() + " priority = " + currentThread.getPriority());
//        });
//
//        thread1.setName("Thread_1");
//        thread1.setPriority(Thread.MAX_PRIORITY);
//
//        Thread thread2 = new Thread(() -> {
//            Thread currentThread = Thread.currentThread();
//            System.out.println(currentThread.getName() + " priority = " + currentThread.getPriority());
//        });
//
//        thread2.setName("Thread_2");
//        thread2.setPriority(Thread.MIN_PRIORITY);
//
//        thread1.start();
//        thread2.start();
//
//        thread1.join();
//        thread2.join();
//    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            Thread currentThread = Thread.currentThread();
            System.out.println("[1] State: " + currentThread.getState());
        });

        System.out.println("[2] State: " + thread.getState());

        thread.start();

        System.out.println("[3] State: " + thread.getState());

        thread.join();

        System.out.println("[4] State: " + thread.getState());
    }
}
