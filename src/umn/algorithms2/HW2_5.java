package umn.algorithms2;

public class HW2_5 {

    public static void find(int n, int[][] matrix) {
        int max = Integer.MIN_VALUE;
        int maxRow = -1, maxCol = -1;

        for(int row = n - 2; row >= 0; row--) {
            for(int col = n - 2; col >= 0; col--) {
                if(matrix[row][col] == 0) continue;

                int r = matrix[row][col + 1];
                int b = matrix[row + 1][col];
                int br = matrix[row + 1][col + 1];

                int q = Math.min(Math.min(r, b), br);

                matrix[row][col] = q + 1;

                if(matrix[row][col] > max) {
                    max = matrix[row][col];
                    maxRow = row + 1;
                    maxCol = col + 1;
                }
            }
        }

        System.out.println(max+",["+maxRow+","+maxCol+"]");

        StringBuilder builder = new StringBuilder();
        for(int row = 1; row <= n; row++) {
            for(int col = 1; col <= n; col++) {
                builder.append(matrix[row-1][col-1]).append(" ");
            }
            builder.append("\n");
        }

        System.out.println(builder.toString());
    }

    public static void main(String[] args) {
        int[][] m = new int[][]{
                {0, 1, 1, 0, 1, 0},
                {0, 0, 1, 1, 1, 0},
                {1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 0},
                {1, 0, 0, 1, 1, 1},
                {1, 0, 0, 0, 1, 1}
        };

        find(6, m);
    }

}
