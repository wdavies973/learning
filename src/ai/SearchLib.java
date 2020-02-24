package ai;

// Definitions
    // Frontier/fringe - leaf nodes available for expansion
    // Expanding - apply legal action to the current state
    // Branch factor - maximum amount of successors to any node
    // depth - distance from the top
    // height - distance from the bottom

import java.util.ArrayList;

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
                if(matrix[index][0] < Integer.MAX_VALUE && i != index) { // a path exists, ignore path to self
                    vertices.add(this.vertices[i]);
                }
            }

            return vertices.toArray(new Vertex[0]);
        }

        public Graph clone() {
            Graph g = new Graph(numVertices);

            g.matrix = matrix;

            return g;
        }
    }


    /*
     * Uninformed strategies
     */
    private static class BreadthFirstSearch {
        private Graph graph;

        public BreadthFirstSearch(Graph g) {
            this.graph = g.clone();
        }

        public void search(int source, int destination) {

        }

    }

}
