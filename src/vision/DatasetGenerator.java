package vision;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatasetGenerator {

    private int numTrain, numTest;

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

        public void drawResistorBase(ResistorBase rb, int x, int y, int rotation) {
            double angle = Math.toRadians(rotation);

            // Perform rotation if needed
            if(rotation != 0) {
                AffineTransform i = new AffineTransform();
                i.setTransform(new AffineTransform());
                i.translate(x, y);
                i.rotate(angle);
                this.g.drawImage(rb.copy(20).image, i, null);
            } else {
                this.g.drawImage(rb.copy(20).image, x, y, null);
            }

            // Add annotation information
            anno.append(rb.start.toAnnotation(x, y, angle, regionId, true)).append("\n");
            anno.append(rb.end.toAnnotation(x, y, angle, regionId + 1, false)).append("\n");

            regionId += 2;
        }

        public void save(File parent, boolean isTest, int id) throws Exception {
            // save the image
            File img = new File(parent, (isTest ? "/val/" : "/train/") + id + ".png");
            img.createNewFile();
            ImageIO.write(this.img, "PNG", img);

            File annotations = new File(img.getParent(), "annotations" + id + ".csv");
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

        private ResistorBase(BufferedImage image, ResistorRegion start, ResistorRegion end) {
            this.image = image;
            this.start = start;
            this.end = end;
        }

        Random rnd = new Random();

        public ResistorBase copy(int delta) {
            BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

            int w = image.getWidth();
            int h = image.getHeight();

            for(int i = 0; i < w; i++) {
                for(int j = 0; j < h; j++) {
                    int rgb = image.getRGB(i, j);
                    Color c = new Color(rgb);
                    int r = c.getRed();
                    int b = c.getBlue();
                    int g = c.getGreen();

//                    if(rgb != 0) {
//                        r += rnd.nextInt(delta * 2) - delta;
//                        g += rnd.nextInt(delta * 2) - delta;
//                        b += rnd.nextInt(delta * 2) - delta;
//
//                        if(r < 0) r = 0;
//                        else if(r > 255) r = 255;
//
//                        if(b < 0) b = 0;
//                        else if(b > 255) b = 255;
//
//                        if(g < 0) g = 0;
//                        else if(g > 255) g = 255;
//                    }
                    byte alpha = 0;
                    if(rgb == 0) {
                        alpha = (byte) 0;
                    } else alpha = (byte) (254 % 0xff);

                    int mc = (alpha << 24) | 0x00ffffff;
                    copy.setRGB(i, j, new Color(r, g, b).getRGB() & mc);
                }
            }

            return new ResistorBase(copy, start, end);
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

        private Point transform(int px, int py, double angle, int x, int y) {
            double s = Math.sin(angle);
            double c = Math.cos(angle);

            x -= px;
            y -= py;

            double xNew = x * c - y * s;
            double yNew = x * s + y * c;

            x = (int) Math.round(xNew + px);
            y = (int) Math.round(yNew + py);
            return new Point(x, y);
        }

        public String toAnnotation(int startX, int startY, double rotation, int id, boolean side) {
            // Transform all points
            ArrayList<Point> transformed = new ArrayList<>();
            for(int i = 0; i < x.size(); i++) {
                transformed.add(transform(startX, startY, rotation, x.get(i) + startX, y.get(i) + startY));
            }

            StringBuilder b = new StringBuilder();
            b.append("#####").append(",").append("^^^^^").append(",\"{}\",$$$$$,").append(id).append(",");
            b.append("\"{\"\"name\"\":\"\"polygon\"\",\"\"all_points_x\"\":[");
            for(int i = 0; i < transformed.size(); i++) {
                b.append(transformed.get(i).x);
                if(i != x.size() - 1) b.append(",");
            }
            b.append("],\"\"all_points_y\"\":[");
            for(int i = 0; i < transformed.size(); i++) {
                b.append(transformed.get(i).y);
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

    public DatasetGenerator(int numTrain, int numTest) {
        this.numTrain = numTrain;
        this.numTest = numTest;
    }

    public void generate(File workingDir) throws Exception {
        ArrayList<ResistorBase> bases = loadBases(workingDir);
        ArrayList<TrainingImage> backgrounds = loadBackgrounds(workingDir);

        Random rnd = new Random();
        for(int j = 0; j < numTrain + numTest; j++) {
            TrainingImage bck = backgrounds.get(rnd.nextInt(backgrounds.size())).copy();

            for(int i = 0; i < 4; i++) {
                ResistorBase rb1 = bases.get(rnd.nextInt(bases.size()));

                bck.drawResistorBase(rb1,
                        nextIntRange(rnd, rb1.image.getWidth() * 3, bck.img.getWidth() - rb1.image.getWidth() * 3),
                        nextIntRange(rnd, rb1.image.getHeight() * 3, bck.img.getHeight() - rb1.image.getHeight() * 3),
                        rnd.nextInt(360));
            }

            bck.save(workingDir, j >= numTrain, j);
        }
    }

    private int nextIntRange(Random rnd, int low, int high) {
        return low + rnd.nextInt(high - low + 1);
    }

    @SuppressWarnings("all")
    private ArrayList<ResistorBase> loadBases(File working) throws Exception {
        // 1) Load resistor images into ResistorBase class
        BufferedReader br = new BufferedReader(new FileReader(new File(working, "\\resistors\\annotations-baseline" +
                ".csv")));
        // Skip the first line
        br.readLine();

        String line1 = br.readLine(), line2 = br.readLine();

        ArrayList<ResistorBase> bases = new ArrayList<>();

        while(true) {
            // File name
            Matcher m = Pattern.compile(".*\\.png").matcher(line1);
            m.find();
            File imagePath = new File(new File(working, "\\resistors"),
                    m.group());

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
    private ArrayList<TrainingImage> loadBackgrounds(File working) throws Exception {
        File dir = new File(working, "\\backgrounds");

        File[] children = dir.listFiles();

        ArrayList<TrainingImage> image = new ArrayList<>();

        for(File f : children) {
            image.add(new TrainingImage(f));
        }

        return image;
    }

    public static void main(String[] args) throws Exception {
        File working = new File(args[0]);

        new DatasetGenerator(Integer.parseInt(args[1]), Integer.parseInt(args[2])).generate(working);
    }

}
