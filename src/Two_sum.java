
public class Two_sum {

    static public int[] sum_find (int [] arr, int tar) {
        for (int i = 0; i < arr.length-1; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                int total = arr[i] + arr[j];
                if (total == tar) {
                    int ans[] = {arr[i], arr[j]};
                    return ans;
                }
            }
        }
        return new int[]{0};
    }

    public static void main (String[] args) {
        int[] nums = new int [] {2, 7, 11, 15};
        int target = 9;

        int totals[] = sum_find (nums, target);
        System.out.print("[" );
        for (int i = 0; i < 2; i++) {
            System.out.print(i);
            if (i == 0) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }
}