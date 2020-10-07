package umn.algorithms2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HW1 {

    // D&C to eliminate half the search space on a digit j
    //

    // bit=0, bit=1, bit=2
    private static int findMissingInteger(List<Integer> list, int bit) {
        int oddCount = 0;
        int evenCount = 0;

        for(Integer i : list) {
            // 0 or 1
            int value = (i >> bit) & 1;

            if(value == 0) {
                evenCount++;
            } else {
                oddCount++;
            }
        }

        if(oddCount + evenCount == 1) {
            // Complement
            return list.get(0) ^ (1 << bit);
        }

        // Delete
        for(int i = 0; i < list.size(); i++) {
            int value = (list.get(i) >> bit) & 1;

            if(value == 0) {
                if(oddCount < evenCount) {
                    list.remove(i);
                    i--;
                }
            } else {
                if(evenCount < oddCount) {
                    list.remove(i);
                    i--;
                }
            }
        }

        // bit << 1 | 1
        // 2 | 1

        return findMissingInteger(list, bit + 1);
    }

    private static int[] multiply(int k, int[] v) {
        return null;
    }

    // Will generate the respective matrix of size nxn
    private static int[][] generateRecursion(int n) {
        if(n == 1) {
            return new int[][]{{1}};
        }

        int[][] m = new int[n][n];

        int half = n / 2;

        // Make a recursive request for 2 sub-problems
        int[][] previous = generateRecursion(half);

        // O((n/2)^2)
        for(int row = 0; row < half; row++) {
            for(int col = 0; col < half; col++) {
                // Write into m
                // Upper left
                m[row][col] = previous[row][col];
                // Bottom left
                m[row + half][col] = previous[row][col];
                // UPper right
                m[row][col + half] = previous[row][col];
                // Bottom right
                m[row + half][col + half] = -1 * previous[row][col];
            }
        }

        return m;
    }

    private static int[] hadamard(int[] vector) {
        if(vector.length == 1) {
            return vector;
        }

        int half = vector.length / 2;

        int[] v1 = new int[half];
        int[] v2 = new int[half];

        for(int i = 0; i < half; i++) {
            v1[i] = vector[i] + vector[i+half];
            v2[i] = vector[i] - vector[i+half];
        }

        int[] top = hadamard(v1);
        int[] bottom = hadamard(v2);

        for(int i = 0; i < half; i++) {
            vector[i] = top[i];
            vector[i+half] = bottom[i];
        }

        return vector;
    }

    public static void main(String[] args) {
       List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 0, 6, 4, 5, 7));

       System.out.println(findMissingInteger(list, 0));
    }

}
