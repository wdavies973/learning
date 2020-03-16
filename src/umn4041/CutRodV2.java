package umn4041;

public class CutRodV2 {

    public static void main(String[] args) {
        float a = 3.2f;

        long start = System.nanoTime();

        for(int i = 0; i < 100_000_000; i++) {
            a += 1.0023;
        }

        System.out.println("Elapsed: "+(System.nanoTime()-start));
    }

}
