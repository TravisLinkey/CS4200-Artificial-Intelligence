import java.util.*;

public class Engine {

    // A* Functions
    public PuzzleStats solve_puzzle(Puzzle puzzle) {
        PuzzleStats solution_stats = new PuzzleStats();

        // if unsolvable return error
        if(puzzle.inversion_num %2 == 0)
        {
            // Create a goal puzzle
            Puzzle goal_puzzle = new Puzzle(-1);

            // get the runtime of each algorithm
            long start_time_h2 = System.nanoTime();
            solution_stats.h2 = A_Star_h2(puzzle, goal_puzzle);
            long end_time_h2 = System.nanoTime();
            solution_stats.h2.process_time = end_time_h2 - start_time_h2;


            long start_time_h1 = System.nanoTime();
            solution_stats.h1 = A_Star_h1(puzzle, goal_puzzle);
            long end_time_h1 = System.nanoTime();
            solution_stats.h1.process_time = end_time_h1 - start_time_h1;

//            print_path(solution_stats);

            return solution_stats;

//            if(heuristic_num == 1)
//            {
////            solution_path = A_Star_h1(puzzle, goal_puzzle);
//                solution_stats = A_Star_h1(puzzle, goal_puzzle);
//                return solution_stats;
//            }
//            else if(heuristic_num == 2)
//            {
////            solution_path = A_Star_h2(puzzle, goal_puzzle);
//                solution_stats = A_Star_h2(puzzle, goal_puzzle);
//                return solution_stats;
//            }

//        print_path(solution_path);

        }
        else
            System.out.println("Puzzle unsolvable.");

        return new PuzzleStats();
    }

    private PuzzleStats.Heuristic_Data A_Star_h1(Puzzle first_puzzle, Puzzle goal_array) {

        PuzzleStats solution_stats = new PuzzleStats();

        // set of tiles already evaluated
        Stack<Puzzle> explored_set = new Stack<>();

        // set the heuristic of the algorithm
        PriorityQueue<Puzzle> frontier_set = new PriorityQueue<>(new PuzzleComparator());

        // set of currently discovered puzzles not yet evaluated
        Puzzle current = first_puzzle;
        frontier_set.add(current);

        while(!frontier_set.isEmpty())
        {
            explored_set.add(current);

            current.total_nodes_generated = explored_set.size() + frontier_set.size();

            // if current is goal, done
            if(current.equals(goal_array))
            {
//                return reconstruct_path(explored_set, current);
                  solution_stats.h1.solution_path = reconstruct_path(explored_set, current);
                  solution_stats.h1.solution_depth = current.puzzle_depth;
                  solution_stats.h1.nodes_generated = current.total_nodes_generated;
                  return solution_stats.h1;
            }


            // For each valid move, expand the most desired state
            Puzzle.Move[] valid_moves = get_valid_moves(current);

            for(Puzzle.Move m: valid_moves)
            {
                // if neighbor is already in explored set, skip it
                if(is_explored(m, current, explored_set))
                    continue;
                else
                {
                    // New puzzle found
                    Puzzle next_move = make_move(m,current);

                    // If not in the frontier set, add to frontier set
                    if(is_in_frontier(m, current,frontier_set))
                        continue;
                    else
                    {
                        // Best path yet
                        next_move.puzzle_total = next_move.puzzle_depth + next_move.misplaced_tiles;
                        frontier_set.add(next_move);
                    }
                }
            }
            current = frontier_set.poll();
        }
        return null;

    }
    private PuzzleStats.Heuristic_Data A_Star_h2(Puzzle first_puzzle, Puzzle goal_array) {

        int puzzle_counter = 0;

        PuzzleStats solution_stats = new PuzzleStats();

        // set of tiles already evaluated
        Stack<Puzzle> explored_set = new Stack<>();

        // set the heuristic of the algorithm
        PriorityQueue<Puzzle> frontier_set = new PriorityQueue<>(new PuzzleComparator());

        // set of currently discovered puzzles not yet evaluated
        Puzzle current = first_puzzle;
        frontier_set.add(current);

        while(!frontier_set.isEmpty())
        {
            explored_set.add(current);

            current.total_nodes_generated = explored_set.size() + frontier_set.size();

            // if current is goal, done
            if(current.equals(goal_array))
            {
                System.out.println();
                solution_stats.h2.solution_path = reconstruct_path(explored_set, current);
                solution_stats.h2.solution_depth = current.puzzle_depth;
                solution_stats.h2.nodes_generated = current.total_nodes_generated;
                return solution_stats.h2;
            }

            // For each valid move, expand the most desired state
            Puzzle.Move[] valid_moves = get_valid_moves(current);

            for(Puzzle.Move m: valid_moves)
            {
                // if neighbor is already in explored set, skip it
                if(is_explored(m, current, explored_set))
                    continue;
                else
                {
                    // New puzzle found
                    Puzzle next_move = make_move(m,current);

                    // If not in the frontier set, add to frontier set
                    if(is_in_frontier(m, current,frontier_set))
                        continue;
                    else
                    {
                        // Best path yet
                        next_move.puzzle_total = next_move.puzzle_depth + next_move.manhattan_distance;
                        frontier_set.add(next_move);
                    }
                }
            }
            current = frontier_set.poll();
        }
        return null;

    }

