package umn.mining;

public class Driver {

    public static void main(String[] args) {

//        Record z = Record.fromValues(1, 1, 0, 0, 0);
//        Record q = Record.fromValues(0, 0, 0, 1, 1);
        Record m = Record.fromValues(-3, -2, -1, 0, 1, 2, 3);
        Record n = Record.fromValues(9, 4, 1, 0, 1, 4, 9);


        System.out.println(m.mutualInformation(n));

    }
}
