import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Game {
    int max_time, move_counter;
    boolean isAI;
    boolean gameover;
    boolean first_move = true;
    int count = 0;

    Board current_board;
    UI user_interface;
    Queue<String> game_sequence;

    // Class constructor
    Game() {

        initialize_game();
        int user_option = user_interface.main_menu();
        start_game(user_option);
    }

    // Game Functions
    private void initialize_game() {
        // game is beginning
        gameover = false;
        first_move = true;

        // initialize the stack for moves and create the board
        game_sequence = new LinkedList<>();
        current_board = new Board();
        current_board.next_moves = get_next_moves(current_board.board_state);
        user_interface = new UI();
    }
    private void start_game(int user_option) {
        char first_player;

        while (user_option != 3) {
            initialize_game();

            // Single player mode
            if (user_option == 1) {
                // get the max time for turns and the first player
                this.max_time = user_interface.ask_max_time();

                first_player = user_interface.ask_first_player();

                // play the isAI
                play_game_with_bot(first_player);
            }
            // Multi Player mode
            else if (user_option == 2) {
                switch(user_interface.ask_first_player()) {
                    case 'C':
                        this.isAI = true;
                    case 'O':
                        this.isAI = false;

                }
                // play the game
                play_game(isAI);
            }
            user_option = user_interface.main_menu();
        }

        System.out.println("Great game! Goodbye!");
        System.out.println();
    }
    private void play_game(boolean first_player) {
        boolean First_Player = first_player;
        String player_1_move = null;
        String player_2_move = null;

        // TODO Fake gameplay simulation; always loops
        while (!gameover) {
            if (First_Player == false) {
                player_1_move = take_turn(first_player);
                game_sequence.add(player_1_move);

                if (gameover)
                    break;

                // print the board
                print_board(this.current_board);

                player_2_move = take_turn(!first_player);
                game_sequence.add(player_2_move);

                if (gameover)
                    break;

                // print the board
                print_board(this.current_board);

            } else {
                player_2_move = take_turn(!first_player);
                game_sequence.add(player_2_move);

                if (gameover)
                    break;

                // print the board
                print_board(this.current_board);

                player_1_move = take_turn(first_player);
                game_sequence.add(player_1_move);

                if (gameover)
                    break;

                // print the board
                print_board(this.current_board);
            }
        }
    }
    private void play_game_with_bot(char first_player) {
        isAI = true;
        String player_1_move = null;
        String player_2_move = null;
        int depth = 0;

        long start_time = System.currentTimeMillis();
        while (!gameover) {
            if(first_player == 'O') {
                player_1_move = take_turn(!isAI);

                current_board.last_move = player_1_move;
                game_sequence.add(player_1_move);
                print_board(this.current_board);

                //Check if current board matches win.
                check_game_over(current_board);

                if (gameover)
                    break;


                // TODO - need cutoff time to make move, then, evaluate current boardstate as terminal
                count = 0;
                int value = minimax(current_board, depth, -10000, 10000, isAI, start_time);
                System.out.println("                                                        CHOSEN VALUE: " + value);

                // make next move from this value
                // TODO - Not finding the correct next move from minimax!
                player_2_move = get_successor(current_board, value);
                current_board = place_piece(current_board, player_2_move, isAI);

                current_board.last_move = player_2_move;
                game_sequence.add(player_2_move);

                print_board(this.current_board);

                //Check if current board matches win.
                check_game_over(current_board);

                if (gameover)
                    break;

            }
            else
            {
                if (first_move) {
                    player_2_move = get_random_move();
                    current_board = place_piece(current_board, player_2_move, isAI);

                    current_board.last_move = player_2_move;
                    game_sequence.add(player_2_move);
                    print_board(this.current_board);
                    first_move = false;

                    player_1_move = take_turn(!isAI);

                    current_board.last_move = player_1_move;
                    game_sequence.add(player_1_move);
                    print_board(this.current_board);

                    //Check if current board matches win.
                    check_game_over(current_board);

                    if (gameover)
                        break;
                    continue;
                }
                else
                {
                    // TODO - need cutoff time to make move, then, evaluate current boardstate as terminal
                    count = 0;
                    int value = minimax(current_board, depth, -10000, 10000, isAI, start_time);
                    System.out.println("                                                        CHOSEN VALUE: " + value);

                    // make next move from this value
                    // TODO - Not finding the correct next move from minimax!
                    player_2_move = get_successor(current_board, value);
                    current_board = place_piece(current_board, player_2_move, isAI);

                    current_board.last_move = player_2_move;
                    game_sequence.add(player_2_move);

                    print_board(this.current_board);

                    //Check if current board matches win.
                    check_game_over(current_board);

                    if (gameover)
                        break;

                    player_1_move = take_turn(!isAI);

                    current_board.last_move = player_1_move;
                    game_sequence.add(player_1_move);
                    print_board(this.current_board);

                    //Check if current board matches win.
                    check_game_over(current_board);

                    if (gameover)
                        break;
                }

            }
        }
    }
    private String get_random_move() {
//        String move = null;
//        move = get_random_row() + get_random_col();
//        System.out.println("Random move: " + move);
//
//        return move;

        // TODO - Fake values for debugging
        return "C4";
    }
    private String take_turn(boolean AI) {
        boolean move_valid = false;
        String player_move = null;

        // increment total move counter
        move_counter++;

        while (!move_valid) {
            // get player move
            player_move = user_interface.ask_piece_placement(AI);
            move_valid = check_valid_move(current_board, Character.getNumericValue(player_move.charAt(0)) - 10, Character.getNumericValue(player_move.charAt(1)));

            if (!move_valid)
                System.out.println("Illegal move. A piece is already there");
        }
        // move the players piece
        current_board = place_piece(current_board, player_move, AI);

        return player_move;
    }
    private int get_col(String move) {
        int col = Character.getNumericValue(move.charAt(1));
        return col;
    }
    private int get_row(String move) {
        int integer_row = 0;
        char row = move.charAt(0);

        switch (row) {
            case 'A':
                integer_row = 0;
                break;
            case 'B':
                integer_row = 1;
                break;
            case 'C':
                integer_row = 2;
                break;
            case 'D':
                integer_row = 3;
                break;
            case 'E':
                integer_row = 4;
                break;
            case 'F':
                integer_row = 5;
                break;
            case 'G':
                integer_row = 6;
                break;
            case 'H':
                integer_row = 7;
                break;
        }
        return integer_row;
    }
    public String[] get_next_moves(String potential_board) {
        String[] next_moves;
        ArrayList<String> moves = new ArrayList<String>();

        // keep the row count
        int row = 0;
        int count = 0;

        char[] tiles = new char[64];

        // split string into tiles
        String[] lines = potential_board.split("\\|");

        // for each tile, if it is unoccupied, this is a next state
        for (int j = 1; j < lines.length; j++) {
            for (int i = 0; i < 8; i++) {
                if (lines[j].charAt(i) == '-') {
                    String ans = convert_to_move(row, i);
                    moves.add(ans);
                }
            }
            row++;
        }

        next_moves = new String[moves.size()];
        next_moves = moves.toArray(next_moves);
        return next_moves;
    }
    private String convert_to_move(int row, int i) {
        char new_row = 'a';

        switch (row) {
            case 0: {
                new_row = 'A';
                break;
            }
            case 1: {
                new_row = 'B';
                break;
            }
            case 2: {
                new_row = 'C';
                break;
            }
            case 3: {
                new_row = 'D';
                break;
            }
            case 4: {
                new_row = 'E';
                break;
            }
            case 5: {
                new_row = 'F';
                break;
            }
            case 6: {
                new_row = 'G';
                break;
            }
            case 7: {
                new_row = 'H';
                break;
            }
        }
        String move = new_row + Integer.toString(i + 1);

        return move;
    }


    // Algorithm Functions
    // TODO - Rewrite So they only work on the single board
    int minimax(Board board, int depth, int alpha, int beta, boolean is_AI, long start_time) {

        // Timer variable
        long current_time = System.currentTimeMillis();
        long new_time = (current_time - start_time) / 1000;

        if (depth > 1 ) {
            int value = get_cutoff_move(board);
            return value;
        }

        int max_value, min_value;

        // TODO - ONLY ONE BOARD STATE!
        Board new_board;

        if (board.next_moves == null)
            return get_row_utility(board, board.last_move);

        if (is_AI) {
            max_value = -1000000;

            for (String move : board.next_moves) {
                Board bboard = board;
                new_board = place_piece(bboard, move, is_AI);

                int value = minimax(new_board, depth + 1, alpha, beta, !is_AI, start_time);

                max_value = larger(max_value, value);
                alpha = larger(alpha, max_value);
                System.out.println();

                if (beta <= alpha)
                    break;
            }
            return max_value;
        } else {
            min_value = 1000000;

            for (String move : board.next_moves) {
                Board bboard = board;
                new_board = place_piece(bboard, move, is_AI);

                int value = minimax(new_board, depth + 1, alpha, beta, is_AI, start_time);

                min_value = smaller(min_value, value);
                beta = smaller(beta, min_value);
                System.out.println();

                if (beta <= alpha)
                    break;
            }
            return min_value;
        }
    }
private String get_successor(Board board, int value) {
    String move = null;
    int row_num = 0;
    boolean valid_move = false;
    boolean value_found = false;

    int max_row_value = 0;
    int max_row_index = 0;
    // Get the row which has the highest value
    for (int row = 0; row < board.row_values.length; row++) {
        if (board.row_values[row] > max_row_value) {
            max_row_value = board.row_values[row];
            max_row_index = row;
        }
    }

    // Find the move that creates the row value we want
    while (!value_found) {
        if (max_row_index < 8) {
            for (int i = 0; i < 8; i++) {
                String next_move = get_row(max_row_index) + Integer.toString(i);
                Board returned = place_piece(board, next_move, true);
                if (returned.row_values[max_row_index] == value) {
                    move = next_move;
                    value_found = true;
                }
            }
        } else {
            for (int i = 0; i < 8; i++) {
                String row = get_row(i+1);
                String col =  Integer.toString(max_row_index-8);
                String next_move = row + col;
                Board returned = place_piece(board, next_move, true);
                if (returned.row_values[max_row_index] == value) {
                    move = next_move;
                    value_found = true;
                }
            }

        }

    }

    return move;
    }
    // if row is > 7, it is a collumn

    private String get_best_row_move(Board board, int row) {
        String move=null;
        int max_value=0;
        int highest_window = 0;

        // TODO - Need to fix this function to return a move that is based on count
        // TODO - Idea - get the values into an array. retrieve the highest, second highest, third, ect.

        // we get each window value for the row
        int index = 0;


        // check streak window for the row
        for (int i = row*9+1; i < row*9+5; i++) {
            int row_value = check_window_row(board, i, i+4);

            System.out.println("Row value: " + row_value);

            if(Math.abs(row_value) > Math.abs(max_value))
            {
                max_value = row_value;
            }
            index++;
            highest_window++;
        }


        System.out.println("Max Value: " + max_value);
        System.out.println("Window number: " + highest_window);

        // Select move which produced best total
        move = get_row(row+1) + get_open_col(board, row, highest_window);

        return move;
    }
    private String get_best_col_move(Board board, int col) {
        String move=null;
        int max_value=0;
        int highest_window = 0;

        // check streak window for the col
        for (int i = 0; i < 4; i++) {
            int row_value = check_window_col(board, col, i);

            System.out.println("Col value: " + row_value);

            if(Math.abs(row_value) > Math.abs(max_value))
            {
                max_value = row_value;
                highest_window = i;
            }
        }

        System.out.println("Max Value: " + max_value);
        System.out.println("Window number: " + highest_window);

        // Select move which produced best total
        move = get_row(highest_window+1) + get_open_col(board, highest_window, col);

        return move;
    }
    private String get_open_col(Board board, int row, int highest_window) {
        for (int i = highest_window; i <highest_window+5; i++) {
            System.out.println("Char is: " + board.board_state.charAt(9*row+i));
            if(board.board_state.charAt(9*row+i) == '-')
                return String.valueOf(i);
        }
        return null;
    }

    private Board place_piece(Board board, String move, boolean AI) {
        Board next = new Board(board);
        String next_board;
        char player_piece = '-';

        // get row function
        int integer_row = get_row(move);
        int col = get_col(move);

        // check the isAI
        if(AI)
            player_piece = 'O';
        else if(!AI)
            player_piece = 'X';

        // set the isAI's board position and the unused space position
        int position = 9*integer_row + col;
        StringBuilder newstring = new StringBuilder(board.board_state);
        newstring.setCharAt(position, player_piece);
        next_board=newstring.toString();

        // TODO
        next.board_state = next_board;
        next.next_moves = get_next_moves(next.board_state);
        next.row_values[get_row(move)] = get_row_utility(next, move);
        next.row_values[get_col(move)+8] = get_col_utility(next, move);
        next.last_move = move;

        print_board(next);

        return next;
    }
    private boolean check_vertical_win(Board board, boolean AI) {

        String vertical_win_xs=".*X........X........X........X.*";
        String vertical_win_os=".*O........O........O........O.*";

        if (!AI) {
            if (board.board_state.matches(vertical_win_xs))
                return true;
        }
        else
        {
            if (board.board_state.matches(vertical_win_os))
                return true;
        }


        return false;
    }
    private boolean check_horizontal_win(Board board, boolean AI) {

        String horizontal_win_xs=".*XXXX.*";
        String horizontal_win_os=".*OOOO.*";

        if (!AI)
        {
            if (board.board_state.matches(horizontal_win_xs))
                return true;
        }
        else
        {
            if (board.board_state.matches(horizontal_win_os))
                return true;
        }

        return false;
    }
    private void check_game_over(Board board) {

        if(check_horizontal_win(board,false)||check_horizontal_win(board,true))
        {
            System.out.println("Horizontal Win! Game over!");
            gameover=true;
            return;
        }
        else if(check_vertical_win(board,false)||check_vertical_win(board,true))
        {
            System.out.println("Vertical Win! Game over!");
            gameover=true;
            return;
        }
    }
    private boolean check_valid_move(Board board, String move) {
        int row = get_row(move);
        int col = get_col(move);

        int position = 9*row+col;

        if (board.board_state.charAt(position)=='-')
            return true;
        else
            return false;
    }
    private boolean check_valid_move(Board board, int row, int col) {
        int position = 9*row+col;

        if (board.board_state.charAt(position)=='-')
            return true;
        else
            return false;
    }

    // Utility Functions
    private int get_row_utility(Board board, String move) {
        int row = get_row(move);
        int row_sum = 0;

        // check the streak window 4 times
        for (int i = row*9+1; i < row*9+5; i++) {
            int add = check_window_row(board, i, i+4);
            row_sum += add;
        }

        return row_sum;
    }
    private int get_col_utility(Board board, String move) {
        int row = get_row(move);
        int col = get_col(move);
        int row_sum = 0;

        // moves the window 4 times
        for (int i = 0; i < 4; i++) {
            row_sum += check_window_col(board, col, i);
        }

        return row_sum;
    }
    private String get_row_string(Board board, int low, int high) {
        String new_string = "";
        StringBuilder stringBuilder = new StringBuilder(new_string);

        for (int i = low; i <= high; i++) {
            stringBuilder.append(board.board_state.charAt(i));
        }

        new_string = stringBuilder.toString();

        return new_string;
    }
    private String get_col_string(Board board, int col, int row) {
        String new_string = "";
        StringBuilder stringBuilder = new StringBuilder(new_string);

        for (int i = row; i < row+4; i++) {
            stringBuilder.append(board.board_state.charAt(i*9+col));
        }

        new_string = stringBuilder.toString();

        return new_string;
    }
    private int get_cutoff_move(Board board) {
        int max_value = 0;

        for (int row = 0; row < 16; row++) {
            if (Math.abs(board.row_values[row]) > Math.abs(max_value))
                max_value = board.row_values[row];
        }

        return max_value;
    }
    private boolean check_match(Pattern pattern, String board_state) {
        Matcher m = pattern.matcher(board_state);
        if(m.find())
            return true;
        else
            return false;
    }
    private int check_window_row(Board board, int low, int high) {
        int o_count = 0;
        int sp_count = 0;
        int value = 0;

        // gets the string for the window
        String window = get_row_string(board, low, high);

        // TODO - check the window string against the Utility for row
        value = one_line_row(window, value);
        value = two_line_row(window, value);
        value = three_line_row(window, value);
        value = four_line_row(window, value);

        return value;
    }
    private int check_window_col(Board board, int col, int row) {
        int o_count = 0;
        int sp_count = 0;
        int value = 0;

        // gets the string for the window
        String window = get_col_string(board, col, row);

        // TODO - check the window string against the Utility for row
        value = one_line_row(window, value);
        value = two_line_row(window, value);
        value = three_line_row(window, value);
        value = four_line_row(window, value);

        return value;
    }

    private int one_line_row(String window, int value) {
        int val = value;
        String check_1 = "[^O]*O---[^O]*";
        String check_2 = "[^O]*-O--[^O]*";
        String check_3 = "[^O]*--O--[^O]*";
        String check_4 = "[^O]*--O-[^O]*";
        String check_5 = "[^O]*---O[^O]*";
        String check_6 = "[^X]*X---[^X]*";
        String check_7 = "[^X]*-X--[^X]*";
        String check_8 = "[^X]*--X--[^X]*";
        String check_9 = "[^x]*--X-[^X]*";
        String check_10 = "[^X]*---X[^X]*";

        Pattern pattern_1 = Pattern.compile(check_1);
        Pattern pattern_2 = Pattern.compile(check_2);
        Pattern pattern_3 = Pattern.compile(check_3);
        Pattern pattern_4 = Pattern.compile(check_4);
        Pattern pattern_5 = Pattern.compile(check_5);

        Pattern pattern_6 = Pattern.compile(check_6);
        Pattern pattern_7 = Pattern.compile(check_7);
        Pattern pattern_8 = Pattern.compile(check_8);
        Pattern pattern_9 = Pattern.compile(check_9);
        Pattern pattern_10 = Pattern.compile(check_10);

        if(check_match(pattern_1, window) || check_match(pattern_5,window))
            val = 12;
        else if(check_match(pattern_2,window) || check_match(pattern_4, window))
            val = 15;
        else if(check_match(pattern_3, window))
            val = 18;

        if(check_match(pattern_6, window) || check_match(pattern_10,window))
            val = -10;
        else if(check_match(pattern_7,window) || check_match(pattern_9, window))
            val = -13;
        else if(check_match(pattern_8, window))
            val = -16;

        return val;
    }
    private int two_line_row(String window, int value) {
        int val = value;

        String check_1 = "[^O]*OO--[^O]*";
        String check_2 = "[^O]*O-O-[^O]*";
        String check_3 = "[^O]*O--O[^O]*";
        String check_4 = "[^O]*-OO--[^O]*";
        String check_5 = "[^O]*-O-O-[^O]*";
        String check_6 = "[^O]*--OO-[^O]*";
        String check_7 = "[^O]*-O-O[^O]*";
        String check_8 = "[^O]*O--O[^O]*";
        String check_9 = "[^O]*--OO[^O]*";
        String check_10 = "[^O]*O---O[^O]*";

        String check_11 = "[^X]*XX--[^X]*";
        String check_12 = "[^X]*X-X-[^X]*";
        String check_13 = "[^X]*X--X[^X]*";
        String check_14 = "[^X]*-XX--[^X]*";
        String check_15 = "[^X]*-X-X-[^X]*";
        String check_16 = "[^X]*--XX-[^X]*";
        String check_17 = "[^X]*-X-X[^X]*";
        String check_18 = "[^X]*X--X[^X]*";
        String check_19 = "[^X]*--XX[^X]*";
        String check_20 = "[^X]*X---X[^X]*";

        Pattern pattern_1 = Pattern.compile(check_1);
        Pattern pattern_2 = Pattern.compile(check_2);
        Pattern pattern_3 = Pattern.compile(check_3);
        Pattern pattern_4 = Pattern.compile(check_4);
        Pattern pattern_5 = Pattern.compile(check_5);
        Pattern pattern_6 = Pattern.compile(check_6);
        Pattern pattern_7 = Pattern.compile(check_7);
        Pattern pattern_8 = Pattern.compile(check_8);
        Pattern pattern_9 = Pattern.compile(check_9);
        Pattern pattern_10 = Pattern.compile(check_10);

        Pattern pattern_11 = Pattern.compile(check_11);
        Pattern pattern_12 = Pattern.compile(check_12);
        Pattern pattern_13 = Pattern.compile(check_13);
        Pattern pattern_14 = Pattern.compile(check_14);
        Pattern pattern_15 = Pattern.compile(check_15);
        Pattern pattern_16 = Pattern.compile(check_16);
        Pattern pattern_17 = Pattern.compile(check_17);
        Pattern pattern_18 = Pattern.compile(check_18);
        Pattern pattern_19 = Pattern.compile(check_19);
        Pattern pattern_20 = Pattern.compile(check_20);

        if(check_match(pattern_1, window) ||
                check_match(pattern_2,window) ||
                check_match(pattern_3,window) ||
                check_match(pattern_8,window) ||
                check_match(pattern_9,window))
        {
            val = 22;
        }
        else if(check_match(pattern_4, window) ||
                check_match(pattern_5,window) ||
                check_match(pattern_6,window) ||
                check_match(pattern_7,window))
        {
            val = 26;
        }
        if(check_match(pattern_10, window))
            val = 12;


        if(check_match(pattern_11, window) ||
                check_match(pattern_12,window) ||
                check_match(pattern_13,window) ||
                check_match(pattern_18,window) ||
                check_match(pattern_19,window))
        {
            val = -22;
        }
        else if(check_match(pattern_14, window) ||
                check_match(pattern_15,window) ||
                check_match(pattern_16,window) ||
                check_match(pattern_17,window))
        {
            val = -26;
        }
        if(check_match(pattern_20, window))
            val = -12;

        return val;
    }
    private int three_line_row(String window, int value) {
        int val = value;

        String check_1 = "[^O]*OOO-[^O]*";
        String check_2 = "[^O]*OO-O[^O]*";
        String check_3 = "[^O]*O-OO[^O]*";
        String check_4 = "[^O]*-OOO-[^O]*";
        String check_5 = "[^O]*OO--O[^O]*";
        String check_6 = "[^O]*O-O-O[^O]*";
        String check_7 = "[^O]*-OO-O[^O]*";
        String check_8 = "[^O]*O--OO[^O]*";
        String check_9 = "[^O]*-O-OO[^O]*";
        String check_10 = "[^O]*--OOO[^O]*";

        String check_11 = "[^X]*XXX-[^X]*";
        String check_12 = "[^X]*XX-X[^X]*";
        String check_13 = "[^X]*X-XX[^X]*";
        String check_14 = "[^X]*-XXX-[^X]*";
        String check_15 = "[^X]*XX--X[^X]*";
        String check_16 = "[^X]*X-X-X[^X]*";
        String check_17 = "[^X]*-XX-X[^X]*";
        String check_18 = "[^X]*X--XX[^X]*";
        String check_19 = "[^X]*-X-XX[^X]*";
        String check_20 = "[^X]*--XXX[^X]*";

        Pattern pattern_1 = Pattern.compile(check_1);
        Pattern pattern_2 = Pattern.compile(check_2);
        Pattern pattern_3 = Pattern.compile(check_3);
        Pattern pattern_4 = Pattern.compile(check_4);
        Pattern pattern_5 = Pattern.compile(check_5);
        Pattern pattern_6 = Pattern.compile(check_6);
        Pattern pattern_7 = Pattern.compile(check_7);
        Pattern pattern_8 = Pattern.compile(check_8);
        Pattern pattern_9 = Pattern.compile(check_9);
        Pattern pattern_10 = Pattern.compile(check_10);

        Pattern pattern_11 = Pattern.compile(check_11);
        Pattern pattern_12 = Pattern.compile(check_12);
        Pattern pattern_13 = Pattern.compile(check_13);
        Pattern pattern_14 = Pattern.compile(check_14);
        Pattern pattern_15 = Pattern.compile(check_15);
        Pattern pattern_16 = Pattern.compile(check_16);
        Pattern pattern_17 = Pattern.compile(check_17);
        Pattern pattern_18 = Pattern.compile(check_18);
        Pattern pattern_19 = Pattern.compile(check_19);
        Pattern pattern_20 = Pattern.compile(check_20);

        if(check_match(pattern_1, window) || check_match(pattern_10, window))
            val = 36;

        if(check_match(pattern_2,window) ||
                check_match(pattern_3,window) ||
                check_match(pattern_6,window) ||
                check_match(pattern_7,window) ||
                check_match(pattern_9,window))
        {
            val = 33;
        }
        else if(check_match(pattern_5, window) ||
                check_match(pattern_8,window))
        {
            val = 30;
        }
        else if(check_match(pattern_4, window))
            val = 40;


        if(check_match(pattern_11, window) || check_match(pattern_20,window))
        {
            val = -36;
        }

        if(check_match(pattern_12,window) ||
                check_match(pattern_13,window) ||
                check_match(pattern_16,window) ||
                check_match(pattern_17,window) ||
                check_match(pattern_19,window))
        {
            val = -33;
        }
        else if(check_match(pattern_15, window) ||
                check_match(pattern_18,window))
        {
            val = 30;
        }
        else if(check_match(pattern_14, window))
            val = -40;


        return val;
    }
    private int four_line_row(String window, int value) {
        int val = value;
        String check_1 = "[^O]*OOOO-[^O]*";
        String check_2 = "[^O]*OOO-O[^O]*";
        String check_3 = "[^O]*OO-OO[^O]*";
        String check_4 = "[^O]*O-OOO[^O]*";
        String check_5 = "[^O]*-OOOO[^O]*";

        String check_6 = "[^X]*XXXX-[^X]*";
        String check_7 = "[^X]*XXX-X[^X]*";
        String check_8 = "[^X]*XX-XX[^X]*";
        String check_9 = "[^X]*X-XXX[^X]*";
        String check_10 = "[^X]*-XXXX[^X]*";

        Pattern pattern_1 = Pattern.compile(check_1);
        Pattern pattern_2 = Pattern.compile(check_2);
        Pattern pattern_3 = Pattern.compile(check_3);
        Pattern pattern_4 = Pattern.compile(check_4);
        Pattern pattern_5 = Pattern.compile(check_5);

        Pattern pattern_6 = Pattern.compile(check_6);
        Pattern pattern_7 = Pattern.compile(check_7);
        Pattern pattern_8 = Pattern.compile(check_8);
        Pattern pattern_9 = Pattern.compile(check_9);
        Pattern pattern_10 = Pattern.compile(check_10);

        if(check_match(pattern_2, window) || check_match(pattern_4,window))
            val = 40;
        else if(check_match(pattern_1, window) || check_match(pattern_5, window))
            val = 70;
        else if(check_match(pattern_3, window))
            val = 35;

        if(check_match(pattern_7, window) || check_match(pattern_9,window))
            val = -40;
        else if(check_match(pattern_6, window) || check_match(pattern_10, window))
            val = -70;
        else if(check_match(pattern_8, window))
            val = -35;

        return val;
    }

    private String get_random_row() {
        String row;

        Random random = new Random();
        int num = random.nextInt(7)+1;
        switch(num)
        {
            case 1:
                row = "A";
                break;
            case 2:
                row = "B";
                break;
            case 3:
                row = "C";
                break;
            case 4:
                row = "D";
                break;
            case 5:
                row = "E";
                break;
            case 6:
                row = "F";
                break;
            case 7:
                row = "G";
                break;
            case 8:
                row = "H";
                break;
            default:
                row = "C";
        }

        return row;
    }
    private String get_random_col() {
        Random random = new Random();

        int num = random.nextInt(7)+1;
        String col = Integer.toString(num);

        return col;
    }
    private String get_row(int board_index) {
        String integer_row="";

        int mod = (board_index % 9)-1;

        switch (mod)
        {
            case 0:
            {
                integer_row = "A";
                break;
            }
            case 1:
            {
                integer_row = "B";
                break;
            }
            case 2:
            {
                integer_row = "C";
                break;
            }
            case 3:
            {
                integer_row = "D";
                break;
            }
            case 4:
            {
                integer_row = "E";
                break;
            }
            case 5:
            {
                integer_row = "F";
                break;
            }
            case 6:
            {
                integer_row = "G";
                break;
            }
            case 7:
            {
                integer_row = "H";
                break;
            }
        }

        return integer_row;
    }
    private String get_col(int move) {
        int col = 0;
        String new_col="";

        col = move / 9;

        switch (col)
        {
            case 0:
                new_col = "1";
                break;
            case 1:
                new_col = "2";
                break;
            case 2:
                new_col = "3";
                break;
            case 3:
                new_col = "4";
                break;
            case 4:
                new_col = "5";
                break;
            case 5:
                new_col = "6";
                break;
            case 6:
                new_col = "7";
                break;
            case 7:
                new_col = "8";
                break;
        }

        return new_col;
    }

    // Misc functions
    int smaller(int first, int second) {
        if (first <= second)
            return first;
        else
            return second;
    }
    int larger(int first, int second) {
        if (first >= second)
            return first;
        else
            return second;
    }

    // Printing Functions
    void print_board(Board board) {
        boolean empty = false;

        // Print the collumn header
        System.out.println("  1 2 3 4 5 6 7 8       Player vs. Computer");

        // Prints the beginning character for each row
        for (int i = 0; i < board.dimension; i++) {
            switch (i) {
                case 0:
                    System.out.print("A ");
                    break;
                case 1:
                    System.out.print("B ");
                    break;
                case 2:
                    System.out.print("C ");
                    break;
                case 3:
                    System.out.print("D ");
                    break;
                case 4:
                    System.out.print("E ");
                    break;
                case 5:
                    System.out.print("F ");
                    break;
                case 6:
                    System.out.print("G ");
                    break;
                case 7:
                    System.out.print("H ");
                    break;
            }

            // For each collumn prints the piece
            for (int j = 1; j < board.dimension + 1; j++) {
                // for each collumn, print the board piece in that position
                if (board.board_state.charAt(i * 9 + j) == 'X')
                    System.out.print("X ");
                else if (board.board_state.charAt(i * 9 + j) == 'O')
                    System.out.print("O ");
                else
                    System.out.print("- ");
            }

            // Print pairs for each index
            print_pair(i);

            // end of line character
            System.out.println();
        }

        // copy the queue
        Queue<String> copy_queue = new LinkedList<>(game_sequence);

        // pop the top 8 entries from the queue
        for (int i = 0; i < 16; i++) {
            copy_queue.poll();
        }

        // counter to track the pair count
        int count = 9;

        // If moves still exist, keep printing them
        while (copy_queue.peek() != null) {
            if (count > 9)
                System.out.print("                     " + count++ + ".  " + copy_queue.poll());
            else
                System.out.print("                      " + count++ + ".  " + copy_queue.poll());

            if (copy_queue.peek() != null)
                System.out.print("          " + copy_queue.poll());

            System.out.println();
        }
        System.out.println("Row values: ");
        for (int x = 0; x < board.row_values.length; x++) {
            System.out.print(board.row_values[x] + " ");
        }
        System.out.println();
    }
    void print_pair(int index) {

        // make copy of queue
        Queue<String> copy = new LinkedList<>(game_sequence);
        int size = copy.size();

        // empty queue; return
        if (copy.peek() == null)
            return;

        // pull off the top 2*index from queue
        if (index >= 1) {
            for (int i = 0; i < index; i++) {
                copy.poll();
                copy.poll();
            }
        }

        if (index == 0) {
            // pop the first index pair
            if (copy.peek() != null)
                System.out.print("    " + ++index + ".  " + copy.poll());
            if (copy.peek() != null)
                System.out.print("          " + copy.poll());
        } else if (index >= 1) {
            // pop the desired index pair
            if (copy.peek() != null)
                System.out.print("    " + ++index + ".  " + copy.poll());
            if (copy.peek() != null)
                System.out.print("          " + copy.poll());
        }
    }
}


