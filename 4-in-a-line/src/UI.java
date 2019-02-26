import java.util.Scanner;

public class UI {
    Scanner input = new Scanner(System.in);

    // Class constructor
    UI() {
        boolean quit = false;
    }

    int main_menu() {
        int ans = 3;
        boolean input_valid = false;

        System.out.println("Please select gameplay type:");
        System.out.println("    [1] Single Player");
        System.out.println("    [2] Multi Player");
        System.out.println("    [3] Exit");

        while(!input_valid) {
            try {
                ans = get_valid_input(1,3,input.next());
                input_valid = true;
            } catch (Invalid_Input e) {}
        }

        return ans;
    }

    // User questions
    String ask_piece_placement(boolean player_number) {
        boolean valid_row=false;
        boolean valid_col=false;
        char row=' ';
        int col=0;

        int player=0;
        if (player_number)
        {
           player = 0;
        }
        else
            player = 1;

        System.out.println("PLAYER " + player + "'s turn.");


        while(!valid_row || !valid_col)
        {

            // invalid row
            while(!valid_row)
            {
                System.out.println("Please input a row [A-H]");
                char input_row = input.next().charAt(0);

                try {
                    row = get_valid_input('A','H', input_row);
                    valid_row = true;
                } catch(Invalid_Input e) {}
            }

            while(!valid_col)
            {
                System.out.println("Please input a collumn [1-8]");
                String input_col = input.next();

                try {
                    col = get_valid_input(1,8, input_col);
                    valid_col = true;
                } catch(Invalid_Input e) {}
            }
        }

        String collumn = Integer.toString(col);
        String move = row + collumn;

        return move;
    }
    int ask_max_time() {
        int ans=0;
        boolean valid_input=false;

        System.out.println("What is maximum wait time?");

        while(!valid_input) {
            String max_time = input.next();
            try {
                ans = get_valid_input(0,25, max_time);
                valid_input = true;
            } catch(Invalid_Input e) {}
        }

        return ans;
    }
    char ask_first_player() {
        char ans=' ';
        boolean valid_input=false;

        System.out.println("Who moves first?");
        System.out.println("    [O] Opponent");
        System.out.println("    [C] Computer");

        while(!valid_input) {
            String first_player = input.next();

            try {
                ans = get_valid_input('C','O', first_player);
                valid_input = true;
            } catch (Invalid_Input e) {}

        }

        return ans;
    }

    // Input validation checks
    private int get_valid_input(int low, int high, String user_input) throws Invalid_Input {

        int user_answer = Character.getNumericValue(user_input.charAt(0));

            if(user_answer >= low && user_answer <= high)
                return user_answer;
            else
            {
                System.out.println("Invalid input. Must be an INTEGER [" + low + "-" + high + "]");
                throw new Invalid_Input();
            }
    }
    private char get_valid_input(char low, char high, char user_answer) throws Invalid_Input {

            if(user_answer >= low && user_answer <= high)
                return user_answer;
            else
            {
                System.out.println("Invalid input. Must be a CHARACTER in the range: [" + low + "-" + high + "]");
                throw new Invalid_Input();
            }
    }
    private char get_valid_input(char first, char second, String user_answer) throws Invalid_Input {

        if(user_answer.charAt(0) == first || user_answer.charAt(0) == second)
            return user_answer.charAt(0);
        else
        {
            System.out.println("Invalid input. Must be a CHARACTER [" + first + " or " + second + "]");
            throw new Invalid_Input();
        }
    }

    // Exception Classes
    class Invalid_Input extends Exception {}
}
