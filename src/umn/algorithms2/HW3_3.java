package umn.algorithms2;

import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

// https://rcoh.me/posts/linear-time-median-finding/
public class HW3_3 {

//    private int[] discreteKnapsack(int n, double capacity, double[] values, double[] weights) {
//        Double[] ratios = new Double[n];
//
//        for(int i = 0; i < n; i++) {
//            ratios[i] = values[i] / weights[i];
//        }
//
//        Double median = TSelection.select(ratios, n / 2);
//
//        int q = TSelection.partition(ratios, 0, ratios.length - 1, median);
//
//        double equal = 0, least = 0, most = 0;
//
//        for(int i = 0; i < n; i++) {
//            if(ratios[i] < median) {
//                least += weights[i];
//            } else if(ratios[i] > median) {
//                most += weights[i];
//            } else {
//                equal += weights[i];
//            }
//        }
//
//        if(most > capacity) {
//
//        } else
//
//    }

    private static class Item implements Comparable<Item> {
        int i;
        double weight;
        double value;

        public Item(int i, double weight, double value) {
            this.i = i;
            this.weight = weight;
            this.value = value;
        }

        @Override
        public int compareTo(Item o) {
            return Double.compare(value, o.value);
        }

        @Override
        public String toString() {
            return "Item{" +
                    "i=" + i +
                    ", weight=" + weight +
                    ", value=" + value +
                    '}';
        }
    }

    public static HashSet<Item> linearDiscreteKnapsack(int p, int r, double w, Item[] items) {
        int n = r - p + 1;

        if(p == r) {
            return items[p].weight <= w ? new HashSet<Item>(Collections.singleton(items[p])) : new HashSet<>();
        }

        Item median = TSelection.select(items, p, r, (n - 1) / 2 + 1);

        int q = TSelection.partition(items, p, r, median);

        // Compute the total weights of three groups:
        // Left: The group of items which have values less than or equal to median
        // Right: The group of items which have values greater than the median

        double weightLE = 0;
        double weightG = 0;

        for(int i = p; i <= r; i++) {
            if(items[i].value <= median.value) {
                weightLE += items[i].weight;
            } else {
                weightG += items[i].weight;
            }
        }

        if(weightG > w) {
            return linearDiscreteKnapsack(q + 1, r, w, items);
        } else {
            // All items can fit within greater
            HashSet<Item> take = new HashSet<>();

            // Take greater items
            for(int i = q + 1; i <= r; i++) {
                take.add(items[i]);
            }

            HashSet<Item> smaller = linearDiscreteKnapsack(p, q, w - weightG, items);

            take.addAll(smaller);
            return take;

        }
    }

    public static void main(String[] args) {
        Item a = new Item(0, 10, 50);
        Item b = new Item(1, 20, 40);
        Item c = new Item(2, 30, 30);
        Item d = new Item(3, 40, 20);
        Item e = new Item(4, 50, 10);

        Item[] items = new Item[]{a, b, c, d, e};

        //TSelection.shuffle(items);

        HashSet<Item> result = linearDiscreteKnapsack(0, items.length - 1, 80, items);
        System.out.println(result.stream().map(Item::toString).collect(Collectors.joining(",")));
    }

}
