package mining;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Record {

    // Internally, represent everything as a double
    public static class Attribute {
        private double value;

        public Attribute(double value) {
            this.value = value;
        }

        public boolean equals(Attribute attribute) {
            return value == attribute.value;
        }

        public int compareTo(Attribute attribute) {
            return Double.compare(value, attribute.value);
        }

        public double value() {
            return value;
        }
    }

    public ArrayList<Attribute> attributes = new ArrayList<>();

    private Record() {

    }

    public double length() {
        double sum = 0;

        for(Attribute a : attributes) {
            sum += a.value * a.value;
        }

        return Math.sqrt(sum);
    }

    public double average() {
        double sum = 0;

        for(Attribute a : attributes) {
            sum += a.value;
        }

        return sum / attributes.size();
    }

    public double std() {
        double avg = average();

        double sum = 0;

        for(Attribute a : attributes) {
            sum += Math.pow((a.value - avg), 2);
        }

        sum = sum / (attributes.size() - 1.0);

        return Math.sqrt(sum);
    }

    public static Record fromValues(double ... values) {
        Record r = new Record();
        for(double d : values) {
            r.attributes.add(new Attribute(d));
        }

        return r;
    }

    public static class TupleHelper {
        ArrayList<Double> a1 = new ArrayList<>();
        ArrayList<Double> a2 = new ArrayList<>();

        public TupleHelper(Record r, Record r2) {

        }

        public void put(double x, double y) {
            // Make sure the pair isn't already contained
            for(int i = 0; i < a1.size(); i++) {
                if(a1.get(i) ==x && a2.get(i) == y) return;
            }

            a1.add(x);
            a2.add(y);
        }

        public double getProbability(double x, double y) {
            double c = 0;

            for(int i = 0; i < a1.size(); i++) {
                if(a1.get(i) == x && a2.get(i) == y) c++;
            }

            return c;
        }
    }

    public static class Iterator {
        private Record r1, r2;

        private int next = 0;

        public double x = 0;
        public double y = 0;

        public Iterator(Record r1, Record r2) {
            this.r1 = r1;
            this.r2 = r2;

            if(r1.attributes.size() != r2.attributes.size() || r1.attributes.size() == 0)
                throw new IllegalArgumentException("Must have same number of attributes and > 0 attributes");
        }

        public boolean next() {
            if(next == size()) return false;

            x = r1.attributes.get(next).value();
            y = r2.attributes.get(next).value();

            next++;

            return next <= r1.attributes.size();
        }

        public double probabilityX() {
            double c = 0;

            for(Attribute d : r1.attributes) {
                if(d.value == x) c++;
            }

            return c / size();
        }

        public double probabilityY() {
            double c = 0;

            for(Attribute d : r2.attributes) {
                if(d.value == y) c++;
            }

            return c / size();
        }

        public double size() {
            return r1.attributes.size();
        }
    }

    /*
     * Proximity measures
     */
    public double manhattanDistance(Record record) {
        Iterator i = new Iterator(this, record);

        double sum = 0;

        while(i.next()) {
            sum += Math.abs(i.x - i .y);
        }

        return sum;
    }

    public double euclidDistance(Record record) {
        Iterator i = new Iterator(this, record);

        double sum = 0;

        while(i.next()) {
            sum += Math.pow(i.x - i.y, 2);
        }

        return Math.sqrt(sum);
    }

    public double supermumDistance(Record record) {
        Iterator i = new Iterator(this, record);

        double max = 0;

        while(i.next()) {
            if(Math.abs(i.x - i.y) > max) {
                max = Math.abs(i.x - i.y);
            }
        }

        return max;
    }

    public double smc(Record record) {
        Iterator i = new Iterator(this, record);

        double matching = 0;

        while(i.next()) {
            if(i.x == i.y) matching++;
        }

        return matching / i.size();
    }

    public double jaccard(Record record) {
        Iterator i = new Iterator(this, record);

        double matching = 0;
        double denominator = 0;

        while(i.next()) {
            if(i.x == i.y && i.x == 1) matching++;

            if(!(i.x == 0 && i.y == 0)) denominator++;
        }

        return matching / denominator;
    }

    public double cosine(Record record) {
        Iterator i = new Iterator(this, record);

        double numerator = 0;

        while(i.next()) {
            numerator += (i.x * i.y);
        }

        return numerator / (this.length() * record.length());
    }

    public double extendedJaccard(Record record) {
        Iterator i = new Iterator(this, record);

        double numerator = 0;

        while(i.next()) {
            numerator += (i.x * i.y);
        }

        return numerator / (this.length() + record.length() - numerator);
    }

    public double correlation(Record record) {
        Iterator i = new Iterator(this, record);

        double cov = 0;
        double r1Avg = this.average();
        double r2Avg = record.average();

        while(i.next()) {
            cov += (i.x - r1Avg) * (i.y - r2Avg);
        }

        cov = cov / (i.size() - 1);

        return cov / (this.std() * record.std());

    }

    private double dualProbability(Record r1, Record r2, double x, double y) {
        double c = 0;

        for(int i = 0; i < r1.attributes.size(); i++) {
            if(r1.attributes.get(i).value == x && r2.attributes.get(i).value == y) {
                c++;
            }
        }

        return c / (double)r1.attributes.size();
    }

    private double probability(Record r, double v) {
        double c = 0;

        for(Attribute d : r.attributes) {
            if(d.value == v) c++;
        }

        return c / (double)r.attributes.size();
    }

    // Basically correlation for non-linear data
    public double mutualInformation(Record record) {
        // Construct sets out of records
        Set<Double> set1 = new HashSet<>();
        Set<Double> set2 = new HashSet<>();

        TupleHelper helper = new TupleHelper(this, record);

        for(int i = 0; i < attributes.size(); i++) {
            set1.add(this.attributes.get(i).value);
            set2.add(record.attributes.get(i).value);

            helper.put(this.attributes.get(i).value, record.attributes.get(i).value);
        }

        java.util.Iterator<Double> ie = set1.iterator();

        double hx = 0, hy = 0, hxy = 0;

        while(ie.hasNext()) {
            double oof = probability(this, ie.next());
            hx += entropy(oof, oof);
        }

        ie = set2.iterator();

        while(ie.hasNext()) {
            double oof = probability(record, ie.next());
            hy += entropy(oof, oof);
        }

        hx *= -1;
        hy *= -1;

        /*
         * Computing hxy
         */
        for(int i = 0; i < helper.a1.size(); i++) {
            double sum = 0;

            for(int j = 0; j < helper.a2.size(); j++) {
                sum += helper.getProbability(helper.a1.get(i), helper.a2.get(i));
            }
        }

        System.out.println(hx + ", " + hy);

        return 69;
    }

    public static double entropy(double coef, double logOf) {
        if(coef == 0 && logOf == 0) return 0;

        return coef * log2(logOf);
    }

    public static double log2(double x) {
        return Math.log(x) / Math.log(2) + 1e-10;
    }

}
