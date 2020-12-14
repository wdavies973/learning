package umn.algorithms2;

/*
 * Red-black trees address the problem of keeping a BST balanced. The idea is that
 * the user may issue ("attack") calls to Search, Insert, and Delete that would normally
 * produce an unbalanced tree, which would degrade performance to linear time. A RBT
 * has several criteria that when met implicitly guarantee that the tree will be balanced.
 *
 * A red-black tree satisfies 5 properties:
 * 1) Every node is colored either red or black
 * 2) The root is black
 * 3) Every external node is black
 * 4) If a node is red, then both children are black
 * 5) For any node, all paths descending from it to external nodes have to have the same
 *    number of black nodes.
 *
 * The first goal is to prove that a red-black tree implies a height of log(N).
 *
 * Definitions:
 * h(x): The distance from x to its furthest external node, not including x (node count).
 *  - Monotonically increasing
 * bh(x): Same as h(x), except only black nodes are counted
 *  - Monotonically not guaranteed (may stay the same between levels)
 *
 * Lemmas:
 * h(x) <= 2*bh(x) for all nodes
 *
 * Denote h(x) = r + b
 * l(x) <= 2 * s(x)
 * l(x) = h(x)
 * s(x) >= bh(x)
 *
 * where l(x) is the length of longest path
 * and s(x) is the length of the shortest path
 *
 * Main result:
 * n(x) = number of internal nodes (including x, if it is internal) in the subtree
 * n(root) = n
 *
 * Goal:
 * n(x) >= 2 ^bh(x) - 1
 */
@SuppressWarnings("SuspiciousNameCombination")
public class RBT {

    public static class Node implements BSTPrinter.PrintableNode {
        Double key;
        private Node parent, left, right;
        private boolean red;

        public Node(Double key, Node parent) {
            this.key = key;
            this.parent = parent;
        }

        private boolean isNil() {
            return key == null;
        }


        private boolean isRed() {
            return red;
        }

        private boolean isBlack() {
            return !red;
        }

        @Override
        public BSTPrinter.PrintableNode left() {
            return left.key != null ? left : null;
        }

        @Override
        public BSTPrinter.PrintableNode right() {
            return right.key != null ? right : null;
        }

        @Override
        public double value() {
            return key;
        }

        @Override
        public String extra() {
            return red ? "R" : "";
        }
    }

    Node root;

    public RBT() {
        root = new Node(null, new Node(null, null));
        root.left = new Node(null, root);
        root.right = new Node(null, root);
    }

