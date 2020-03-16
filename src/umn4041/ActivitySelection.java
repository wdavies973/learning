package umn4041;

import org.omg.PortableInterceptor.INACTIVE;

import java.util.HashSet;
import java.util.Set;

public class ActivitySelection {

    private static class Activity {
        int id;
        int start;
        int finish;
    }

    // What's the runtime? O(N) if already sorted
    public static Set<Activity> recursiveActivitySelector(Activity[] activities, int k, int n) {
        int m = k + 1;

        // The k > 0 case is a sentinel case so that the assumption that Sk is a subset of activities holds
        while(m <= n && k > 0 && activities[m - 1].start < activities[k - 1].finish) { // while incompatible, find the next compatible one
            m = m + 1;
        }
        if(m <= n) {
            HashSet<Activity> compatible = new HashSet<>();
            compatible.add(activities[m - 1]);

            Set<Activity> result = recursiveActivitySelector(activities, m, n);
            result.addAll(compatible);

            return result;
        } else {
            return new HashSet<>();
        }
    }

    // O(n) time as well
    public static Set<Activity> iterativeActivitySelector(Activity[] activities) {
        HashSet<Activity> compatible = new HashSet<>();

        Activity current = activities[0]; // current is MAX finish time in compatible
        compatible.add(current);

        for(int i = 1; i < activities.length; i++) {
            if(activities[i].start >= current.finish) {
                current = activities[i];
                compatible.add(current);
            }
        }

        return compatible;
    }

    // Takes the naive, dynamic programming approach (so not totally naive)
    public static int naiveActivitySelectorCount(Activity[] activities, int p, int r) {
        System.out.println(p + "," + r);

        int n = r - p + 1;

        if(n == 0) {
            return 0;
        }

        // Select all activities:
        // 1) Start after activities[p] finishes
        // 2) End before activities[r] starts

        int left = p + 1;
        int right = r - 1;

        while(left <= r && activities[left].start < activities[p].finish) {
            left++;
        }

        while(right >= p && activities[right].finish > activities[r].start) {
            right--;
        }

        int size = 0;

        for(int k = left; k <= right; k++) {
            size = Math.max(size, naiveActivitySelectorCount(activities, p, k) + naiveActivitySelectorCount(activities, k, r) + 1);
        }

        return size;
    }



    // http://ranger.uta.edu/~huang/teaching/CSE5311/HW3_Solution.pdf
    // TODO incomplete
    public static Set<Activity> dynamicNaiveActivitySelector(Activity[] activities) {
        int n = activities.length;

        Matrix m = new Matrix(n, n);

        for(int i = 0; i < n; i++) {
            m.set(i + 1, i + 1, 1); // if we choose any single activity, we can always select it
        }

        // Handles increasing diagonals
        for(int diagonal = 2; diagonal <= n; diagonal++) {

            for(int row = 1; row <= n - diagonal + 1; row++) {
                int col = row + diagonal - 1;

                for(int split = row - 1; split <= col - 1; split++) {
                    // split = k
                    // Validate both sides of the split, ONLY if the split is valid should it be counted
                    // remember though, splits are independent

                    // Check compatible
                    int left = split - 1; // activity to left of split
                    int right = split + 1; // activity to right of split

                    boolean valid = true;

                    if(left >= row - 1 &&activities[split].start < activities[left].finish) {
                        valid = false;
                    }

                    if(right <= col - 1 && activities[right].finish > activities[split].start) {
                        valid = false;
                    }

                    if(valid) {
                        int q = m.get(row, split+1) + m.get(split+1, col) + 1;

                        if(q > m.get(row, col)) {
                            m.set(row, col, q);
                        }
                    }
                }

                if(m.get(row, col) == 0) {
                    int q1 = m.get(row, col-1);
                    int q2 = m.get(row+1, col);

                    m.set(row, col, Math.max(q1, q2));
                }

            }
        }

        System.out.println(m);

        return null;
    }

    // Algorithms to write
        // Finish dynamic activities
        // Finish weighted dynamic activities
        // Professor gecko water problem
        // Huffman

    public static Set<HashSet> dynamic2(Activity[] activities) {
        int n = activities.length;

        int[][] c = new int[n + 2][n + 2];
        int[][] act = new int[n + 2][n + 2];

        for(int i = 0; i <= n; i++) {
            c[i][i] = 0;
            c[i][i + 1] = 0;
        }
        c[n + 1][n + 1] = 0;

        for(int l = 2; l <= n + 1; l++) {
            for(int i = 0; i <= n - l + 1; i++) {
                int j = i + l;
                c[i][j] = 0;
                int k = j - 1;
                while(activities[i].finish < activities[k].finish) {
                    if(activities[i].finish <= activities[i].start && c[i][k] + c[k][j] + 1 > c[i][j]) {
                        c[i][j] = c[i][k] + c[k][j] + 1;
                        act[i][j] = k;
                    }
                    k = k - 1;
                }
            }
        }

        System.out.println("MAXIMUM SIZE is: "+c[0][n + 1]);

        return  null;
    }



    public static void main(String[] args) {
//        int[] s = {1, 3, 0, 5, 3, 5, 6, 8, 8, 2, 12};
//        int[] f = {4, 5, 6, 7, 9, 9, 10, 11, 12, 14, 16};

        int[] s = {1, 3, 4, 5};
        int[] f = {4, 5, 5, 9};

//
//        int[] s = {0, 1, 3, 0, 5, Integer.MAX_VALUE};
//        int[] f = {0, 4, 5, 6, 7, Integer.MAX_VALUE};

        Activity[] activities = new Activity[s.length];

        for(int i = 0; i < s.length; i++) {
            Activity a = new Activity();
            a.id = i;
            a.start = s[i];
            a.finish = f[i];
            activities[i] = a;
        }

        Set<Activity> result;// recursiveActivitySelector(activities, 0, 11);
        //result = iterativeActivitySelector(activities);

        //System.out.println("Compatible" + naiveActivitySelectorCount(activities, 0, activities.length - 1));

        dynamic2(activities);

//        for(Activity m : result) {
//            System.out.println("Activity " + m.id);
//        }
    }

}
