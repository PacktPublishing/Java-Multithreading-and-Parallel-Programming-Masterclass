import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final int N = 2000;

    private static final Random random = new Random();

    private static final int[][] A = new int[N][N];
    private static final int[][] B = new int[N][N];
    private static final int[][] C = new int[N][N];

    // Matrix multiplication results
    private static final int[][] result1 = new int[N / 2][N / 2];
    private static final int[][] result2 = new int[N / 2][N / 2];
    private static final int[][] result3 = new int[N / 2][N / 2];
    private static final int[][] result4 = new int[N / 2][N / 2];
    private static final int[][] result5 = new int[N / 2][N / 2];
    private static final int[][] result6 = new int[N / 2][N / 2];
    private static final int[][] result7 = new int[N / 2][N / 2];
    private static final int[][] result8 = new int[N / 2][N / 2];

    // 1. How many task types do we need to have? -> 2 tasks -> multiplication, a sum - task
    // 2. How can we identify the blocks that we need to multiply?
    // 3. How can we write the blocks in the output array?


    // Serial, 2K x 2K
    // Run 1 -> 54.48 seconds
    // Run 2 -> 54.15 seconds
    // Run 3 -> 53.80 seconds

    // Block parallel, 2K x 2K (4 threads)
    // Run 1 -> 23.81 seconds
    // Run 2 -> 24.20 seconds
    // Run 3 -> 25.16 seconds


    public static void main(String[] args) throws InterruptedException {
        initMatrixes();

//        printMatrix(A);
//        printMatrix(B);

        long start = System.nanoTime();
        multiplyParallelPerBlock();
//        multiplySerial();
        long end = System.nanoTime();

        System.out.println("Execution time = " + (end - start));

        checkResult();

//        printMatrix(C);
    }

    static void multiplyParallelPerBlock() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(8);

        Semaphore semaphore1 = new Semaphore(0);
        Semaphore semaphore2 = new Semaphore(0);
        Semaphore semaphore3 = new Semaphore(0);
        Semaphore semaphore4 = new Semaphore(0);

        // This computes upper-left quadrant
        executorService.submit(new BlockMultiplyTask(0, 0, 0, result1, semaphore1));
        executorService.submit(new BlockMultiplyTask(0, 0, N / 2, result2, semaphore1));
        executorService.submit(new SumTask(0, 0, result1, result2, semaphore1));

        // This computes upper-right quadrant
        executorService.submit(new BlockMultiplyTask(0, N / 2, 0, result3, semaphore2));
        executorService.submit(new BlockMultiplyTask(0, N / 2, N / 2, result4, semaphore2));
        executorService.submit(new SumTask(0, N / 2, result3, result4, semaphore2));

        // This computes bottom-left quadrant
        executorService.submit(new BlockMultiplyTask(N / 2, 0, 0, result5, semaphore3));
        executorService.submit(new BlockMultiplyTask(N / 2, 0, N / 2, result6, semaphore3));
        executorService.submit(new SumTask(N / 2, 0, result5, result6, semaphore3));

        // This computes bottom-right quadrant
        executorService.submit(new BlockMultiplyTask(N / 2, N / 2, 0, result7, semaphore4));
        executorService.submit(new BlockMultiplyTask(N / 2, N / 2, N / 2, result8, semaphore4));
        executorService.submit(new SumTask(N / 2, N / 2, result7, result8, semaphore4));

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }

    static class BlockMultiplyTask implements Runnable {
        private final int lineStart;
        private final int colStart;
        private final int blockStart;
        private final int[][] result;
        private final Semaphore semaphore;

        public BlockMultiplyTask(int lineStart, int colStart, int blockStart, int[][] result, Semaphore semaphore) {
            this.lineStart = lineStart;
            this.colStart = colStart;
            this.blockStart = blockStart;

            this.result = result;
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            for (int i = lineStart, resultX = 0; i < lineStart + N / 2; i++, resultX++) {
                for (int j = colStart, resultY = 0; j < colStart + N / 2; j++, resultY++) {

                    result[resultX][resultY] = 0;

                    for (int k = blockStart; k < blockStart + N / 2; k++) {
                        result[resultX][resultY] += A[i][k] * B[k][j];
                    }
                }
            }
            semaphore.release();
        }
    }

    static class SumTask implements Runnable {
        private final int lineStart;
        private final int colStart;
        private final int[][] resultA;
        private final int[][] resultB;
        private final Semaphore semaphore;

        public SumTask(int lineStart, int colStart, int[][] resultA, int[][] resultB, Semaphore semaphore) {
            this.lineStart = lineStart;
            this.colStart = colStart;
            this.resultA = resultA;
            this.resultB = resultB;
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                semaphore.acquire(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = lineStart, x = 0; i < lineStart + N / 2; i++, x++) {
                for (int j = colStart, y= 0; j < colStart + N / 2; j++, y++) {
                    C[i][j] = resultA[x][y] + resultB[x][y];
                }
            }
        }
    }

    static void multiplyParallel() throws InterruptedException {
        ExecutorService threadPool = Executors.newWorkStealingPool(8);

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                threadPool.submit(new MatrixMultiplyTask(i, j));
            }
        }

        threadPool.shutdown();
        threadPool.awaitTermination(1, TimeUnit.MINUTES);
    }

    static class MatrixMultiplyTask implements Runnable {

        private final int line;
        private final int column;
        public MatrixMultiplyTask(int line, int column) {
            this.line = line;
            this.column = column;
        }

        @Override
        public void run() {
            C[line][column] = 0;
            for (int i = 0; i < N; i++) {
                C[line][column] += A[line][i] * B[i][column];
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
        System.out.println("");
    }
}
