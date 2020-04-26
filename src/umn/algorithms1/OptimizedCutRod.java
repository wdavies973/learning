package umn.algorithms1;

import java.util.*;

/*
 * A (potentially?) more optimized version of the cut rod problem.
 *
 *
 * @author Will Davies
 */
public class OptimizedCutRod {

    private PricePoint[] sortedPriceCache;

    private static class PricePoint {
        int length;
        double pricePerUnit;

        public PricePoint(int length, double pricePerUnit) {
            this.length = length;
            this.pricePerUnit = pricePerUnit;
        }
    }

    /*
     * The following constructor creates the lookup array which is super simple,
     * sort the array in decreasing order, using "price per unit" as keys.
     * This could be accomplished in only 1 pass if incorporated directly in the
     * sorting algorithm, but this was easier, and I'm lazy.
     */
    public OptimizedCutRod(double[] prices) {
        sortedPriceCache = new PricePoint[prices.length];

        for(int i = 0; i < prices.length; i++) {
            // Create an array of PricePoints, also calculate the effective price per length
            sortedPriceCache[i] = new PricePoint((i + 1), prices[i] / (i + 1.0));
        }

        // Sort price points in reverse order, using merge sort for O(nlgn) time
        new MergeSort().sort(sortedPriceCache, PricePoint.class, new Comparator<PricePoint>() {
            @Override
            public int compare(PricePoint o1, PricePoint o2) {
                return Double.compare(o2.pricePerUnit, o1.pricePerUnit);
            }
        });
    }

    /*
     * Using the lookup table, try to apply the most valuable cuts first.
     *
     * -If we run out of rod to cut, return
     * -If the most valuable cut is longer than the remaining rod (maybe after applying the most valuable cut
     *  a few times), try to apply the next most valuable cut, and so on
     * -Eventually, we'll hit the cut of length 1, the algorithm will always terminate when we hit the cut of length 1,
     * so we're always guaranteed that cutRod halts
     *
     *
     *
     */
    public void cutRod(int n) {
        int index = 0;
        int currentLength = sortedPriceCache[0].length;

        while(n >= 0) {
            // Check if the current length can be applied to the remaining length
            if(currentLength <= n) {
                System.out.print(currentLength + " ");
                n -= currentLength;
            } else {
                // Move on to the next length
                if(index < sortedPriceCache.length) {
                    currentLength = sortedPriceCache[index].length;
                    index++;
                } else {
                    break;
                }
            }
        }
    }

    // Generate a random number within a range
    public static double getRandom(double min, double max) {
        return (Math.random() * (max + 1 - min)) + min;
    }

    public static void main(String[] args) {

        /*
         * Generate some random prices per lengths,
         * count specifies how many prices we'd like to define,
         * prices are generated between a range:
         * $1/unit - $50/unit
         */
        int count = 1000000;

        double[] prices = new double[count];
        for(int i = 0; i < prices.length; i++) {
            prices[i] = getRandom(i + 1, 50 * (i + 1));
        }

        OptimizedCutRod optimizedCutRod = new OptimizedCutRod(prices);
        CutRod cutRod = new CutRod(prices); // implemented as shown in the book

        long time = System.nanoTime();

        // Haven't gotten it to halt in any reasonable time
        cutRod.printSolution(count);

        System.out.println("\nGiven solution time: " + (System.nanoTime() - time));

        // Halts instantly
        time = System.nanoTime();

        optimizedCutRod.cutRod(count);

        System.out.println("\nPotentially optimized solution time: " + (System.nanoTime() - time));
    }

}
