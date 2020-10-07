package umn.algorithms2;

public class OptimalBST {

    private static int longestSubsequence(String x, String y) {
        int m = x.length();
        int n = y.length();

        int[][] s = new int[m + 1][n + 1];

        for(int i = 0; i < m; i++) {
            for(int j = 0; j < n; j++) {
                if(x.charAt(i) == y.charAt(j)) {
                    s[i + 1][j + 1] = s[i][j] + 1;
                } else {
                    s[i + 1][j + 1] = Math.max(s[i + 1][j], s[i][j + 1]);
                }
            }
        }

        return s[m][n];
    }

    public static void main(String[] args) {
        System.out.println(longestSubsequence("ace", "ace"));
    }

}
