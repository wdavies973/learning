package vision;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("all")
public class DGV2 {

    // Image used for training/testing
    private static class Background {
        File fileBackground;
        BufferedImage imageBackground;
        Graphics2D g;

        StringBuilder regions = new StringBuilder();
        int regionId = 0;

        public Background(BufferedImage background) {
            // Copy the image
            ColorModel cm = background.getColorModel();
            boolean isAlphaPreMultiplied = cm.isAlphaPremultiplied();
            WritableRaster raster = background.copyData(null);
            this.imageBackground = new BufferedImage(cm, raster, isAlphaPreMultiplied, null);

            // Create graphics
            this.g = (Graphics2D) this.imageBackground.getGraphics();
        }

        public void drawResistorBase(StringBuilder annotations, Resistor resistor, int x, int y, int rotation,
                                     int noise) {
            double angle = Math.toRadians(rotation);

            // Perform rotation if needed
            if(rotation != 0) {
                AffineTransform i = new AffineTransform();
                i.setTransform(new AffineTransform());
                i.setToTranslation(x, y);
                i.rotate(angle);
                this.g.drawImage(resistor.copy(noise).image, i, null);
            } else {
                this.g.drawImage(resistor.copy(noise).image, x, y, null);
            }

            // Add annotation information
            regions.append(resistor.start.toAnnotation(x, y, angle, regionId, true)).append("\n");
            regions.append(resistor.end.toAnnotation(x, y, angle, regionId + 1, false)).append("\n");

            regionId += 2;
        }

        public String save(File destination) throws Exception {
            if(!destination.exists()) destination.createNewFile();
            ImageIO.write(this.imageBackground, "PNG", destination);

            StringBuilder annotation = new StringBuilder();

            annotation.append("\"").append(destination.getName()).append(destination.length()).append("\":{");
            annotation.append("\"fileref\":\"\",").append("\"size\":").append(destination.length()).append(",");
            annotation.append("\"filename\":\"").append(destination.getName()).append("\",");
            annotation.append("\"base64_img_data\":\"\",").append("\"file_attributes\":{},");
            annotation.append("\"regions\":{");
            String r = regions.toString();
            annotation.append(r.substring(0, r.length() - 2));
            annotation.append("}}");
            return annotation.toString();
        }

        public Background copy() {
            return new Background(imageBackground);
        }
    }

    // Represents a resistor
    private static class Resistor {
        private BufferedImage image;
        private File file;
        private ResistorRegion start, end;
        private Random rnd = new Random();

        private Resistor(File file, ResistorRegion start, ResistorRegion end) throws Exception {
            this.file = file;
            this.start = start;
            this.end = end;

            image = ImageIO.read(file);
        }

        private Resistor(BufferedImage image, ResistorRegion start, ResistorRegion end) {
            this.image = image;
            this.start = start;
            this.end = end;
        }

        public Resistor copy(int delta) {
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

                    if(rgb != 0 && delta != 0) {
                        r += rnd.nextInt(delta * 2) - delta;
                        g += rnd.nextInt(delta * 2) - delta;
                        b += rnd.nextInt(delta * 2) - delta;

                        if(r < 0) r = 0;
                        else if(r > 255) r = 255;

                        if(b < 0) b = 0;
                        else if(b > 255) b = 255;

                        if(g < 0) g = 0;
                        else if(g > 255) g = 255;
                    }
                    byte alpha = 0;
                    if(rgb == 0) {
                        alpha = (byte) 0;
                    } else alpha = (byte) (254 % 0xff);

                    int mc = (alpha << 24) | 0x00ffffff;
                    copy.setRGB(i, j, new Color(r, g, b).getRGB() & mc);
                }
            }

            return new Resistor(copy, start, end);
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

