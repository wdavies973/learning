package umn.algorithms1;

/*
 * A matrix wrapper class.
 *
 * -Indices are offset by 1 (see get(...) and set(...) methods) for convenience in multiply implementation below
 *  -Set and retrieve values using 1,1 to maxRows,maxCols
 */

public class Matrix {
    private int columns;
    private int rows;
    private int[][] values;

    public Matrix(int rows, int columns) {
        this.columns = columns;
        this.rows = rows;
        values = new int[rows][columns];
    }

    public Matrix(int[][] values) {
        this.values = values;
        this.rows = values.length;
        this.columns = values[0].length;
    }

    public int get(int row, int col) {
        return values[row - 1][col - 1];
    }

    public void set(int row, int col, int value) {
        values[row - 1][col - 1] = value;
    }

    public void add(int row, int col, int value) {
        values[row - 1][col - 1] += value;
    }

    public boolean canMultiplyWith(Matrix other) {
        return columns == other.rows;
    }

    public Matrix multiply(Matrix other) {
        if(!canMultiplyWith(other)) throw new IllegalArgumentException("Can not multiply incompatible matrices.");

        Matrix result = new Matrix(rows, other.columns);
        for(int row = 1; row <= getRows(); row++) {
            for(int col = 1; col <= other.getCols(); col++) {
                int c = 0;
                for(int k = 1; k <= columns; k++) {
                    result.add(row, col, get(row, k) * other.get(k, col));
                }
            }
        }

        return result;
    }

    public int getCols() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Size: " + getRows() + " x " + getCols() + "\n");

        for(int row = 1; row <= getRows(); row++) {
            for(int col = 1; col <= getCols(); col++) {
                builder.append(get(row, col)).append(" ");
            }
            builder.append("\n");
        }

        return builder.toString();
    }
}