import java.util.ArrayList;

public class Board {
    String[] next_moves;
    String last_move=null;
    final int dimension = 8;
    public boolean is_terminal;
    public Board utility_value;
    public int[] row_values;
    String board_state=null;

    // Board Constructor
    Board() {
        board_state = "|--------|--------|--------|--------|--------|--------|--------|--------|";
        row_values = new int[17];
    }

    Board(Board board) {
        this.board_state = board.board_state;
        this.last_move = board.last_move;
        this.row_values = new int[board.row_values.length];
        for (int i = 0; i < board.row_values.length; i++) {
            this.row_values[i] = board.row_values[i];
        }
        this.next_moves = board.next_moves;
    }

    // Tile Class
    class Tile {
        char piece;

        Tile() {
            piece='-';
        }
    }

}
