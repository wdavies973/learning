package mining;

import java.util.ArrayList;

public class Metrics {

    static boolean stringsEql(String h, String z) {
        String o = h.replaceAll(",", "").replaceAll("\\{", "").replaceAll("}", "");
        String t = z.replaceAll(",", "").replaceAll("\\{", "").replaceAll("}", "");

        for(int i = 0; i < o.length(); i++) {
            boolean found = false;

            for(int j = 0; j < t.length(); j++) {
                if(t.charAt(j) == o.charAt(i)) {
                    found = true;
                    break;
                }
            }

            if(!found) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {

//        String[] wow = new String[]{
//                "{a,b,c,d}",
//                "{a,b,c,e}",
//                "{a,b,c,f}",
//                "{a,b,d,c}",
//                "{a,b,d,e}",
//                "{a,b,d,f}",
//                "{a,b,e,c}",
//                "{a,b,e,d}",
//                "{a,b,e,f}",
//                "{a,c,d,b}",
//                "{a,c,d,e}",
//                "{a,c,d,f}",
//                "{a,d,e,b}",
//                "{a,d,e,c}",
//                "{a,d,e,f}",
//                "{a,e,f,b}",
//                "{a,e,f,c}",
//                "{a,e,f,d}",
//                "{c,d,e,a}",
//                "{c,d,e,b}",
//                "{c,d,e,f}",
//                "{c,e,f,a}",
//                "{c,e,f,b}",
//                "{c,e,f,d}",
//                "{d,e,f,a}",
//                "{d,e,f,b}",
//                "{d,e,f,c}",
//        };
//
//        for(int i = 0; i < wow.length; i++) {
//            for(int j = 0; j < wow.length; j++) {
//                if(i == j || "".equals(wow[j])) continue;
//
//                // Verify two strings are equal
//                if(stringsEql(wow[j], wow[i])) {
//                    wow[j] = "";
//                    break;
//                }
//            }
//        }
//
//        for(String s : wow) {
//            if(s.equals("")) continue;
//
//            System.out.print(s+" ");
//        }

        /*
         * Section of interest 1: metrics on a chart
         */
        double TP = 90;
        double FP = 90;
        double FN = 10;
        double TN = 810;

        double precision = TP / (TP + FP);
        double recall = TP / (TP + FN);
        double fpr = FP / (FP + TN);
        double fMeasure = (2 * TP) / (2 * TP + FP + FN);
        double fMeasure2 = 2.0 / ((1.0 / recall) + (1.0 / precision));

        System.out.println("Precision: "+precision);
        System.out.println("Recall: "+recall);
        System.out.println("FPR: "+fpr);
        System.out.println("FMeasure: "+fMeasure);
        System.out.println("FMeasure2: "+fMeasure2);

//        ModelTest t = new ModelTest(1000, 100);
//
//        t.testModel(0.4, 0.1);
//        t.testModel(0.5, 0.1);
        
//        ArrayList<Instance> m1 = new ArrayList<>();
//
//        m1.add(new Instance(true, 0.94));
//        m1.add(new Instance(true, 0.31));
//        m1.add(new Instance(true, 0.76));
//        m1.add(new Instance(true, 0.31));
//        m1.add(new Instance(true, 0.82));
//
//        m1.add(new Instance(false, 0.33));
//        m1.add(new Instance(false, 0.47));
//        m1.add(new Instance(false, 0.46));
//        m1.add(new Instance(false, 0.24));
//        m1.add(new Instance(false, 0.45));
//
//        ArrayList<Instance> m2 = new ArrayList<>();
//
//        m2.add(new Instance(true, 0.27));
//        m2.add(new Instance(true, 0.45));
//        m2.add(new Instance(true, 0.95));
//        m2.add(new Instance(true, 0.46));
//        m2.add(new Instance(true, 0.23));
//
//        m2.add(new Instance(false, 0.13));
//        m2.add(new Instance(false, 0.08));
//        m2.add(new Instance(false, 0.19));
//        m2.add(new Instance(false, 0.37));
//        m2.add(new Instance(false, 0.04));
//
//        //constructCurve(is);
//        metrics(m1, 0.7);
//        metrics(m2, 0.7);

    }

    public static class Instance {
        boolean c;
        double p;
        double tpr;
        double fpr;

        public Instance(boolean c, double p) {
            this.c = c;
            this.p = p;
        }
    }

    public static void metrics(ArrayList<Instance> instances, double threshold) {
            double tp = 0;
            double fp = 0;
            double tn = 0;
            double fn = 0;

            // above positive
            // below negative
            for(Instance j : instances) {
                if(j.p >= threshold && j.c) {
                    tp++;
                } else if(j.p >= threshold && !j.c) {
                    fp++;
                } else if(j.p < threshold && j.c) {
                    fn++;
                } else if(j.p < threshold && !j.c) {
                    tn++;
                }
            }

            double p = tp / (tp + fp);
            double r = tp / (tp + fn);
            double f = 2 * r * p / (r + p);

            System.out.println("Precision: "+p);
            System.out.println("Recall: "+r);
            System.out.println("F-measure: "+f);
    }

    public static void constructCurve(ArrayList<Instance> instances) {
        instances.sort((o1, o2) -> Double.compare(o2.p, o1.p));

        for(Instance i : instances) {
            double threshold = i.p;

            double tp = 0;
            double fp = 0;
            double tn = 0;
            double fn = 0;

            // above positive
            // below negative
            for(Instance j : instances) {
                if(j.p >= threshold && j.c) {
                    tp++;
                } else if(j.p >= threshold && !j.c) {
                    fp++;
                } else if(j.p < threshold && j.c) {
                    fn++;
                } else if(j.p < threshold && !j.c) {
                    tn++;
                }
            }

            i.tpr = tp / (tp + fn);
            i.fpr = fp / (fp + tn);

            System.out.print(i.p+"("+(i.c ? '+' : '-')+")["+i.fpr+","+i.tpr+"] ");
        }

    }

    public static class ModelTest {

        private int populationSize;
        private int positives;

        public ModelTest(int populationSize, int positives) {
            this.populationSize = populationSize;
            this.positives = positives;
        }

        public void testModel(double tpr, double fpr) {
            double fnr = 1 - tpr;
            double tnr = 1 - fpr;

            System.out.println("Important: The model correctly identified "+((double)positives * tpr) +" / "+positives +" positives");
            System.out.println("Important: The model correctly identified "+((double)(populationSize - positives) * tnr) +" / "+(populationSize - positives) +" negatives");
            System.out.println("Moderate: The model mistakenly identified "+((double)(populationSize - positives) * fpr)+" / "+(populationSize - positives)+" negatives as positive");
            System.out.println("Important: The model mistakenly identified "+(((double)positives * fnr)+" / "+positives)+" positives as negatives\n");
        }

    }




}
