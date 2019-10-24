package umn4041;

import java.util.Arrays;

public class TwoArrayMedian {

    // already sorted, find the median in O(lg(n)) time
    // same length
    public static double getMedian(int[] A, int[] B) {
        int m = A.length;
        int n = B.length;

        int iMin = 0, iMax = m, halfLen = (m + n + 1) / 2;
        while(iMin <= iMax) {
            int i = (iMin + iMax) / 2;
            int j = halfLen - i;
            if(i < iMax && B[j - 1] > A[i]) {
                iMin = i + 1; // i is too small
            } else if(i > iMin && A[i - 1] > B[j]) {
                iMax = i - 1; // i is too big
            } else { // i is perfect
                int maxLeft = 0;
                if(i == 0) {
                    maxLeft = B[j - 1];
                } else if(j == 0) {
                    maxLeft = A[i - 1];
                } else {
                    maxLeft = Math.max(A[i - 1], B[j - 1]);
                }
                if((m + n) % 2 == 1) {
                    return maxLeft;
                }

                int minRight = 0;
                if(i == m) {
                    minRight = B[j];
                } else if(j == n) {
                    minRight = A[i];
                } else {
                    minRight = Math.min(B[j], A[i]);
                }

                return (maxLeft + minRight) / 2.0;
            }
        }
        return 0.0;
    }

    // Returns the sub array a[p..r] inclusive
    private static int[] sub(int[] array, int p, int r) {
        System.out.println("p= " + p + " r= " + r);

        int[] copy = new int[r - p + 1];
        if(r + 1 - p >= 0) System.arraycopy(array, p, copy, 0, r + 1 - p);

        return copy;
    }

    static int median(int arr[], int n) {
        if(n % 2 == 0)
            return (arr[n / 2] + arr[n / 2 - 1]) / 2;
        else
            return arr[n / 2];
    }

    private static int[] concat(int[] x, int[] y) {
        if(x.length != y.length) throw new IllegalArgumentException("Must be same length");

        int[] copy = new int[x.length + y.length];

        for(int i = 0; i < x.length; i++) {
            copy[i] = x[i];
            copy[i + x.length] = y[i];
        }

        return copy;
    }

    public static void main(String[] args) {
        int[] x = {100, 320, 400, 900};
        int[] y = {1, 200, 600, 800};
        int[] meme = concat(x, y);
        Arrays.sort(meme);

        System.out.println(Arrays.toString(meme) + ", median=" + meme[((meme.length + 1) / 2) - 1]);

        System.out.println(getMedian(x, y));
    }

}
