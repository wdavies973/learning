package ai;

// Definitions
// Frontier/fringe - leaf nodes available for expansion
// Expanding - apply legal action to the current state
// Branch factor - maximum amount of successors to any node
// depth - distance from the top
// height - distance from the bottom

import umn4041.GraphLib;

import java.util.*;

public class SearchLib {

    /*
     * Graph representations
     */
    // vertex id = index
    public interface Searchable {
        Vertex vertex(int index);

        Vertex[] adjList(int index);

        int cost(int v1, int v2);
    }

    public static class Vertex {
        public enum Color {
            White, // undiscovered
            Gray, // discovered but not explored
            Black // explored
        }

        int id;
        Color color;

        // the distance from the source vertex to this one
        int distance;
        // the vertex that was discovered, and whose exploration which lead to this vertex
        Vertex predecessor;

        public Vertex(int id) {
            this.id = id;
            color = Color.White;
            distance = Integer.MAX_VALUE; // TODO infinity & nan
        }
    }

    public static class Graph implements Searchable {

        private Vertex[] vertices;

        public int numVertices;
        private int[][] matrix; // stores connections + weights, infinity if not reachable

        public Graph(int numVertices) {
            this.vertices = new Vertex[numVertices];
            this.numVertices = numVertices;

            // Create vertices
            for (int i = 0; i < numVertices; i++) {
                this.vertices[i] = new Vertex(i);
            }

            this.matrix = new int[numVertices][numVertices];

            for (int i = 0; i < numVertices; i++) {
                for (int j = 0; j < numVertices; j++) {
                    if (i != j) {
                        this.matrix[i][j] = Integer.MAX_VALUE;
                    }
                }
            }
        }

        public void addEdge(int v1, int v2) {
            addEdge(v1, v2, 0);
        }

        public void addEdge(int v1, int v2, int cost) {
            // assumes non-directed edge
            this.matrix[v1][v2] = cost;
        }

        public void addTwoWayEdge(int v1, int v2, int cost) {
            this.matrix[v1][v2] = cost;
            this.matrix[v2][v1] = cost;
        }

        @Override
        public Vertex vertex(int index) {
            return vertices[index];
        }

        @Override
        public Vertex[] adjList(int index) {
            ArrayList<Vertex> vertices = new ArrayList<>();

            for (int i = 0; i < numVertices; i++) {
                if (matrix[index][i] < Integer.MAX_VALUE && i != index) { // a path exists, ignore path to self
                    vertices.add(this.vertices[i]);
                }
            }

            return vertices.toArray(new Vertex[0]);
        }

        @Override
        public int cost(int v1, int v2) {
            return matrix[v1][v2];
        }

        public Graph clone() {
            Graph g = new Graph(numVertices);

            g.matrix = matrix;

            return g;
        }

        public void print() {
            System.out.println("Costs: ");
            for (int row = 0; row < numVertices; row++) {
                for (int col = 0; col < numVertices; col++) {
                    System.out.print(matrix[row][col] + " ");
                }
                System.out.println();
            }
        }

        public static void printPath(Vertex v) {
            Stack<Vertex> path = new Stack<>();

            System.out.print("Path (cost = " + v.distance + "): ");

            while (v != null) {
                path.push(v);

                v = v.predecessor;
            }

            // Print path
            while (!path.isEmpty()) {
                System.out.print(path.pop().id + (path.isEmpty() ? "" : " --> "));
            }
        }

        public static void printPath(HashMap<Character, Integer> map, Vertex v) {
            Stack<Vertex> path = new Stack<>();

            System.out.print("Path (cost = " + v.distance + "): ");

            while (v != null) {
                path.push(v);

                v = v.predecessor;
            }

            // Print path
            while (!path.isEmpty()) {
                for(Character c : map.keySet()) {
                    if(map.get(c) == path.peek().id) {
                        path.pop();
                        System.out.print(c + (path.isEmpty() ? "" : " --> "));
                        break;
                    }
                }


            }
        }
    }


    /*
     * Uninformed strategies
     */

