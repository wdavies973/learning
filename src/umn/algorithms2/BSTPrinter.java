package umn.algorithms2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BSTPrinter {

    public interface PrintableNode {
        PrintableNode left();
        PrintableNode right();
        double value();
        String extra();
    }

    public static void printNode(PrintableNode root) {
        int maxLevel = BSTPrinter.maxLevel(root);

        printNodeInternal(Collections.singletonList(root), 1, maxLevel);
    }

    private static <T extends Comparable<?>> void printNodeInternal(List<PrintableNode> nodes, int level, int maxLevel) {
        if (nodes.isEmpty() || BSTPrinter.isAllElementsNull(nodes))
            return;

        int floor = maxLevel - level;
        int endgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
        int firstSpaces = (int) Math.pow(2, (floor)) - 1;
        int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;

        BSTPrinter.printWhitespaces(firstSpaces);

        List<PrintableNode> newNodes = new ArrayList<PrintableNode>();
        for (PrintableNode node : nodes) {
            if (node != null) {
                System.out.print(node.value()+node.extra());
                newNodes.add(node.left());
                newNodes.add(node.right());
            } else {
                newNodes.add(null);
                newNodes.add(null);
                System.out.print(" ");
            }

            BSTPrinter.printWhitespaces(betweenSpaces);
        }
        System.out.println("");

        for (int i = 1; i <= endgeLines; i++) {
            for(PrintableNode node : nodes) {
                BSTPrinter.printWhitespaces(firstSpaces - i);
                if(node == null) {
                    BSTPrinter.printWhitespaces(endgeLines + endgeLines + i + 1);
                    continue;
                }

                if(node.left() != null)
                    System.out.print("/");
                else
                    BSTPrinter.printWhitespaces(1);

                BSTPrinter.printWhitespaces(i + i - 1);

                if(node.right() != null)
                    System.out.print("\\");
                else
                    BSTPrinter.printWhitespaces(1);

                BSTPrinter.printWhitespaces(endgeLines + endgeLines - i);
            }

            System.out.println("");
        }

        printNodeInternal(newNodes, level + 1, maxLevel);
    }

    private static void printWhitespaces(int count) {
        for (int i = 0; i < count; i++)
            System.out.print(" ");
    }

    private static <T extends Comparable<?>> int maxLevel(PrintableNode node) {
        if (node == null)
            return 0;

        return Math.max(BSTPrinter.maxLevel(node.left()), BSTPrinter.maxLevel(node.right())) + 1;
    }

    private static <T> boolean isAllElementsNull(List<T> list) {
        for (Object object : list) {
            if (object != null)
                return false;
        }

        return true;
    }

}