    // A* Extra Functions
    private boolean is_in_frontier(Puzzle.Move m, Puzzle current, PriorityQueue<Puzzle> frontier_set) {
        Puzzle new_puzzle = new Puzzle(current.puzzle_depth);

        new_puzzle = make_move(m, current);

        for (Puzzle p: frontier_set) {
            if(p.equals(new_puzzle))
                return true;
        }
        return false;
    }
    private boolean is_explored(Puzzle.Move m, Puzzle current, Stack<Puzzle> explored_set) {
        Puzzle new_puzzle = make_move(m, current);

        for (Puzzle p: explored_set) {

            if(p.equals(new_puzzle))
            {
//                System.out.println("Duplicate found!");
//                p.print_puzzle();
//                System.out.println("and");
//                new_puzzle.print_puzzle();
                return true;
            }

        }
        return false;
    }
    private Stack<Puzzle> reconstruct_path(Stack<Puzzle> came_from, Puzzle current) {

        Stack<Puzzle> optimal_path = new Stack<>();
        optimal_path.push(current);

        while(!came_from.empty())
        {
            // If the current puzzle was reached by the top of the stack puzzle
            // Add that puzzle to the optimal path
            if(came_from.peek() == current.previous_puzzle)
            {
                current = came_from.pop();
                optimal_path.push(current);

                if(came_from.empty())
                    return optimal_path;
            }
            else
                came_from.pop();
            if(came_from.empty())
                return optimal_path;

        }

        return optimal_path;
    }

    class PuzzleComparator implements Comparator<Puzzle> {
        public int compare(Puzzle puzzle1, Puzzle puzzle2) {
            // New puzzle has less misplaced tiles, return true
            if(puzzle1.puzzle_total > puzzle2.puzzle_total)
                return 1;
            else if(puzzle1.puzzle_total < puzzle2.puzzle_total)
                return -1;

            return 0;
        }
    }

