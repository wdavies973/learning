package mining;

import sun.plugin2.os.windows.SECURITY_ATTRIBUTES;

import javax.xml.soap.SAAJMetaFactory;
import java.util.ArrayList;
import java.util.Collections;

public class CandidateSequences {

    public static class Tuple {
        public String[] symbols;
        public Tuple(String ... symbols) {
            this.symbols = symbols;
        }

        public static Tuple inflate(String text) {
            String[] symbols = new String[text.length()];
            for(int i = 0; i < text.length(); i++) {
                symbols[i] = String.valueOf(text.charAt(i));
            }
            return new Tuple(symbols);
        }

        public Tuple copy() {
            String[] symbols = new String[this.symbols.length];
            for(int i = 0; i < this.symbols.length; i++) {
                symbols[i] = this.symbols[i];
            }
            return new Tuple(symbols);
        }

        public Tuple delete(int index) {
            String[] symbols = new String[this.symbols.length - 1];

            for(int i = 0; i < index; i++) {
                symbols[i] = this.symbols[i];
            }

            for(int i = index + 1; i < this.symbols.length; i++) {
                symbols[i - 1] = this.symbols[i];
            }

            return new Tuple(symbols);
        }

        public Tuple deleteFirst() {
            if(symbols.length == 1) {
                return null;
            } else {
                String[] cpy = new String[symbols.length - 1];
                for(int i = 1; i < this.symbols.length; i++) {
                    cpy[i-1] = this.symbols[i];
                }
                return new Tuple(cpy);
            }
        }

        public Tuple deleteLast() {
            if(symbols.length == 1) {
                return null;
            } else {
                String[] cpy = new String[symbols.length - 1];
                for(int i = 0; i < this.symbols.length - 1; i++) {
                    cpy[i] = this.symbols[i];
                }
                return new Tuple(cpy);
            }
        }

        public String getLast() {
            return symbols[symbols.length - 1];
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for(String s : symbols) {
                sb.append(s);
            }
            return sb.toString();
        }

        public int length() {
            return symbols.length;
        }
    }

    public static class MergeResult {
        Sequence first;
        Sequence last;

        Sequence merged;

        public MergeResult(Sequence first, Sequence last, Sequence merged) {
            this.first = first;
            this.last = last;
            this.merged = merged;
        }

        public String toString() {
            return merged.toString() +" ("+first.id.toLowerCase() +" & "+last.id.toLowerCase()+")";
        }
    }

    public static class Sequence {
        public Tuple[] tuples;
        public String id;
        public Sequence(String id, Tuple ... tuples) {
            this.id = id;
            this.tuples = tuples;
        }

        static Sequence inflateSequence(String id, String flattened) {
            String[] tokens = flattened.split(",");

            ArrayList<String> cleaned = new ArrayList<>();

            for(String s : tokens) {
                if(!s.equals("")) {
                    cleaned.add(s);
                }
            }

            Tuple[] tuples = new Tuple[cleaned.size()];

            for(int i = 0; i < cleaned.size(); i++) {
                tuples[i] = Tuple.inflate(cleaned.get(i));
            }

            return new Sequence(id, tuples);
        }

        public MergeResult merge(Sequence seq) {
            // First, figure out if a merge is possible
            if(!deleteFirst().flatten().equals(seq.deleteLast().flatten())) {
                return null;
            }

            Tuple[] cpy = new Tuple[tuples.length + 1];
            // Get last event of last tuple in seq
            for(int i = 0; i < tuples.length; i++) {
                cpy[i] = tuples[i];
            }
            cpy[tuples.length] = new Tuple(seq.getLast());

            return new MergeResult(this, seq, new Sequence("", cpy));
        }

        public String getLast() {
            return tuples[tuples.length - 1].getLast();
        }

        public String flatten() {
            StringBuilder sb = new StringBuilder();
            for(Tuple t : tuples) {
                sb.append(t.toString()).append(",");
            }
            return sb.toString().substring(0, sb.length() - 1);
        }

        public boolean equalTo(Sequence s) {
            return flatten().equals(s.flatten());
        }

        public Sequence delete(int index) {
            String flatten = flatten();

            //flatten = flatten.substring(0, index) + flatten.substring(index + 1);
            int actual = 0;

            for(int i = 0; i < flatten.length(); i++) {
                if(flatten.charAt(i) == ',') {
                    continue;
                }
                if(index == actual) {
                    flatten = flatten.substring(0, i) + flatten.substring(i + 1);
                    break;
                }
                actual++;
            }

            return Sequence.inflateSequence(id, flatten);
        }

