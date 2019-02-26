import java.util.Scanner;
import java.util.Stack;

public class UI {
    private Scanner input = new Scanner(System.in);
    private Engine AI_machine = new Engine();

    UI() {
        main_menu();
    }
    public void main_menu() {
        boolean quit = false;

        do{

            System.out.println("Please select an option below:");
            System.out.println("    [1] Enter Puzzle Sequence");
            System.out.println("    [2] Generate Random Puzzle");
            System.out.println("    [3] 1000 Random Puzzles");
            System.out.println("    [4] User Play");
            System.out.println("    [5] Quit");

            int ans_1 = input.nextInt();
            input.nextLine();

//            System.out.println("Choose Heuristic :");
//            System.out.println("    [1] Misplaced Tiles");
//            System.out.println("    [2] Manhattan Distance");
//            int ans_2 = input.nextInt();
//            input.nextLine();

            switch(ans_1)
            {
                case 1:
                {
                    PuzzleStats user_stats = new PuzzleStats();
                    // Get user input sequence
                    int[] user_sequence = get_user_sequence();
                    Puzzle user_puzzle = new Puzzle(user_sequence);

                    user_stats = AI_machine.solve_puzzle(user_puzzle);
                    AI_machine.print_path(user_stats);

                    break;
                }
                case 2:
                {
                    // Create Random Puzzle
                    Puzzle test_puzzle = AI_machine.generate_random();
                    test_puzzle.print_puzzle();
                    AI_machine.solve_puzzle(test_puzzle);
                    break;
                }
                case 3:
                {
                    AI_machine.generate_random_array();
                    break;
                }
                case 4:
                {
                    AI_machine.let_user_play();
                    break;
                }
                case 5:
                {
                    quit = true;
                    break;
                }
            }

        } while(!quit);
    }
    int[] get_user_sequence() {
        boolean valid_sequence = false;
        String[] full_message;

        do {
            System.out.println("Please input a valid sequence:");
            String input_string = input.nextLine();
            full_message = input_string.split("\\s+");
            valid_sequence = check_sequence(full_message);
        } while (!valid_sequence);

        int[] user_sequence = new int[9];
        for (int i = 0; i < 9; i++) {
            user_sequence[i] = Integer.valueOf(full_message[i]);
        }

        return user_sequence;
    }
    private boolean check_sequence(String[] message) {
        int[] new_sequence = new int[9];
        int index = 0;
        for (String s:
                message) {


            // if too many inputs
            if(index>8)
            {
                System.out.println("Sequence must be 8 integers. [0,1,2...]");
                return false;
            }

            try{
                int value = Integer.valueOf(s);

                // if repeats
                new_sequence[index] = value;
                if(index > 0)
                {
                    for (int i = 0; i < index; i++) {
                        if(new_sequence[i] == value)
                        {
                            System.out.println("repeated value:" + new_sequence[i] + "and" + value);
                            System.out.println("Sequence has repeated value(s)");
                            return false;
                        }
                    }
                }

                if(value < 0 || value > 8)
                {
                    System.out.println(s + " is outside of valid range [0-8]");
                    return false;
                }
                index++;

            } catch (NumberFormatException e)
            {
                System.out.println(s+" is not a number!");
                return false;
            }
        }

        if(index < 8)
        {
            System.out.println("Input sequence must be 9 integers long");
            return false;
        }

        return true;
    }
}