package ai;

public class MonteCarlo {

    public static void main(String[] args) {

        double times = 0;

        double leftTimes = 0;
        double rightTimes = 0;

        double leftUCB = Double.MAX_VALUE;
        double rightUCB = Double.MAX_VALUE;

        int leftInARow = 0;

        for(int i = 0; i < 100; i++) {

            if(leftUCB >= rightUCB) {
                leftTimes++;
                leftInARow++;
            } else {
                rightTimes++;
                 System.out.println("Streak: "+leftInARow);
                 leftInARow = 0;
            }

            times++;

            // Recompute ucbs
            leftUCB = 1 + Math.sqrt((2 * Math.log(times)) / leftTimes);
            rightUCB = Math.sqrt((2 * Math.log(times)) / rightTimes);

            System.out.println(leftUCB+" "+rightUCB);
        }

        System.out.println("Went left: "+leftTimes);
        System.out.println("Went right: "+rightTimes);

    }

}
