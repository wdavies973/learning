package umn.ai1;

import java.util.ArrayList;
import java.util.LinkedList;

public class AlphaBeta {

    public static class Node {
        String id;

        Node left;
        Node middle;
        Node right;

        boolean max; // true = max, false = min

        int value;

        boolean pruned = false;

        int depth;

        Node(String id, boolean max, int depth) {
            this.id = id;
            this.max = max;
            this.depth = depth;
        }

        Node(String id, int value, int depth) {
            this.id = id;
            this.value = value;
            this.depth = depth;
        }

        ArrayList<Node> nonNull() {
            ArrayList<Node> nodes = new ArrayList<>();

            if(left != null) nodes.add(left);
            if(middle != null) nodes.add(middle);
            if(right != null) nodes.add(right);

            return nodes;
        }

        boolean isLeaf() {
            return left == null && middle == null && right == null;
        }

        @Override
        public String toString() {
            return id +"("+value+")"+(pruned ? "P" : "");
        }
    }

    public static int solve(Node node, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if(node.isLeaf()) {
            return node.value;
        }

        if(maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            boolean prunedOccurred = false;

            for(Node n : node.nonNull()) {
                if(prunedOccurred) {
                    n.pruned = true;
                    continue;
                }

                int eval = solve(n, depth - 1, alpha, beta, n.max);
                n.value = eval;

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if(beta <= alpha) {
                    prunedOccurred = true;
                }
            }

            node.value = maxEval;

            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            boolean prunedOccurred = false;

            for(Node n : node.nonNull()) {
                if(prunedOccurred) {
                    n.pruned = true;
                    continue;
                }

                int eval = solve(n, depth - 1, alpha, beta, n.max);
                n.value = eval;

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if(beta <= alpha) {
                    prunedOccurred = true;
                }

            }

            node.value = minEval;

            return minEval;
        }
    }

    public static void printTree(Node root) {
        LinkedList<Node> queue = new LinkedList<>();

        queue.add(root);

        int depth = 0;

        while(!queue.isEmpty()) {
            Node n = queue.pop();

            if(n.depth != depth) {
                System.out.println();
                depth = n.depth;
            }

            System.out.print(n+" ");

            queue.addAll(n.nonNull());
        }
    }

    public static void main(String[] args) {
        Node a = new Node("a", true, 0);
        Node b = new Node("b", false, 1);
        Node c = new Node("c", false, 1);
        Node d = new Node("d", true, 1);
        Node e = new Node("e", true, 2);
        Node f = new Node("f", true, 2);
        Node g = new Node("g", false, 2);
        Node h = new Node("h", true, 2);
        Node i = new Node("i", false, 2);
        Node j = new Node("j", 4, 2);
        Node k = new Node("k", true, 2);
        Node l = new Node("l", false, 3);
        Node m = new Node("m", 6, 3);
        Node n = new Node("n", 100, 3);
        Node o = new Node("o", true, 3);
        Node p = new Node("p", false, 3);
        Node q = new Node("q", 43, 3);
        Node r = new Node("r", 7, 3);
        Node s = new Node("s", 3, 3);
        Node t = new Node("t", true, 3);
        Node u = new Node("u", false, 3);
        Node v = new Node("v", true, 3);
        Node w = new Node("w", 0, 3);
        Node x = new Node("x", 9, 4);
        Node y = new Node("y", 2, 4);
        Node z = new Node("z", 12, 4);
        Node one = new Node("1", 33, 4);
        Node two = new Node("2", 5, 4);
        Node three = new Node("3", 72, 4);
        Node four = new Node("4", 23, 4);
        Node five = new Node("5", 18, 4);
        Node six = new Node("6", 11, 4);
        Node seven = new Node("7", 29, 4);
        Node eight = new Node("8", 8, 4);
        Node nine = new Node("9", 6, 4);

        a.left = b;
        a.middle = c;
        a.right = d;
        b.left = e;
        b.right = f;
        c.left = g;
        c.right = h;
        d.left = i;
        d.middle = j;
        d.right = k;
        e.left = l;
        e.right = m;
        f.left = n;
        f.right = o;
        g.left = p;
        g.right = q;
        h.left = r;
        h.right = s;
        i.left = t;
        i.right = u;
        k.left = v;
        k.right = w;
        l.left = x;
        l.right = y;
        o.left = z;
        o.right = one;
        p.left = two;
        p.right = three;
        t.left = four;
        t.right = five;
        u.left = six;
        u.right = seven;
        v.left = eight;
        v.right = nine;

        solve(a, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        printTree(a);

    }

}
