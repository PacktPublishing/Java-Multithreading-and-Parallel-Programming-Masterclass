public class Main {
//    public static void main(String[] args) throws InterruptedException {
//        Thread thread = Thread.currentThread();
//        System.out.println("Current thread: " + thread.getName());
//
//        Thread.sleep(3000);
//
//        System.out.println("Current thread: " + thread.getName());
//    }

//    public static void main(String[] args) {
//        // 1. Extend the Thread class
//        // 2. Use Runnable interface
//
//        MyThread myThread = new MyThread();
//        myThread.run();
//    }
//
//    static class MyThread extends Thread {
//        public void run() {
//            setName("MyThread");
//            System.out.println("Current thread: " + Thread.currentThread().getName());
//        }
//    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("[1] Current thread: " + Thread.currentThread().getName());

        Thread thread = new Thread(
                () -> {
                    System.out.println("[2] Current thread: " + Thread.currentThread().getName());
                }
        );
        thread.setName("MyThread");
        thread.start();
        thread.join();
        System.out.println("[3] Current thread: " + Thread.currentThread().getName());
    }
}