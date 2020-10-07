package umn.algorithms2;

import java.util.Random;

public class Selection {

    // Finds the ith smallest element of arr.
    // Works in average case linear time. Assumes
    // elements in arr are distinct
    private int select(int[] arr, int i) {
        return 1;
    }

    // Here, p & r are the bounds of the array. This is going
    // to partition elements around A[q] such that each element in the
    // subarray before it are less than A[q], and helps in the subarray after
    // it are greater than A[q].
    // A[r] is used as pivot initially. The qth smallest element is also found.
    // Partition is linear.
    private int partition(int[] A, int p, int r) {
        int x = A[r];
        // i is current boundary between "less than x" and "greater than x"
        int i = p - 1;
        // j is the element currently being inspected
        for(int j = p; j <= r - 1; j++) {
            if(A[j] <= x) {
                i = i + 1;
                // Exchange A[i] with A[j]
                int temp = A[i];
                A[i] = A[j];
                A[j] = temp;
            }
        }

        // Put pivot in position
        int temp = A[i + 1];
        A[i + 1] = A[r];
        A[r] = temp;
        // q = i+1
        return i + 1;
    }

    // Chooses a randomized pivot location, moving it to A[r]
    private int randomizedPartition(int[] A, int p, int r) {
        Random random = new Random();
        int pivot = random.nextInt(r - p) + p;
        int temp = A[r];
        A[r] = A[pivot];
        A[pivot] = temp;

        // Pivot is now at A[r]
        return partition(A, p, r);
    }

    // The worse case can happen when either the smallest or largest item is chosen.
    // In this case, the entire arr has to be partitioned n times, so the worst case is O(n^2)
    private int randomizedSelect(int[] A, int p, int r, int i) {
        if(p == r) {
            return A[p];
        }

        int q = partition(A, p, r);
        // k is the number of elements in the smaller subarray
        int k = q - p + 1;

        if(i == k) {
            return A[q];
        } else if(i < k) {
            return randomizedSelect(A, p, q - 1, i);
        } else { // i > k
            return randomizedSelect(A, q + 1, r, i - k);
        }
    }

    /*
     * So the issue with the above approach, is that bad splits can be chosen.
     * To fix this, we must choose better splits from the get go.
     */

    public static void main(String[] args) {
        int[] arr = new int[]{1, 2, 6, 5, 4, 3};

        System.out.println("Ith smallest" + new Selection().randomizedSelect(arr, 0, arr.length - 1, 6));
    }

}