    // Misc functions
    public Puzzle generate_random() {
        int[] new_sequence = new int[9];
        int index;
        boolean valid_puzzle = false;
        Puzzle new_puzzle;

        do {
            new_puzzle = new Puzzle(0);

            for (int i = 0; i < new_sequence.length; i++)
            {
                index = (int)(Math.random()*9);
                new_puzzle = swap_index(i,index,new_puzzle);
            }
            new_puzzle.calculate_num_inversions();

            if(new_puzzle.inversion_num%2 == 0)
                valid_puzzle =true;

        } while (!valid_puzzle);


        return new_puzzle;
    }
    public void generate_random_array() {
        int array_size = 35;
        // arrays to hold the depth time elapsed
        int[] depth_time_taken_h1 = new int[array_size];
        int[] depth_time_taken_h2 = new int[array_size];
        // arrays to hold the depth nodes generated
        int[] average_depth_h1 = new int[array_size];
        int[] average_depth_h2 = new int[array_size];
        // arrays to hold the depth count
        int[] depth_count_h1 = new int[35];
        int[] depth_count_h2 = new int[35];
        PuzzleStats solution_stats;


        // for loop to generate and solve 1000 puzzles using h1
        for (int i = 0; i < 500; i++) {
            Puzzle test_puzzle = generate_random();
            System.out.println("Puzzle Number: #"+i);
            test_puzzle.print_puzzle();

            solution_stats = solve_puzzle(test_puzzle);

            System.out.print("[ H1 ] Solution Depth = "+solution_stats.h1.solution_depth);
            System.out.print(" Solution Cost = "+solution_stats.h1.nodes_generated + "  ");
            System.out.println(" Time Taken = "+ Math.abs(solution_stats.h1.process_time/1000000000.0) + "  ");

            average_depth_h1[solution_stats.h1.solution_depth] += solution_stats.h1.nodes_generated;
            depth_count_h1[solution_stats.h1.solution_depth] ++;
            depth_time_taken_h1[solution_stats.h1.solution_depth]+=solution_stats.h1.process_time;

            System.out.print("[ H2 ] Solution Depth = "+solution_stats.h2.solution_depth);
            System.out.print(" Solution Cost = "+ solution_stats.h2.nodes_generated);
            System.out.println(" Time Taken = "+ Math.abs(solution_stats.h2.process_time/1000000000.0) + "  ");

            average_depth_h2[solution_stats.h2.solution_depth] += solution_stats.h2.nodes_generated;
            depth_count_h2[solution_stats.h2.solution_depth] ++;
            depth_time_taken_h2[solution_stats.h2.solution_depth]+=solution_stats.h2.process_time;

            }

        System.out.println("+++++++++++++++++++Finished+++++++++++++++++");
        System.out.println("Average solution depth: ");
        System.out.println("        [Depth]     [Cases Tested]   [Average Runtime]      [Average Solution Depth]");
        for (int i = 0; i < average_depth_h1.length; i++) {
            if (depth_count_h1[i] == 0)
                continue;
            average_depth_h1[i] = average_depth_h1[i]/depth_count_h1[i];
            depth_time_taken_h1[i] = depth_time_taken_h1[i]/depth_count_h1[i];
            if (depth_count_h2[i] == 0)
                continue;
            average_depth_h2[i] = average_depth_h2[i]/depth_count_h2[i];
            depth_time_taken_h2[i] = depth_time_taken_h2[i]/depth_count_h2[i];

            //


            System.out.print("[ H1 ]    ");
            System.out.print(i);
            System.out.print("              "+ depth_count_h1[i]);
            System.out.print("              " + (double)Math.abs(depth_time_taken_h1[i]/1000000000.0) + " seconds");
            System.out.println("            " + average_depth_h1[i]);

            System.out.print("[ H2 ]    ");
            System.out.print(i);
            System.out.print("              "+ depth_count_h2[i]);
            System.out.print("              " + (double)Math.abs(depth_time_taken_h2[i]/1000000000.0) + " seconds");
            System.out.println("            "+average_depth_h2[i]);
        }

    }