    /*
     * Searches all nodes at a given depth before moving to the next
     * Complete: As long as branching factor is finite
     * Time & Space: O(b^d)
     * Optimal: As long as step costs are identical
     */
    public static class BreadthFirstSearch {
        private Graph graph;


        public BreadthFirstSearch(Graph g) {
            this.graph = g.clone();
        }

        public void traverse(int source) {
            Vertex s = graph.vertex(source);
            s.predecessor = null;
            s.distance = 0;
            s.color = Vertex.Color.Gray;

            Queue<Vertex> exploring = new LinkedList<>();
            exploring.add(s);

            System.out.print(source + " ");

            while (!exploring.isEmpty()) {
                Vertex u = exploring.remove();

                Vertex[] adj = graph.adjList(u.id);

                for (Vertex v : adj) {
                    if (v.color == Vertex.Color.White) {
                        v.color = Vertex.Color.Gray;
                        v.distance = u.distance + 1;
                        v.predecessor = u;

                        exploring.add(v);

                        System.out.print(v.id + " ");
                    }
                    u.color = Vertex.Color.Black;
                }
                System.out.println();
            }
        }

        public void search(int source, int destination) {
            Vertex s = graph.vertex(source);
            s.predecessor = null;
            s.distance = 0;
            s.color = Vertex.Color.Gray;

            Queue<Vertex> exploring = new LinkedList<>();
            exploring.add(s);

            while (!exploring.isEmpty()) {
                Vertex u = exploring.remove();

                Vertex[] adj = graph.adjList(u.id);
                System.out.print("Frontier: ");
                for (Vertex v : adj) {
                    System.out.println(v.id + " ");

                    if (v.color == Vertex.Color.White) {
                        v.color = Vertex.Color.Gray;
                        v.distance = u.distance + 1;
                        v.predecessor = u;

                        if (v.id == destination) {
                            Graph.printPath(v);
                            return;
                        }

                        exploring.add(v);
                    }
                    u.color = Vertex.Color.Black;
                }
                System.out.println();
            }
        }

    }

    /*
     * Like breadth first but with three modifications
     * 1) Priority queue instead of FIFO queue to expand lowest cost node first
     * 2) Goal node must be tested when expanded rather than discovered (in case something is more optimal)
     * 3) If a different optimal route to the frontier is found, it needs to be replaced
     *
     * Searches given depth before searching next, however, chooses the lowest cost node in frontier to explore first.
     * Note - the number of path steps does not get factored in AT ALL
     * Complete: As long as branching factor is finite, can get broken if it gets into a 0-cost loop
     * Time & Space: O(b^d + A constant) At best: b^d+1 Can be much worse than BFS though
     * Optimal: Yes
     */
    public static class UniformCostSearch {

        private Graph graph;

        public UniformCostSearch(Graph g) {
            this.graph = g.clone();
        }

        public void search(int source, int destination) {
            Vertex s = graph.vertex(source);
            s.predecessor = null;
            s.distance = 0;
            s.color = Vertex.Color.Gray;

            PriorityQueue exploring = new PriorityQueue();
            exploring.add(s, 0);

            while (!exploring.isEmpty()) {
                Vertex u = exploring.pop();

                if (u.id == destination) {
                    Graph.printPath(u);
                    return;
                }

                Vertex[] adj = graph.adjList(u.id);
                for (Vertex v : adj) {
                    if (v.color == Vertex.Color.White) {
                        v.color = Vertex.Color.Gray;
                        v.distance = u.distance + graph.cost(u.id, v.id);
                        v.predecessor = u;

                        exploring.add(v, v.distance);
                    }
                    // We are looking for a path to 'v', if the frontier already contains 'v' with a higher cost,
                    // add the updated cost to it
                    else if (exploring.cost(v.id) > u.distance + graph.cost(u.id, v.id)) {
                        v.predecessor = u;
                        v.distance = u.distance + graph.cost(u.id, v.id);
                        exploring.replace(v.id, v, u.distance + graph.cost(u.id, v.id));
                    }
                    u.color = Vertex.Color.Black;
                }
            }
        }
    }

    public static class IterateUniformCostSearch {

        private Graph graph;
        private PriorityQueue exploring = new PriorityQueue();

