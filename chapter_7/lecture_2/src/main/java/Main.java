import java.util.Arrays;

public class Main {

    private static int arraySize = 20;
    private static int numberToSearch = 5;
    private static int[] array = new int[arraySize];

    public static void main(String[] args) {
        for (int i = 0; i < arraySize; i++) {
            array[i] = i;
        }

        System.out.println(Arrays.toString(array));

        System.out.println("Number = " + binarySearch(0, arraySize - 1));
    }

    static int binarySearch(int left, int right) {
        int mid = (left + right) / 2;

        if (right < left) {
            return -1;
        }

        if (array[mid] == numberToSearch) {
            return mid;
        } else if (array[mid] < numberToSearch) {
            return binarySearch(mid, right);
        } else {
            return binarySearch(left, mid);
        }
    }
}
