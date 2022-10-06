import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    private static List<Lock> forks = new ArrayList<>();

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            forks.add(new ReentrantLock());
        }

        Semaphore semaphore = new Semaphore(4);

        for (int i = 0; i < 5; i++) {
            new Thread(new Philosoher(i, semaphore)).start();
        }

    }

    static class Philosoher implements Runnable {

        private final int id;
        private final Semaphore semaphore;

        public Philosoher(int id, Semaphore semaphore) {
            this.id = id;
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            while (true) {
                think();
                pick_forks();
                eat();
                put_forks();
            }
        }

        void pick_forks() {

            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            forks.get(id).lock();
            System.out.println("Philospher " + id + " picked the right fork");
            forks.get((id + 1) % 5).lock();
            System.out.println("Philospher " + id + " picked the left fork");
        }

        void put_forks() {
            forks.get(id).unlock();
            forks.get((id + 1) % 5).unlock();

            semaphore.release();
        }

        void think() {
            System.out.println("Philospher " + id + " thinks");
        }

        void eat() {
            System.out.println("Philospher " + id + " eats");
        }
    }
}