            StringBuilder sb = new StringBuilder("\"").append(id).append("\":{\"shape_attributes\":{");
            sb.append("\"name\":\"polygon\",\"all_points_x\":[");
            for(int i = 0; i < transformed.size(); i++) {
                sb.append(transformed.get(i).x);
                if(i != x.size() - 1) sb.append(",");
            }
            sb.append("],\"all_points_y\":[");
            for(int i = 0; i < transformed.size(); i++) {
                sb.append(transformed.get(i).y);
                if(i != x.size() - 1) sb.append(",");
            }
            sb.append("]},\"region_attributes\":{},\"side\":\"").append(side ? "start" : "end").append("\"},");
            return sb.toString();
        }

        @Override
        public String toString() {
            return "x=" + Arrays.toString(x.toArray(new Integer[0])) + "y=" + Arrays.toString(y.toArray(new Integer[0]));
        }
    }

    /*
     * Generation code
     */
    public void generate(File workingDir, boolean isTrain, int count, int noise, int resistors) throws Exception {
        ArrayList<Resistor> bases = loadBases(workingDir);
        ArrayList<Background> backgrounds = loadBackgrounds(workingDir);

        StringBuilder annotations = new StringBuilder("{");

        Random rnd = new Random();
        for(int j = 0; j < count; j++) {
            System.out.println((j+1)+" / "+count);

            Background bck = backgrounds.get(rnd.nextInt(backgrounds.size())).copy();


            for(int i = 0; i < resistors; i++) {
                Resistor rb1 = bases.get(rnd.nextInt(bases.size()));

                int validWidth = bck.imageBackground.getWidth() - rb1.image.getWidth() * 2;
                int validHeight = bck.imageBackground.getHeight() - rb1.image.getWidth() * 2;

                int chunkWidth = validWidth / (resistors/2);
                int chunkHeight = validHeight / (resistors/2);

                int quadX = (i * chunkWidth) % validWidth;
                int quadY = ((i * chunkHeight) / validHeight) * chunkHeight;

                bck.drawResistorBase(annotations, rb1,
                        nextIntRange(rnd, quadX + rb1.image.getWidth(),
                                quadX + chunkWidth + rb1.image.getWidth()),
                        nextIntRange(rnd, quadY + rb1.image.getWidth(),
                                quadY + chunkHeight + rb1.image.getWidth()),
                        rnd.nextInt(360), noise);
            }

            // Output location
            File f = isTrain ? new File(workingDir, "/train/" + j + ".png") : new File(workingDir, "/val/" + j +
                    ".png");
            annotations.append(bck.save(f)).append(",");
        }

        annotations.setLength(annotations.length() - 1);

        annotations.append("}");

        File fileAnnotations = isTrain ? new File(workingDir, "/train/via_region_data.json") :
                new File(workingDir, "/val/via_region_data.json");

        BufferedWriter bw = new BufferedWriter(new FileWriter(fileAnnotations));
        bw.write(annotations.toString());
        bw.close();
    }

    private int nextIntRange(Random rnd, int low, int high) {
        return low + rnd.nextInt(high - low + 1);
    }

    @SuppressWarnings("all")
    private ArrayList<Resistor> loadBases(File working) throws Exception {
        // 1) Load resistor images into ResistorBase class
        BufferedReader br = new BufferedReader(new FileReader(new File(working, "\\resistors\\annotations-baseline" +
                ".csv")));
        // Skip the first line
        br.readLine();

        String line1 = br.readLine(), line2 = br.readLine();

        ArrayList<Resistor> bases = new ArrayList<>();

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

            bases.add(new Resistor(imagePath, start, end));

            line1 = br.readLine();
            if(line1 == null) break;
            line2 = br.readLine();
        }

        br.close();

        return bases;
    }

    @SuppressWarnings("all")
    private ArrayList<Background> loadBackgrounds(File working) throws Exception {
        File dir = new File(working, "\\backgrounds");

        File[] children = dir.listFiles();

        ArrayList<Background> image = new ArrayList<>();

        for(File f : children) {
            image.add(new Background(ImageIO.read(f)));
        }

        return image;
    }

    public static void main(String[] args) throws Exception {
        File working = new File(args[0]);

        boolean isTrain = "train".equals(args[1]);
        int count = Integer.parseInt(args[2]);
        int noise = Integer.parseInt(args[3]);
        int resistrCount = Integer.parseInt(args[4]);

        System.out.println(
                working+","+isTrain+","+count+","+noise
        );

        new DGV2().generate(working, isTrain, count, noise, resistrCount);
    }
}
