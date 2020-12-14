package vision;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class ResistorDetector {

    private ArrayList<ColorBin> colors = new ArrayList<>();

    public ResistorDetector() {
        // Add appropriate color bins
        colors.add(new ColorBin(new Color(103, 43, 42), "red")); // red
        colors.add(new ColorBin(new Color(123, 67, 31), "orange")); // orange
        colors.add(new ColorBin(new Color(125, 120, 55), "yellow")); // yellow
        colors.add(new ColorBin(new Color(54, 86, 54), "green")); // green
        colors.add(new ColorBin(new Color(0, 0, 255), "blue")); // blue
        colors.add(new ColorBin(new Color(32, 30, 43), "violet")); // violet
        //colors.add(new ColorBin(new Color(238, 130, 238), "violet")); // violet
        colors.add(new ColorBin(new Color(113, 110, 71), "gold")); // gold
        colors.add(new ColorBin(new Color(55, 48, 40), "brown")); // brown
        colors.add(new ColorBin(0, 0, .5f, "grey")); // grey
        colors.add(new ColorBin(0, 0, .8f, "silver")); // silver
        colors.add(new ColorBin(0, 0, 1f, "white")); // white
        colors.add(new ColorBin(new Color(30, 32, 28), "black")); // black
        colors.add(new ColorBin(new Color(30, 32, 28), "tan")); // black
    }

    private ArrayList<ColorBin> getColors() {
        return colors;
    }

    private static class ColorBin {
        float hue, saturation, brightness;
        String name;

        private ColorBin(float hue, float saturation, float brightness, String name) {
            this.hue = hue / 360f;
            this.saturation = saturation / 100f;
            this.brightness = brightness / 100f;
            this.name = name;
        }

        private ColorBin(Color c, String name) {
            float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
            this.hue = hsb[0];
            this.saturation = hsb[1];
            this.brightness = hsb[2];
            this.name = name;
            System.out.println(name+"==="+Arrays.toString(hsb));
        }

        public double dist(double[] vals) {
            double dh = Math.min(Math.abs(vals[0] - hue), 1f - Math.abs(vals[0] - hue));
            double ds = Math.abs(vals[1] - saturation);
            double dv = Math.abs(vals[2] - brightness);

//            System.out.println(name+","+dh+","+ds+","+dv);

            return Math.sqrt(dh * dh * 500f + ds * ds * 99f + dv * dv);
        }
    }

    private double maxDiff() {
        return Math.sqrt(.5f * .5f * 500f + 99f + 1);
    }

    private static class ClosestRet {
        double dist;
        ColorBin c;

        public ClosestRet(double dist, ColorBin c) {
            this.dist = dist;
            this.c = c;
        }
    }

    private ClosestRet closest(float[] c) {
        double[] hsb = {c[0], c[1], c[2]};

        ColorBin smallest = null;
        double min = Float.MAX_VALUE;

        for(ColorBin color : colors) {
            double dist = color.dist(hsb);
            if(dist < min) {
                min = dist;
                smallest = color;
            }
        }

        System.out.println(min);

        return new ClosestRet(min, smallest);
    }

    ColorBinner cb = new ColorBinner();

    public void determineResistance(BufferedImage img) throws Exception {
        HashMap<String, Double> hues = new HashMap<>();
        for(ColorBin c : colors) hues.put(c.name, 0.0);

        for(int y = 0; y < img.getHeight(); y++) {
            for(int x = 0; x < img.getWidth(); x++) {
                int p = img.getRGB(x, y);

                // Ignore transparent pixels
                if((p >> 24) == 0x00) {
                    continue;
                }

                float[] hsb = Color.RGBtoHSB((p >> 16) & 0xff, (p >> 8) & 0xff, p & 0xff, null);
                ColorBinner.Result closest = cb.query(hsb);

                if(closest != null) {
                    //if(closest.equals("orange")) System.out.println(x+","+y);

                    hues.put(closest.name, hues.get(closest.name) + closest.score);
                }
            }
        }

        for(String s : hues.keySet()) {
            System.out.println(s + "=" + hues.get(s));
        }
    }

    // Improvement: Some tolerances/color bands can't swap

    public static void main(String[] args) throws Exception {
        File resistor = new File("C:\\Users\\wdavi\\Desktop\\UMN-5561\\ocular\\data\\resistors\\resistors\\47kohm.png");
        //File resistor = new File("C:\\Users\\wdavi\\Downloads\\47kohm.png");

        new ResistorDetector().determineResistance(ImageIO.read(resistor));

        Color s = new Color(110, 60, 26);
        float[] test = Color.RGBtoHSB(s.getRed(), s.getGreen(), s.getBlue(), null);
        System.out.println(Arrays.toString(test));
        System.out.println(new ColorBinner().query(test));
    }

}
