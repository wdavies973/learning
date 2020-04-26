package umn.algorithms1;

/*
 * For a rod of length n, and prices for each length P1, P2, P(n),
 * find the optimal divisions to optimize revenue.
 *
 * Interesting thought: Wouldn't it be more efficient to first sort the array
 * by most valuable per length amount, and then apply these lengths in order?
 */
public class CutRod {

    private double[] PRICES;

    private int[] solutions;

    public CutRod(double[] prices) {
        this.PRICES = prices;
    }

    /*
     * Figures out the maximum revenue possible for a rod of length n,
     * does not use memoization/dynamic programming and as such its
     * performance drops off very quickly.
     *
     * Runtime is 2^n
     */
    public double cutRod(int n) {
        if(n == 0) return 0; // a rod of length 0 is worth nothing

        double q = -Double.MAX_VALUE;

        for(int i = 1; i <= n; i++) {
            q = Math.max(q, PRICES[i - 1] + cutRod(n - i));
        }

        return q;
    }

    public double extendedBottomUpCutRod(int n) {
        double[] r = new double[n+1]; // stores already computed results
        int[] s = new int[n]; // stores the number of each length

        r[0] = 0;

        for(int j = 1; j <= n; j++) {
            double q = -Integer.MAX_VALUE;

            for(int i = 1; i <= j; i++) {
                if(q < PRICES[i-1] + r[j - i]) {
                    q = Math.max(q, PRICES[i-1] + r[j - i]);
                    s[j-1] = i;
                }
            }
            r[j] = q;
        }

        this.solutions = s;

        return r[n];
    }

    public int withCutCost(int[] prices, int n, int c) {
        int[] r = new int[n+1];
        r[0] = 0;

        for(int j = 1; j <= n; j++) {
            int q = prices[j-1];

            for(int i = 1; i < j; i++) {
                q = Math.max(q, prices[i-1] + r[j - i] - c);
            }

            r[j] = q;
        }
        return r[n];
    }

    public void printSolution(int n) {
        double result = extendedBottomUpCutRod(n);

        while(n > 0) {
            System.out.println(solutions[n-1]);
            n = n - solutions[n-1];
        }
    }

    public static void main(String[] args) {
        System.out.println(new CutRod(null).withCutCost(new int[]{5, 60, 12, 1}, 4, 100));
    }

}
