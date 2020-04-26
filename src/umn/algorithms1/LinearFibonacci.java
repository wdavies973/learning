package umn.algorithms1;

public class LinearFibonacci {


    public int nthFibonacci(int n) {
        int a = 0;
        int b = 1;
        int c;

        for(int i = 0; i < n; i++) {
            c = a + b;
            a = b;
            b = c;
        }

        return a;
    }

    public static void main(String[] args) {
        LinearFibonacci fib = new LinearFibonacci();

        for(int i = 0; i < 15; i++) {
            System.out.print(new LinearFibonacci().nthFibonacci(i)+" ");
        }

        System.out.println(new LinearFibonacci().nthFibonacci(3));
    }

}