    // Rotates the tree, taking x as the focus
    public void rotateLeft(Node x) {
        Node y = x.right;
        x.right = y.left;
        if(!y.left.isNil()) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if(x.parent.isNil()) {
            root = y;
        } else if(x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    public void rotateRight(Node y) {
        Node x = y.left;
        y.left = x.right;
        if(!x.right.isNil()) {
            x.right.parent = y;
        }
        x.parent = y.parent;
        if(y.parent.isNil()) {
            root = x;
        } else if(y == y.parent.left) {
            y.parent.left = x;
        } else {
            y.parent.right = x;
        }
        x.right = y;
        y.parent = x;
    }

    public void insert(Node z) {
        // 1) First, insert the node

        // In general, x is the current node, y is the parent of x
        Node x = root;
        Node y = new Node(null, null);

        while(!x.isNil()) {
            y = x;
            if(z.key < x.key) {
                x = x.left;
            } else {
                x = x.right;
            }
        }
        // Begin inserting
        z.parent = y;
        if(y.isNil()) {
            root = z;
        } else if(z.key < y.key) {
            y.left = z;
        } else {
            y.right = z;
        }

        // 2) Color the node red
        z.red = true;

        // 3) Fix a possible violation of property 4 or 2
        insertFixup(z);
    }

    public void insert(double... keys) {
        for(double key : keys) {
            Node z = new Node(key, null);
            z.left = new Node(null, z);
            z.right = new Node(null, z);
            insert(z);
        }
    }

    // Will resolve property-4 violation, "Red-violation". Some important details:
    // 1) There is at most one red-violation
    // 2) There is a special case when the inserted node z becomes the root. If this is the case,
    //    z can be colored black and the algorithm can terminate
    // 3) Wherever the red-violation exists, the parent of z can't be the root (root is always black).
    //    In other words, the red violation exists between p(z) and z. Also, a grandparent must exist!
    private void insertFixup(Node z) {
        Node y;

        while(z.parent.red) {
            if(z.parent == z.parent.parent.left) {
                // y is the uncle
                y = z.parent.parent.right;

                // Case 1
                if(y.red) {
                    z.parent.red = false;
                    y.red = false;
                    z.parent.parent.red = true;
                    z = z.parent.parent;
                } else if(z == z.parent.right) {
                    // Case 2
                    z = z.parent;
                    rotateLeft(z);
                } else {
                    // Case 3
                    z.parent.red = false;
                    z.parent.parent.red = true;
                    rotateRight(z.parent.parent);
                }

            } else {
                y = z.parent.parent.left;
                if(y.red) {
                    z.parent.red = false;
                    y.red = false;
                    z.parent.parent.red = true;
                    z = z.parent.parent;
                } else if(z == z.parent.left) {
                    z = z.parent;
                    rotateRight(z);
                } else {
                    z.parent.red = false;
                    z.parent.parent.red = true;
                    rotateLeft(z.parent.parent);
                }
            }
        }
        root.red = false;
    }

    // Returns key that is 1 greater than provided
    private Node successor(Node z) {
        // First, try to find minimum of right subtree
        Node r = z.right;
        if(r.key != null) {
            while(r.left.key != null) {
                r = r.left;
            }
            return r;
        }

        // If there was no subtree, follow parent pointers until a left branch is followed
        while(z.parent.key != null) {
            if(z.parent.left == z) {
                return z.parent;
            }

            z = z.parent;
        }

        return null;
    }

    public Node search(double key) {
        Node n = root;

        while(n != null) {
            if(key > n.key) {
                n = n.right;
            } else if(key < n.key) {
                n = n.left;
            } else {
                return n;
            }
        }

        // Key not found
        return null;
    }

    public void delete(double key) {
        Node z = search(key);

        delete(z);
    }

    public void delete(Node z) {
        /*
         * Standard tree delete code
         */
        Node y;

        // Determine y
        if(z.left.isNil() || z.isNil()) {
            y = z;
        } else {
            y = successor(z);
        }

        // Determine x
        Node x;
        if(!y.left.isNil()) {
            x = y.left;
        } else {
            x = y.right;
        }

        // Splice out y
        if(!x.isNil()) {
            x.parent = y.parent;
        }
        if(y.parent.isNil()) {
            root = x;
        } else if(y == y.parent.left) {
            y.parent.left = x;
        } else {
            y.parent.right = x;
        }

        // Copy data
        if(y != z) {
            z.key = y.key;
            // Satellite info gets copied here
        }

        // Fixup
        // 1) x is not the root, either non-nil or nil
        // 2) If x is red, simply recolor black
        // 2) If x is black, we may have to do some fixup
        if(y.isBlack()) {
            deleteFixup(x);
        }
    }

    // Fixup tree when y is black. If x is red, fairly easy,
    // otherwise can be difficult.

    // 1) x is non-nil child of y if it exists
    // 2) if x is red, easy, remove y, color it black
    // 3) If x is black, need to eliminate the double black
    private void deleteFixup(Node x) {
        // Will use the notation w = sibling of x (node that has the same parent)
        // First claim, w cannot be nil.
        // Rotations typically happen on edge betweeen w and parent
        // Two symmetric cases: x is left child of parent, and x is right child of parent

        // Several cases
        // w is red
        // w is black, with two black children
        // w is black, with left red right black
        // w is black, left is red/black, right is red

        Node w;

        while(x != root && x.isBlack()) {
            if(x == x.parent.left) {
                w = x.parent.right;
                // Case 1
               if(w.isRed()) {
                   w.red = false;
                   x.parent.red = true;
                   rotateLeft(x.parent);
                   w = x.parent.right;
               }
               // Case 2
               if(w.left.isBlack() && w.right.isBlack()) {
                   w.red = true;
                   x = x.parent;
               }
               // Case 3
               else if(w.right.isBlack()) {
                   w.left.red = false;
                   w.red = true;
                   rotateRight(w);
               }
               // Case 4
               else {
                   w.red = x.parent.red;
                   x.parent.red = false;
                   w.right.red = false;
                   rotateLeft(x.parent);
                   x = root;
               }
            } else {
                w = x.parent.left;
                // Case 1
                if(w.isRed()) {
                    w.red = false;
                    x.parent.red = true;
                    rotateRight(x.parent);
                    w = x.parent.left;
                }
                // Case 2
                if(w.right.isBlack() && w.left.isBlack()) {
                    w.red = true;
                    x = x.parent;
                }
                // Case 3
                else if(w.left.isBlack()) {
                    w.right.red = false;
                    w.red = true;
                    rotateLeft(w);
                }
                // Case 4
                else {
                    w.red = x.parent.red;
                    x.parent.red = false;
                    w.left.red = false;
                    rotateRight(x.parent);
                    x = root;
                }
            }
        }
    }

    public static void main(String[] args) {
//        RBT rbt = new RBT();
//        double[] items = new double[]{7, 2, 11, 1, 5, 8, 14, 4, 15};
//        //Arrays.sort(items);
//        rbt.insert(items);
//
//        BSTPrinter.printNode(rbt.root);
//
//        rbt.insert(3);
//
//        BSTPrinter.printNode(rbt.root);
//
//        rbt.insert(2);
//
//        BSTPrinter.printNode(rbt.root);
//
//        rbt.delete(rbt.root);
//
//        BSTPrinter.printNode(rbt.root);

        double hw1 = 44.5 / 45 * 100 * .1;
        double hw2 = 32.5 / 47 * 100 * .12;
        double hw3 = 46.0 / 46 * 100 * .12;
        double hw4 = 20.0 / 51 * 100 * .12;
        double hw5 = 45.5 / 46 * 100 * .12;
        double hw6 = 52.0 / 53 * 100 * .12;

        double test1 = 23.0 / 25 * 100 * .15;
        double test2 = 100 * .15;

        double needed = 85 - (hw1 + hw2 + hw3 + hw4 + hw5 + hw6 + test1);
        System.out.println(needed / .15);

        double grade = hw1 + hw2 + hw3 + hw4 + hw5 + hw6 + test1 + test2;
        System.out.println(grade);


    }

}
