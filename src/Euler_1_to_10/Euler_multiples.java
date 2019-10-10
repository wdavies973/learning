package Euler_1_to_10;//problem #1 Euler

public class Euler_multiples {

    public static void main (String[] args) {
        int total = 0;
        for (int i = 0; i < 1000; i++) {
            if ((i % 3 == 0) || (i % 5 == 0)) {
                total += i;
            }
        }
        System.out.println(total);
    }
}
