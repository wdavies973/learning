package umn.algorithms1;

import java.util.Arrays;
import java.util.Random;

/*
 * Finds the "ith" smallest element in worst case O(n) time
 */
public class Selection {

    /*
     * Prints out information about the specified depth, any value >= 0
     */
    private static final int OUTPUT_DEPTH = 2;
    private static Random random;

    // Wrapper function for the below function
    public static int select(int[] array, int i) {
        return select(array, 0, array.length - 1, i, 0);
    }

    // Selects the ith smallest element
    private static int select(int[] array, int p, int r, int i, int depth) {
        int n = r - p + 1;

        if(n == 1) return array[p];

        // NOTE: Only used for debugging
        int[] original = Arrays.copyOf(array, array.length);

        int[] medians = new int[numGroups(n)];

        // First, use insertion sort on each subgroup
        for(int j = 0; j < n / 5; j++) {
            // If s = group number, then
            // s*5 = start position
            // s*5 + 4 == end position

            sort(array, p + j * 5, 5);
        }

        // Sort a potentially remaining subgroup at the end
        int start = p + (n / 5) * 5;
        sort(array, start, (n % 5));

        // Find medians
        for(int j = 0; j < n / 5; j++) {
            medians[j] = array[p + j * 5 + 2];
        }
        // Potentially remaining group
        if(n % 5 != 0) {
            medians[numGroups(n) - 1] = array[start + (n % 5 + 1) / 2 - 1];
        }

        // Find overall median
        int x = select(medians, 0, medians.length - 1, (medians.length + 1) / 2, -Integer.MAX_VALUE);

        // NOTE: Only used for debugging
        int[] groups = Arrays.copyOf(array, array.length);

        int q = partition(array, p, r, x);

        if(depth == OUTPUT_DEPTH) {
            System.out.println("----------------------DEPTH = " + depth + " ----------------------");
            System.out.println("p=" + p + " r=" + r + " i=" + i);
            System.out.println("Input: " + Arrays.toString(original));
            System.out.println("Groups: " + Arrays.toString(groups));
            System.out.println("Medians: " + Arrays.toString(medians));
            System.out.println("Median of medians: " + x);
            System.out.println("Partition: " + Arrays.toString(array));
            System.out.println("q=" + (q) + " k=" + (q - p + 1));
            System.out.println("--------------------------------------------------------------");
        }

        int k = q - p + 1;

        if(i == k) return array[q];
        else if(i < k) return select(array, p, q - 1, i, depth + 1);
        else return select(array, q + 1, r, i - k, depth + 1);
    }

    // Partitions the array around the pivot value,
    private static int partition(int[] array, int p, int r, int pivot) {
        // Search for the pivot value and exchange it into the last place,
        // there might be a better way to do this, but this still maintains the same
        // runtime of O(n) for partition
        for(int a = p; a <= r; a++) {
            if(array[a] == pivot) {
                array[a] = array[r];
                array[r] = pivot;
                break;
            }
        }

        // Pivot like normal
        int i = p - 1;
        for(int j = p; j < r - 1; j++) {
            if(array[j] <= pivot) {
                i = i + 1;
                int temp = array[j];
                array[j] = array[i];
                array[i] = temp;
            }
        }

        array[r] = array[i + 1];
        array[i + 1] = pivot;

        return i + 1;
    }

    // Sort the array using insertion sort
    private static void sort(int[] array, int p, int length) {
        if(length < 1) return;

        for(int i = p + 1; i < p + length; i++) {
            int j = i - 1;

            int key = array[i];

            while(j >= p && key < array[j]) {
                array[j + 1] = array[j];
                j--;
            }

            array[j + 1] = key;
        }
    }

    // returns number of groups
    private static int numGroups(int length) {
        return (length / 5) + (length % 5 == 0 ? 0 : 1);
    }

    // Randomizes the order of array, basically generates a different permutation
    // to help test select to make sure it returns the same result regardless
    // of ordering
    public static void shuffle(int[] array) {
        if(random == null) random = new Random();
        int count = array.length;
        for(int i = count; i > 1; i--) {
            int rand = random.nextInt(i);
            int temp = array[i - 1];
            array[i - 1] = array[rand];
            array[rand] = temp;
        }
    }

    public static void main(String[] args) {
        int[] array = new int[]{10, 3, 2, 1, 5, 6, 11, 9, 13, 52, 7, 14, 8, 12};
        shuffle(array);

        int i = 14; // the "ith" smallest element to find in array

        System.out.println("The " + i + "th smallest element, found in O(n) time is: " + Selection.select(array, i));
    }
}
