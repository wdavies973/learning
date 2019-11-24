package umn4041;

import java.util.LinkedList;
import java.util.Queue;

public class WrestlerDesignation {

    public static class Wrestler extends GraphLib.DefaultVertexImpl {
        public enum Side {
            BabyFace,
            Heel
        }

        public Side side;

        public Side getSide() {
            return side;
        }

        public void setSide(Side side) {
            this.side = side;
        }

        public Side flip() {
            return side == Side.BabyFace ? Side.Heel : Side.BabyFace;
        }

        @Override
        public void reset() {
            super.reset();

            side = null;
        }

        @Override
        public String toString() {
            return color + "," + (distance == Integer.MAX_VALUE ? "∞" : String.valueOf(distance)) + "," + side;
        }
    }

    public static class WrestlerDesignator {
        private GraphLib.Graph<Wrestler> graph;

        public WrestlerDesignator(GraphLib.Graph<Wrestler> graph) {
            this.graph = graph;
        }

        public void search(int sourceVertexID) {
            graph.resetForSearch();

            Wrestler source = graph.getVertexForId(sourceVertexID);
            source.setPredecessor(null);
            source.setDistance(0);
            source.setColor(GraphLib.DefaultVertexImpl.Color.Gray);
            source.setSide(Wrestler.Side.BabyFace);

            Queue<Wrestler> exploring = new LinkedList<>();
            exploring.add(source);

            while(!exploring.isEmpty()) {
                Wrestler u = exploring.remove();

                GraphLib.Vertex[] adj = graph.getAdjListForId(u.getId());

                for(GraphLib.Vertex vn : adj) {
                    Wrestler v = (Wrestler) vn;

                    if(u.getSide() == v.getSide()) {
                        throw new RuntimeException("No possible designation");
                    }

                    if(v.getColor() == GraphLib.DefaultVertexImpl.Color.White) {
                        v.setColor(GraphLib.DefaultVertexImpl.Color.Gray);
                        v.setDistance(u.getDistance() + 1);
                        v.setPredecessor(u);
                        v.setSide(u.flip());
                        exploring.add(v);
                    }
                }
                u.setColor(GraphLib.DefaultVertexImpl.Color.Black);
            }

        }
    }

    public static void main(String[] args) {
        GraphLib.Graph<Wrestler> g = new GraphLib.UndirectedAdjListGraphImpl(6, Wrestler.class);

        g.addVertexEdges(1, 2);
        g.addVertexEdges(2, 1, 5);
        g.addVertexEdges(3, 6, 5);
        g.addVertexEdges(4, 5);
        g.addVertexEdges(5, 4, 3, 2);
        g.addVertexEdges(6, 3);

        //ew WrestlerDesignator(g).search(6);

        new GraphLib.BreadthFirstSearch(g).search(3);

        System.out.println(g);

//        g.addVertexEdges(1, 4, 5);
//        g.addVertexEdges(2, 5);
//        g.addVertexEdges(3, 4);
//        g.addVertexEdges(4, 1,3, 6);
//        g.addVertexEdges(5, 1, 2);
//        g.addVertexEdges(6, 4);

    }

}
