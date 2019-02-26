import java.util.Scanner;

public class UI {
    Scanner input;
    Board board;
    int dim;

    UI() {
        input = new Scanner(System.in);
        display_menu();
    }
    public void display_menu() {
        long beginTime = 0, endTime = 0, timeTaken = 0;
        boolean quit = false;

        dim = get_dimension();
        int[] solution = new int[dim];
        board = new Board(dim);

        while(!quit) {
            System.out.println("Select Algorithm:");
            System.out.println("    [1]: Simulated Annealing");
            System.out.println("    [2]: Genetic Algorithm");
            System.out.println("    [3]: Iterative Test");
            System.out.println("    [4]: Quit");

            int ans = input.nextInt();

            switch(ans) {
                case 1:
                    System.out.println("SIMULATED ANNEALING");
                    beginTime = System.nanoTime();
                    solution = board.simulated_annealing().current_state;
                    endTime = System.nanoTime();
                    print_board(solution);
                    timeTaken = (endTime - beginTime)/1000000;
                    System.out.println("Time Taken: " + timeTaken + " ms");
                    break;
                case 2:
                    System.out.println("GENETIC ALGORITHM");
                    solution = board.genetic_algorithm().current_state;
                    print_board(solution);
                    timeTaken = (endTime - beginTime)/1000000;
                    System.out.println("Time Taken: " + timeTaken + " ms");
                    break;
                case 3:
                    int alg = get_alg_used();
                    board.numerous_runs(500,alg);
                    break;
                case 4:
                    quit = true;
                    break;
                default:
                {
                    System.out.println("Input error. Input must be an integer [1-3]");
                }
            }
        }
    }
    private int get_alg_used() {
        int answer;

        System.out.println("Which algorithm should be used?");
        System.out.println("    [1] Simulated Annealing");
        System.out.println("    [2] Genetic Algorithm");
        answer = input.nextInt();
        return answer;
    }
    private void print_board(int[] solution) {
        for (int row = 0; row < dim; row++) {
            System.out.print(row);
            System.out.print("|");
            for (int col = 0; col < dim; col++) {
                if(row==0)
                    System.out.print(col+" ");
                else
                {
                    // if solution[col] == row;
                    if( solution[col] == row )
                        System.out.print("Q ");
                    else
                        System.out.print("  ");
                }
            }
            System.out.println("|");
        }
    }
    private int get_dimension() {
        boolean valid = false;
        int ans=0;

        while(!valid) {
            System.out.println("Please enter a board size.");
            System.out.print("size: ");

            ans = input.nextInt();

            valid = check_size(ans);
        }
        return ans;
    }
    private boolean check_size(int ans) {
        if(ans <= 0)
            return false;
        else if(ans > 40)
            return false;
        else
            return true;
    }
}
