public class Euler_Even_Fib {

    public static void main (String[] args) {
        int a = 1;
        int b = 2;
        int sum = 2;
        while (b < 4000000) {
            int c = a + b;
            a = b;
            b = c;
            if (b % 2 == 0) {
                sum += b;
            }
        }
        System.out.println(sum);
    }
}
