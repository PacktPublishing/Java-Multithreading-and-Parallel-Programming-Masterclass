import java.util.concurrent.Phaser;

public class Main {

    private static int S = 0;
    private static int[] array = new int[] {1, 2, 3, 4, 5, 6, 7, 8};

    private static Phaser phaser = new Phaser(1);

    public static void main(String[] args) {
        for (int i = 0; i < array.length; i++) {
            Thread t = new Thread(new WorkerThread(i));
            t.start();
        }

        phaser.arriveAndAwaitAdvance();
        phaser.arriveAndAwaitAdvance();

        System.out.println("The sum is: " + S);
        System.out.println("Phase count: " + phaser.getPhase());

    }

    static class WorkerThread implements Runnable {

        private final int threadIndex;

        public WorkerThread(int threadIndex) {
            this.threadIndex = threadIndex;
            phaser.register();
        }

        @Override
        public void run() {
            array[threadIndex] = array[threadIndex] * 2;
            phaser.arriveAndAwaitAdvance();

            if (threadIndex == 0) {
                for (int j : array) {
                    S = S + j;
                }
                phaser.arriveAndAwaitAdvance();
            } else {
                phaser.arriveAndDeregister();
            }
        }
    }
}