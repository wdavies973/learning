package umn.ai1;

import java.util.ArrayList;
import java.util.Collections;

// very (very) quick and dirty propositional logic resolver
public class Resolver {

    public static class Symbol {
        boolean modifier = true;
        char symbol;

        public Symbol(boolean modifier, char symbol) {
            this.modifier = modifier;
            this.symbol = symbol;
        }

        public Symbol(char symbol) {
            this.symbol = symbol;
        }

        public boolean cancels(Symbol s) {
            return s.symbol == symbol && modifier != s.modifier;
        }

        public boolean equals(Symbol s) {
            return s.symbol == symbol && modifier == s.modifier;
        }
    }

    public static class SymbolSet {
        public ArrayList<Symbol> symbols = new ArrayList<>();

        public static int ID = 0;

        private int id;

        public String mergedString = "";
        public SymbolSet merge1, merge2;

        public SymbolSet(ArrayList<Symbol> symbols) {
            this.symbols = symbols;
        }

        public SymbolSet(Symbol ... symbols) {
            Collections.addAll(this.symbols, symbols);
            id = ++ID;
        }

        private ArrayList<Symbol> oneWayMerge(ArrayList<Symbol> one, ArrayList<Symbol> two, boolean addDupes) {
            ArrayList<Symbol> result = new ArrayList<>();

            for(Symbol i : symbols) {
                boolean canceled = false;
                boolean equaled = false;

                for(Symbol j : symbols) {
                    if(i.cancels(j)) {
                        canceled = true;
                        break;
                    }
                }

                if(!canceled) {
                    result.add(i);
                }
            }

            return result;
        }

        public boolean equals(SymbolSet set2) {
            return set2.id == id;
        }

        public boolean actuallyEquals(SymbolSet set3) {
            if(symbols.size() != set3.symbols.size()) return false;

            for(int i = 0; i < symbols.size(); i++) {
                boolean okay = false;

                for(int j = 0; j < set3.symbols.size(); j++) {
                    if(symbols.get(i).equals(set3.symbols.get(j))) {
                        okay = true;
                        break;
                    }
                }

                if(!okay) {
                    return false;
                }
            }

            for(int i = 0; i < set3.symbols.size(); i++) {
                boolean okay = false;

                for(int j = 0; j < symbols.size(); j++) {
                    if(symbols.get(i).equals(set3.symbols.get(j))) {
                        okay = true;
                        break;
                    }
                }

                if(!okay) {
                    return false;
                }
            }

            return true;
        }

        public SymbolSet merge(SymbolSet s) {
            ArrayList<Symbol> d = new ArrayList<>();

            boolean canceled = false;

            // Ensure at least one conflict
            loop : for(Symbol i : symbols) {
                for(Symbol j : s.symbols) {
                    if(i.cancels(j)) {
                        canceled = true;
                        break loop;
                    }
                }
            }

            if(!canceled) {
                return null;
            }

            d.addAll(symbols);
            d.addAll(s.symbols);

            for(int i = 0; i < d.size(); i++) {
                for(int j = 0;j < d.size(); j++) {
                    if(i == j) continue;

                    if(d.get(i).equals(d.get(j))) {
                        d.remove(j);
                        i = 0;
                        j = 0;
                    } else if(d.get(i).cancels(d.get(j))) {
                        int finalJ = j;
                        d.removeIf((v) -> v.symbol == d.get(finalJ).symbol);
                        i = 0;
                        j = 0;
                    }
                }
            }

            SymbolSet s2 = new SymbolSet(d);
            s2.mergedString = "("+toString()+" & "+s.toString()+")";
            s2.merge1 = this;
            s2.merge2 = s;
            return s2;
        }

        public boolean empty() {
            return symbols.size() == 0;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            for(Symbol s : symbols) {
                sb.append(s.modifier ? "" : '¬').append(s.symbol).append(", ");
            }
            //sb.append(mergedString);

            return sb.toString().substring(0, sb.length() - 2);
        }
    }

    public static void resolve(SymbolSet ... sets) {
        ArrayList<SymbolSet> result = new ArrayList<>();

        Collections.addAll(result, sets);

        for(int i = 0; i < result.size(); i++) {
            for(int j = 0;j < result.size();j++) {
                if(i == j) continue;

                // if(sets[i].equals(sets[j])) continue;

                SymbolSet merged = result.get(i).merge(result.get(j));

                if(merged == null) continue;

                if(merged.symbols.size() == 0) {
                    for(SymbolSet s : result) {
                        System.out.println(s);
                    }

                    System.out.println("ENTAILS");
                    return;
                }

                // Check to make sure it's not already there
                boolean shouldAdd = true;

                for(SymbolSet t : result) {
                    if(t.actuallyEquals(merged)) {
                        shouldAdd = false;
                        break;
                    }
                }


                if(shouldAdd) {
                    merged.id = ++SymbolSet.ID;
                    result.add(merged);
                    i = 0;
                    j = 0;
                }
            }
        }

        for(SymbolSet s : result) {
            if(s.merge1 == null) System.out.println(s+" Premise");
            else System.out.println("{"+s+"} ("+s.merge1.id+" & "+s.merge2.id+")");
        }

        System.out.println("NOT ENTAILS");
    }

    public static void main(String[] args) {
        SymbolSet one = new SymbolSet(new Symbol('a'), new Symbol('c'));
        SymbolSet two = new SymbolSet(new Symbol(false, 'a'), new Symbol('b'));
        SymbolSet three = new SymbolSet(new Symbol('c'), new Symbol(false, 'd'), new Symbol(false, 'e'));
        SymbolSet four = new SymbolSet(new Symbol('d'), new Symbol('c'));
        SymbolSet five = new SymbolSet(new Symbol(false, 'b'), new Symbol(false, 'd'), new Symbol('e'));
        SymbolSet six = new SymbolSet(new Symbol(false, 'e'));

       resolve(one, two, three, four, five, six);
    }

}
