package umn4041;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

/*
 * Find k quantiles of an n-element set
 *
 * K quantiles define k-1 order statistics from the set that divide the sorted set into
 * k equal-sized sets (to within 1).
 *
 * As an example, let's say I have 100 (values 1 to 100, in order) elements and I'd like to find the 4 quantiles, then
 * I will need to find k - 1 = 3 order statistics, in this case, this would be 25, 50, and 75, the 25 smallest element,
 * the 50th smallest element, and the 75 smallest element.
 *
 * The function generates a UNIFORM distribution, so the order statistics may not be spaced out evenly as they are above
 * (with a difference of 25 between each one).
 *
 * The following code runs in O(n * lg(k)) time
 *
 *
 *
 */
public class KQuantiles {

    // Precondition: array must be sorted
    public static ArrayList<Integer> kQuantiles(int[] array, int k) {
        int n = array.length;

        ArrayList<Integer> quantiles = new ArrayList<>();
        int median = n / 2;

        if(k == 1) return new ArrayList<>();
        else {
            int i = k / 2;
            // This value represents the start of the middle quantile
            int x = Selection.select(array, (i * n) / k);

            // Partition around this middle quantile
            partition(array, x);

            // Repeat the process for each side
            quantiles.addAll(kQuantiles(sub(array, 0, i * n / k), i));
            quantiles.addAll(kQuantiles(sub(array,i * n / k + 1 , n-1), (int)Math.ceil(k / 2.0)));

            quantiles.add(x);
        }

        return quantiles;
    }

    // Returns the sub array a[p..r] inclusive
    private static int[] sub(int[] array, int p, int r) {
        if(p > r) throw new IllegalArgumentException("Invalid sub array");

        int[] copy = new int[r - p + 1];
        if(r + 1 - p >= 0) System.arraycopy(array, p, copy, 0, r + 1 - p);

        return copy;
    }

    private static int partition(int[] array, int pivot) {
        int p = 0;
        int r = array.length - 1;

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

    public static void main(String[] args) {
        int[] array = new int[100];
        for(int i = 1; i <= 100; i++) {
            array[i - 1] = i;
        }

        ArrayList<Integer> quantiles = kQuantiles(array, 2);
        for(Integer quantile : quantiles) {
            System.out.print(quantile + " ");
        }
    }

}
