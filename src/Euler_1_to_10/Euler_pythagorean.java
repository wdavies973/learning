package Euler_1_to_10;// Problem #9 on Euler

// I knew when I was making this it was not the fastest solution, but the time taken to perform this
// action is not all that much more, and the program is not all that mutable. This was a really
// easy way of doing it though. One way in which I could have improve my code was having the loop begin
// at 300 rather than build up to 300, as I was confident non of the values would exceed that.
// Interesting how close to 300 variable b was though.

import java.lang.Math;

public class Euler_pythagorean {

    private static int[] triplet() {
        int[] arr = new int[3];
        for (int i = 0; i < 300; i++) {
            for (int k = 0; k < 300; k++) {
                int c = (int) Math.sqrt(Math.pow(i, 2) + Math.pow(k,2));
                if ((i + k + c) == 1000) {
                    arr[0] = i;
                    arr[1] = k;
                    arr[2] = c;
                    return arr;
                }
            }
        }
        return arr;
    }
    public static void main (String[] args) {
        int[] arr = triplet();
        System.out.println("a = " + arr[0]);
        System.out.println("b = " + arr[1]);
        System.out.println("c = " + arr[2]);
    }
}