    // Mechanical Functions
    public Puzzle swap_index(int i, int index, Puzzle puzzle) {

        Puzzle new_puzzle = puzzle;
        Tile temp = new Tile(0,0);

        temp.position_on_board = new_puzzle.tiles[i].position_on_board;
        new_puzzle.tiles[i].position_on_board = new_puzzle.tiles[index].position_on_board;
        new_puzzle.tiles[index].position_on_board = temp.position_on_board;

        // swaps the array
        temp = new_puzzle.tiles[i];
        new_puzzle.tiles[i] = new_puzzle.tiles[index];
        new_puzzle.tiles[index] = temp;

        return new_puzzle;
    }
    private Puzzle.Move[] get_valid_moves(Puzzle puzzle) {
        int empty_index = find_zero(puzzle);

        switch (empty_index) {
            case 0:
            {
                puzzle.valid_moves = new Puzzle.Move[2];
                puzzle.valid_moves[0] = Puzzle.Move.RIGHT;
                puzzle.valid_moves[1] = Puzzle.Move.DOWN;
                break;
            }
            case 1:
            {
                puzzle.valid_moves = new Puzzle.Move[3];
                puzzle.valid_moves[0] = Puzzle.Move.RIGHT;
                puzzle.valid_moves[1] = Puzzle.Move.DOWN;
                puzzle.valid_moves[2] = Puzzle.Move.LEFT;
                break;
            }
            case 2:
            {
                puzzle.valid_moves = new Puzzle.Move[2];
                puzzle.valid_moves[0] = Puzzle.Move.LEFT;
                puzzle.valid_moves[1] = Puzzle.Move.DOWN;
                break;
            }
            case 3:
            {
                puzzle.valid_moves = new Puzzle.Move[3];
                puzzle.valid_moves[0] = Puzzle.Move.RIGHT;
                puzzle.valid_moves[1] = Puzzle.Move.DOWN;
                puzzle.valid_moves[2] = Puzzle.Move.UP;
                break;
            }
            case 4:
            {
                puzzle.valid_moves = new Puzzle.Move[4];
                puzzle.valid_moves[0] = Puzzle.Move.LEFT;
                puzzle.valid_moves[1] = Puzzle.Move.DOWN;
                puzzle.valid_moves[2] = Puzzle.Move.UP;
                puzzle.valid_moves[3] = Puzzle.Move.RIGHT;
                break;
            }
            case 5:
            {
                puzzle.valid_moves = new Puzzle.Move[3];
                puzzle.valid_moves[0] = Puzzle.Move.LEFT;
                puzzle.valid_moves[1] = Puzzle.Move.DOWN;
                puzzle.valid_moves[2] = Puzzle.Move.UP;
                break;
            }
            case 6:
            {
                puzzle.valid_moves = new Puzzle.Move[2];
                puzzle.valid_moves[0] = Puzzle.Move.RIGHT;
                puzzle.valid_moves[1] = Puzzle.Move.UP;
                break;
            }
            case 7:
            {
                puzzle.valid_moves = new Puzzle.Move[3];
                puzzle.valid_moves[0] = Puzzle.Move.LEFT;
                puzzle.valid_moves[1] = Puzzle.Move.RIGHT;
                puzzle.valid_moves[2] = Puzzle.Move.UP;
                break;
            }
            case 8:
            {
                puzzle.valid_moves = new Puzzle.Move[2];
                puzzle.valid_moves[0] = Puzzle.Move.LEFT;
                puzzle.valid_moves[1] = Puzzle.Move.UP;
                break;
            }
        }
//        puzzle.print_valid_moves();

        return puzzle.valid_moves;
    }
    private int find_zero(Puzzle puzzle) {
        int empty_index = 0;

        for (int i = 0; i < puzzle.tiles.length; i++) {
            if(puzzle.tiles[i].tile_number == 0)
                empty_index = i;
        }

        return empty_index;
    }
    private void print_path(Stack<Puzzle> solution_path) {
        int solution_depth = 0;

        System.out.println("______________________");
        System.out.println("Optimal Solution Path:");

        for (Puzzle p: solution_path) {
            if(p.last_move == null)
                continue;
            else
            {
                solution_depth++;
                System.out.print(p.last_move+ " ");
            }
        }
        System.out.println();
        System.out.println("Solution Depth: " + solution_depth);
        System.out.println();
    }
    public void print_path(PuzzleStats solution_stats) {

        System.out.println("______________________");
        System.out.println("Optimal Solution Path:");

        for (Puzzle p: solution_stats.h1.solution_path) {
            if(p.last_move == null)
                continue;
            else
            {
                System.out.print(p.last_move+ " ");
            }
        }
        System.out.println();
        System.out.println("Solution Depth: " + solution_stats.h1.solution_depth);
        System.out.println("Search Cost: " + solution_stats.h1.nodes_generated);
        System.out.println("Time Taken: " + solution_stats.h1.process_time/1000000 + " seconds");
        System.out.println();

        for (Puzzle p: solution_stats.h2.solution_path) {
            if(p.last_move == null)
                continue;
            else
            {
                System.out.print(p.last_move+ " ");
            }
        }
        System.out.println();
        System.out.println("Solution Depth: " + solution_stats.h2.solution_depth);
        System.out.println("Search Cost: " + solution_stats.h2.nodes_generated);
        System.out.println("Time Taken: " + solution_stats.h2.process_time/1000000 + " seconds");
        System.out.println();

    }

