package vision;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatasetGenerator {

    private static final String PATH_BACKGROUNDS = "C:\\Users\\wdavi\\Downloads\\resistors\\backgrounds";
    private static final String PATH_RESISTORS = "C:\\Users\\wdavi\\Downloads\\resistors\\full";
    private static final String PATH_OUTPUT = "C:\\Users\\wdavi\\Downloads\\resistors\\dataset";

    private int numEach;

    private static class TrainingImage {
        File bnd;
        BufferedImage img;
        Graphics2D g;
        StringBuilder anno = new StringBuilder("filename,file_size,file_attributes,region_count,region_id," +
                "region_shape_attributes,region_attributes\n");

        int regionId = 0;

        public TrainingImage(File bnd) throws Exception {
            this.bnd = bnd;
            this.img = ImageIO.read(bnd);
            this.g = (Graphics2D) this.img.getGraphics();
        }

        public void drawResistorBase(ResistorBase rb, int x, int y) {
            this.g.drawImage(rb.image, x, y, null);

            // Add annotation information
            anno.append(rb.start.toAnnotation(x, y, regionId, true)).append("\n");
            anno.append(rb.end.toAnnotation(x, y, regionId + 1, false)).append("\n");

            regionId += 2;
        }

        public void save(File parent, int id) throws Exception {
            // save the image
            File img = new File(parent, id + ".png");
            img.createNewFile();
            ImageIO.write(this.img, "PNG", img);

            File annotations = new File(parent, "annotations" + id + ".csv");
            annotations.createNewFile();

            BufferedWriter bw = new BufferedWriter(new FileWriter(annotations));

            bw.write(anno.toString().replaceAll("\\^\\^\\^\\^\\^",
                    String.valueOf(img.length())).replace("$$$$$", String.valueOf(regionId)).replaceAll("#####",
                    img.getPath().replaceAll("\\\\", "\\\\\\\\")));

            bw.close();
        }

        public TrainingImage copy() throws Exception {
            return new TrainingImage(bnd);
        }
    }

    private static class ResistorBase {
        private BufferedImage image;
        private File file;
        private ResistorRegion start, end;

        private ResistorBase(File file, ResistorRegion start, ResistorRegion end) throws Exception {
            this.file = file;
            this.start = start;
            this.end = end;

            image = ImageIO.read(file);
        }

        @Override
        public String toString() {
            return "ResistorBase{" +
                    "image=" + (image != null) +
                    ", file=" + file +
                    ", start=" + start +
                    ", end=" + end +
                    '}';
        }
    }

    private static class ResistorRegion {
        private List<Integer> x, y;

        public ResistorRegion(Integer[] x, Integer[] y) {
            this.x = Arrays.asList(x);
            this.y = Arrays.asList(y);
        }

        public String toAnnotation(int startX, int startY, int id, boolean side) {
            StringBuilder b = new StringBuilder();
            b.append("#####").append(",").append("^^^^^").append(",\"{}\",$$$$$,").append(id).append(",");
            b.append("\"{\"\"name\"\":\"\"polygon\"\",\"\"all_points_x\"\":[");
            for(int i = 0; i < x.size(); i++) {
                b.append(x.get(i) + startX);
                if(i != x.size() - 1) b.append(",");
            }
            b.append("],\"\"all_points_y\"\":[");
            for(int i = 0; i < y.size(); i++) {
                b.append(y.get(i) + startY);
                if(i != y.size() - 1) b.append(",");
            }
            b.append("]}\",\"{\"\"side\"\":\"\"");
            b.append(side ? "start" : "end").append("\"\"}\"");
            return b.toString();
        }

        @Override
        public String toString() {
            return "x=" + Arrays.toString(x.toArray(new Integer[0])) + "y=" + Arrays.toString(y.toArray(new Integer[0]));
        }
    }

    public DatasetGenerator(int numEach) {
        this.numEach = numEach;
    }

    public void generate() throws Exception {
        ArrayList<ResistorBase> bases = loadBases();
        ArrayList<TrainingImage> backgrounds = loadBackgrounds();

        TrainingImage test = backgrounds.get(0);

        test.drawResistorBase(bases.get(0), 0, 0);
        test.save(new File("C:\\Users\\wdavi\\Downloads\\resistors\\dataset"), 0);
    }

    @SuppressWarnings("all")
    private ArrayList<ResistorBase> loadBases() throws Exception {
        // 1) Load resistor images into ResistorBase class
        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\wdavi\\Downloads\\resistors\\full" +
                "\\annotations-baseline.csv"));
        // Skip the first line
        br.readLine();

        String line1 = br.readLine(), line2 = br.readLine();

        ArrayList<ResistorBase> bases = new ArrayList<>();

        while(true) {
            // File name
            Matcher m = Pattern.compile(".*\\.png").matcher(line1);
            m.find();
            File imagePath = new File("C:\\Users\\wdavi\\Downloads\\resistors\\full", m.group());

            // Get regions for both
            Pattern xPattern = Pattern.compile("\"\"all_points_x\"\":\\[((\\d+,|\\d+)*)]");
            Pattern yPattern = Pattern.compile("\"\"all_points_y\"\":\\[((\\d+,|\\d+)*)]");

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

            ResistorRegion start = new ResistorRegion(l1IsStart ? x1 : x2, l1IsStart ? y1 : y2);
            ResistorRegion end = new ResistorRegion(l2IsStart ? x1 : x2, l2IsStart ? y1 : y2);

            bases.add(new ResistorBase(imagePath, start, end));

            line1 = br.readLine();
            if(line1 == null) break;
            line2 = br.readLine();
        }

        br.close();

        return bases;
    }

    @SuppressWarnings("all")
    private ArrayList<TrainingImage> loadBackgrounds() throws Exception {
        File dir = new File("C:\\Users\\wdavi\\Downloads\\resistors\\backgrounds");

        File[] children = dir.listFiles();

        ArrayList<TrainingImage> image = new ArrayList<>();

        for(File f : children) {
            image.add(new TrainingImage(f));
        }

        return image;
    }

    public static void main(String[] args) throws Exception {
        new DatasetGenerator(100).generate();
    }

}
