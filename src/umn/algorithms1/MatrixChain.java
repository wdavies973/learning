package umn.algorithms1;

/*
 * The following class implements an optimal matrix chain multiplication by
 * first finding the optimal parenthesization using dynamic programming, and
 * then using a recursive function to multiply based off the optimal strategy.
 *
 * The following video is really good:
 * https://www.youtube.com/watch?v=prx1psByp7U
 */
public class MatrixChain {

    // Note, to determine the number of multiplications (cost) performed in a matrix multiplication,
    // multiply the dimensions of the resultant matrix by the inner matching dimensions
    // So a 4x6 * 6x3 = 4 * 6 * 3 multiplications total

    /*
     * A matrix wrapper class.
     *
     * -Indices are offset by 1 (see get(...) and set(...) methods) for convenience in multiply implementation below
     *  -Set and retrieve values using 1,1 to maxRows,maxCols
     */
    static class Matrix {
        private int columns;
        private int rows;
        private int[][] values;

        public Matrix(int rows, int columns) {
            this.columns = columns;
            this.rows = rows;
            values = new int[rows][columns];
        }

        static Matrix identity(int rows, int cols) {
            Matrix m = new Matrix(rows, cols);
            for(int row = 0; row < rows; row++) {
                for(int col = 0; col < cols; col++) {
                    if(col == row) m.set(row + 1, col + 1, 1);
                }
            }
            return m;
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

        public String toLatexString() {
            StringBuilder builder = new StringBuilder("Size: " + getRows() + " x " + getCols() + "\n");

            for(int row = 1; row <= getRows(); row++) {
                for(int col = 1; col <= getCols(); col++) {

                    if(col >= row) builder.append(get(row, col)).append("&");
                    else builder.append("&");
                }
                builder.append("\\\\\n");
            }

            return builder.toString();
        }
    }

