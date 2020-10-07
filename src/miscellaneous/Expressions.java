package miscellaneous;

import com.udojava.evalex.Expression;

import java.io.*;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Expressions {

    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Users\\wdavi\\Downloads\\to-convert.txt");

        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;

        while((line = br.readLine()) != null) {
            String copy = line;

            line = line.replaceFirst("assertEquals", "expect");
            line = line.replaceFirst("new ", "");
            line = line.replaceAll("toPlainString\\(\\)", "toString\\(\\)");
            line = line.replaceAll("intValue\\(\\)", "toInt\\(\\)");
            // Swap operands
            Pattern pattern = Pattern.compile("\\\".+\\\"(?=,\\s+)");

            Matcher matcher = pattern.matcher(line);
            matcher.find();


            line = line.replaceFirst(pattern.pattern()+",\\s+", "");

            try {
                line = line.replaceFirst("\\);", ", "+matcher.group(0)+");");
            } catch(Exception e) {
                System.out.println("Failed to match!!!!!!!!!: "+ copy);
            }



           System.out.println(line);

        }

    }

}
