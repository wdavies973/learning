package umn.algorithms2;

public class Practice {

    // Finds the greatest difference between buy and sell prices
    public static double maxStockPrice(double[] prices, int q, int p, int r) {
        if(p == r) {
            return prices[p];
        } else if(p >= r) {
            return Double.MIN_VALUE;
        }

        double min = Double.MAX_VALUE;
        int minIndex = -1;
        for(int i = p; i <= r; i++) {
            if(prices[i] < min) {
                min = prices[i];
                minIndex = i;
            }
        }

        if(prices[p] >= prices[q]) {
            q = p;
        }

        double profit = prices[q] - prices[minIndex];

        double other = maxStockPrice(prices, q + 1, p + 1, r);

        if(profit > other) {
            System.out.println("Max profit, take "+q+","+minIndex);
            return profit;
        } else {
            return other;
        }
    }

    public static void main(String[] args) {
        double[] prices = new double[]{30.5, 0.5, 2.5, 3.5, 9.5, 0.5, 0.01, 0.01};

        System.out.println(maxStockPrice(prices, 0, 1, prices.length - 1));
    }
}
