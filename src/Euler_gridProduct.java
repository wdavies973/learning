// Problem # 11 on Euler

// The other solutions I checked also has theta (n^2) as their runtime, so yay me. This function has
// 3 nested for loops, but one of them is to keep the project mutable for num (the number of
// numbers user would like to find in a row), so this program is slightly more versatile

// If looking at the blocks, one can see I didn't really plan ahead before coding this one, due
// to the changed structure of the 3rd nested for loop of the first diagonal block, which I found
// to be easier to conceptualize and easier to change for other blocks.

public class Euler_gridProduct {

    private static int[] find (int num) {
        String str =
                "08 02 22 97 38 15 00 40 00 75 04 05 07 78 52 12 50 77 91 08\n" +
                "49 49 99 40 17 81 18 57 60 87 17 40 98 43 69 48 04 56 62 00\n" +
                "81 49 31 73 55 79 14 29 93 71 40 67 53 88 30 03 49 13 36 65\n" +
                "52 70 95 23 04 60 11 42 69 24 68 56 01 32 56 71 37 02 36 91\n" +
                "22 31 16 71 51 67 63 89 41 92 36 54 22 40 40 28 66 33 13 80\n" +
                "24 47 32 60 99 03 45 02 44 75 33 53 78 36 84 20 35 17 12 50\n" +
                "32 98 81 28 64 23 67 10 26 38 40 67 59 54 70 66 18 38 64 70\n" +
                "67 26 20 68 02 62 12 20 95 63 94 39 63 08 40 91 66 49 94 21\n" +
                "24 55 58 05 66 73 99 26 97 17 78 78 96 83 14 88 34 89 63 72\n" +
                "21 36 23 09 75 00 76 44 20 45 35 14 00 61 33 97 34 31 33 95\n" +
                "78 17 53 28 22 75 31 67 15 94 03 80 04 62 16 14 09 53 56 92\n" +
                "16 39 05 42 96 35 31 47 55 58 88 24 00 17 54 24 36 29 85 57\n" +
                "86 56 00 48 35 71 89 07 05 44 44 37 44 60 21 58 51 54 17 58\n" +
                "19 80 81 68 05 94 47 69 28 73 92 13 86 52 17 77 04 89 55 40\n" +
                "04 52 08 83 97 35 99 16 07 97 57 32 16 26 26 79 33 27 98 66\n" +
                "88 36 68 87 57 62 20 72 03 46 33 67 46 55 12 32 63 93 53 69\n" +
                "04 42 16 73 38 25 39 11 24 94 72 18 08 46 29 32 40 62 76 36\n" +
                "20 69 36 41 72 30 23 88 34 62 99 69 82 67 59 85 74 04 36 16\n" +
                "20 73 35 29 78 31 90 01 74 31 49 71 48 86 81 16 23 57 05 54\n" +
                "01 70 54 71 83 51 54 69 16 92 33 48 61 43 52 01 89 19 67 48";

        // this section converts the string from the site to a double matrix
        int[][] arr = new int[20][20]; // int[row][col]
        int ind = 0;
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) { //59 total characters in each line of the string, last is 58th
                arr[i][j] = Integer.parseInt(str.substring(ind, ind + 2));
                ind +=3;
            }
        }


        // this section finds the possible max numbers
        // putting these variables into an array would have been more efficient, but this is
        // much easier to read, so I'm keeping it this way
        int max = 0;
        int[] direction = new int[num]; // records the variable values


        // this block finds numbers proceeding to the right
        // the +1 in the for loop is to remind me the location containing the first
        // variable needs to be included in order to be checked
        for (int i = 0; i < 20; i++) { // which row
            for (int j = 0; j < 20 - num + 1; j++) { // which column
                int current = 1;
                for (int k = j; k < j + num; k++) { //which part of the 4 numbers
                    current *= arr[i][k];
                }
                if (current > max) {
                    max = current;
                    System.arraycopy(arr[i], j, direction, 0, num);
                }
            }
        }


        // this block finds numbers proceeding downward
        for (int i = 0; i < 20 - num + 1; i++) { // which row
            for (int j = 0; j < 20; j++) { // which column
                int current = 1;
                for (int k = i; k < i + num; k++) { //which part of the 4 numbers
                    current *= arr[k][j];
                }
                if (current > max) {
                    max = current;
                    for (int k = i; k < i + num; k++) {
                        direction[k - i] = arr[k][j];
                    }
                }
            }
        }


        // this block finds numbers proceeding diagonally down right
        // found a better for loop structure for the 3rd nested for loop
        for (int i = 0; i < 20 - num + 1; i++) { // which row
            for (int j = 0; j < 20 - num + 1; j++) { // which column
                int current = 1;
                for (int k = 0; k < num; k++) { //which part of the 4 numbers
                    current *= arr[i + k][j + k];
                }
                if (current > max) {
                    max = current;
                    for (int k = 0; k < num; k++) {
                        direction[k] = arr[i + k][j + k];
                    }
                }
            }
        }


        // this block finds numbers proceeding diagonally down left
        // found a better for loop structure for the 3rd nested for loop
        for (int i = 0; i < 20 - num + 1; i++) { // which row
            for (int j = num - 1; j < 20; j++) { // which column
                int current = 1;
                for (int k = 0; k < num; k++) { //which part of the 4 numbers
                    current *= arr[i + k][j - k];
                }
                if (current > max) {
                    max = current;
                    for (int k = 0; k < num; k++) {
                        direction[k] = arr[i + k][j - k];
                    }
                }
            }
        }

        return direction;
    }

    public static void main (String[] args) {
        int num = 4;
        int[] found = find(num);
        int total = 1;
        for (int i = 0; i < num; i++) {
            total *= found[i];
            System.out.print(found[i]);
            if (i < num - 1) {
                System.out.print(" * ");
            }
        }
        System.out.println();
        System.out.println("This is multiplied for a total of " + total);
    }
}
