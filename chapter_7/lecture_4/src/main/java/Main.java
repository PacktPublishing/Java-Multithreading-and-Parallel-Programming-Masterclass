import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Main {
    private static int N = 2000;
    private static int blockSize = N / 2;

    private static int[][] A = new int[N][N];
    private static int[][] B = new int[N][N];
    private static int[][] C = new int[N][N];

    private static int[][] result1 = new int[N / 2][N / 2];
    private static int[][] result2 = new int[N / 2][N / 2];
    private static int[][] result3 = new int[N / 2][N / 2];
    private static int[][] result4 = new int[N / 2][N / 2];
    private static int[][] result5 = new int[N / 2][N / 2];
    private static int[][] result6 = new int[N / 2][N / 2];
    private static int[][] result7 = new int[N / 2][N / 2];
    private static int[][] result8 = new int[N / 2][N / 2];

    // 1. How many types do we need to have? -> 2 tasks -> a multiply task, and a sum task
    // 2. How can we identify the blocks that we need to multiply?
    // 3. How can we synchronize the multiplication and sum tasks?
    // 4. How can we identify the place where the block should be written in the final matrix?
    // 5. The block size is equal to N / 2;

    // Serial, 2K x 2K
    // Run 1 -> 54.48 seconds
    // Run 2 -> 54.15 seconds
    // Run 3 -> 53.80 seconds

    // Block parallel, 2K x 2K (4 threads)
    // Run 1 -> 23.81 seconds
    // Run 2 -> 24.20 seconds
    // Run 3 -> 25.16 seconds

    // Speedup > 2X

    public static void main(String[] args) throws InterruptedException {
        initMatrixes();
        printMatrix(A);
        System.out.println("");
        printMatrix(B);

        long start = System.nanoTime();
        multiplyBlockParallel();

        long end = System.nanoTime();

        System.out.println("Execution time = " + (end - start));
        System.out.println("");
        printMatrix(C);
    }

    static void multiplyBlockParallel() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        Semaphore semaphore1 = new Semaphore(0);
        Semaphore semaphore2 = new Semaphore(0);
        Semaphore semaphore3 = new Semaphore(0);
        Semaphore semaphore4 = new Semaphore(0);

        // This computes the upper-left block
        executorService.submit(new MultiplyTask(0, 0, 0, result1, semaphore1));
        executorService.submit(new MultiplyTask(0, 0, N / 2, result2, semaphore1));
        executorService.submit(new SumTask(0, 0, result1, result2, semaphore1));

        // This computes the upper-right block
        executorService.submit(new MultiplyTask(0, N / 2, 0, result3, semaphore2));
        executorService.submit(new MultiplyTask(0, N / 2, N / 2, result4, semaphore2));
        executorService.submit(new SumTask(0, N / 2, result3, result4, semaphore2));

        // This computes the lower-left block
        executorService.submit(new MultiplyTask(N / 2, 0, 0, result5, semaphore3));
        executorService.submit(new MultiplyTask(N / 2, 0, N / 2, result6, semaphore3));
        executorService.submit(new SumTask(N / 2, 0, result5, result6, semaphore3));

        // This computes the lower-right block
        executorService.submit(new MultiplyTask(N / 2, N / 2, 0, result7, semaphore4));
        executorService.submit(new MultiplyTask(N / 2, N / 2, N / 2, result8, semaphore4));
        executorService.submit(new SumTask(N / 2, N / 2, result7, result8, semaphore4));

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }

    static class MultiplyTask implements Runnable {

        private final int lineStart;
        private final int colStart;
        private final int blockStart;
        private final int[][] result;
        private final Semaphore semaphore;

        public MultiplyTask(int lineStart, int colStart, int blockStart, int[][] result, Semaphore semaphore) {
            this.lineStart = lineStart;
            this.colStart = colStart;
            this.blockStart = blockStart;
            this.result = result;
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            for (int i = lineStart, x = 0; i < lineStart + blockSize; i++, x++) {
                for (int j = colStart, y = 0; j < colStart + blockSize; j++, y++) {

                    result[x][y] = 0;

                    for (int k = blockStart; k < blockStart + blockSize; k++) {
                        result[x][y] += A[i][k] * B[k][j];
                    }
                }
            }
            semaphore.release();
        }
    }

    static class SumTask implements Runnable {

        private final int lineStart;
        private final int colStart;
        private final int[][] result1;
        private final int[][] result2;
        private final Semaphore semaphore;

        public SumTask(int lineStart, int colStart, int[][] result1, int[][] result2, Semaphore semaphore) {
            this.lineStart = lineStart;
            this.colStart = colStart;
            this.result1 = result1;
            this.result2 = result2;
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                semaphore.acquire(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = lineStart, x = 0; i < lineStart + blockSize; i++, x++) {
                for (int j = colStart, y = 0; j < colStart + blockSize; j++, y++) {
                    C[i][j] = result1[x][y] + result2[x][y];
                }
            }
        }
    }


    static void multiplySerial() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {

                C[i][j] = 0;

                for (int k = 0; k < N; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }
    }

    static void initMatrixes() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                A[i][j] = 1;
                B[i][j] = 1;
            }
        }
    }

    static void checkResult() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (C[i][j] != N) {
                    System.out.println("Incorrect!");
                }
            }
        }
        System.out.println("Correct!");
    }

    static void printMatrix(int[][] matrix) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println("");
        }
    }
}
