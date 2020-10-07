package umn.algorithms2;

public class Matrix {

    int rows, cols;
    int[][] value;

    Matrix(int n) {
        value = new int[n][n];
        rows = cols = n;
    }

    Matrix(int[][] matrix) {
        this.value = matrix;
        this.rows = matrix[0].length;
        this.cols = matrix.length;
    }

    Matrix(int cols, int ... values) {
        assert(values.length % cols == 0);

        rows = values.length / cols;
        this.cols = cols;
        value = new int[rows][cols];


        for(int i = 0; i < values.length; i++) {
            value[i / cols][i % cols] = values[i];
        }
    }

    // Naive matrix multiplication, O(n^3)
    Matrix multiply(Matrix matrix) {
        assert(cols == matrix.rows);

        int[][] out = new int[rows][matrix.cols];

        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < matrix.cols; col++) {
                for(int c = 0; c < rows; c++) {
                    out[row][col] += value[row][c] * matrix.value[c][col];
                }
            }
        }

        return new Matrix(out);
    }

    Matrix nextRecurse() {
        int[][] matrix = new int[rows * 2][cols * 2];

        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                matrix[row][col] = value[row][col];
                matrix[row][col + cols] = value[row][col];
                matrix[row + rows][col] = value[row][col];
                matrix[row + rows][col + cols] = -1 * value[row][col];
            }
        }

        return new Matrix(matrix);
    }

    // D&C strategy. Not implemented here.
    // Time complexity T= 8T(n/2) + cn^2 (adding is n^2) when n > 2
    // 1 when n <= 2
    // Still O(n^3)
    // Slightly worse than iterative, because it uses function stack

    /*
     * Strassen came up with a method to do it in O(n^2.81).
     * Recurrence 7T(n/2) + n^2
     */

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                if(value[row][col] < 0) {
                    builder.append(value[row][col]).append(" ");
                } else {
                    builder.append(" ").append(value[row][col]).append(" ");
                }


            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        Matrix m = new Matrix(1, 1);
        for(int i = 0; i < 5; i++) {
            System.out.println(m);
            m = m.nextRecurse();
            System.out.println();
        }
    }
}
