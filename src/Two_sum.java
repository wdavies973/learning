//this is problem #1 on leetcode, just starting to work through them
public class Two_sum {

    static private int[] sum_find (int [] arr, int tar) {
        for (int i = 0; i < arr.length-1; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                int total = arr[i] + arr[j];
                if (total == tar) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{0};
    }

    public static void main (String[] args) {
        int[] nums = new int [] {2, 4, 7, 11, 15};
        int tar = 9;

        int[] totals = sum_find (nums, tar);
        System.out.print("[" );
        for (int i = 0; i < 2; i++) {
            System.out.print(totals[i]);
            if (i == 0) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }
}