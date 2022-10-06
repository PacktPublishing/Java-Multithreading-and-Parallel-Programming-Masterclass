import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    private static Lock lock1 = new ReentrantLock();
    private static Lock lock2 = new ReentrantLock();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            lock2.tryLock();
            System.out.println("Thread 1 acquired lock2");

            lock1.lock();
            System.out.println("Thread 1 acquired lock1");
            lock1.unlock();

            lock2.unlock();
        });

        Thread t2 = new Thread(() -> {
            lock2.lock();
            System.out.println("Thread 2 acquired lock2");

            lock1.lock();
            System.out.println("Thread 2 acquired lock1");
            lock1.unlock();

            lock2.unlock();
        });

        t1.start();
        t2.start();
    }
}
