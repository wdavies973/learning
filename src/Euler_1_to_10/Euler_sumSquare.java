package Euler_1_to_10;// problem #6 on Euler

//Found a way better solution online when comparing answers,
// but this is what I thought of, and the other one is an equation.

public class Euler_sumSquare {

    private static long summed_squares (int max) {
        long total = 0;

        for (int i = 0; i <= max; i++) {
            total += Math.pow(i, 2);
        }

        return total;
    }

    private static long squared_sums (int max) {
        long total = 0L;

        for (int i = 0; i <= max; i++) {
            total += i;
        }

        total = (int) Math.pow(total, 2);
        return total;
    }


    public static void main (String[] args) {
        int max = 100;

        long summed = summed_squares(max);
        long squared = squared_sums(max);
        long difference = squared - summed;
        System.out.println(difference);
    }
}