        public IterateUniformCostSearch(Graph g, int source) {
            this.graph = g.clone();

            Vertex s = graph.vertex(source);
            s.predecessor = null;
            s.distance = 0;
            s.color = Vertex.Color.Gray;
            exploring.add(s, 0);
        }

        public void search(int destination) {
            Vertex u = exploring.pop();

            if (u.id == destination) {
                Graph.printPath(u);
                return;
            }

            Vertex[] adj = graph.adjList(u.id);
            for (Vertex v : adj) {
                if (v.color == Vertex.Color.White) {
                    v.color = Vertex.Color.Gray;
                    v.distance = u.distance + graph.cost(u.id, v.id);
                    v.predecessor = u;

                    exploring.add(v, v.distance);
                }
                // We are looking for a path to 'v', if the frontier already contains 'v' with a higher cost,
                // add the updated cost to it
                else if (exploring.cost(v.id) > u.distance + graph.cost(u.id, v.id)) {
                    v.predecessor = u;
                    v.distance = u.distance + graph.cost(u.id, v.id);
                    exploring.replace(v.id, v, u.distance + graph.cost(u.id, v.id));
                }
                u.color = Vertex.Color.Black;
            }

        }

        public void printSolution(HashMap<Character, Integer> map, IterateUniformCostSearch ucs) {
            for (int i = 0; i < graph.numVertices; i++) {
                if (graph.vertices[i].color == Vertex.Color.Black
                        && ucs.graph.vertices[i].color == Vertex.Color.Black) {
                    Graph.printPath(map, graph.vertex(i));
                    return;
                }
            }
        }

        public boolean overlaps(IterateUniformCostSearch ucs) {
            // Search to see if explored sets overlap
            for (int i = 0; i < graph.numVertices; i++) {
                if (graph.vertices[i].color == Vertex.Color.Black
                        && ucs.graph.vertices[i].color == Vertex.Color.Black) return true;
            }

            return false;
        }
    }

    /*
     * Searches deepest depth first
     *
     * Pretty much trash, don't use this algorithm
     * Complete: No: Can get in loops
     * Time & Space: O(b^m) & O(bm)
     * Optimal: No
     */
    public static class DepthFirstSearch {
        private Graph graph;

        public DepthFirstSearch(Graph g) {
            this.graph = g.clone();
        }

        public void search(int source, int destination) {
            Stack<Vertex> chain = new Stack<>();

            Vertex s = graph.vertex(source);
            s.distance = 0;
            s.color = Vertex.Color.Black;
            chain.push(s);

            while (!chain.isEmpty()) {
                Vertex u = chain.pop();

                System.out.println(u.id + " "); // traverse

                if (u.id == destination) {
                    Graph.printPath(u);
                    return;
                }

                for (Vertex v : graph.adjList(u.id)) {
                    if (v.color != Vertex.Color.Black) {
                        v.distance = u.distance + 1;
                        v.predecessor = u;
                        v.color = Vertex.Color.Black;
                        chain.push(v);
                    }
                }
            }
        }
    }

    public static class RecursiveDFS {
        private Graph graph;
        private int destination;

        public RecursiveDFS(Graph g) {
            this.graph = g.clone();
        }

        // Exactly the same behavior as DFS
        public void search(int source, int destination, int limit) {
            this.destination = destination;
            graph.vertex(source).distance = 0;
            recurse(source, limit);
        }

        // Optimal and complete, same runtime resources
        public void iterativeSearch(int source, int destination) {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                try {
                    search(source, destination, i);
                } catch (Exception e) {
                    // Couldn't find a solution at the specified limit
                }
            }
        }

