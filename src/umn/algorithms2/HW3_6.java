package umn.algorithms2;

import umn.ai1.SearchLib;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

// vertex ids must be 0-(n-1)
public class HW3_6 {

    private static class Edge {
        // ids of u/v
        int u, v;
        int weight;

        public Edge(int u, int v, int weight) {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }
    }

    private static class State {
        int id;
        boolean state;
        int key;
        int p;

        public State(int id) {
            this.id = id;
            this.state = false;
            this.key = Integer.MAX_VALUE;
        }
    }

    private static void computeMstPrim(Edge[][] graph) {
        // The number of vertices, ids 0 to (n-1)
        int n = graph.length;

        State[] states = new State[n];

        for(int i = 0; i < n; i++) {
            states[i] = new State(i);
        }

        states[0].key = 0;
        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(o -> o.key));

        for(int i = 0; i < n; i++) {
            queue.add(states[i]);
        }

        int q = 0;

        while(!queue.isEmpty()) {
            q++;
            /*
             * Find state with smallest key (among states with state=false).
             */
            int u = queue.remove().id;

            State s = states[u];
            s.state = true;

            // Check neighbors of min
            for(int k = 0; k < graph[u].length; k++) {
                Edge e = graph[u][k];
                if(e == null) break;
                State v = states[e.v];

                if(!v.state && e.weight < v.key) {
                    v.key = e.weight;
                    v.p = u;
                    queue.remove(v);
                    queue.add(v);
                }

            }
        }

        System.out.println("q"+q);

        // Output answer
        System.out.println("The following edges define the MST:");
        for(int i = 1; i < n; i++) {
            System.out.println("(" + (char) ('a' + i) + "," + (char) ('a' + states[i].p) + ")");
        }
    }

    private static void addEdge(Edge[][] edges, int u, int v, int weight) {
        for(int i = 0; i < edges[u].length; i++) {
            if(edges[u][i] == null) {
                edges[u][i] = new Edge(u, v, weight);
                break;
            }
        }

        for(int i = 0; i < edges[v].length; i++) {
            if(edges[v][i] == null) {
                edges[v][i] = new Edge(v, u, weight);
                break;
            }
        }
    }

    private static int c(char c) {
        return c - 'a';
    }

    public static void main(String[] args) {
        // a = 0, b = 1, c = 2, d = 3, e = 4, f = 5

        Edge[][] graph = new Edge[6][6];
        addEdge(graph, c('a'), c('b'), 2); // a-b
        addEdge(graph, c('a'), c('e'), 3); // a-e
        addEdge(graph, c('b'), c('e'), 4); // b-e
        addEdge(graph, c('d'), c('e'), 7); // d-e
        addEdge(graph, c('b'), c('d'), 9); // b-d
        addEdge(graph, c('b'), c('c'), 6); // b-c
        addEdge(graph, c('c'), c('d'), 1); // d-c
        addEdge(graph, c('e'), c('c'), 8); // e-c
        addEdge(graph, c('c'), c('f'), 9); // c-f
        addEdge(graph, c('e'), c('f'), 10); // e-f

//        for(int i = 0; i < graph.length; i++) {
//            char c = (char)('a' + i);
//
//            System.out.print(c+": ");
//
//            for(int k = 0; k < graph[i].length; k++) {
//                if(graph[i][k] == null) break;
//
//                char m = (char)('a' + graph[i][k].v);
//                System.out.print(m+", ");
//            }
//            System.out.println();
//        }

        computeMstPrim(graph);
    }

}
