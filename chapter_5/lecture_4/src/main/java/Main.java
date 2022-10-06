import java.util.concurrent.Exchanger;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Exchanger<String> exchanger = new Exchanger<>();

        Thread t = new Thread(() -> {
            try {
                String receivedValue = exchanger.exchange("value1");
                System.out.println("Received: " + receivedValue + " in thread " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t.start();

        String receivedValue = exchanger.exchange("value2");
        System.out.println("Received: " + receivedValue + " in thread " + Thread.currentThread().getName());
    }
}
