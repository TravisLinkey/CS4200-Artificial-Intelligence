import java.sql.Time;
import java.text.DecimalFormat;
import java.util.*;

public class Board {
    Node current_board;
    Node next_board;
    int dimension;
    int k = 100;
    double mutation_chance=0.3;
    int current_conflicts;
    int current_depth;
    int top_percent = 30;
    PriorityQueue<Node> old_population;
    Random rand = new Random();

    Board(int dimension) {
        this.dimension = dimension;
        current_board = new Node();
        next_board = new Node();
    }

    // Algorithm functions
    public Node simulated_annealing() {
        int count = 0;
        // decreasing constant
        double dec_percent = 0.9997;
        // Initial temperature
        double T = 1;
        // Final temp
        double T_min = 0.0001;
        int next_conflicts;

        // initialize array with random array
        current_board = get_rand_node();
        current_conflicts = get_attacking_pairs(current_board.current_state);

        while(T > T_min)
        {
            if(current_conflicts==0)
            {
                print_board(current_board.current_state);
                this.current_depth += count;
                return current_board;
            }

            count++;
            T *= dec_percent;

            // Next potential state
            System.arraycopy(current_board.current_state, 0, next_board.current_state, 0, dimension);

            // Move one index randomly
            int index = rand.nextInt(dimension);
            int value = rand.nextInt(dimension)+1;

            // If next value is the same as last value, try again
            while(value == current_board.current_state[index])
                value = rand.nextInt(dimension)+1;

            // change next state at a random position
            next_board.current_state[index] = value;
            next_conflicts = get_attacking_pairs(next_board.current_state);

            double e = current_conflicts - next_conflicts;

            // check if next solution is fitter
            if (e>0) {
                // better solution
                current_board.current_state[index] = value;
            }
            else {

                // cooling function
                double ap = Math.pow(Math.E, (e/ T));

                // if random number is less than cooling function
                if (ap > Math.random()) {
                    current_board.current_state[index] = value;
                }
                current_conflicts = get_attacking_pairs(current_board.current_state);
            }

        }

        this.current_depth += count;

        return current_board;
    }
    public Node genetic_algorithm() {
        //Comparator<Node> comparator = new genetic_comparator();
        Node solution = new Node();
        int top_fitness = 0;
        int t_max=(dimension*(dimension-1)/2);
        int count=0;

        // 1. Initial pop - Initial population of random candidates
        old_population = new PriorityQueue<Node>(boardComparator);
        PriorityQueue<Node> new_population = new PriorityQueue<Node>(boardComparator);

//        System.out.println("T MAX: " + t_max);

        // Fill population with random arrays
        old_population.clear();
        for (int i = 0; i < k; i++) {
            Node new_node = new Node();
            old_population.add(new_node);
        }

        while (top_fitness < t_max) {

            // loop through old population, creating new population
            for (int i = 0; i < k/2; i++) {
                Node child_1 = new Node();
                Node child_2 = new Node();

                count++;

                Node[] parents = new Node[2];
                parents[0] = new Node();
                parents[1] = new Node();

                PriorityQueue<Node> population_copy = new PriorityQueue<>(old_population);
                PriorityQueue<Node> population_copy_2 = new PriorityQueue<>(old_population);

                parents[0] = plain_selection(population_copy);
                parents[1] = plain_selection(population_copy_2);

//                parents[0] = roulette_selection(population_copy);
//                parents[1] = roulette_selection(population_copy_2);

                int split_index = rand.nextInt(dimension-1) + 1;
                child_1 = get_single_crossover(parents[0], parents[1], split_index);
                child_2 = get_single_crossover(parents[1], parents[0], split_index);

//                child_1 = mutate_1(child_1, mutation_chance);
//                child_2 = mutate_1(child_2, mutation_chance);

                child_1 = mutate_2(child_1, mutation_chance);
                child_2 = mutate_2(child_2, mutation_chance);

                child_1.non_attacking_pairs=get_nonattacking_pairs(child_1.current_state);
                child_2.non_attacking_pairs=get_nonattacking_pairs(child_2.current_state);

                // add child to new population
                new_population.add(child_1);
                new_population.add(child_2);
                top_fitness = get_nonattacking_pairs(new_population.peek().current_state);
            }
            // old pop = new population
            set_equal(new_population);

//            System.out.println("Top fitness: " + top_fitness);
//            print_board(old_population.peek().current_state);
            new_population.clear();
        }

        solution = old_population.peek();
        print_board(solution.current_state);

        System.out.println("Iteration Count: " + count);
        this.current_depth += count;

        return solution;
    }

