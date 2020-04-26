package umn.ai1;

// Ideas for approaches
    // 1. Recursive building up?
    // 2. Shuffling?
    // 3. Define diagonals + rows + columns
        // Some are unused, so just iterate through which ones are reused
    // 3. seems the most interesting

public class NQueens {

    private int size;

    boolean[][] board;

    public NQueens(int size) {
        this.size = size;

        board = new boolean[size][size];
    }

    // A bucket defines a region that only 1 queen can be present in,
    // this is implied from the structure of the game

    // A bucket will always contain the row and column its in

    // if not in a corner, it is by definition in 2 diagonals

    // so how do we generate the buckets?

    // buckets must be separated - I.E., no two buckets can overlap on any square
    // or optionally, use buckets as conflict groups, only 1 can be selected from the group

    public void place(int ith) {
        // Search for open
        
    }

}
