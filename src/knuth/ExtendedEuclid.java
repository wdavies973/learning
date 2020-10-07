package knuth;

public class ExtendedEuclid {

    // finds greatest common denominator of m & n, along with an a & b such that am + bn = d
    public static void extendedEuclid(int m, int n) {
        int a, b, c;
        int d;
        int aPrime, bPrime;

        aPrime = b = 1;
        a = bPrime = 0;
        c = m;
        d = n;

        while(true) {
            int q = c / d;
            int r = c % d;
            // c = qd + r - definition of division
            // 0 <= r < d

            // The following hold true:
            // a'm + b'n = c
            // am + bn = d

            if(r == 0) {
                System.out.println(a+"m + "+b+"n"+" = "+d);
                break;
            }

            c = d;
            d = r;
            int t = aPrime; // temp var

            // adjust a
            aPrime = a;
            a = t - q * a;

            // adjust b
            t = bPrime;
            bPrime = b;
            b = t - q * b;
        }
    }

    // Well, how do I know its correct? For all m & n


    public static void main(String[] args) {
        extendedEuclid(20, 30);
    }

}
