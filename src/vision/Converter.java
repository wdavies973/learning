package vision;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Converter {

    public static void main(String[] args) throws Exception {
        File directory = new File("C:\\dev_sdks\\tensorflow\\workspace\\resistor\\images\\train");

        File[] children = directory.listFiles();

        for(File f : children) {
            System.out.println(f.getName());
            if(!f.getName().contains(".csv")) continue;

            convert(directory, f);

            if(true) break;
        }
    }

    private static void convert(File dir, File f) throws Exception{
        // 1) Load resistor images into ResistorBase class
        BufferedReader br = new BufferedReader(new FileReader(f));
        // Skip the first line
        br.readLine();

        String line1 = br.readLine(), line2 = br.readLine();

        StringBuilder sb = new StringBuilder();

        sb.append("[");
        while(true) {
            System.out.println(line1);
            System.out.println(line2);
            System.out.println("\n");
            // File name
            Matcher m = Pattern.compile(".*\\.png").matcher(line1);
            m.find();
            File imagePath = new File(m.group());
            imagePath = new File(dir, imagePath.getName());

            // Get regions for both
            Pattern xPattern = Pattern.compile("\"\"all_points_x\"\":\\[((-*\\d+,|-*\\d+)*)]");
            Pattern yPattern = Pattern.compile("\"\"all_points_y\"\":\\[((-*\\d+,|-*\\d+)*)]");

            Matcher lx1 = xPattern.matcher(line1);
            Matcher ly1 = yPattern.matcher(line1);
            Matcher lx2 = xPattern.matcher(line2);
            Matcher ly2 = yPattern.matcher(line2);
            lx1.find();
            ly1.find();
            lx2.find();
            ly2.find();

            Integer[] x1 = Arrays.stream(lx1.group(1).split(",")).map(Integer::parseInt).toArray(Integer[]::new);
            Integer[] y1 = Arrays.stream(ly1.group(1).split(",")).map(Integer::parseInt).toArray(Integer[]::new);
            Integer[] x2 = Arrays.stream(lx2.group(1).split(",")).map(Integer::parseInt).toArray(Integer[]::new);
            Integer[] y2 = Arrays.stream(ly2.group(1).split(",")).map(Integer::parseInt).toArray(Integer[]::new);

            // Get type
            boolean l1IsStart = !line1.contains("\"\"side\"\":\"\"end\"\"");
            boolean l2IsStart = !line2.contains("\"\"side\"\":\"\"end\"\"");

            sb.append("{'filename': '").append(imagePath.getPath()).append("','regions':{'0': {");
            sb.append(" 'region_attributes': {},'shape_attributes': {'all_points_x':[");
            if(l1IsStart) {
                for(int i = 0; i < x1.length; i++) {
                    sb.append(x1[i]);
                    if(i != x1.length - 1) sb.append(",");
                }
                sb.append("],'all_points_y':[");
                for(int i = 0; i < y1.length; i++) {
                    sb.append(y1[i]);
                    if(i != y1.length - 1) sb.append(",");
                }
            } else {
                for(int i = 0; i < x2.length; i++) {
                    sb.append(x2[i]);
                    if(i != x2.length - 1) sb.append(",");
                }
                sb.append("],'all_points_y':[");
                for(int i = 0; i < y2.length; i++) {
                    sb.append(y2[i]);
                    if(i != y2.length - 1) sb.append(",");
                }
            }
            sb.append("],'name':'polygon'}}");
            sb.append(",'1': {");
            sb.append(" 'region_attributes': {},'shape_attributes': {'all_points_x':[");
            if(!l1IsStart) {
                for(int i = 0; i < x1.length; i++) {
                    sb.append(x1[i]);
                    if(i != x1.length - 1) sb.append(",");
                }
                sb.append("],'all_points_y':[");
                for(int i = 0; i < y1.length; i++) {
                    sb.append(y1[i]);
                    if(i != y1.length - 1) sb.append(",");
                }
            } else {
                for(int i = 0; i < x2.length; i++) {
                    sb.append(x2[i]);
                    if(i != x2.length - 1) sb.append(",");
                }
                sb.append("],'all_points_y':[");
                for(int i = 0; i < y2.length; i++) {
                    sb.append(y2[i]);
                    if(i != y2.length - 1) sb.append(",");
                }
            }
            sb.append("],'name':'polygon'}}");
            sb.append("},'size':").append(imagePath.length());
            sb.append("}");


            line1 = br.readLine();
            if(line1 == null) break;
            line2 = br.readLine();

            sb.append(",");
        }

        sb.append("]");

        br.close();

        System.out.println(f.getName());
        System.out.println(sb.toString());
        File r = new File(dir,
                f.getName().replaceFirst(".csv", ".json"));
        r.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(r));

        bw.write(sb.toString());
        bw.close();

    }

}
