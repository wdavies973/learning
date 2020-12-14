package vision;

import umn.ai1.SearchLib;

import java.awt.*;
import java.util.ArrayList;

public class ColorBinner {

    public static class Result {
        // Guessed color
        public String name;

        // The score that should be applied to this result
        public double score;

        // Higher the less far away it was from thresholds
        public double confidence;

        public Result(String name, double score, double confidence) {
            this.name = name;
            this.score = score;
            this.confidence = confidence;
        }
    }

    // Takes hsb color, from [0,1] for all values
    public Result query(float[] hsb) {
        float hue = hsb[0] * 360f;
        float saturation = hsb[1] * 100f;
        float bright = hsb[2] * 100f;

        double score = saturation;
        double confidence = 0;

        // Figure out if its a black, grey, white, or silver
        if(bright < 12) {
            confidence = 12 - bright;

            return new Result("black", score, 0);
        } else if(saturation < 15 && bright > 80) {
            confidence = Math.min(15 - saturation, bright - 80);

            return new Result("white", score, 0);
        } else if(saturation < 15 && bright > 60) {
            confidence = Math.min(15 - saturation, bright - 60);

            return new Result("silver", score, 0);
        } else if(saturation < 15 && bright > 25) {
            confidence = Math.min(15 - saturation, bright - 25);

            return new Result("grey", score, 0);
        }

        if(hue >= 336 || hue < 15) {
            if(hue >= 336) {
                confidence = 336 - hue;
            } else {
                confidence = 15 - hue;
            }

            return new Result("red", score, 0);
        } else if(hue >= 15 && hue < 32) {
            confidence = Math.min(15 - hue, 32 - hue);

            if(bright < 60 && bright < 25) {
                return new Result("brown", score, 0);
            }

            return new Result("orange", score, 0);
        } else if(hue >= 32 && hue < 60) {
            if(saturation < 40 && bright < 60) {
                return new Result("tan", score, 0);
            }

            return new Result("gold", score, 0);
        } else if(hue >= 60 && hue < 80) {
            if(saturation < 20) {
                return new Result("tan", score, 0);
            }
            
            return new Result("yellow", score, 0);
        } else if(hue >= 80 && hue < 161) {
            return new Result("green", score, 0);
        } else if(hue >= 161 && hue < 241) {
            return new Result("blue", score, 0);
        } else if(hue >= 241 && hue < 336) {
            return new Result("violet", score, 0);
        }

        return null;
    }

}
