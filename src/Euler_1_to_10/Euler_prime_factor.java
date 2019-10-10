package Euler_1_to_10;//problem # 3 Euler

public class Euler_prime_factor {

    private static int divider (Long num) {
        long current = num;
        int largest = 2;

        for (int i = 2; i <= current; i++) {
            if (current % i == 0) {
                current = current / i;
                largest = i;
                i = 2;
            }
        }
        return largest;
    }

    public static void main (String[] args) {
        long current = 600851475143L;

        System.out.println(divider (current));

    }
}
