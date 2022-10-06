import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    private static int counter = 0;
    private static int readers = 0;
    private static Semaphore writerLock = new Semaphore(1);
    private static Lock readerLock = new ReentrantLock();

    public static void main(String[] args) {
        for(int i = 0; i < 4; i++) {
            new Thread(new ReaderThread()).start();
        }

        for(int i = 0; i < 2; i++) {
            new Thread(new WriterThread()).start();
        }
    }

    static class ReaderThread implements Runnable {

        @Override
        public void run() {
            while(true) {
                readerLock.lock();
                readers++;
                if (readers == 1) {
                    try {
                        writerLock.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                readerLock.unlock();

                System.out.println("The value is: " + counter + " thread -> " + Thread.currentThread().getName());

                readerLock.lock();
                readers--;
                if (readers == 0) {
                    writerLock.release();
                }
                readerLock.unlock();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class WriterThread implements Runnable {

        @Override
        public void run() {
            while(true) {
                try {
                    writerLock.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                counter++;
                System.out.println("Writer thread updated the counter. Id -> " + Thread.currentThread().getId());
                writerLock.release();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}