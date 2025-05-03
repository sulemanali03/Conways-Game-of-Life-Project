package cp317.backend;

import cp317.gui.components.StateGrid;


public class CA1Drule extends CARule{
    /*
     * Description: Specific implementaion of wolfram's 
     * 0-255 1D cellular automata rules. A new CA1Drule
     * is created when the rule number is changed.
     */
    
    private int rule_number; 
    // private StateGrid sg;

    public CA1Drule(StateGrid stateGrid) {
        super(stateGrid);
    }

    // Public functions
    @Override
    public void nextStep() {
        int size = sg.getGridSize();
        int[] new_row = new int[size];

        updateHistory();
        
        for (int i = 0; i < size; i++){
            new_row[i] = getNewState(i, 0);
        }
        
        sg.pushNewRow(new_row, false);

    }

    @Override
    public void prevStep() {
        if (history.isEmpty()) {
            System.err.println("End of histoy reached");
            
        } else {
            int[][] prev_row = history.pop();
            sg.pushNewRow(prev_row[0], true);
        }
    }

    @Override
    protected int getNewState(int x, int y) {
        // Get the 3-bit number value of the state where the left most cell is most sig and left is most sig
        int grid_size = sg.getGridSize();
        int bit_num = 0;
        for (int i = 0; i < 3; i++) {
            bit_num += (int)Math.pow(2, i) * sg.getCellState((x - 1 + i + grid_size) % grid_size, y % grid_size);
        }
        // Now that we have this check if the bit corresponding to bit num in the rule is set to 1
        return getBit(rule_number, bit_num);
    }

    @Override
    public void setRule(int rule) {
        rule_number = rule > 255 ? 255 : rule;
        rule_number = rule;
    }

    @Override
    public void setRule(int[] life_list, int[] death_list) {
        System.err.println("Rule should be set with a rule number");
    }

    // Private functions
    private int getBit(int number, int n) {
        return (number >> n) & 1;
    }

    private void updateHistory() {
        if (history.size() >= max_history_size) {
            history.remove(0);
        }

        int size = sg.getGridSize();
        int[][] new_row = new int[1][size];

        
        for (int i = 0; i < size; i++) {
            new_row[0][i] = sg.getCellState(i, 0);
        } 

        history.push(new_row);
    }

}
