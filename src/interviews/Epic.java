package interviews;

import java.util.ArrayList;
import java.util.List;

public class Epic {

    public static void permute(char[] c, int start) {
        if(start >= c.length) {
            return;
        }

        // Generate all permutations
        // 1) Swap every possible letter into the first position
        // 2) Recurse

        for(int i = start; i < c.length; i++) {
            // Swap characters
            char temp = c[start];
            c[start] = c[i];
            c[i] = temp;

            System.out.println(new String(c));

            permute(c, start + 1);
        }
    }

//    public static void permute(String str, int start) {
//        for(int i = start; i < str.length(); i++) {
//            String s = str.substring(0, start) + str.charAt(i) + str.substring(start, i) + str.substring(i + 1);
//
//            if(!(i == start && start != 0))
//                System.out.println(s);
//
//            permute(s, start + 1);
//        }
//    }

    public static void permute(List<String> results, String str, int start) {
        for(int i = start; i < str.length(); i++) {
            String s = str.substring(0, start) + str.charAt(i) + str.substring(start, i) + str.substring(i + 1);

            if(!(i == start && start != 0))
                results.add(s);

            permute(results, s, start + 1);
        }
    }

    public static void permuteCased(String str) {
        ArrayList<String> results = new ArrayList<>();

        String lowercase = str.replaceAll("[A-Z]", "");
        permute(results, lowercase, 0);

        // For each permutation, add capital letters back in
        for(int i = 0; i < str.length(); i++) {
            if(Character.isUpperCase(str.charAt(i))) {
                for(int j = 0; j < results.size(); j++) {
                    String item = results.get(j);
                    String updated = item.substring(0, i) + str.charAt(i) + item.substring(i);
                    results.set(j, updated);
                }
            }
        }

        // Print results
        for(String s : results) {
            System.out.println(s);
        }
    }

    public static void fibonacci(int n) {
        int a = 1;
        int b = 1;

        for(int i = 0; i < n; i++) {
            System.out.println(a);
            int c = a + b;
            a = b;
            b = c;
        }

    }

    public static void main(String[] args) {
        //permuteCased("RabcD");
        fibonacci(10);
    }

}
