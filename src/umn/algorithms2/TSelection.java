package umn.algorithms2;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

/*
 * Finds the "ith" smallest element in worst case O(n) time
 */
public class TSelection {

    /*
     * Prints out information about the specified depth, any value >= 0
     */
    private static Random random;

    // Wrapper function for the below function
    public static <T extends Comparable<T>> T select(T[] array, int i) {
        return select(array, 0, array.length - 1, i);
    }

    // Selects the ith smallest element
    public static <T extends Comparable<T>> T select(T[] array, int p, int r, int i) {
        int n = r - p + 1;

        if(n == 1) return array[p];

        // NOTE: Only used for debugging
        T[] original = Arrays.copyOf(array, array.length);

        @SuppressWarnings("unchecked")
        T[] medians = (T[]) Array.newInstance(Comparable.class, numGroups(n));

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
        T x = select(medians, 0, medians.length - 1, (medians.length + 1) / 2);

        // NOTE: Only used for debugging
        T[] groups = Arrays.copyOf(array, array.length);

        int q = partition(array, p, r, x);

        int k = q - p + 1;

        if(i == k) return array[q];
        else if(i < k) return select(array, p, q - 1, i);
        else return select(array, q + 1, r, i - k);
    }

    // Partitions the array around the pivot value,
    public static <T extends Comparable<T>> int partition(T[] array, int p, int r, T pivot) {
        // Search for the pivot value and exchange it into the last place,
        // there might be a better way to do this, but this still maintains the same
        // runtime of O(n) for partition
        for(int a = p; a <= r; a++) {
            if(array[a].compareTo(pivot) == 0) {
                array[a] = array[r];
                array[r] = pivot;
                break;
            }
        }

        // Pivot like normal
        int i = p - 1;
        for(int j = p; j < r - 1; j++) {
            if(array[j].compareTo(pivot) <= 0) {
                i = i + 1;
                T temp = array[j];
                array[j] = array[i];
                array[i] = temp;
            }
        }

        array[r] = array[i + 1];
        array[i + 1] = pivot;

        return i + 1;
    }

    // Sort the array using insertion sort
    private static <T extends Comparable<T>> void sort(T[] array, int p, int length) {
        if(length < 1) return;

        for(int i = p + 1; i < p + length; i++) {
            int j = i - 1;

            T key = array[i];

            while(j >= p && key.compareTo(array[j]) < 0) {
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
    public static <T> void shuffle(T[] array) {
        if(random == null) random = new Random();
        int count = array.length;
        for(int i = count; i > 1; i--) {
            int rand = random.nextInt(i);
            T temp = array[i - 1];
            array[i - 1] = array[rand];
            array[rand] = temp;
        }
    }

    public static void main(String[] args) {
        Integer[] array = new Integer[]{10, 3, 2, 1, 5, 6, 11, 9, 13, 52, 7, 14, 8, 12};
        shuffle(array);

        int i = 13; // the "ith" smallest element to find in array

        System.out.println("The " + i + "th smallest element, found in O(n) time is: " + TSelection.select(array, i));

        Double[] nums = new Double[]{1.3, 1.4, 1.5, 1.8, 1.9, 2.2};
        shuffle(nums);

        int j = 2;

        System.out.println("The "+ j + "th smallest element, found in O(n) time is: "+TSelection.select(nums, j));
    }
}