    /*
     * Optimally multiply a chain of matrices together
     */
    public Matrix multiply(Matrix... matrices) { // I refer to these as A[1], A[2], etc.
        // Verify that all matrix multiplications are valid (# of cols of first = # of rows of second)
        for(int i = 0; i < matrices.length - 1; i++) {
            if(!matrices[i].canMultiplyWith(matrices[i + 1])) {
                throw new IllegalArgumentException("Matrix at index " + i + " cannot be multiplied with matrix at " +
                        "index " + (i + 1));
            }
        }

        // The goal is to figure out an optimal parenthesization to MINIMIZE the total number of multiplications
        // The solution will still check every possible parenthesization, but will use dynamically programming
        // to make it more efficient. This works by saving results of previously computed answers that are needed in
        // future
        // computations. If you wanted to compute the total possible number of parenthesizations, you would
        // find the Catalan number, or T(n) = 2n(Cn)/(n+1) (where Cn is combinations)

        // To use dynamic programming, we'll create 2 tables to help figure out optimal parenthesization.
        // They are square tables, with the dimensions equal to the number of matrices we're multiplying together
        // So if we have four matrices, we'll want a 4x4 m and a 4x4 s matrix

        /*
         * The m matrix stores the cost (number of multiplications) of multiplying a range of matrices together.
         * For example, m[2,3] stores the number of multiplications ops required to multiply A2 * A3
         *
         * The s matrix stores the split that the minimal cost occurred at. For example, in the multiplication:
         * A1 * A2 * A3 * A4
         * split=1 would be A1 * (A2 * A3 * A4)
         * split=3 = (A1 * A2 * A3) * A4
         *
         * Interestingly enough, m[2,4] represents A[2] * A[3] * A[4], which can be defined in terms of a previous
         * solution, in other words, m[2,4] = min{ m[2,k] + m[k+1,4] + d(i-1) * d(k) * d(j)}, when the range is larger,
         * say m[1,4], k can have several values, so use min to select the smallest one.
         * --> In other words, iterate through each possible split location:
         *  If m[2,6], then check the number of multiplications at splits k=2,3,4,5,6. Take the minimum split cost.
         * Record this in m, and record the split location in s.
         *
         * The matrix s stores the partition (k) that lead to the smallest multiplication amount. So for example, we
         * may have
         * the resultant matrices:
         *
         * m:
         * 0   120    88   158
         * X     0    48   104
         * X     X     0    84
         * X     X     X     0
         *
         * s:
         * X     1     1     3
         * X     X     2     3
         * X     X     X     3
         * X     X     X     X
         *
         * In order to know how to optimally, all we need is s.
         * So we have four matrices, A1, A2, A3, A4.
         *
         * We start in the top right of s and work town,
         *
         * s[1,4] has a value of three, so partition after A3
         * (A1 * A2 * A3) * A4
         *
         * Now lookup s[1,3], which is 1, so partition after A1
         * (A1 * (A2 * A3)) * A4
         *
         * This is the optimal multiplication order.
         *
         */
        Matrix m = new Matrix(matrices.length, matrices.length);
        Matrix s = new Matrix(matrices.length, matrices.length);

        int n = matrices.length;

        // Set the diagonal of m to zero, a single matrix by itself
        // has no real cost because a matrix is never multiplied by itself
        for(int i = 1; i <= n; i++) {
            m.set(i, i, 0);
        }

        /*
         * Here, l is the chain length.
         *
         * We have to start at 2, per the bottom up definition.
         * Basically, chains of length three depend on chains of length two,
         * four depend on three, etc. So we'll first go through our
         * matrices and compute the costs to multiply any two pairs
         * of matrices together and fill in their appropriate costs
         * in matrix m. These form diagonals, there's the diagonal of 0s,
         * the next diagonal up and to the right holds costs for matrices of length 2,
         * and so on
         *
         * For example, let's say we're multiplying 4 matrices together.
         * chain length=2
         * The following costs will be calculated and inserted into m,
         * as each multiplies two matrices together
         *  m[1,2]
         *  m[2,3]
         *  m[3,4]
         */
        for(int l = 2; l <= n; l++) {

            // Loop through all rows in the diagonal we're considering
            for(int row = 1; row <= n - l + 1; row++) {

                int col = row + l - 1; // calculates the column, this is just the offset from the diagonal in the
                // corresponding row

                m.set(row, col, Integer.MAX_VALUE);

                StringBuilder expression = new StringBuilder("\t\\item $m[" + row + "," + col + "] = min\\{");

                // This will iterate through the possible splits, or partition of the matrices
                // provided. Let's say split = 2, that is equivalent to the parenthesization:
                // (A[1] * A[2]) * (A[3] * A[4])
                // The split should iterate from the row to the column, as m[i,j] defines
                for(int split = row; split <= col - 1; split++) {
                    // Figure out the cost as factor of previous results
                    int q = m.get(row, split) + m.get(split + 1, col) + matrices[row - 1].getRows() * matrices[split - 1].getCols() * matrices[col - 1].getCols();

                    expression.append("m[" + row + "," + split + "] + m[" + (split + 1) +
                            "," + col + "] + "
                            + matrices[row - 1].getRows() + " \\cdot " + matrices[split - 1].getCols() + " \\cdot " + matrices[col - 1].getCols() + " = "+q);

                    // If it's smaller than the current min, set it. If this is the first iteration, it will always
                    // be smaller than Integer.MAX_VALUE
                    if(q < m.get(row, col)) {
                        m.set(row, col, q);
                        s.set(row, col, split);
                    }
                    if(split != col - 1)
                        expression.append(",\\;");
                }
                expression.append("\\}$, best split="+s.get(row, col)+"\n");
                System.out.println(expression);
            }
        }

        System.out.println(m);
        System.out.println(s);

        System.out.println("Optimal parenthesization: " + printOptimalParenthesization(s, 1, n));

        // Now, we're ready to perform the optimal multiplication
        return matrixChainMultiply(matrices, s, 1, n);
    }

    // Prints the optimal parenthesization, before multiplying
    // the matrices
    private String printOptimalParenthesization(Matrix s, int i, int j) {
        if(i == j) return "A" + i;
        else if(i == j - 1) return "(A" + i + " * A" + j + ")";

        int k = s.get(i, j);
        return "(" + printOptimalParenthesization(s, i, k) + " * " + printOptimalParenthesization(s, k + 1, j) + ")";
    }

    /*
     * i & j track the current position in s
     */
    private Matrix matrixChainMultiply(Matrix[] matrices, Matrix s, int i, int j) {
        if(i == j) return matrices[i - 1];
        else if(i == j - 1) return matrices[i - 1].multiply(matrices[j - 1]);

        int k = s.get(i, j);

        return matrixChainMultiply(matrices, s, i, k).multiply(matrixChainMultiply(matrices, s, k + 1, j));
    }

    public static void main(String[] args) {
//        Matrix A1 = new Matrix(5, 4);
//        Matrix A2 = new Matrix(4, 6);
//        Matrix A3 = new Matrix(6, 2);
//        Matrix A4 = new Matrix(2, 7);

        Matrix A1 = new Matrix(30,1);
        Matrix A2 = new Matrix(1, 40);
        Matrix A3 = new Matrix(40, 10);
        Matrix A4 = new Matrix(10, 25);
        Matrix A5 = new Matrix(25, 50);
        Matrix A6 = new Matrix(50, 5);

        //System.out.println(A5.canMultiplyWith(A6));

        new MatrixChain().multiply(A1, A2, A3, A4, A5, A6);

//        System.out.println(new MatrixChain().multiply(A1, A2, A3, A4));
    }

}