        public boolean shouldPrune(ArrayList<Sequence> given) {
            ArrayList<Sequence> subset = new ArrayList<>();

            for(int i = 0; i < 4; i++) {
                subset.add(delete(i));
            }

            for(Sequence s : subset) {
                boolean okay = false;
                for(Sequence g : given) {
                    if(g.equalTo(s)) {
                        okay = true;
                        break;
                    }
                }
                if(!okay) {
                    System.out.println("Pruned "+this+" because given didn't have subset: "+s);
                    return true;
                }
            }


            return false;
        }

        public Sequence deleteFirst() {
            // Create a copy of the first tuple
            Tuple t = tuples[0].deleteFirst();

            Tuple[] cpy;
            if(t == null) {
                cpy = new Tuple[this.tuples.length - 1];
                for(int i = 1; i < this.tuples.length; i++) {
                    cpy[i-1] = this.tuples[i];
                }
            } else {
                cpy = new Tuple[this.tuples.length];
                cpy[0] = t;
                for(int i = 1; i < this.tuples.length; i++) {
                    cpy[i] = this.tuples[i];
                }
            }
            return new Sequence(id, cpy);
        }

        public Sequence deleteLast() {
            Tuple t = tuples[tuples.length - 1].deleteLast();

            Tuple[] cpy;
            if(t == null) {
                cpy = new Tuple[this.tuples.length - 1];
                for(int i = 0; i < this.tuples.length - 1; i++) {
                    cpy[i] = this.tuples[i];
                }
            } else {
                cpy = new Tuple[this.tuples.length];
                for(int i = 0; i < this.tuples.length - 1; i++) {
                    cpy[i] = this.tuples[i];
                }
                cpy[this.tuples.length - 1] = t;
            }
            return new Sequence(id, cpy);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("<");

            for(Tuple t : tuples) {
                sb.append("{").append(t.toString()).append("} ");
            }

            return id+""+sb.substring(0, sb.length() - 1) + ">";
        }
    }

    public static void generateCandidates(ArrayList<Sequence> given) {
        ArrayList<Sequence> candidates = new ArrayList<>();

        for(int i = 0; i < given.size(); i++) {
            for(int j = 0; j < given.size(); j++) {
                MergeResult mergeResult = given.get(i).merge(given.get(j));
                if(mergeResult != null) {
                    candidates.add(mergeResult.merged);
                    System.out.println(mergeResult);
                }
            }
        }

        for(int i = 0; i < candidates.size(); i++) {
            if(candidates.get(i).shouldPrune(given)) {
                candidates.remove(i);
                i--;
            }
        }

        // Prune
        System.out.println("After pruning: ");
        for(Sequence s : candidates) {
            System.out.println(s);
        }

    }

    public static void main(String[] args) {
        Tuple a = new Tuple("a");
        Tuple b = new Tuple("b");
        Tuple c = new Tuple("c");
        Tuple d = new Tuple("d");
        Tuple ab = new Tuple("a", "b");
        Tuple cd = new Tuple("c", "d");
        Tuple bc = new Tuple("b", "c");
        Tuple bcd = new Tuple("b", "c", "d");
        Tuple abc = new Tuple("a", "b", "c");

        Sequence sa = new Sequence("A", ab.copy(), b.copy());
        Sequence sb = new Sequence("B", b.copy(), b.copy(), b.copy());
        Sequence sc = new Sequence("C", a.copy(), b.copy(), b.copy());
        Sequence sd = new Sequence("D", a.copy(), ab.copy());
        Sequence se = new Sequence("E", b.copy(), cd.copy());
        Sequence sf = new Sequence("F", ab.copy(), c.copy());
        Sequence sg = new Sequence("G", bc.copy(), d.copy());
        Sequence sh = new Sequence("H", abc.copy());
        Sequence si = new Sequence("I", bcd.copy());

        ArrayList<Sequence> given = new ArrayList<>();

        Collections.addAll(given, sa, sb, sc, sd, se, sf, sg, sh, si);

        for(Sequence s : given) {
            System.out.println(s);
        }

        // Couple of quick tests
        for(int i = 0; i < given.size(); i++) {
            for(int j = 0; j < given.size(); j++) {
                if(i != j && given.get(i).equalTo(given.get(j))) {
                    System.out.println(given.get(i).id+" equal to "+given.get(j).id);
                }

                if(i != j && Sequence.inflateSequence(given.get(i).id, given.get(i).flatten()).equalTo(given.get(j))) {
                    System.out.println(given.get(i).id+" inflation equal to "+given.get(j).id);
                }
            }
        }

        System.out.println("RESULTS");
        generateCandidates(given);
    }

}
