public class Median {

    private static double med (int[] num1, int[] num2) {
        int length = num1.length + num2.length;
        int[] combined = new int[length];
        int ind1 = 0, ind2 = 0;
        for (int i = 0; i < length; i++) {
            if (ind1 >= num1.length) {
                if (ind2 < num2.length) {
                    combined[i] = num2[ind2];
                    ind2++;
                }
            }
            else if (ind2 >= num2.length) {
                combined[i] = num1[ind1];
                ind1++;
            }

            else if (num1[ind1] < num2[ind2]) {
                combined[i] = num1[ind1];
                ind1++;
            }

            else {
                combined[i] = num2[ind2];
                ind2++;
            }


            if (length % 2 == 0) {
                if (i == length / 2) {
                    return (((double)(combined[i] + combined[i - 1])) / 2);
                }
            }

            else {
                if (i == length / 2) {
                    return combined[i];
                }
            }
        }
        return combined[0];
    }

    public static void main (String[] args) {
        int[] ind1 = new int[] {1,2};
        int[] ind2 = new int[] {3,4};

        System.out.println(med(ind1, ind2));

    }
}
