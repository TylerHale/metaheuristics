package studio2;

import java.util.*;
import java.util.stream.Collectors;

public class NineQueen {
    static Random rng = new Random();
    public List<Integer> posS;
    public int[] board;
    private int n;

    public NineQueen(int n_) {
        n = n_;
        board = new int[n * n];

        posS = (new ArrayList<>(
                Collections.nCopies(n, n)))
                .stream()
                .map((v -> rng.nextInt(v * v))).collect(Collectors.toList());

        Arrays.fill(board, 0);
        posS.stream().forEach(x -> board[x] = 1);
    }

    public int size() {
        assert (n > 0);
        return n;
    }

    public NineQueen copy() {
        NineQueen other = new NineQueen(n);
        other.posS = posS.stream().collect(Collectors.toList());
        other.board = board.clone();
        return other;
    }

    public void print() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(board[i * n + j] + " ");
            }
            System.out.println();
        }
    }
}