    // Algorithm Selection/Mutation Functions
    private Node roulette_selection(PriorityQueue<Node> old_population) {
        double sum = 0;
        Random random = new Random();
        Node[] numbers = new Node[k];
        Node parent = new Node();
        int[] conflicts = new int[k];
        double[] percents = new double[k];

        // initialize Node[]
        for (int i = 0; i < top_percent; i++) {
            numbers[i] = new Node();
        }

        // get sum and numbers
        for (int i = 0; i < top_percent; i++) {
            numbers[i].current_state = new int[dimension];
            numbers[i].current_state = old_population.peek().current_state;
            conflicts[i] = get_nonattacking_pairs(old_population.poll().current_state);
            sum += conflicts[i];
        }

        // get percents
        for (int i = 0; i < top_percent; i++) {
            if(i==0)
                percents[i] = conflicts[i]/sum;
            else
                percents[i] = conflicts[i]/sum + percents[i-1];
        }
        double roulette_value = Math.random();
        boolean done = false;
        int index = 0;

        while(!done) {
            if(roulette_value <= percents[index])
            {
                done = true;
                parent = numbers[index];
            }
            index++;
        }

        return parent;
    }
    private Node plain_selection(PriorityQueue<Node> old_population) {
        Random random = new Random();
        Node[] parents = new Node[2];
        parents[0] = new Node();
        parents[1] = new Node();

        int roulette_value = random.nextInt(top_percent)-1;

        for (int j = 0; j < roulette_value; j++) {
            old_population.poll();
        }

        return old_population.poll();
    }
    private Node mutate_1(Node child, double mutation_chance) {
        // chance of mutation
        if (Math.random() <= mutation_chance) {
            int index = rand.nextInt(dimension);
            int value = rand.nextInt(dimension) + 1;

            int temp = child.current_state[index];
            while (child.current_state[index] == value)
                value = rand.nextInt(dimension) + 1;

            child.current_state[index] = value;
        }

        return child;
    }
    private Node mutate_2(Node child, double mutation_chance) {

        // chance of mutation
        if (Math.random() <= (mutation_chance*2)) {
            int index = rand.nextInt(dimension);
            int value = rand.nextInt(dimension) + 1;

            int temp = child.current_state[index];
            while (child.current_state[index] == value)
                value = rand.nextInt(dimension) + 1;

            child.current_state[index] = value;

            if (Math.random() <= mutation_chance) {
                index = rand.nextInt(dimension);
                value = rand.nextInt(dimension) + 1;

                temp = child.current_state[index];
                while (child.current_state[index] == value)
                    value = rand.nextInt(dimension) + 1;

                child.current_state[index] = value;
            }
        }

        return child;
    }

