package ai;

public class Checkers {

    public static void main(String[] args) {
        int[][] array = {
            // nothing = 0, O = 1, X = 2
            {2, 0, 0, 0, 2, 0},
            {0, 0, 0, 1, 0, 1},
            {2, 0, 2, 0, 0, 0},
            {0, 0, 0, 0, 0, 1}
        };

        int myValue = 0;

        for(int row = 0; row < array.length; row++) {
            for(int col = 0; col < array[row].length; col++) {
                if(array[row][col] == 2) {
                    myValue += col;
                }
            }
        }

        myValue -= 3;

        System.out.println(myValue);
    }

}
