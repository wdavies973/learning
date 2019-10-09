import java.lang.Math;

//problem # 5 on Euler

// This program is only meant to work on variables that are at least +2. If a
// smaller variable needs to be used, I guess special cases could be added
// fairly easily

public class Euler_multiple {

    private static int primes (int num) {
        int[] primes = new int[num + 1];
        int total = 1;

        for (int i = 0; i <= num; i++) { //array begins at index 0, holds nonzero values beginning at index 1
            primes[i] = 0;
        }

        for (int i = 2; i <= num; i++) { //goes through the numbers between 1 and (num)
            int current = i;
            int [] inst = new int[i + 1];

            for (int j = 0; j <= i; j++) { //initiates inst array, values beginning at [1]
                inst[j] = 0;
            }


            for (int j = 2; j <= current; j++) { //breaks the given number current into its primes
                if (current % j == 0) {
                    inst[j] = inst[j] + 1;
                    current = current / j;
                    j = 1;
                }
            }

            for (int k = 2; k <= i; k++) { //compares the numbers of the given current number, makes prime recurrence larger if necessary
                if (inst[k] > primes[k]) {
                    primes[k] = inst[k];
                }
            }
        }

        for (int i = 2; i <= num; i++) {
            //System.out.println("at index " + i + " there are " + primes[i] + " instances");
            if (primes[i] != 0) {
                total *= Math.pow(i, primes[i]);
            }
        }

        return total;
    }

    public static void main (String[] args) {
        int total = 20;

        System.out.println(primes(total));
    }
}
