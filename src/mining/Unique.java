package mining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class Unique {

    public static void main(String[] args) {
        File f = new File("C:\\Users\\wdavi\\Desktop\\School\\Project2_class\\Project2_class\\Experiment - 2\\instacart_transaction.csv");

        double sum = 0;
        double count = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(f));

            String line;

            HashMap<String, Integer> map = new HashMap<>();

            while((line = br.readLine()) != null) {
                String[] tokens = line.split(",");

                sum += tokens.length;

                count++;
            }

            System.out.println(sum / count);

        } catch(Exception e) {
            e.printStackTrace();
        }


    }

}
