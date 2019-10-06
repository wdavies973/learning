
public class Longest_Substring {
    private static int length(String str) {

        char[] chars = str.toCharArray();
        int longest = 0;
        for (int k = 0; k < str.length(); k++) {
            int cur = 0;
            boolean repeats = false;
            char[] contains = new char[str.length()];
            int contained = 0;
            for (int i = k; i < str.length(); i++) {
                for (int j = 0; j < contained; j++) {

                    if (chars[i] == (contains[j])) {
                        repeats = true;
                        if (longest < cur) {
                            longest = cur;
                        }
                        j += contained;
                        i += str.length();
                    }

                }
                if (!repeats) {
                    contains[contained] = chars[i];
                    contained++;
                    cur++;
                }
            }
            if (longest < cur) {
                longest = cur;
            }
        }
        return longest;
    }


    public static void main(String[] args) {
        String str = "abcdefghijk";
        System.out.println("" + length(str));
    }
}
