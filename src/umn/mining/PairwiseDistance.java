package umn.mining;

import java.text.DecimalFormat;

public class PairwiseDistance {

    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    public static void clusterDistance(double[][] points, int[] cluster1, int[] cluster2) {
        double min = Double.MAX_VALUE;

        for(int i = 0; i < cluster1.length; i++) {
            for(int j = 0; j < cluster2.length; j++) {
                double[] p1 = points[cluster1[i] - 1];
                double[] p2 = points[cluster2[j] - 1];

                min = Math.max(min, distance(p1[0], p1[1], p2[0], p2[1]));
            }
        }

        System.out.println("Minimum distance is: "+min);
    }

    public static void main(String[] args) {
        double[][] points = {
                {9, 8},
                {6, 8},
                {6, 4},
                {10, 6},
                {3, 1}
        };

        double[][] nextPoint = {
                {0.4005, 0.5306},
                {0.2148, 0.3854},
                {0.3457, 0.3156},
                {0.2652, 0.1875},
                {0.0789, 0.4139},
                {0.4548, 0.3022}
        };

        //points = nextPoint;

        DecimalFormat df = new DecimalFormat("#.###");
        for(int i = 0; i < points.length; i++) {
            for(int j = 0; j < points.length; j++) {
                System.out.print(df.format(distance(points[i][0], points[i][1], points[j][0], points[j][1]))+" ");
            }
           System.out.println();
        }

        clusterDistance(points, new int[]{1, 4, 2}, new int[]{3});

    }

}
