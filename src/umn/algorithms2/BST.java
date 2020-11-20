package umn.algorithms2;

import java.util.Arrays;
import java.util.Collections;

// BST addresses the problem of searching.
// Holds the property:
// For every node in the BST, keys in left subtree
// are all less than root key, keys in right subtree are
// all greater.
// All operations run in O(h), best case log(N), worst case N
public class BST {

    private static class Node implements BSTPrinter.PrintableNode {
        Integer key;
        Node parent;
        Node left;
        Node right;

        public Node(Integer key, Node parent) {
            this.key = key;
            this.parent = parent;
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
            return "";
        }
    }

    Node root;

    public BST() {
        root = new Node(null, new Node(null, null));
        root.left = new Node(null, root);
        root.right = new Node(null, root);
    }

    public Node insert(int key) {
        Node n = root;

        while(n.key != null) {
            if(key >= n.key) {
                n = n.right;
            } else {
                n = n.left;
            }
        }

        n.key = key;
        n.left = new Node(null, n);
        n.right = new Node(null, n);

        return n;
    }

    // Some special notation for this one:
        // z - the node containing key, this is the one we're deleting
        // y - the node that will actually be removed. It has <= 1 children. y=z possible.
        // x - the non-null child of y, if it exists
    // The cases:
        // Case 1: z has no children
            // Simply delete the node
        // Case 2: z has one child
            // Set the parent of z to z's child
        // Case 3: z has two children
            // Replace the successor. Remove successor. Goes back to the first case.
    public void delete(int key) {
        Node z = search(key);
        Node y;

        // Determine y
        if(z.left.key == null || z.right.key == null) {
            y = z;
        } else {
            y = successor(key);
        }

        // Determine x
        Node x;
        if(y.left.key != null) {
            x = y.left;
        } else {
            x = y.right;
        }

        // Splice out y
        if(x.key != null) {
            x.parent = y.parent;
        }
        if(y.parent.key == null) {
            root = x;
        }
        else if(y == y.parent.left) {
            y.parent.left = x;
        } else {
            y.parent.right = x;
        }

        // Copy data
        if(y != z) {
            z.key = y.key;
            // Satellite info gets copied here
        }
    }

    public void insert(int... keys) {
        for(int key : keys) {
            insert(key);
        }
    }

    // Iterative version of search, takes O(h) time, but doesn't
    // have function call overhead
    public Node search(int key) {
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

    // A recursive version of search. Worst case will
    // take the entire height of the tree, which is O(h)
    public Node search(Node n, int key) {
        if(n == null || key == n.key) {
            return n;
        } else if(key > n.key) {
            return search(n.right, key);
        } else {
            return search(n.left, key);
        }
    }

    // Returns node with minimum key, O(h)
    // Can have at most 1 child
    private Node min() {
        Node n = root;
        while(n.left.key != null) {
            n = n.left;
        }
        return n;
    }

    // Returns node with maximum key, O(h)
    // Can have at most 1 child
    private Node max() {
        Node n = root;
        while(n.right.key != null) {
            n = n.right;
        }
        return n;
    }

    // Returns key that is 1 greater than provided
    private Node successor(int key) {
        // First, find the node that has "key"
        Node n = search(key);

        // First, try to find minimum of right subtree
        Node r = n.right;
        if(r.key != null) {
            while(r.left.key != null) {
                r = r.left;
            }
            return r;
        }

        // If there was no subtree, follow parent pointers until a left branch is followed
        while(n.parent.key != null) {
            if(n.parent.left == n) {
                return n.parent;
            }

            n = n.parent;
        }

        return null;
    }

    // Returns key that is 1 greater than provided
    private Node predecessor(int key) {
        // First, find the node that has "key"
        Node n = search(key);

        // First, try to find minimum of right subtree
        Node r = n.left;
        if(r.key != null) {
            while(r.right.key != null) {
                r = r.right;
            }
            return r;
        }

        // If there was no subtree, follow parent pointers until a left branch is followed
        while(n.parent.key != null) {
            if(n.parent.right == n) {
                return n.parent;
            }

            n = n.parent;
        }

        return null;
    }

    public static void main(String[] args) {
        BST bst = new BST();
        int[] items = new int[]{35, 15, 50, 8, 20, 40, 55, 10, 17, 28, 70, 23, 25};
        //Arrays.sort(items);
        bst.insert(items);
        Node n = bst.root;

        BSTPrinter.printNode(bst.root);
        //bst.delete(20);
        //BSTPrinter.printNode(bst.root);
        System.out.println("Done!");;
        //System.out.println(bst.predecessor(23).key);
    }

}
