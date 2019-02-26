
public class Puzzle {
    public enum Move {
        RIGHT,
        LEFT,
        UP,
        DOWN,
        None
    }

    Tile[] tiles;
    int inversion_num;
    int misplaced_tiles;
    int manhattan_distance;

    int puzzle_total;
    int puzzle_depth;
    int total_nodes_generated;

    Move[] valid_moves;
    Puzzle previous_puzzle;
    Move last_move;

    Puzzle(int depth) {
        puzzle_depth = depth+1;
        instantiate_array();
        initialize_puzzle();
    }
    Puzzle(int[] sequence) { ;
        puzzle_depth = 0;
        instantiate_array();
        for (int i = 0; i < 9; i++) {
            this.tiles[i].tile_number = sequence[i];
        }
        initialize_puzzle();
    }
    Puzzle(Puzzle old_puzzle) {
        puzzle_depth = old_puzzle.puzzle_depth+1;

        instantiate_array();

        for (int i = 0; i < tiles.length; i++) {
            this.tiles[i].tile_number = old_puzzle.tiles[i].tile_number;
        }

        initialize_puzzle();
        previous_puzzle = old_puzzle;
    }

    // Board Mechanic Functions
    private void instantiate_array() {
        this.tiles = new Tile[9];
        for (int i = 0; i < 9; i++) {
            this.tiles[i] = new Tile(i,i);
        }
    }
    private void initialize_puzzle() {
        calculate_manhattan_distance();
        calculate_misplaced_tiles();
        calculate_num_inversions();
    }

    // Print functions
    public void print_valid_moves() {
        for (Move m: valid_moves) {
            if(m==Move.DOWN)
                System.out.print("Down ");
            else if(m==Move.UP)
                System.out.print("Up ");
            else if(m==Move.LEFT)
                System.out.print("Left ");
            if(m==Move.RIGHT)
                System.out.print("Right ");
        }
        System.out.println();
    }
    void print_puzzle() {
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                System.out.print(this.tiles[3*i+j].tile_number+" ");
            }
            System.out.println("| ");
        }
    }

    public boolean equals(Puzzle puzzle) {
        boolean is_equal = false;

        for (int i = 0; i < this.tiles.length; i++) {
            if(this.tiles[i].tile_number == puzzle.tiles[i].tile_number)
                is_equal = true;
            else
                return false;
        }
        return is_equal;
    }

    public void calculate_manhattan_distance() {
        // zero the distance
        this.manhattan_distance = 0;

        for (Tile t: tiles) {
            if(t.tile_number == 0)
                continue;
            else{
                t.calculate_distance_from_goal();
                this.manhattan_distance += t.distance_from_goal;
            }
        }

    }
    public void calculate_misplaced_tiles() {
        // zero the tile count
        this.misplaced_tiles = 0;

        for (int i = 0; i < tiles.length; i++) {
            if(tiles[i].tile_number != i) {
                if (tiles[i].tile_number == 0)
                    continue;
                else
                {
                    this.misplaced_tiles++;
                }
            }
        }

    }
    public void calculate_num_inversions() {
        inversion_num = 0;

        for (int i = 0; i < tiles.length-1; i++) {
            if(tiles[i].tile_number == 0)
                continue;
            else
            {
                for (int j = i+1; j < tiles.length; j++) {
                    if(tiles[i].tile_number > tiles[j].tile_number && tiles[j].tile_number != 0)
                        inversion_num++;
                }
            }
        }

//        System.out.println("Number of inversions:" + puzzle.inversion_num);

        if(inversion_num%2 != 0) {
//            System.out.println("Invalid board!");
        }
    }
}