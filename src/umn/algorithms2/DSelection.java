package umn.algorithms2;

import java.util.Random;

/*
 * Finds the "ith" smallest element in worst case O(n) time
 */

/*
 * Basic idea:
 *
 * Sorting is a bit of overkill for this problem. Instead we can use the PARTITION idea
 * from quicksort to improve runtime.
 *
 * A very simple example to explain:
 * 4 7 3 18 13 15 19 12 23 5 14 17 16 20 27
 *
 * Using groups of n=3 to explain. Divide into groups of 3:
 * 4 7 3 | 18 13 15 | 19 12 23 | 5 14 17 | 16 20 27
 *
 * Next, medians are found by brute force (just use sorting of any kind):
 * 4 7 3 | 18 13 15 | 19 12 23 | 5 14 17 | 16 20 27
 *   4        15         19        14         20
 *
 * Find the median of these medians: 15
 *
 * Scan the input, forming two sets.
 * S1 = items <= 15
 * S2 = items > 15
 *
 * If i = S1n + 1, then return median of medians.
 * Otherwise, recurse on S1 or S2
 *
 */
public class DSelection {

    /*
     * Prints out information about the specified depth, any value >= 0
     */
    private static final int OUTPUT_DEPTH = -1;
    private static Random random;

    // Wrapper function for the below function
    public static double select(double[] array, int i) {
        return select(array, 0, array.length - 1, i);
    }

    // Selects the ith smallest element
    public static double select(double[] array, int p, int r, int i) {
        int n = r - p + 1;

        if(n == 1) return array[p];

        double[] medians = new double[numGroups(n)];

        /*
         * Step 1: Divide into (n/5) groups of 5 elements each (+
         * possibly one non-empty group of < 5 elements). Find the median
         * (3rd smallest) of each using bruteforce
         */

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

        /*
         * Step 2: Find the median of the medians
         */

        // Find medians
        for(int j = 0; j < n / 5; j++) {
            medians[j] = array[p + j * 5 + 2];
        }
        // Potentially remaining group
        if(n % 5 != 0) {
            medians[numGroups(n) - 1] = array[start + (n % 5 + 1) / 2 - 1];
        }

        // Find overall median
        double x = select(medians, 0, medians.length - 1, (medians.length + 1) / 2);

        int q = partition(array, p, r, x);

        int k = q - p + 1;

        if(i == k) return array[q];
        else if(i < k) return select(array, p, q - 1, i);
        else return select(array, q + 1, r, i - k);
    }

    // Partitions the array around the pivot value,
    public static int partition(double[] array, int p, int r, double pivot) {
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
                double temp = array[j];
                array[j] = array[i];
                array[i] = temp;
            }
        }

        array[r] = array[i + 1];
        array[i + 1] = pivot;

        return i + 1;
    }

    // Sort the array using insertion sort
    private static void sort(double[] array, int p, int length) {
        if(length < 1) return;

        for(int i = p + 1; i < p + length; i++) {
            int j = i - 1;

            double key = array[i];

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
    public static void shuffle(double[] array) {
        if(random == null) random = new Random();
        int count = array.length;
        for(int i = count; i > 1; i--) {
            int rand = random.nextInt(i);
            double temp = array[i - 1];
            array[i - 1] = array[rand];
            array[rand] = temp;
        }
    }

    public static void main(String[] args) {
        double[] array = new double[]{10, 3, 2, 1, 5, 6, 11, 9, 13, 52, 7, 14, 8, 12};
        shuffle(array);

        int i = 14; // the "ith" smallest element to find in array

        System.out.println("The " + i + "th smallest element, found in O(n) time is: " + DSelection.select(array, i));
    }
}
