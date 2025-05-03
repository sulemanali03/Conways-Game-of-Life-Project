package cp317.backend;

import cp317.gui.components.StateGrid;

public class CA2Drule extends CARule {
    /*
     * Description: Specific implementaion for 2D cellular 
     * automata rules which takes a life and death list that 
     * each specify the number of neighbour cells required to
     * give a cell life or when the cell dies. A cell's neigbourhood
     * is the 8 cells directly adjacent or kitty corner to it.
     */
    
    private int[] life_list; // Neighbour counts that produce alive cell
    private int[] death_list; // Neighbour counts that produce dead cell
    // All other counts not in the death or live list persist in the state that they are in
    private int[][] neighborhood = null; // A list of pairs that defines relative indicies to include in the neighbourhood
    private int[][] temp_grid = null;

    public CA2Drule(StateGrid stateGrid, int[][] new_neighborhood) {
        super(stateGrid);
        setNeigbourhood(new_neighborhood);
    }

    // Public functions
    @Override
    public void nextStep() {
        int size = sg.getGridSize();
        updateHistory();
        temp_grid = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                temp_grid[i][j] = getNewState(j, i);
            }
        }

        sg.setStateMatrix(temp_grid);
    }

    @Override 
    public void prevStep() {
        // Check if there is any history
        if (history.empty()) {
            System.err.println("End of history reached");
        } else {
            temp_grid = history.pop();
            sg.setStateMatrix(temp_grid);
        }
    }

    @Override 
    protected int getNewState(int x, int y) {
        if (neighborhood == null) {
            System.err.println("ERROR: please create a 2D rule before running the simulation");
            return 0;
        } 
        int total = 0;
        for (int[] pair : neighborhood) {
            total += sg.getCellState(x + pair[0], y + pair[1]);
        }

        if (searchArray(life_list, total) != -1) {
            return 1;
        } else if (searchArray(death_list, total) != -1) {
            return 0;
        } else {
            return sg.getCellState(x, y);
        }
    }

    @Override
    public void setRule(int[] new_life_list, int[] new_death_list) {
        life_list = new int[new_life_list.length];
        System.arraycopy(new_life_list, 0, life_list, 0, new_life_list.length);
        death_list = new int[new_death_list.length];
        System.arraycopy(new_death_list, 0, death_list, 0, new_death_list.length);
    }

    @Override 
    public void setRule(int rule) {
        System.err.println("2D rule should not be set with a rule number");
    }

    // Private functions
    private void setNeigbourhood(int[][] new_neighborhood) {
        // Verifies and sets the rule's neighbourhood definition
        if (new_neighborhood == null || new_neighborhood.length == 0) {
            System.err.println("Invalid neighbourhood given for 2D rule");
        } else {
            // All neighbourhood entries should be a pair of 2 ints
            neighborhood = new int[new_neighborhood.length][2];
            for (int i = 0; i < new_neighborhood.length; i++) {
                if (new_neighborhood[i].length != 2) {
                    System.err.println("ERROR: invalid neighbourhood pair given");
                    neighborhood = null;
                    return;
                }
                System.arraycopy(new_neighborhood[i], 0, neighborhood[i], 0, 2);
            }
        }
        
    }

    private int searchArray(int[] list, int value) {
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                if (list[i] == value) {
                    return i;
                }
            }
        }

        return -1;
    }

    private void updateHistory() {
        if (history.size() >= max_history_size) {
            history.remove(0);
        }

        history.push(sg.copyStateMatrix());
    }
}
