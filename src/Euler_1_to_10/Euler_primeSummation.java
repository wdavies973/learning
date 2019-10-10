package Euler_1_to_10;// Problem #10 on Euler

// From doing problem 9, I learned java initiates array values to 0 if I don't do anything, so that
// was not a worry. Used Sieve method, though I know primes follow form 6n +- 1 from previous problems,
// I just think sieve is a cool solution. Having the long verses the int actually got me for a few minutes.

public class Euler_primeSummation {

    private static long summation (int max) {
        boolean[] primes = new boolean[max + 1];
        java.util.Arrays.fill(primes, true);
        long total = 0L;
        for (int i = 2; i <= max; i++) {
            if (primes[i]) {
                total += i;
                for (int k = 2; (k * i) <= max; k++) {
                    primes[i * k] = false;
                }
            }
        }
        return total;
    }
    public static void main (String[] args) {
        int num = 2000000;
        System.out.println(summation(num));
    }
}