    // Board misc functions
    private void set_equal(PriorityQueue<Node> new_population) {
        old_population.clear();

        for (int i = 0; i < k; i++) {
            this.old_population.add(new_population.poll());
        }
    }
    public int get_attacking_pairs(int[] state) {
        int num_conflicts = 0;

        for (int i = 0; i < dimension-1; i++) {
            for (int j = i+1; j < dimension; j++) {
                // row conflict
                if(state[i] == state[j])
                    num_conflicts++;

                // col conflict
                if(Math.abs(i-j) == Math.abs(state[i] - state[j]))
                    num_conflicts++;
            }
        }
        return num_conflicts;
    }
    public int get_nonattacking_pairs(int[] state) {
        int num_conflicts = 0;

        for (int i = 0; i < dimension-1; i++) {
            for (int j = i+1; j < dimension; j++) {
                // row conflict
                if(state[i] == state[j])
                    num_conflicts++;

                // col conflict
                if(Math.abs(i-j) == Math.abs(state[i] - state[j]))
                    num_conflicts++;
            }
        }
        return (dimension*(dimension-1)/2)-num_conflicts;
    }
    private Node get_single_crossover(Node parent_1, Node parent_2, int crossover_index) {
        Node child = new Node();

        for (int i = 0; i < crossover_index; i++) {
            child.current_state[i] = parent_1.current_state[i];
        }

        for (int i = crossover_index; i < dimension; i++) {
            child.current_state[i] = parent_2.current_state[i];
        }

        return child;
    }
    private Node get_rand_node() {
        Node rand_node = new Node();
        Random rand = new Random();

        for (int i = 0; i < dimension; i++) {
            rand_node.current_state[i] = rand.nextInt(dimension)+1;
        }
        return rand_node;
    }
    public void numerous_runs(int iterations, int algorithm_choice) {
        long iteration_startTime = 0, iteration_endTime = 0, beginTime = 0, endTime = 0;
        long duration;
        double avg_solved = 0.0;
        double total_time_taken = 0.0;
        int total_depth = 0;
        Node temp_solution = new Node();
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        System.out.println("Running " + iterations + " times");

        switch(algorithm_choice)
        {
            case 1:
            {
                beginTime = System.nanoTime();
                for (int i = 0; i < iterations; i++) {
                    System.out.println("Board #" + i);
                    iteration_startTime = System.nanoTime();
                    temp_solution = simulated_annealing();
                    total_depth = current_depth;
                    avg_solved++;
                    iteration_endTime = System.nanoTime();
                    duration = (iteration_endTime - iteration_startTime)/1000000;
                    System.out.println("Time taken: " + duration + "ms");
                    System.out.println();
                }
                System.out.println("ANNEALING ALGORITHM");
                break;
            }
            case 2:
            {
                beginTime = System.nanoTime();
                for (int i = 0; i < iterations; i++) {
                    System.out.println("Board #" + i);
                    iteration_startTime = System.nanoTime();
                    temp_solution = genetic_algorithm();
                    total_depth = current_depth;
                    avg_solved++;
                    iteration_endTime = System.nanoTime();
                    duration = (iteration_endTime - iteration_startTime)/1000000;
                    System.out.println("Time taken: " + duration + "ms");
                    System.out.println();
                }
                System.out.println("GENETIC ALGORITHM");
                break;
            }
        }
        endTime = System.nanoTime();
        total_time_taken = (endTime-beginTime)/1000000;
        avg_solved = avg_solved/iterations;
        double avg_time = total_time_taken/iterations;
        int avg_depth = total_depth/iterations;

        System.out.println("Queen Count: "+dimension);
        System.out.println("Solution percentage: " + avg_solved);
        System.out.println("Total time taken: " + decimalFormat.format(total_time_taken/1000) + " seconds");
        System.out.println("Average solution time: " + decimalFormat.format(avg_time/1000) + " seconds");
        System.out.println("Average solution depth: " + avg_depth);
        System.out.println();

        // reset current_depth to zero
        this.current_depth = 0;
    }

    // Board state class
    class Node {
        int[] current_state;
        int attacking_pairs;
        int non_attacking_pairs;

        Node() {
            this.current_state = new int[dimension];
            randomize();
            attacking_pairs = get_attacking_pairs(current_state);
            non_attacking_pairs = get_nonattacking_pairs(current_state);
        }

