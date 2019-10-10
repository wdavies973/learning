// problem #7 on Euler

// When looking at a solution to compare it to my own, I was reminded that all prime
// numbers except 2 and 3 follow form 6x +- 1, which allow for much more optimized
// solutions. My solution follows the sieve approach.

import java.util.Arrays;

public class Euler_primes10001 {

    private static int prime (int num) {
        boolean[] primes = new boolean[num * 50];
        int count = 0;
        Arrays.fill(primes, true);

        for (int i = 2; i < primes.length; i++) {
            if (primes[i] == true) {
                count++;
                if (count == num) {
                    return i;
                }
                for (int j = 2; (j * i) < primes.length; j++) {
                    primes[j * i] = false;
                }
            }
        }
        return 0;
    }

    public static void main (String[] args) {
        int index = prime(10001);
        System.out.println(index);
    }
}
