import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    private static Lock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();
    private static Condition condition1 = lock.newCondition();

    public static void main(String[] args) {
        Queue<String> queue = new LinkedList<>();

        Thread producer = new Thread(new Producer(queue));
        Thread consumer = new Thread(new Consumer(queue));

        producer.start();
        consumer.start();
    }

    static class Producer implements Runnable {
        private final Queue<String> queue;

        public Producer(Queue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while(true) {
                try {
                    produceData();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void produceData() throws InterruptedException {
            lock.lock();
            if (queue.size() == 10) {
                System.out.println("In producer, waiting...");
                condition.await();
            }

            Thread.sleep(1000);

            System.out.println("Producing data with id " + queue.size());
            queue.add("element_" + queue.size());

            if (queue.size() == 1) {
                condition.signal();
            }
            lock.unlock();
        }
    }

    static class Consumer implements Runnable {

        private final Queue<String> queue;

        public Consumer(Queue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    consumeData();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void consumeData() throws InterruptedException {
            lock.lock();
            if (queue.isEmpty()) {
                System.out.println("Consumer is waiting...");
                condition.await();
            }

            Thread.sleep(700);

            String data = queue.remove();
            System.out.println("Consumed data: " + data);

            if (queue.size() == 9) {
                condition.signal();
            }
            lock.unlock();
        }
    }
}