        Node(int[] state) {
            this.current_state = new int[dimension];
            System.arraycopy(state,0,current_state,0,dimension);
            this.attacking_pairs = get_attacking_pairs(current_state);
            this.non_attacking_pairs = (dimension*dimension-1)/2 - this.attacking_pairs;
        }
        private void randomize() {
            for (int i = 0; i < dimension; i++) {
                current_state[i] = (int)(Math.random()*dimension);
            }
        }

    }
    Comparator<Node> boardComparator = (b1,b2) -> {
        return (b2.non_attacking_pairs)-(b1.non_attacking_pairs);
    };

    // Printing Functions
    public void print_board(int[] board) {
        for (int i = 0; i < dimension; i++) {
            System.out.print(board[i] + " ");
        }
        System.out.println("Fitness: " + get_attacking_pairs(board));
    }

    // Unused
    int random_selection (int[] population) {
        Random rand = new Random();
        double[] averages = new double[dimension+1];
        int sum = 0;

        // get sum
        for(int i : population)
            sum += i;

        // get percentage of each index, swapping first and last
        for(int i = 1; i<dimension+1; i++)
        {
            averages[dimension-i] = population[i-1]*100/sum;
            System.out.println("Index: " + (dimension-i) + "     " + averages[dimension-i]);
        }

        // choose candidate
        int curr_percentage = 0;
        int random = rand.nextInt(100)+1;

        System.out.println("Random number: " + random);
        for(int i = 0; i<dimension; i++)
        {
            curr_percentage += averages[i];
            System.out.println("Current percentage: " + curr_percentage);

            if(random <= curr_percentage)
            {
                System.out.println("Chose " + population[i]);
                return population[i];
            }
        }

        return 1;
    }
    private void move_column() {
        Random rand = new Random();
        int[] new_state = new int[dimension];

        for (int i = 0; i < dimension; i++) {
            {
                // 50% to do something for each index
                if(rand.nextInt(2) == 1)
                {
                    // check boundary conditions => cant move down if == 0
                    if(rand.nextInt(2) == 1 && this.next_board.current_state[i] > 0)
                    {
                        // Move down row
                        this.next_board.current_state[i] = this.next_board.current_state[i] - 1;
                    }
                    // check boundary conditions => cant move up if == size
                    else if(this.next_board.current_state[i] < dimension)
                    {
                        // Move up row
                        this.next_board.current_state[i] = this.next_board.current_state[i] + 1;
                    }
                }
            }
        }
    }
    private int[] get_rand_board() {
        int[] random_board = new int[dimension];
        Random rand = new Random();
        for (int i = 0; i < dimension; i++) {
            random_board[i] = rand.nextInt(dimension) + 1;
        }
        return random_board;
    }
    private int[][] rank_for_fitness(int[][] new_pop) {
        for (int i = 0; i < dimension-1; i++) {
            for (int j = i+1; j < dimension; j++) {
                if(get_attacking_pairs(new_pop[j]) < get_attacking_pairs(new_pop[i]))
                {
                    int[] temp = new_pop[i];
                    new_pop[i] = new_pop[j];
                    new_pop[j] = temp;
                }
            }
        }
        return new_pop;
    }
    private int[] get_double_crossover(int[] parent_1, int[] parent_2, int start, int finish) {
        int[] child = new int[dimension];

        System.arraycopy(parent_1, 0, child, 0, dimension);

        if(start < finish)
        {
            for (int i = start; i < finish; i++) {
                child[i] = parent_1[i];
            }
        }
        else
        {
            for (int i = finish; i < start; i++) {
                child[i] = parent_1[i];
            }
        }


        return child;
    }
    private Node get_bottom(PriorityQueue<Node> population_copy) {
        for (int i = 0; i <k-2; i++) {
            population_copy.poll();
        }
        return population_copy.poll();
    }
    public class genetic_comparator implements Comparator<Node> {

        public int compare(Node a, Node b) {

            int conflicts_a = get_nonattacking_pairs(a.current_state);
            int conflicts_b = get_nonattacking_pairs(b.current_state);

            if(conflicts_a < conflicts_b)
                return 1;
            else if(conflicts_a > conflicts_b)
                return -1;
            else
                return 0;
        }
    }
}
