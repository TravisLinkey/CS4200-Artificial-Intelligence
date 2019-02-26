import java.util.Stack;

public class PuzzleStats {
    Heuristic_Data h1 = new Heuristic_Data();
    Heuristic_Data h2 = new Heuristic_Data();

    class Heuristic_Data {
        int solution_depth;
        int nodes_generated;;
        Stack<Puzzle> solution_path;
        long process_time;


        Heuristic_Data() {
            solution_path = new Stack<>();
        }
    }
}
