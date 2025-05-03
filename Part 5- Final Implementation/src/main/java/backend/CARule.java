package cp317.backend;

import java.util.Stack;

import cp317.gui.components.StateGrid;

public abstract class CARule {
    /*
     * Description: Abstract parent class that provides 
     * the common functionality of state grid setting and
     * state history to both the 1D and 2D rules.
     */
    
    protected StateGrid sg;
    protected Stack<int[][]> history;
    protected int max_history_size = 50;

    public CARule(StateGrid stateGrid) {
        sg = stateGrid;
        history = new Stack<>();
    }

    public int getCurrentState(int x, int y) {
        return sg.getCellState(x, y);
    }

    public void clearHistory() {
        history.clear();
    }

    public abstract void nextStep();
    public abstract void prevStep();
    public abstract void setRule(int[] life_list, int[] death_list); // For 2D rule setting
    public abstract void setRule(int rule); // For 1D rule setting

    protected abstract int getNewState(int x, int y);

    protected int[][] copyMatrix(int[][] mat) {
        if (mat == null) {
            return null;
        }

        int[][] new_mat = new int[mat.length][mat[0].length];
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                new_mat[i][j] = mat[i][j];
            }
        }

        return new_mat;
    }

}
