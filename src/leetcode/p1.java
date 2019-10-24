package leetcode;

import java.util.HashMap;

public class p1 {

    public int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer> map = new HashMap<>();

        for(int i = 0; i < nums.length; i++) {
            if(map.containsKey(nums[i])) {
                return new int[]{i, map.get(nums[i])};
            }

            map.put(target - nums[i], i);
        }

        throw new RuntimeException("No solution");
    }

    public void computeSubMatrix(int[][] matrix, int x1, int x2, int y1, int y2) {
        if(x1 > x2 || y1 > y2) return;

        // Left to right
        for(int col = x1; col <= x2; col++) {
            System.out.print(matrix[y1][col]+" ");
        }

        // Top to bottom
        for(int row = y1 + 1; row <= y2; row++) {
            System.out.print(matrix[row][x2]+" ");
        }

        // Right to left
        for(int col = x2 - 1; col >= x1; col--) {
            System.out.print(matrix[y2][col]+" ");
        }

        // Bottom to top
        for(int row = y2 - 1; row > y1; row--) {
            System.out.print(matrix[row][x1]+" ");
        }

        computeSubMatrix(matrix, x1+1,x2-1,y1+1,y2-1);
    }

    public void spiral(int[][] matrix) {
        computeSubMatrix(matrix, 0, matrix[0].length - 1, 0, matrix.length - 1);
    }

    public static void main(String[] args) {
        int[][] matrix = {{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,16}};

        new p1().spiral(matrix);
    }

}
