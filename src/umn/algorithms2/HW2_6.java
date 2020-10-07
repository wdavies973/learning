//package umn.algorithms2;
//
//import umn.algorithms1.MatrixChain;
//
//import java.util.Stack;
//
//public class HW2_6 {
//
//    private static class Node {
//        Node left;
//        Node right;
//        int label;
//        int cost;
//    }
//
//    public int[][] createLabeledTree(int[] s) { // I refer to these as A[1], A[2], etc.
//        int n = s.length;
//        int[][] c = new int[n][n];
//
//        /*
//         * Here, l is the chain length.
//         *
//         * We have to start at 2, per the bottom up definition.
//         * Basically, chains of length three depend on chains of length two,
//         * four depend on three, etc. So we'll first go through our
//         * matrices and compute the costs to multiply any two pairs
//         * of matrices together and fill in their appropriate costs
//         * in matrix m. These form diagonals, there's the diagonal of 0s,
//         * the next diagonal up and to the right holds costs for matrices of length 2,
//         * and so on
//         *
//         * For example, let's say we're multiplying 4 matrices together.
//         * chain length=2
//         * The following costs will be calculated and inserted into m,
//         * as each multiplies two matrices together
//         *  m[1,2]
//         *  m[2,3]
//         *  m[3,4]
//         */
//        for(int l = 2; l <= n; l++) {
//
//            // Loop through all rows in the diagonal we're considering
//            for(int row = 1; row <= n - l + 1; row++) {
//
//                int col = row + l - 1; // calculates the column, this is just the offset from the diagonal in the
//                // corresponding row
//
//                c[row - 1][col - 1] = Integer.MIN_VALUE;
//
//                // This will iterate through the possible splits, or partition of the matrices
//                // provided. Let's say split = 2, that is equivalent to the parenthesization:
//                // (A[1] * A[2]) * (A[3] * A[4])
//                // The split should iterate from the row to the column, as m[i,j] defines
//                for(int split = row; split <= col - 1; split++) {
//                    // Figure out the cost as factor of previous results
//                    int q =
//
//                    int q = m.get(row, split) + m.get(split + 1, col) + matrices[row - 1].getRows() * matrices[split - 1].getCols() * matrices[col - 1].getCols();
//
//                    expression.append("m[" + row + "," + split + "] + m[" + (split + 1) +
//                            "," + col + "] + "
//                            + matrices[row - 1].getRows() + " \\cdot " + matrices[split - 1].getCols() + " \\cdot " + matrices[col - 1].getCols() + " = "+q);
//
//                    // If it's smaller than the current min, set it. If this is the first iteration, it will always
//                    // be smaller than Integer.MAX_VALUE
//                    if(q > m.get(row, col)) {
//                        m.set(row, col, q);
//                        s.set(row, col, split);
//                    }
//                    if(split != col - 1)
//                        expression.append(",\\;");
//                }
//                expression.append("\\}$, best split="+s.get(row, col)+"\n");
//                System.out.println(expression);
//            }
//        }
//
//        System.out.println(m);
//        System.out.println(s);
//
//        System.out.println("Optimal parenthesization: " + printOptimalParenthesization(s, 1, n));
//
//        // Now, we're ready to perform the optimal multiplication
//        return matrixChainMultiply(matrices, s, 1, n);
//    }
//
//    public static void main(String[] args) {
//
//    }
//}
