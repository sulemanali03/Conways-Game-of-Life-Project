package cp317.gui.components;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cp317.gui.MainFrame;

public class StatisticsPanel extends TabParentClass {
    /*
     * Description: Tab class that provides some basic statistics
     * about the simulation to the user.
     */
    
    // Attributes
    public JLabel deadCellsLabel = new JLabel("Dead Cells: 0");
    public JLabel aliveCellsLabel = new JLabel("Alive Cells: 0");
    public JLabel populationLabel = new JLabel("Population: 0");
    public JLabel percentageLabel = new JLabel("Percentage of Grid Used: 0%");


    // Contructor
    public  StatisticsPanel(MainFrame mainFrame) {
        title = "Statistics";

        JPanel statisticsPanel = new JPanel();
        statisticsPanel.setLayout(new BoxLayout(statisticsPanel, BoxLayout.Y_AXIS));
        // Add padding between labels.
        deadCellsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        aliveCellsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        populationLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        percentageLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        statisticsPanel.add(deadCellsLabel);
        statisticsPanel.add(aliveCellsLabel);
        statisticsPanel.add(populationLabel);
        statisticsPanel.add(percentageLabel);
        this.add(statisticsPanel);
        
        //update stats
        updateStats(mainFrame.stateGrid);

    }

    // Public methods
    public int calculateAliveCells(StateGrid gridPanel){
        int size = gridPanel.getGridSize();
	    int alive_cells = 0;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (gridPanel.getCellState(x, y) == 1) {
                    alive_cells++;
                }
            }
        }
        return alive_cells;
    }

    public final void updateStats(StateGrid gridPanel){
        int alive_cells = calculateAliveCells(gridPanel);
        int total_cells = gridPanel.getGridSize() * gridPanel.getGridSize();
        int dead_cells = total_cells - alive_cells;
        double population_percent = (alive_cells / (double) total_cells) * 100;

        deadCellsLabel.setText("Dead Cells: " + dead_cells);
	    aliveCellsLabel.setText("Alive Cells: " + alive_cells);
	    populationLabel.setText("Population: " + alive_cells);
	    percentageLabel.setText(String.format("Percentage of Grid Used: %.2f%%", population_percent));
    }

}
