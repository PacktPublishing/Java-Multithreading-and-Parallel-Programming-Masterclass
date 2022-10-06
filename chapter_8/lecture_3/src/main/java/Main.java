import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    private static int customers = 0;
    private static Lock customersLock = new ReentrantLock();

    private static Semaphore customer = new Semaphore(0);
    private static Semaphore barber = new Semaphore(0);

    private static Semaphore customerFinished = new Semaphore(0);
    private static Semaphore barberFinished = new Semaphore(0);

    private static String customerName = "";
    private static Semaphore nameExchanged = new Semaphore(0);

    public static void main(String[] args) {
        new Thread(new Barber()).start();

        for (int i = 0; i < 10; i++) {
            new Thread(new Customer("John_" + i)).start();
        }

    }

    static class Barber implements Runnable {

        @Override
        public void run() {
            while(true) {
                acquire(customer);
                barber.release();

                acquire(nameExchanged);
                System.out.println("[BARBER] Doing the hair cut " + customerName);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                barberFinished.release();
                acquire(customerFinished);

            }
        }

        void acquire(Semaphore semaphore) {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Customer implements Runnable {

        private final String name;

        public Customer(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            customersLock.lock();
            if (customers == 4) {
                customersLock.unlock();
                System.out.println("Room is full, customer " + name + " leaves");
                return;
            }
            customers++;
            customersLock.unlock();

            customer.release();
            acquire(barber);

            customerName = name;
            nameExchanged.release();

            acquire(barberFinished);
            customerFinished.release();

            customersLock.lock();
            customers--;
            customersLock.unlock();
        }

        void acquire(Semaphore semaphore) {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
