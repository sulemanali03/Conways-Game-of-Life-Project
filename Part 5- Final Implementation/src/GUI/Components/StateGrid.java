package cp317.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JPanel;

import cp317.gui.MainFrame;
import cp317.gui.components.StateGrid.DisplayFormat;

public class StateGrid extends JPanel {
    /*
     * Description: Interactive visual element that displays
     * the state at each step in the simulation and allows the 
     * user to change the inital conditions by clicking or dragging
     * their mouse. This also overrides the paintcomponent method 
     * to update the visual size whenever the state grid is changed
     * in anyway.
     */

    private static final Random random = new Random();

    public String title = "StateGrid";

    // Attributes
    private int fp = 0;
    public int[][] state_matrix = null; // Please note that the state matrix holds indicies of possible states
    private int possible_states = 2;

    // Grid visualization attributes
    private double cell_width;
    private double cell_height;
    private int last_x = -1, last_y = -1;
    public enum DisplayFormat {
        BLACK_WHITE,
        NUMBERS
    };
    private DisplayFormat cur_fmt = DisplayFormat.BLACK_WHITE;

    public Color aliveColor = Color.BLACK;
    public Color deadColor = Color.WHITE;

    private final MainFrame mf;


    // Constructor
    public StateGrid(MainFrame mainFrame) { 
        mf = mainFrame;
        setPreferredSize(new Dimension(mainFrame.getHeight(), mainFrame.getHeight())); // Set the size to always be square
        createMatrix(50); // default size of 50

        // Now add the click handler to the class action handler
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseActive(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Reset the last x and y to out of range values 
                last_x = -1;
                last_y = -1;
            }

        });

        addMouseMotionListener(new MouseMotionAdapter() {
            
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseActive(e);
            }
            
        });

    }

    // Public methods
    public void pushNewRow(int[] row, boolean reverse) {
        /*
         * NOTE: To speed up the row insertion we use a dynamic first row 
         * pointer (fp) to track the location of the first row in the matrix.
         * Consecutive rows follow by incrementing the fp. Meaning that each 
         * we insert a new row it should be inserted at fp - 1 (mod size).
         * This effectively treats the state matrix as a queue.
         */
        if (state_matrix == null) {
            System.err.println("Error: cannot push row to null matrix");
        }
        else if (state_matrix[0].length == row.length) {
            if (reverse) {
                fp = getRowIndex(1);
            } else {
                fp = getRowIndex(-1); // decrement our first pointer by one
            }
            for (int i = 0; i < state_matrix.length; i++) {
                if (row[i] < possible_states) {
                    state_matrix[fp][i] = row[i];
                } else {
                    state_matrix[fp][i] =  0;
                }
            }

            repaint();
        } else {
            System.err.printf("Error: tried to push row with length %d to grid with width %d\n", row.length, state_matrix[0].length);
        }
    }

    public int[][] copyStateMatrix() {
        /*
         * NOTE: this creates a copy of the matrix where row 0 corresponds 
         * to the first row in the state grid so at index fp and then goes 
         * in order from there.
         */
        if (state_matrix == null) {
            return null;
        }

        int[][] copy = new int[state_matrix.length][];
        for (int i = 0; i < state_matrix.length; i++) {
            copy[i] = Arrays.copyOf(state_matrix[getRowIndex(i)], state_matrix[i].length);
        }
        return copy;
    }

    public void setStateMatrix(int[][] new_matrix) {
        if (new_matrix != null && new_matrix.length == new_matrix[0].length) {
            state_matrix = new_matrix;
            fp = 0; // set the first row to the 0th position

            repaint();
        } else {
            System.err.println("Invalid state matrix passed check that it is not null and is square");
        }
    }

    public void setGridSize(int n) {
        if (n < 1 && getGridSize() != n) {
            System.out.printf("Cannot update grid size to %d\n", n);
        } else if (getGridSize() == n) {
            // Do nothing since no change was made
        } else {
            createMatrix(n);
        }
    }

    public int getGridSize() {
        return state_matrix.length;
    }

    public int getCellState(int x, int y) {
        /*
         * NOTE: the cell states (x, y) correspond to position in the grid
         * not the state matrix.
         * Also this gives the actual value that corresponds to the index 
         * in the possible states. These are the values that will be used 
         * in the actual rule execution.
         */
        // if (x >= 0 && x < getGridSize() && y >= 0 && y < getGridSize())
        x = bringIndexInRange(x);
        y = bringIndexInRange(y);

        return state_matrix[getRowIndex(y)][x];
    }

    public void setPossibleStates(int new_possible_states) {
        possible_states = new_possible_states;
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Update the statistics
        if (mf != null && mf.statisticsPanel != null) {
            mf.statisticsPanel.updateStats(this);            
        } else {
            System.err.println("Stats pannel is null");
        }

        super.paintComponent(g);

        if (state_matrix == null) {
            return;
        }

        updateCellSize();

        for (int i = 0; i < getGridSize(); i++) {
            for (int j = 0; j < getGridSize(); j++) {
                paintCell(j, i, g);
            }
        }
    }

    public void paintCell(int x, int y, Graphics g) {
        int state = state_matrix[getRowIndex(y)][x];
        int cellX = (int)Math.round(x * cell_width);
        int cellY = (int)Math.round(y * cell_height);
        if (cur_fmt == DisplayFormat.NUMBERS) {
            g.setColor(deadColor);
            g.fillRect(cellX, cellY, (int)Math.ceil(cell_width), (int) Math.ceil(cell_height));
            g.setColor(aliveColor);
            Font originalFont = g.getFont();
            Font numberFont = originalFont.deriveFont(Font.BOLD, (int)Math.round(cell_height) / 2);
            g.setFont(numberFont);
            String text = String.format("%d", state);
            g.drawString(text, cellX + (int)Math.round(cell_width / 4), cellY + (int)Math.round((cell_height * 3) / 4));
            g.setFont(originalFont);
        } else {
            if (state == 1) {
                g.setColor(aliveColor);
            } else {
                g.setColor(deadColor);
            }
            g.fillRect(cellX, cellY, (int)Math.ceil(cell_width), (int) Math.ceil(cell_height));

        }
    }

    public final void initializeRandomGrid() {
        for (int i = 0; i < getGridSize(); i++) {
            for (int j = 0; j < getGridSize(); j++) {
                state_matrix[i][j] = random.nextInt(possible_states);
            }
        }

        repaint();
    }

    public void setDisplayFormat(DisplayFormat new_fmt) {
        if (new_fmt != cur_fmt) {
            cur_fmt = new_fmt;
            repaint();
        }
    }

    public void resetGrid() {
        for (int i = 0; i < getGridSize(); i++) {
            for (int j = 0; j < getGridSize(); j++ ) {
                state_matrix[i][j] = 0; // Set to the default states
            }
        }

        repaint();
    }

    // Private methods
    private void handleMouseActive(MouseEvent e) {
        // Implementation here
        int x = (int)Math.floor(e.getX() / cell_width);
        int y = (int)Math.floor(e.getY() / cell_height);

        // Prevent changing a cell twice while dragging
        if (last_x != x || last_y != y) {
            last_x = x;
            last_y = y;
            int index = getCellState(last_x, last_y);
            setCellState(last_x, last_y, (index + 1) % possible_states);
        }
    }

    private int bringIndexInRange(int index) {
        // Uses modular arithmetic to bring the index into range [0, size - 1]
        int size = getGridSize();
        return ((index % size) + size) % size; // This handles negative indices
    }
    
    private void setCellState(int x, int y, int index) {
        /*
         * NOTE: the cell states (x, y) correspond to (col, row) position in the grid
         * not the state matrix.
         */

        x = bringIndexInRange(x);
        y = bringIndexInRange(y);

        if (index < possible_states && state_matrix[getRowIndex(y)][x] != index) {
            state_matrix[getRowIndex(y)][x] = index;
            repaint();
        }
    }

    private void createMatrix(int size) {
        /*
         * Creates a matrix of size nxn preserving as much of the current matrix
         * as possible starting at top left corner
         */
        int[][] new_state_matrix = new int[size][size];

        // preserve the old states
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                if (state_matrix != null && i < getGridSize() && j < getGridSize()) {
                    new_state_matrix[i][j] = state_matrix[getRowIndex(i)][j];
                } else {
                    new_state_matrix[i][j] = 0;
                }

            }
        }

        setStateMatrix(new_state_matrix);
    }

    private int getRowIndex(int y) {
        /*
         * For 1D simulations our the first row in the state matrix is not 
         * guaranteed to correspond with the first row in the grid. We treat 
         * the state matrix as a queue of rows and to save on computation we 
         * cycle through the rows using our first pointer fp.
         */

        return (fp + y + getGridSize()) % getGridSize();
    }

    private void updateCellSize() {
        if (getGridSize() > 0) {
            // Calculate cell dimensions
            cell_width = (double) getWidth() / getGridSize();
            cell_height = (double) getHeight() / getGridSize();
        } else {
            cell_width = 1;
            cell_height = 1;
            System.err.println("ERROR tried to set cell width with size = 0");
       }
    }

    public int[][] getStateMatrix(){
        return state_matrix;
    }
}
