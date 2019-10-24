package miscellaneous;

import java.util.ArrayList;

public class p1 {

    private ArrayList<Point> greatest;
    private int currentGreatest = 1;

    // Starts at 0,0, can move only left & right if delta is +-1
    // Couple of ways to make this faster
        // Don't start off on a chain if it can't exceed current greatest

    public void printMaxLengthSnake(int[][] matrix) {
        followRoute(matrix, new ArrayList<>(), 0, 0);

        // Find the longest snake
        for(Point p : greatest) {
            System.out.print(matrix[p.y][p.x]+" ");
        }
    }

    private void followRoute(int[][] matrix, ArrayList<Point> currentRoute, int x, int y) {
        currentRoute.add(new Point(x,y));

        // Try y direction
            // If y+1 is still in the matrix
                // Is it delta +- 1? Continue or start new route
            // Not in matrix
                // End current route

        int both = 0;

        if(y+1 < matrix.length) {
            if(Math.abs(matrix[y+1][x] - matrix[y][x]) == 1) {
                followRoute(matrix, new ArrayList<>(currentRoute), x, y+1);
            } else {
                // The current route has ended
                addRoute(currentRoute); // routes get added twice if they are only 1 in length

                if(greatestPossibleFromXY(matrix, x, y+1) > currentGreatest) followRoute(matrix, new ArrayList<>(), x, y+1);
            }
        } else both++;

        if(x+1 < matrix[y].length) {
            if(Math.abs(matrix[y][x+1] - matrix[y][x]) == 1) {
                followRoute(matrix, new ArrayList<>(currentRoute), x+1, y);
            } else {
                addRoute(currentRoute);
                if(greatestPossibleFromXY(matrix, x+1, y) > currentGreatest) followRoute(matrix, new ArrayList<>(), x+1, y);
            }
        } else both++;

        if(both == 2) {
            addRoute(currentRoute);
        }
    }

    private void addRoute(ArrayList<Point> route) {
        if(route.size() > currentGreatest) {
            currentGreatest = route.size();
            greatest = route;
        }
    }

    private int greatestPossibleFromXY(int[][] matrix, int x, int y) {
        return (matrix.length - y) + (matrix[0].length - x) - 1;
    }

    private static class Point {
        private int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        int[][] matrix = {{9,6,5,2,1},{8,7,6,5,5},{7,3,1,6,2},{1,1,1,7,8}};

        new p1().printMaxLengthSnake(matrix);
    }


}
