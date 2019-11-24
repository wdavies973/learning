package umn4041;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/*
 * Basic graph library structures and algorithms
 */
public class GraphLib {

    // A common graph interface so that graphs can have multiple underlying implementations
    // such as an adjacency list or adjacency matrix
    public interface Graph<T extends Vertex> {
        Graph addVertexEdges(int id, int... connectedToVertices);

        T getVertexForId(int id);

        T[] getAdjListForId(int id);

        void resetForSearch();
    }

    // This vertex class can be extended to store more attributes if needed
    public interface Vertex {
        void setId(int id);
        void setColor(DefaultVertexImpl.Color color);
        void setDistance(int distance);
        void setPredecessor(Vertex v);

        int getId();
        DefaultVertexImpl.Color getColor();
        int getDistance();
        Vertex getPredecessor();

        void reset();
    }

    public static class DefaultVertexImpl implements Vertex {
        public enum Color {
            White, // undiscovered
            Gray, // discovered but not explored
            Black // explored
        }

        int ID;
        Color color;

        int distance; // the distance from the source vertex to this one
        Vertex predecessor; // the vertex that was discovered, and whose exploration which lead to this vertex

        @Override
        public void setId(int id) {
            this.ID = id;
        }

        @Override
        public int getId() {
            return ID;
        }

        @Override
        public void setColor(Color color) {
            this.color = color;
        }

        @Override
        public void setDistance(int distance) {
            this.distance = distance;
        }

        @Override
        public void setPredecessor(Vertex v) {
            this.predecessor = v;
        }

        @Override
        public Color getColor() {
            return color;
        }

        @Override
        public int getDistance() {
            return distance;
        }

        @Override
        public Vertex getPredecessor() {
            return predecessor;
        }

        @Override
        public void reset() {
            color = Color.White;
            distance = Integer.MAX_VALUE;
            predecessor = null;
        }

        @Override
        public String toString() {
            return color + "," + (distance == Integer.MAX_VALUE ? "∞" : String.valueOf(distance));
        }
    }

    public static class UndirectedAdjListGraphImpl implements Graph {
        private Vertex[][] data;
        private Class vertexImpl;

        public UndirectedAdjListGraphImpl(int numVertices) {
            this(numVertices, DefaultVertexImpl.class);
        }

        public UndirectedAdjListGraphImpl(int numVertices, Class vertexImpl) {
            data = new Vertex[numVertices][numVertices + 1];
            this.vertexImpl = vertexImpl;


            try {
                for(int i = 0; i < numVertices; i++) {
                    Vertex v = (Vertex) vertexImpl.newInstance();
                    v.setId(i + 1);
                    data[i][0] = v;
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public UndirectedAdjListGraphImpl addVertexEdges(int id, int... connectedToVertices) {
            try {
                Vertex created = (Vertex) vertexImpl.newInstance();
                created.setId(id);

                // Add matrices this vertex points to
                for(int i = 0; i < connectedToVertices.length; i++) {
                    Vertex connectedVertex = getVertexForId(connectedToVertices[i]);
                    data[id - 1][i + 1] = connectedVertex;
                }

            } catch(Exception e) {
                e.printStackTrace();
            }

            return this;
        }

        @Override
        public Vertex getVertexForId(int id) {
            return data[id - 1][0];
        }

        @Override
        public Vertex[] getAdjListForId(int id) {
            Vertex[] source = data[id-1];

            ArrayList<Vertex> copy = new ArrayList<>();

            for(int i = 1; i < source.length && source[i] != null; i++) {
                copy.add(source[i]);
            }

            return copy.toArray(new Vertex[0]);
        }

        @Override
        public void resetForSearch() {
            for(Vertex[] datum : data) {
                datum[0].reset();
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();

            for(int i = 0; i < data.length; i++) {
                builder.append((i + 1)).append("(").append(data[i][0]).append(")").append(": ");

                for(int j = 1; j < data[i].length && data[i][j] != null; j++) {
                    builder.append(" ").append(data[i][j].getId());
                }

                builder.append("\n");
            }

            return builder.toString();
        }
    }

    public static class BreadthFirstSearch {
        private Graph graph;

        public BreadthFirstSearch(Graph graph) {
            this.graph = graph;
        }

        public void search(int sourceVertexID) {
            graph.resetForSearch();

            Vertex source = graph.getVertexForId(sourceVertexID);
            source.setPredecessor(null);
            source.setDistance(0);
            source.setColor(DefaultVertexImpl.Color.Gray);

            Queue<Vertex> exploring = new LinkedList<>();
            exploring.add(source);

            while(!exploring.isEmpty()) {
                Vertex u = exploring.remove();

                System.out.print(u.getId() + " ");

                Vertex[] adj = graph.getAdjListForId(u.getId());
                for(Vertex v : adj) {
                    if(v.getColor() == DefaultVertexImpl.Color.White) {
                        v.setColor(DefaultVertexImpl.Color.Gray);
                        v.setDistance(u.getDistance() + 1);
                        v.setPredecessor(u);
                        exploring.add(v);
                    }
                }
                u.setColor(DefaultVertexImpl.Color.Black);
            }

            System.out.println();
        }
    }

}
