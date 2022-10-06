import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class Main {

    /*

    -> Intermediate result:

    [
        friend: 1,
        need: 1,
        ...
    ]

    -> Reducers input:

    [
        [
            friend: 1,
            friend: 1
        ],
        [
            a: 1,
            a: 1
        ],
        [
            need: 1
        ],
        ...
    ]



     */

    private static final String input = "a friend in need is a friend indeed";

    private static final CountDownLatch countDownLatch = new CountDownLatch(2);

    private static final List<Map.Entry<String, Integer>> intermediateResult = Collections.synchronizedList(new ArrayList<>());
    private static final List<List<Map.Entry<String, Integer>>> reducersInput = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws InterruptedException {
        List<String> inputList = Arrays.asList(input.split(" "));

        new Thread(new Mapper(inputList.subList(0, inputList.size() / 2))).start();
        new Thread(new Mapper(inputList.subList(inputList.size() / 2, inputList.size()))).start();

        Thread partitioner = new Thread(new Partitioner());
        partitioner.start();
        partitioner.join();

        for (List<Map.Entry<String, Integer>> reducerInput : reducersInput) {
            new Thread(new Reducer(reducerInput)).start();
        }
    }

    static class Mapper implements Runnable {
        private final List<String> input;

        public Mapper(List<String> input) {
            this.input = input;
        }

        @Override
        public void run() {
            for (String word : input) {
                intermediateResult.add(Map.entry(word, 1));
            }
            countDownLatch.countDown();
        }
    }

    static class Partitioner implements Runnable {

        @Override
        public void run() {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<String> uniqueWords = intermediateResult.stream()
                    .map(Map.Entry::getKey)
                    .distinct()
                    .collect(Collectors.toList());

            for (String word : uniqueWords) {
                List<Map.Entry<String, Integer>> reducerInput = intermediateResult.stream()
                        .filter(entry -> entry.getKey().equals(word))
                        .collect(Collectors.toList());
                reducersInput.add(reducerInput);
            }
        }
    }

    static class Reducer implements Runnable {

        private final List<Map.Entry<String, Integer>> reducerInput;

        public Reducer(List<Map.Entry<String, Integer>> reducerInput) {
            this.reducerInput = reducerInput;
        }

        @Override
        public void run() {
            int S = 0;
            for (Map.Entry<String, Integer> entry: reducerInput) {
                S += entry.getValue();
            }

            System.out.println("The word: " + reducerInput.get(0).getKey() + " -> occurences: " + S);
        }
    }
}
