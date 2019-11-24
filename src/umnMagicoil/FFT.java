package umnMagicoil;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.io.*;

public class FFT {

    public void fft() throws IOException  {
        File file = new File("C:\\Users\\wdavi\\Desktop\\learning\\samples.txt");

        int n = 1000000;
        double[] samples = new double[n+48576];

        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;
        int index = 0;
        while((line = reader.readLine()) != null) {
            samples[index] = Double.parseDouble(line);
            index++;
        }

        // The file is loaded, start processing
        double volt = 5.0 / (Math.pow(2, 24));

        for(int i = 0; i < n; i++) {
            samples[i] = samples[i] * volt;
        }

        FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);

        Complex[] transformed = transformer.transform(samples, TransformType.FORWARD);

        double[] abs = new double[transformed.length];

        // Write out to a file
        File output = new File("C:\\Users\\wdavi\\Desktop\\learning\\output.txt");
        output.createNewFile();

        PrintWriter writer = new PrintWriter(new FileWriter(output));
        for(int i = 0; i < transformed.length; i++) {
            abs[i] = transformed[i].abs();
            writer.println(abs[i]);
        }
    }

    public static void main(String[] args) throws IOException {
        new FFT().fft();
    }

}
