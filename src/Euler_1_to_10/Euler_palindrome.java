package Euler_1_to_10;// problem #4 on Euler

public class Euler_palindrome {

    private static String finder () {
        int max = 999;
        int largest= 0;
        int max_i = 0;
        int max_j = 0;
        for (int i = max; i > 1; i--) {
            for (int j = max; j > 1; j--) {
                String str = "" + (i * j);
                boolean pal = true;
                for (int k = 0; k < str.length() / 2; k++) {
                    if (str.charAt(k) != str.charAt(str.length() - k - 1)) {
                        pal = false;
                        break;
                    }
                }
                if (pal && largest < i * j) {
                    largest = i * j;
                    max_i = i;
                    max_j = j;
                }
            }
        }
        return "" + max_i + " * " + max_j;
    }
    public static void main (String[] args) {

        System.out.println(finder());
    }
}
