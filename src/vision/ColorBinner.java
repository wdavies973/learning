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

            return new Result("black", score, confidence);
        } else if(saturation < 15 && bright > 80) {
            confidence = Math.min(15 - saturation, bright - 80);

            return new Result("white", score, confidence);
        } else if(saturation < 15 && bright > 60) {
            confidence = Math.min(15 - saturation, bright - 60);

            return new Result("silver", score, confidence);
        } else if(saturation < 15 && bright > 25) {
            confidence = Math.min(15 - saturation, bright - 25);

            return new Result("grey", score, confidence);
        }

        if(hue >= 336 || hue < 15) {
            if(hue >= 336) {
                confidence = 336 - hue;
            } else {
                confidence = 15 - hue;
            }

            return new Result("red", score, confidence);
        } else if(hue >= 15 && hue < 32) {
            confidence = Math.min(hue - 15, 32 - hue);

            if(bright < 60 && bright < 25) {
                return new Result("brown", score, confidence);
            }

            return new Result("orange", score, confidence);
        } else if(hue >= 32 && hue < 60) {
            confidence = Math.min(hue - 32, 60 - hue);
            
            if(saturation < 40 && bright < 60) {
                double check = Math.min(40 - saturation, 60 - bright);
                
                return new Result("tan", score, Math.min(confidence, check));
            }

            return new Result("gold", score, confidence);
        } else if(hue >= 60 && hue < 80) {
            confidence = Math.min(hue - 60, 80 - hue);
                
            if(saturation < 20) {
                double check = Math.min(confidence, 20 - saturation);
                
                return new Result("tan", score, check);
            }
            
            return new Result("yellow", score, confidence);
        } else if(hue >= 80 && hue < 161) {
            confidence = Math.min(hue - 80, 161- hue);
            
            return new Result("green", score, confidence);
        } else if(hue >= 161 && hue < 241) {
            confidence = Math.min(hue - 161, 241 - hue);
            
            return new Result("blue", score, confidence);
        } else if(hue >= 241 && hue < 336) {
            confidence = Math.min(hue - 243, 336 - hue);
            
            return new Result("violet", score, confidence);
        }

        return null;
    }

}