    // Board Movements
    private Puzzle make_move(Puzzle.Move m, Puzzle current) {
        Puzzle new_puzzle = new Puzzle(current);
        Puzzle.Move[] moves = get_valid_moves(new_puzzle);
        int index_of_zero = find_zero(new_puzzle);

        switch(m)
        {
            case RIGHT:
            {
                // if move is valid, take the move
                if(Arrays.asList(moves).contains(Puzzle.Move.RIGHT))
                {
                    new_puzzle.last_move = Puzzle.Move.RIGHT;

                    switch(index_of_zero)
                    {
                        case 0:
                            return swap_index(0, 1, new_puzzle);
                        case 1:
                            return swap_index(1, 2, new_puzzle);
                        case 3:
                            return swap_index(3, 4, new_puzzle);
                        case 4:
                            return swap_index(4, 5, new_puzzle);
                        case 6:
                            return swap_index(6, 7, new_puzzle);
                        case 7:
                            return swap_index(7, 8, new_puzzle);
                    }
                }
                break;
            }
            case UP:
            {
                if(Arrays.asList(moves).contains(Puzzle.Move.UP))
                {
                    new_puzzle.last_move = Puzzle.Move.UP;

                    switch(index_of_zero)
                    {
                        case 3:
                            return swap_index(0, 3, new_puzzle);
                        case 4:
                            return swap_index(1, 4, new_puzzle);
                        case 5:
                            return swap_index(2, 5, new_puzzle);
                        case 6:
                            return swap_index(3, 6, new_puzzle);
                        case 7:
                            return swap_index(4, 7, new_puzzle);
                        case 8:
                            return swap_index(5, 8, new_puzzle);
                    }
                }
                break;
            }
            case DOWN:
            {
                if(Arrays.asList(moves).contains(Puzzle.Move.DOWN))
                {
                    new_puzzle.last_move = Puzzle.Move.DOWN;
                    switch(index_of_zero)
                    {
                        case 0:
                            return swap_index(0, 3, new_puzzle);
                        case 1:
                            return swap_index(1, 4, new_puzzle);
                        case 2:
                            return swap_index(2, 5, new_puzzle);
                        case 3:
                            return swap_index(3, 6, new_puzzle);
                        case 4:
                            return swap_index(4, 7, new_puzzle);
                        case 5:
                            return swap_index(5, 8, new_puzzle);
                    }
                }
                break;
            }
            case LEFT:
            {
                if(Arrays.asList(moves).contains(Puzzle.Move.LEFT))
                {
                    new_puzzle.last_move = Puzzle.Move.LEFT;

                    switch(index_of_zero)
                    {
                        case 1:
                            return swap_index(0, 1, new_puzzle);
                        case 2:
                            return swap_index(1, 2, new_puzzle);
                        case 4:
                            return swap_index(3, 4, new_puzzle);
                        case 5:
                            return swap_index(4, 5, new_puzzle);
                        case 7:
                            return swap_index(6, 7, new_puzzle);
                        case 8:
                            return swap_index(7, 8, new_puzzle);
                    }
                }
                break;
            }
            case None:
                return new_puzzle;
        }

        return new_puzzle;
    }
//    private Puzzle move_left(Puzzle puzzle) {
//
//        // Copy the old puzzle
//        Puzzle after_move = new Puzzle(puzzle);
//        Puzzle.Move[] moves = get_valid_moves(after_move);
//        int index_of_zero = find_zero(after_move);
//
//        if(Arrays.asList(moves).contains(Puzzle.Move.LEFT))
//        {
//            switch(index_of_zero)
//            {
//                case 1:
//                    return swap_index(0, 1, after_move);
//                case 2:
//                    return swap_index(1, 2, after_move);
//                case 4:
//                    return swap_index(3, 4, after_move);
//                case 5:
//                    return swap_index(4, 5, after_move);
//                case 7:
//                    return swap_index(6, 7, after_move);
//                case 8:
//                    return swap_index(7, 8, after_move);
//            }
//        }
//        return puzzle;
//    }
//    private Puzzle move_right(Puzzle puzzle) {
//
//        Puzzle after_move = new Puzzle(puzzle);
//        Puzzle.Move[] moves = get_valid_moves(after_move);
//        int index_of_zero = find_zero(after_move);
//
//        if(Arrays.asList(moves).contains(Puzzle.Move.RIGHT))
//        {
//            switch(index_of_zero)
//            {
//                case 0:
//                    return swap_index(0, 1, after_move);
//                case 1:
//                    return swap_index(1, 2, after_move);
//                case 3:
//                    return swap_index(3, 4, after_move);
//                case 4:
//                    return swap_index(4, 5, after_move);
//                case 6:
//                    return swap_index(6, 7, after_move);
//                case 7:
//                    return swap_index(7, 8, after_move);
//            }
//        }
//        return puzzle;
//    }
//    private Puzzle move_up(Puzzle puzzle) {
//
//        Puzzle after_move = new Puzzle(puzzle);
//        Puzzle.Move[] moves = get_valid_moves(after_move);
//        int index_of_zero = find_zero(after_move);
//
//        if(Arrays.asList(moves).contains(Puzzle.Move.UP))
//        {
//            switch(index_of_zero)
//            {
//                case 3:
//                    return swap_index(0, 3, after_move);
//                case 4:
//                    return swap_index(1, 4, after_move);
//                case 5:
//                    return swap_index(2, 5, after_move);
//                case 6:
//                    return swap_index(3, 6, after_move);
//                case 7:
//                    return swap_index(4, 7, after_move);
//                case 8:
//                    return swap_index(5, 8, after_move);
//            }
//        }
//        return puzzle;
//    }
//    private Puzzle move_down(Puzzle puzzle) {
//        Puzzle after_move = new Puzzle(puzzle);
//        Puzzle.Move[] moves = get_valid_moves(after_move);
//        int index_of_zero = find_zero(after_move);
//
//        if(Arrays.asList(moves).contains(Puzzle.Move.DOWN))
//        {
//            switch(index_of_zero)
//            {
//                case 0:
//                    return swap_index(0, 3, after_move);
//                case 1:
//                    return swap_index(1, 4, after_move);
//                case 2:
//                    return swap_index(2, 5, after_move);
//                case 3:
//                    return swap_index(3, 6, after_move);
//                case 4:
//                    return swap_index(4, 7, after_move);
//                case 5:
//                    return swap_index(5, 8, after_move);
//            }
//        }
//        return puzzle;
//    }

    private void output_h1_data() {
        // output optimal sequence of states
        // solution depth
        // search cost
    }
    private void output_h2_data() {
    }

    // User Functions
    public void let_user_play() {
        boolean quit = false;
        do {
            Puzzle.Move move = get_user_move();
            System.out.println("Would you like to quit?");
            System.out.println("    [0] No");
            System.out.println("    [1] Yes");
            Scanner input = new Scanner(System.in);
            int ans = input.nextInt();

            if(ans == 1)
                quit = true;

        } while(!quit);

    }
    private Puzzle.Move get_user_move() {
        System.out.println("Please select a move: ");
        System.out.println("    [8] Up");
        System.out.println("    [2] Down");
        System.out.println("    [4] Left");
        System.out.println("    [6] Right");
        Scanner input = new Scanner(System.in);
        int ans = input.nextInt();

        switch(ans)
        {
            case 8:
                return Puzzle.Move.UP;
            case 2:
                return Puzzle.Move.DOWN;
            case 4:
                return Puzzle.Move.LEFT;
            case 6:
                return Puzzle.Move.RIGHT;
        }
        return Puzzle.Move.None;
    }

}