        private boolean recurse(int id, int limit) {
            Vertex u = graph.vertex(id);
            u.color = Vertex.Color.Black;

            if (id == destination) {
                Graph.printPath(u);
                return true;
            } else if (limit == 0) {
                throw new RuntimeException("Limit reached!");
            }

            for (Vertex v : graph.adjList(id)) {
                if (v.color != Vertex.Color.Black) {
                    v.distance = u.distance + 1;
                    v.predecessor = u;

                    if (recurse(v.id, limit - 1)) return true;
                }
            }

            throw new RuntimeException("Could not find ");
        }
    }

    public static class BidirectionalSearch {

        private Graph g;

        public BidirectionalSearch(Graph g) {
            this.g = g.clone();
        }

        public void search(HashMap<Character, Integer> map, int source, int destination) {
            IterateUniformCostSearch s1 = new IterateUniformCostSearch(g.clone(), source);
            IterateUniformCostSearch s2 = new IterateUniformCostSearch(g.clone(), destination);

            boolean flip = false;

            while(true) {
                flip = !flip;

                if(flip) s1.search(destination);
                else s2.search(source);

                if(s1.overlaps(s2)) {
                    System.out.println("FOUND SOLUTION");
                    s1.printSolution(map, s2);
                    s2.printSolution(map, s1);
                    return;
                }
            }

        }
    }

    /*
     * Informed searches
     */
    public static class GreedyBestFirstSearch {

        private Graph graph;
        private HashMap<Integer, Integer> heuristicTable;

        public GreedyBestFirstSearch(Graph g, HashMap<Integer, Integer> heuristicTable) {
            this.graph = g.clone();
            this.heuristicTable = heuristicTable;
        }

        public void search(int source, int destination) {
            Vertex s = graph.vertex(source);
            s.predecessor = null;
            s.distance = 0;
            s.color = Vertex.Color.Gray;

            PriorityQueue exploring = new PriorityQueue();
            exploring.add(s, 0);

            while (!exploring.isEmpty()) {
                Vertex u = exploring.pop();

                if (u.id == destination) {
                    Graph.printPath(u);
                    return;
                }

                Vertex[] adj = graph.adjList(u.id);
                for (Vertex v : adj) {
                    if (v.color == Vertex.Color.White) {
                        v.color = Vertex.Color.Gray;
                        v.distance = u.distance + graph.cost(u.id, v.id);
                        v.predecessor = u;

                        exploring.add(v, heuristicTable.get(v.id));
                    }
                    // We are looking for a path to 'v', if the frontier already contains 'v' with a higher cost,
                    // add the updated cost to it
                    else if (exploring.cost(v.id) > u.distance + graph.cost(u.id, v.id)) {
                        v.predecessor = u;
                        v.distance = u.distance + graph.cost(u.id, v.id);
                        exploring.replace(v.id, v, u.distance + graph.cost(u.id, v.id));
                    }
                    u.color = Vertex.Color.Black;
                }
            }
        }
    }



    public static void main(String[] args) {
        // Example from page 86
//        Graph g = new Graph(5);
//        g.addTwoWayEdge(0, 1, 99);
//        g.addTwoWayEdge(0, 2, 80);
//        g.addTwoWayEdge(2, 3, 97);
//        g.addTwoWayEdge(3, 4, 101);
//        g.addTwoWayEdge(1, 4, 211);
//
//        new RecursiveDFS(g).search(0, 4, 3);

        // Example from HW2, A = 0, B = 1, so forth
//        HashMap<Character, Integer> v = new HashMap<>();
//        v.put('A', 0);
//        v.put('B', 1);
//        v.put('C', 2);
//        v.put('D', 3);
//        v.put('E', 4);
//        v.put('F', 5);
//        v.put('G', 6);
//        v.put('H', 7);
//        v.put('I', 8);
//        v.put('J', 9);
//        v.put('K', 10);
//        v.put('L', 11);
//        v.put('M', 12);
//
//        Graph g = new Graph(13);
//        g.addTwoWayEdge(v.get('A'), v.get('B'), 2);
//        g.addTwoWayEdge(v.get('A'), v.get('C'), 3);
//        g.addTwoWayEdge(v.get('A'), v.get('E'), 9);
//        g.addTwoWayEdge(v.get('B'), v.get('D'), 2);
//        g.addTwoWayEdge(v.get('B'), v.get('G'), 3);
//        g.addTwoWayEdge(v.get('C'), v.get('F'), 1);
//        g.addTwoWayEdge(v.get('E'), v.get('H'), 2);
//        g.addTwoWayEdge(v.get('E'), v.get('F'), 4);
//        g.addTwoWayEdge(v.get('H'), v.get('K'), 7);
//        g.addTwoWayEdge(v.get('F'), v.get('K'), 8);
//        g.addTwoWayEdge(v.get('G'), v.get('J'), 8);
//        g.addTwoWayEdge(v.get('E'), v.get('G'), 6);
//        g.addTwoWayEdge(v.get('D'), v.get('I'), 7);
//        g.addTwoWayEdge(v.get('I'), v.get('L'), 6);
//        g.addTwoWayEdge(v.get('J'), v.get('L'), 5);
//        g.addTwoWayEdge(v.get('L'), v.get('M'), 1);
//        g.addTwoWayEdge(v.get('J'), v.get('M'), 3);
//        g.addTwoWayEdge(v.get('K'), v.get('M'), 5);
//        g.addTwoWayEdge(v.get('H'), v.get('J'), 4);
//        g.addTwoWayEdge(v.get('G'), v.get('I'), 6);
//
//        new BidirectionalSearch(g).search(v, 0, 12);

        // Cities problem, commonly used by the book
        Graph g = new Graph(20);
        g.addTwoWayEdge(0, 1, 118);
        g.addTwoWayEdge(0, 2, 75);
        g.addTwoWayEdge(2, 3, 71);
        g.addTwoWayEdge(3, 4, 151);
        g.addTwoWayEdge(1, 5, 111);
        g.addTwoWayEdge(5, 6, 70);
        g.addTwoWayEdge(6, 7, 75);
        g.addTwoWayEdge(7, 8, 120);
        g.addTwoWayEdge(8, 9, 146);
        g.addTwoWayEdge(9, 4, 80);
        g.addTwoWayEdge(8, 11, 138);
        g.addTwoWayEdge(0, 4, 140);
        g.addTwoWayEdge(4, 10, 99);
        g.addTwoWayEdge(9, 11, 97);
        g.addTwoWayEdge(10, 13, 211);
        g.addTwoWayEdge(11, 13, 101);
        g.addTwoWayEdge(13, 12, 90);
        g.addTwoWayEdge(13, 14, 85);
        g.addTwoWayEdge(14, 15, 98);
        g.addTwoWayEdge(15, 16, 86);
        g.addTwoWayEdge(14, 17, 142);
        g.addTwoWayEdge(17, 18, 92);
        g.addTwoWayEdge(18, 19, 87);

        HashMap<Integer, Integer> heuristics = new HashMap<>();

        heuristics.put(0, 366);
        heuristics.put(13, 0);
        heuristics.put(8, 160);
        heuristics.put(7, 242);
        heuristics.put(16, 161);
        heuristics.put(10, 176);
        heuristics.put(12, 77);
        heuristics.put(15, 151);
        heuristics.put(18, 226);
        heuristics.put(5, 244);
        heuristics.put(6, 241);
        heuristics.put(19, 234);
        heuristics.put(3, 380);
        heuristics.put(11, 100);
        heuristics.put(9, 193);
        heuristics.put(4, 253);
        heuristics.put(1, 329);
        heuristics.put(14, 80);
        heuristics.put(17, 199);
        heuristics.put(2, 374);

    }

    /*
     * Utilities
     */
    // Not exactly optimized, but does the trick
    public static class PriorityQueue {
        public ArrayList<Tuple> list = new ArrayList<>();

        public void add(Vertex item, int cost) {
            Tuple t = new Tuple();
            t.cost = cost;
            t.item = item;
            list.add(t);
        }

        public boolean isEmpty() {
            return list.isEmpty();
        }

        public int cost(int id) {
            for (Tuple t : list) {
                if (t.item.id == id) {
                    return t.cost;
                }
            }

            return -1 * Integer.MAX_VALUE;
        }

        public void replace(int id, Vertex v, int cost) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).item.id == id) {
                    list.remove(i);
                    break;
                }
            }

            add(v, cost);
        }

        public Vertex pop() {
            int min = Integer.MAX_VALUE;
            int index = -1;

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).cost < min) {
                    min = list.get(i).cost;
                    index = i;
                }
            }

            return list.remove(index).item;
        }

        public static class Tuple {
            Vertex item;
            int cost;
        }

    }

}
