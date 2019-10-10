//packaged the classes ListNode and Add_two together
//this is problem #2 on LeetCode
package Euler_1_to_10.Adding_Two;

public class Add_two {

    private static void adding (ListNode l1, ListNode l2) {
        while (l1 != null) {
            l1.val = l1.val + l2.val;
            if (l1.val >= 10) {
                l1.val = l1.val - 10;
                if (l1.next == null) {  //this portion is in case the code causes for a carry in the final position
                    l1.next = new ListNode (0);
                    l2.next = new ListNode (0);
                }
                l1.next.val += 1;
            }
            l1 = l1.next;
            l2 = l2.next;
        }
    }

    public static void main (String[] args) {
        ListNode a = new ListNode(2);
        a.next = new ListNode(4);
        a.next.next = new ListNode(3);

        ListNode b = new ListNode(5);
        b.next = new ListNode(6);
        b.next.next = new ListNode(4);

        adding(a, b);

        while (a != null) {
            System.out.print (a.val);
            if (a.next != null) {
                System.out.print (" -> ");
            }
            a = a.next;
        }
    }
}
