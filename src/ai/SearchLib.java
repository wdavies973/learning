package ai;

// Definitions
    // Frontier/fringe - leaf nodes available for expansion
    // Expanding - apply legal action to the current state
    // Branch factor - maximum amount of successors to any node
    // depth - distance from the top
    // height - distance from the bottom

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class SearchLib {

    /*
     * Graph representations
     */
    // vertex id = index
    public interface Searchable {
        Vertex vertex(int index);
        Vertex[] adjList(int index);
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

        private int numVertices;
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
                    System.out.print(matrix[row][col]+" ");
                }
                System.out.println();
            }
        }

        public static void printPath(Vertex v) {
            Stack<Vertex> path = new Stack<>();

            System.out.print("Path (cost = "+v.distance+"): ");

            while(v != null) {
                path.push(v);

                v = v.predecessor;
            }

            // Print path
            while(!path.isEmpty()) {
                System.out.print(path.pop().id+ (path.isEmpty() ? "" : " --> "));
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

            System.out.print(source+" ");

            while(!exploring.isEmpty()) {
                Vertex u = exploring.remove();

                Vertex[] adj = graph.adjList(u.id);

                for(Vertex v : adj) {
                    if(v.color == Vertex.Color.White) {
                        v.color = Vertex.Color.Gray;
                        v.distance = u.distance + 1;
                        v.predecessor = u;

                        exploring.add(v);

                        System.out.print(v.id+" ");
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
                    System.out.println(v.id+" ");

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
            }
        }
    }

    /*
     * Ex
     */
    public static class DepthFirstSearch {

    }



    public static void main(String[] args) {
        // Example from page 86
        Graph g = new Graph(5);
        g.addTwoWayEdge(0, 1, 99);
        g.addTwoWayEdge(0, 2, 80);
        g.addTwoWayEdge(2, 3, 97);
        g.addTwoWayEdge(3, 4, 101);
        g.addTwoWayEdge(1, 4, 211);

        new UniformCostSearch(g).search(0, 4);
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
            for(Tuple t : list) {
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

        public static class Tuple {
            Vertex item;
            int cost;
        }

    }

}
