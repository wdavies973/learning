package umn.ai1;

// Definitions
// Frontier/fringe - leaf nodes available for expansion
// Expanding - apply legal action to the current state
// Branch factor - maximum amount of successors to any node
// depth - distance from the top
// height - distance from the bottom

import java.util.*;

public class SearchLib {

    /*
     * Graph representations
     */
    // vertex id = index
    public static int c(char c) {
        return c - 'a';
    }

    public static char i(int a) {
        return (char) ('a' + a);
    }

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

        int depth;

        public Vertex(int id) {
            this.id = id;
            color = Color.White;
            distance = Integer.MAX_VALUE; // TODO infinity & nan
        }

        public Vertex copy(int depth) {
            Vertex v = new Vertex(id);
            v.color = color;
            v.distance = distance;
            v.predecessor = predecessor;
            v.depth = depth;

            return v;
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
            for(int i = 0; i < numVertices; i++) {
                this.vertices[i] = new Vertex(i);
            }

            this.matrix = new int[numVertices][numVertices];

            for(int i = 0; i < numVertices; i++) {
                for(int j = 0; j < numVertices; j++) {
                    if(i != j) {
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

            for(int i = 0; i < numVertices; i++) {
                if(matrix[index][i] < Integer.MAX_VALUE && i != index) { // a path exists, ignore path to self
                    vertices.add(this.vertices[i]);
                }
            }

            return vertices.toArray(new Vertex[0]);
        }

        public Vertex[] adjListWithDepth(int index, int depth) {
            ArrayList<Vertex> vertices = new ArrayList<>();

            for(int i = 0; i < numVertices; i++) {
                if(matrix[index][i] < Integer.MAX_VALUE && i != index) { // a path exists, ignore path to self
                    vertices.add(this.vertices[i].copy(depth));
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
            for(int row = 0; row < numVertices; row++) {
                for(int col = 0; col < numVertices; col++) {
                    System.out.print(matrix[row][col] + " ");
                }
                System.out.println();
            }
        }

        public static void printPath(Vertex v) {
            Stack<Vertex> path = new Stack<>();

            System.out.print("Path (cost = " + v.distance + "): ");

            while(v != null) {
                path.push(v);

                v = v.predecessor;
            }

            // Print path
            while(!path.isEmpty()) {
                System.out.print(path.pop().id + (path.isEmpty() ? "" : " --> "));
            }
        }

        public static void printPath(HashMap<Character, Integer> map, Vertex v) {
            Stack<Vertex> path = new Stack<>();

            System.out.print("Path (cost = " + v.distance + "): ");

            while(v != null) {
                path.push(v);

                v = v.predecessor;
            }

            // Print path
            while(!path.isEmpty()) {
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

            while(!exploring.isEmpty()) {
                Vertex u = exploring.remove();

                Vertex[] adj = graph.adjList(u.id);

                for(Vertex v : adj) {
                    if(v.color == Vertex.Color.White) {
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

            while(!exploring.isEmpty()) {
                Vertex u = exploring.remove();

                Vertex[] adj = graph.adjList(u.id);
                System.out.print("Frontier: ");
                for(Vertex v : adj) {
                    System.out.println(v.id + " ");

                    if(v.color == Vertex.Color.White) {
                        v.color = Vertex.Color.Gray;
                        v.distance = u.distance + 1;
                        v.predecessor = u;

                        if(v.id == destination) {
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

            while(!exploring.isEmpty()) {
                Vertex u = exploring.pop();

                if(u.id == destination) {
                    Graph.printPath(u);
                    return;
                }

                Vertex[] adj = graph.adjList(u.id);
                for(Vertex v : adj) {
                    if(v.color == Vertex.Color.White) {
                        v.color = Vertex.Color.Gray;
                        v.distance = u.distance + graph.cost(u.id, v.id);
                        v.predecessor = u;

                        exploring.add(v, v.distance);
                    }
                    // We are looking for a path to 'v', if the frontier already contains 'v' with a higher cost,
                    // add the updated cost to it
                    else if(exploring.cost(v.id) > u.distance + graph.cost(u.id, v.id)) {
                        v.predecessor = u;
                        v.distance = u.distance + graph.cost(u.id, v.id);
                        exploring.replace(v.id, v, u.distance + graph.cost(u.id, v.id));
                    }
                    u.color = Vertex.Color.Black;
                }

                System.out.println("Fringe: " + exploring.toString());
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

            if(u.id == destination) {
                Graph.printPath(u);
                return;
            }

            Vertex[] adj = graph.adjList(u.id);
            for(Vertex v : adj) {
                if(v.color == Vertex.Color.White) {
                    v.color = Vertex.Color.Gray;
                    v.distance = u.distance + graph.cost(u.id, v.id);
                    v.predecessor = u;

                    exploring.add(v, v.distance);
                }
                // We are looking for a path to 'v', if the frontier already contains 'v' with a higher cost,
                // add the updated cost to it
                else if(exploring.cost(v.id) > u.distance + graph.cost(u.id, v.id)) {
                    v.predecessor = u;
                    v.distance = u.distance + graph.cost(u.id, v.id);
                    exploring.replace(v.id, v, u.distance + graph.cost(u.id, v.id));
                }
                u.color = Vertex.Color.Black;
            }

        }

        public void printSolution(HashMap<Character, Integer> map, IterateUniformCostSearch ucs) {
            for(int i = 0; i < graph.numVertices; i++) {
                if(graph.vertices[i].color == Vertex.Color.Black
                        && ucs.graph.vertices[i].color == Vertex.Color.Black) {
                    Graph.printPath(map, graph.vertex(i));
                    return;
                }
            }
        }

        public boolean overlaps(IterateUniformCostSearch ucs) {
            // Search to see if explored sets overlap
            for(int i = 0; i < graph.numVertices; i++) {
                if(graph.vertices[i].color == Vertex.Color.Black
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

            while(!chain.isEmpty()) {
                Vertex u = chain.pop();

                System.out.println(u.id + " "); // traverse

                if(u.id == destination) {
                    Graph.printPath(u);
                    return;
                }

                for(Vertex v : graph.adjList(u.id)) {
                    if(v.color != Vertex.Color.Black) {
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
            for(int i = 0; i < Integer.MAX_VALUE; i++) {
                try {
                    search(source, destination, i);
                } catch(Exception e) {
                    // Couldn't find a solution at the specified limit
                }
            }
        }

        private boolean recurse(int id, int limit) {
            Vertex u = graph.vertex(id);
            u.color = Vertex.Color.Black;

            if(id == destination) {
                Graph.printPath(u);
                return true;
            } else if(limit == 0) {
                throw new RuntimeException("Limit reached!");
            }

            for(Vertex v : graph.adjList(id)) {
                if(v.color != Vertex.Color.Black) {
                    v.distance = u.distance + 1;
                    v.predecessor = u;

                    if(recurse(v.id, limit - 1)) return true;
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

    // Not always optimal
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

            while(!exploring.isEmpty()) {
                Vertex u = exploring.pop();

                if(u.id == destination) {
                    Graph.printPath(u);
                    return;
                }

                Vertex[] adj = graph.adjList(u.id);
                for(Vertex v : adj) {
                    if(v.color == Vertex.Color.White) {
                        v.color = Vertex.Color.Gray;
                        v.distance = u.distance + graph.cost(u.id, v.id);
                        v.predecessor = u;

                        exploring.add(v, heuristicTable.get(v.id));
                    }
                    // We are looking for a path to 'v', if the frontier already contains 'v' with a higher cost,
                    // add the updated cost to it
                    else if(exploring.cost(v.id) > heuristicTable.get(v.id)) {
                        v.predecessor = u;
                        v.distance = u.distance + graph.cost(u.id, v.id);
                        exploring.replace(v.id, v, heuristicTable.get(v.id));
                    }
                    u.color = Vertex.Color.Black;
                }
            }
        }
    }

    public static class AStarSearch {

        private Graph graph;
        private HashMap<Integer, Integer> heuristicTable;

        public AStarSearch(Graph g, HashMap<Integer, Integer> heuristicTable) {
            this.graph = g.clone();
            this.heuristicTable = heuristicTable;
        }

        public void search(int source, int destination, HashMap<Integer, String> friendlyNames) {
            Vertex s = graph.vertex(source);
            s.predecessor = null;
            s.distance = 0;
            s.color = Vertex.Color.Gray;

            PriorityQueue exploring = new PriorityQueue();
            exploring.add(s, 0);

            while(!exploring.isEmpty()) {
                Vertex u = exploring.pop();

                if(friendlyNames != null) {
                    System.out.println("Exploring " + friendlyNames.get(u.id));
                }

                if(u.id == destination) {
                    Graph.printPath(u);
                    return;
                }

                Vertex[] adj = graph.adjList(u.id);
                for(Vertex v : adj) {
                    if(heuristicTable.get(u.id) > graph.cost(u.id, v.id) + heuristicTable.get(v.id)) {
                        throw new RuntimeException("Not consistent from " + u.id + " , " + v.id + "!");
                    }

                    if(v.color == Vertex.Color.White) {
                        v.color = Vertex.Color.Gray;
                        v.distance = u.distance + graph.cost(u.id, v.id);
                        v.predecessor = u;

                        exploring.add(v, heuristicTable.get(v.id) + v.distance);
                    }
                    // We are looking for a path to 'v', if the frontier already contains 'v' with a higher cost,
                    // add the updated cost to it
                    else if(exploring.cost(v.id) > u.distance + graph.cost(u.id, v.id) + heuristicTable.get(v.id)) {
                        v.predecessor = u;
                        v.distance = u.distance + graph.cost(u.id, v.id);
                        exploring.replace(v.id, v, u.distance + graph.cost(u.id, v.id) + heuristicTable.get(v.id));
                    }
                    u.color = Vertex.Color.Black;
                }

                // Print the fringe
                System.out.print("Fringe: ");
                if(friendlyNames != null) {
                    System.out.println(exploring.toStringWithReplacement(friendlyNames));
                } else {
                    System.out.println(exploring.toString());
                }
            }
        }
    }

    public static class BasicHillClimb {
        private Graph g;
        private HashMap<Integer, Integer> h;

        public BasicHillClimb(Graph g, HashMap<Integer, Integer> h) {
            this.g = g.clone();
            this.h = h;
        }

        public void search(int source, int destination) {

            int state = h.get(source);

            Vertex v = g.vertex(source);

            while(true) {
                int min = Integer.MAX_VALUE;
                int id = Integer.MAX_VALUE;

                for(Vertex i : g.adjList(v.id)) {
                    if(h.get(i.id) < min) {
                        min = h.get(i.id);
                        id = i.id;
                    }
                }

                if(min > state) {
                    return;
                } else {
                    System.out.print(i(id) + " ");

                    v = g.vertex(id);

                    state = min;
                }
            }


        }
    }

    /*
     * Randomly chooses an option (from all increasing options),
     * this can scale with how MUCH of an increase an option is
     */
    public static class StochasticHillClimb {
        private Graph g;
        private HashMap<Integer, Integer> h;
        public StochasticHillClimb(Graph g, HashMap<Integer, Integer> h) {
            this.g = g.clone();
            this.h = h;
        }

        public void search(int source, int destination, double... rands) {
            int state = h.get(source);
            Vertex v = g.vertex(source);

            int index = 0;

            while(true) {
                if(index >= rands.length) return;

                /*
                 * Calculate denominator
                 */
                double b = 0;

                Vertex[] adj = g.adjList(v.id);

                for(Vertex i : adj) {
                    b += 1.0 / h.get(i.id);
                }

                /*
                 * Find probability bracket
                 */
                double lastP = 0;

                int a = -1;

                for(Vertex vertex : adj) {
                    double p;

                    if(b == Double.POSITIVE_INFINITY) {
                        p = 0;
                    } else {
                        p = (1.0 / h.get(vertex.id)) / b;
                    }

                    System.out.print(i(vertex.id)+"("+lastP+","+(p + lastP)+") ");

                    if(h.get(vertex.id) == 0 || rands[index] > lastP && rands[index] < p + lastP) {
                        if(a != -1) {
                            throw new RuntimeException("Error");
                        }
                        a = vertex.id;
                    }

                    lastP += p;
                }

                System.out.print("\nv "+i(a)+" ");

                v = g.vertex(a);
                index++;

                if(v.id == destination) return;

                System.out.println();
            }
        }


    }

    public static void main(String[] args) {

        Graph g = new Graph(10);
        g.addTwoWayEdge(c('a'), c('b'), 0);
        g.addTwoWayEdge(c('b'), c('e'), 0);
        g.addTwoWayEdge(c('e'), c('h'), 0);
        g.addTwoWayEdge(c('h'), c('i'), 0);
        g.addTwoWayEdge(c('a'), c('d'), 0);
        g.addTwoWayEdge(c('d'), c('g'), 0);
        g.addTwoWayEdge(c('g'), c('j'), 0);
        g.addTwoWayEdge(c('a'), c('c'), 0);
        g.addTwoWayEdge(c('c'), c('e'), 0);
        g.addTwoWayEdge(c('b'), c('f'), 0);
        g.addTwoWayEdge(c('f'), c('i'), 0);
        g.addTwoWayEdge(c('f'), c('g'), 0);
        g.addTwoWayEdge(c('c'), c('g'), 0);
        g.addTwoWayEdge(c('e'), c('i'), 0);

        ArrayList<Vertex> explored = new ArrayList<>();

        LinkedList<Vertex> queue = new LinkedList<>();

        queue.add(g.vertex(0));

        int depth = 0;

        int count = 0;

        for(int i = 0; count < 11; i++) {
            Vertex v = queue.pop();

            if(depth != v.depth) {
                System.out.println();
                depth = v.depth;

                count++;
            }

            queue.addAll(Arrays.asList(g.adjListWithDepth(v.id, depth + 1)));

            System.out.print(i(v.id)+" ");

            explored.add(v);
        }

//        Graph g = new Graph(10);
//        g.addTwoWayEdge(c('a'), c('b'), 0);
//        g.addTwoWayEdge(c('b'), c('e'), 0);
//        g.addTwoWayEdge(c('e'), c('h'), 0);
//        g.addTwoWayEdge(c('h'), c('i'), 0);
//        g.addTwoWayEdge(c('i'), c('f'), 0);
//        g.addTwoWayEdge(c('c'), c('e'), 0);
//        g.addTwoWayEdge(c('b'), c('f'), 0);
//        g.addTwoWayEdge(c('e'), c('i'), 0);
//        g.addTwoWayEdge(c('a'), c('c'), 0);
//        g.addTwoWayEdge(c('a'), c('d'), 0);
//        g.addTwoWayEdge(c('d'), c('g'), 0);
//        g.addTwoWayEdge(c('c'), c('g'), 0);
//        g.addTwoWayEdge(c('f'), c('g'), 0);
//        g.addTwoWayEdge(c('g'), c('j'), 0);
//
//        HashMap<Integer, Integer> h = new HashMap<>();
//        h.put(c('a'), 32);
//        h.put(c('b'), 30);
//        h.put(c('c'), 12);
//        h.put(c('d'), 20);
//        h.put(c('e'), 4);
//        h.put(c('f'), 6);
//        h.put(c('g'), 8);
//        h.put(c('h'), 1);
//        h.put(c('i'), 2);
//        h.put(c('j'), 0);
//
//        // new BasicHillClimb(g, h).search(c('a'), c('j'));
//        double[] run1 = new double[]{0.566, 0.753, 0.532, 0.753, 0.598, 0.004, 0.103, 0.544, 0.693, 0.974};
//        double[] run2 = new double[]{0.014, 0.186, 0.975, 0.512, 0.407, 0.974, 0.098, 0.350, 0.686, 0.493};
//        double[] run3 = new double[]{0.039, 0.860, 0.330, 0.733, 0.022, 0.110, 0.678, 0.046, 0.101, 0.769};
//
//        new StochasticHillClimb(g, h).search(c('a'), c('j'), run3);
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

        // Cities problem, commonly used by the book pg 70
//        Graph g = new Graph(20);
//        g.addTwoWayEdge(0, 1, 118);
//        g.addTwoWayEdge(0, 2, 75);
//        g.addTwoWayEdge(2, 3, 71);
//        g.addTwoWayEdge(3, 4, 151);
//        g.addTwoWayEdge(1, 5, 111);
//        g.addTwoWayEdge(5, 6, 70);
//        g.addTwoWayEdge(6, 7, 75);
//        g.addTwoWayEdge(7, 8, 120);
//        g.addTwoWayEdge(8, 9, 146);
//        g.addTwoWayEdge(9, 4, 80);
//        g.addTwoWayEdge(8, 11, 138);
//        g.addTwoWayEdge(0, 4, 140);
//        g.addTwoWayEdge(4, 10, 99);
//        g.addTwoWayEdge(9, 11, 97);
//        g.addTwoWayEdge(10, 13, 211);
//        g.addTwoWayEdge(11, 13, 101);
//        g.addTwoWayEdge(13, 12, 90);
//        g.addTwoWayEdge(13, 14, 85);
//        g.addTwoWayEdge(14, 15, 98);
//        g.addTwoWayEdge(15, 16, 86);
//        g.addTwoWayEdge(14, 17, 142);
//        g.addTwoWayEdge(17, 18, 92);
//        g.addTwoWayEdge(18, 19, 87);
//
//        HashMap<Integer, Integer> heuristics = new HashMap<>();
//
//        heuristics.put(0, 366);
//        heuristics.put(13, 0);
//        heuristics.put(8, 160);
//        heuristics.put(7, 242);
//        heuristics.put(16, 161);
//        heuristics.put(10, 176);
//        heuristics.put(12, 77);
//        heuristics.put(15, 151);
//        heuristics.put(18, 226);
//        heuristics.put(5, 244);
//        heuristics.put(6, 241);
//        heuristics.put(19, 234);
//        heuristics.put(3, 380);
//        heuristics.put(11, 100);
//        heuristics.put(9, 193);
//        heuristics.put(4, 253);
//        heuristics.put(1, 329);
//        heuristics.put(14, 80);
//        heuristics.put(17, 199);
//        heuristics.put(2, 374);
//
//        new AStarSearch(g, heuristics).search(0, 13);

//        Graph g = new Graph(10);
//        g.addTwoWayEdge(0, 1, 80);
//        g.addTwoWayEdge(1, 2, 85);
//        g.addTwoWayEdge(2, 3, 173);
//        g.addTwoWayEdge(3, 4, 502);
//        g.addTwoWayEdge(4, 8, 84);
//        g.addTwoWayEdge(8, 0, 250);
//        g.addTwoWayEdge(2, 7, 217);
//        g.addTwoWayEdge(7, 9, 186);
//        g.addTwoWayEdge(7, 5, 103);
//        g.addTwoWayEdge(5, 6, 183);
//        g.addTwoWayEdge(5, 4, 167);
//
//        HashMap<Integer, Integer> h = new HashMap<>();
//        h.put(0, 430);
//        h.put(1, 370);
//        h.put(2, 300);
//        h.put(3, 200);
//        h.put(4, 320);
//        h.put(5, 160);
//        h.put(6, 0);
//        h.put(7, 90);
//        h.put(8, 350);
//        h.put(9, 260);
//
//        HashMap<Integer, String> f = new HashMap<>();
//        f.put(0, "Karlsruhe");
//        f.put(1, "Mannheim");
//        f.put(2, "Frankfurt");
//        f.put(3, "Kassel");
//        f.put(4, "Munchen");
//        f.put(5, "Nurnberg");
//        f.put(6, "Stuttgart");
//        f.put(7, "Wurzburg");
//        f.put(8, "Augsburg");
//        f.put(9, "Erfurt");
//
//        new AStarSearch(g, h).search(0, 6, f);
    }

    /*
     * Utilities
     */
    // Not exactly optimized, but does the trick
    public static class PriorityQueue {
        public ArrayList<PTuple> list = new ArrayList<>();

        public void add(Vertex item, int cost) {
            PTuple t = new PTuple();
            t.cost = cost;
            t.item = item;
            list.add(t);
        }

        public boolean isEmpty() {
            return list.isEmpty();
        }

        public int cost(int id) {
            for(PTuple t : list) {
                if(t.item.id == id) {
                    return t.cost;
                }
            }

            return -1 * Integer.MAX_VALUE;
        }

        public void replace(int id, Vertex v, int cost) {
            for(int i = 0; i < list.size(); i++) {
                if(list.get(i).item.id == id) {
                    list.remove(i);
                    break;
                }
            }

            add(v, cost);
        }

        public Vertex pop() {
            int min = Integer.MAX_VALUE;
            int index = -1;

            for(int i = 0; i < list.size(); i++) {
                if(list.get(i).cost < min) {
                    min = list.get(i).cost;
                    index = i;
                }
            }

            return list.remove(index).item;
        }

        public static class PTuple {
            Vertex item;
            int cost;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for(PTuple t : list) {
                builder.append(t.item.id).append("(").append(t.cost).append(") ");
            }

            return builder.toString();
        }

        public String toStringWithReplacement(HashMap<Integer, String> index) {
            StringBuilder builder = new StringBuilder();
            for(PTuple t : list) {
                builder.append(index.get(t.item.id)).append("(").append(t.cost).append(") ");
            }

            return builder.toString();
        }

    }

}
