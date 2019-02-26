import java.awt.*;

public class Tile {
    int tile_number;
    int position_on_board;
    int distance_from_goal;

    Tile(int tile_number, int position_on_board) {
        this.tile_number = tile_number;
        this.position_on_board = position_on_board;
        calculate_distance_from_goal();
    }
    void calculate_distance_from_goal() {
        // zero the distance
        distance_from_goal = 0;

        Point start = get_point(position_on_board);
        Point goal = get_point(tile_number);

        Point new_point = new Point(goal.x - start.x, goal.y - start.y);
        distance_from_goal += Math.abs(new_point.x);
        distance_from_goal += Math.abs(new_point.y);
    }
    private Point get_point(int position_on_board) {
        Point new_point = new Point();

        switch (position_on_board)
        {
            case 0:
            {
                new_point.x = 0;
                new_point.y = 0;
                break;
            }

            case 1:
            {
                new_point.x = 0;
                new_point.y = 1;
                break;
            }

            case 2:
            {
                new_point.x = 0;
                new_point.y = 2;
                break;
            }

            case 3:
            {
                new_point.x = 1;
                new_point.y = 0;
                break;
            }

            case 4:
            {
                new_point.x = 1;
                new_point.y = 1;
                break;
            }

            case 5:
            {
                new_point.x = 1;
                new_point.y = 2;
                break;
            }

            case 6:
            {
                new_point.x = 2;
                new_point.y = 0;
                break;
            }

            case 7:
            {
                new_point.x = 2;
                new_point.y = 1;
                break;
            }

            case 8:
            {
                new_point.x = 2;
                new_point.y = 2;
                break;
            }

        }

        return new_point;
    }

}