import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.util.Stack;

import javax.swing.JPanel;

/* ======================================================
   CellularAutomataGrid Class
   ====================================================== */
public class CellularAutomataGrid extends JPanel {
	
	
	private Color aliveColor = Color.BLACK;
	private Color deadColor = Color.WHITE;

	// Add getters for the current colors.
	public Color getAliveColor() {
	    return aliveColor;
	}

	public Color getDeadColor() {
	    return deadColor;
	}

	// Add setters to update the colors.
	public void setAliveColor(Color color) {
	    this.aliveColor = color;
	    repaint();
	}

	public void setDeadColor(Color color) {
	    this.deadColor = color;
	    repaint();
	}
	
    /* -------------------------------
       Constants & Variables
       ------------------------------- */
    private int gridSize = 58;
    private static final int PANEL_SIZE = 600;
    private int cellSize = PANEL_SIZE / gridSize;
    private boolean[][] currentState;
    private Stack<boolean[][]> history;
    
    /* -------------------------------
       Game State Enum & Variables
       ------------------------------- */
    public enum GameStatus {
        NEW, STARTED
    }
    private GameStatus gameStatus;

    /* Getter & Setter for Game Status */
    public String getGameStatus() {
        return gameStatus.toString();
    }
    
    public void setGameStatus(GameStatus newStatus) {
        GameStatus oldStatus = this.gameStatus;
        this.gameStatus = newStatus;
        firePropertyChange("gameStatus", oldStatus, newStatus);
    }
    
    /* -------------------------------
       Display Format Enum & Variables
       ------------------------------- */
    public enum DisplayFormat {
        BLACK_WHITE, NUMBER
    }
    private DisplayFormat displayFormat = DisplayFormat.BLACK_WHITE; // Default format

    public void setDisplayFormat(DisplayFormat format) {
        this.displayFormat = format;
        repaint();
    }
    
    /* -------------------------------
       1D Mode Variables
       ------------------------------- */
    private boolean oneDMode = false;
    private int activeRow = 0; // Tracks the current active row in 1D mode

    /* ======================================================
       Constructor & Initialization
       ====================================================== */
    public CellularAutomataGrid() {
        history = new Stack<>();
        setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
        initializeStates();
        
        // Add mouse listener for grid interaction.
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleGridClick(e);
            }
        });
    }
    
    /* -------------------------------
       Grid Size Setter
       ------------------------------- */
    public void setGridSize(int newSize) {
        this.gridSize = newSize;
        cellSize = PANEL_SIZE / gridSize; // Recalculate cell size based on fixed PANEL_SIZE
        initializeStates();                // Reinitialize grid with new dimensions
        setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE)); // Panel size remains fixed
        revalidate();
        repaint();
    }
    
    /* -------------------------------
       Grid Initialization Methods
       ------------------------------- */
    
    // Initializes the grid with all cells set to dead (false)
    public void initializeStates() {
        currentState = new boolean[gridSize][gridSize];
        history.clear();                     // Clear history stack on reset
        history.push(copyGrid(currentState));  // Store initial grid state
        gameStatus = GameStatus.NEW;           // Set status to NEW
        repaint();                           // Redraw grid
    }
    
    // Copy the current grid state (to save it for history)
    private boolean[][] copyGrid(boolean[][] grid) {
        boolean[][] copy = new boolean[gridSize][gridSize];
        for (int y = 0; y < gridSize; y++) {
            System.arraycopy(grid[y], 0, copy[y], 0, gridSize);
        }
        return copy;
    }

    // Returns a copy of the current state matrix
    public boolean[][] getStateMatrix() {
        return copyGrid(currentState);
    }

    public void setStateMatrix(boolean[][] state_matrix) {
        if (state_matrix != null && state_matrix.length == state_matrix[0].length) {
            currentState = copyGrid(state_matrix);
            history.clear();
            history.push(copyGrid(currentState));
            gameStatus = GameStatus.NEW;           // Set status to NEW
            repaint();                           // Redraw grid
        }
    }
    
    /* ======================================================
       Rendering Methods
       ====================================================== */
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
    }
    
    // Draws the grid based on the current display format.
    private void drawGrid(Graphics g) {
    	if (displayFormat == DisplayFormat.BLACK_WHITE) {
            for (int y = 0; y < gridSize; y++) {
                for (int x = 0; x < gridSize; x++) {
                    if (currentState[y][x]) {
                        g.setColor(aliveColor);
                        g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                    } else {
                        g.setColor(deadColor);
                        g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                    }
                }
            }
        } else if (displayFormat == DisplayFormat.NUMBER) {
            // (Keep the existing logic for number display.)
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.BLACK);
            Font originalFont = g.getFont();
            Font numberFont = originalFont.deriveFont(Font.BOLD, cellSize / 2);
            g.setFont(numberFont);
            for (int y = 0; y < gridSize; y++) {
                for (int x = 0; x < gridSize; x++) {
                    String text = currentState[y][x] ? "1" : "0";
                    int cellX = x * cellSize;
                    int cellY = y * cellSize;
                    g.drawString(text, cellX + cellSize / 4, cellY + (cellSize * 3) / 4);
                }
            }
            g.setFont(originalFont);
        }
    }
    
    /* ======================================================
       Event Handling
       ====================================================== */
    
    // Toggle the cell state on mouse click.
    private void handleGridClick(MouseEvent e) {
        int x = e.getX() / cellSize;
        int y = e.getY() / cellSize;
        
        if (x >= 0 && x < gridSize && y >= 0 && y < gridSize) {
            currentState[y][x] = !currentState[y][x];
            if (gameStatus == GameStatus.NEW) {
                setGameStatus(GameStatus.STARTED);
            }
            repaint();
        }
    }
    
    /* ======================================================
       Simulation Methods
       ====================================================== */
    
    // Enable or disable 1D mode and reset the grid accordingly.
    public void setOneDMode(boolean flag) {
        this.oneDMode = flag;
        activeRow = 0;      // Reset active row when switching modes.
        initializeStates();
    }
    
    // Advances the simulation by one generation.
    public void nextGeneration() {
        if (oneDMode) {
            processNextGeneration1D();
            return;
        }
        
        // Process 2D cellular automata logic.
        boolean[][] newState = new boolean[gridSize][gridSize];
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                int liveNeighbors = countLiveNeighbors(x, y);
                if (currentState[y][x]) {
                    newState[y][x] = (liveNeighbors == 2 || liveNeighbors == 3);
                } else {
                    newState[y][x] = (liveNeighbors == 3);
                }
            }
        }
        if (gameStatus == GameStatus.NEW) {
            setGameStatus(GameStatus.STARTED);
        }
        history.push(copyGrid(newState));
        currentState = newState;
        repaint();
    }
    
    // Processes one generation in 1D mode using Rule 30.
    private void processNextGeneration1D() {
        boolean[] newRow = new boolean[gridSize];
        
        // Determine new row based on current active row.
        for (int x = 0; x < gridSize; x++) {
            boolean left = (x == 0) ? false : currentState[activeRow][x - 1];
            boolean center = currentState[activeRow][x];
            boolean right = (x == gridSize - 1) ? false : currentState[activeRow][x + 1];
            int pattern = (left ? 4 : 0) + (center ? 2 : 0) + (right ? 1 : 0);
            
            // Rule 30 mapping:
            // 111->0, 110->0, 101->0, 100->1, 011->1, 010->1, 001->1, 000->0.
            switch (pattern) {
                case 7: newRow[x] = false; break; // 111
                case 6: newRow[x] = false; break; // 110
                case 5: newRow[x] = false; break; // 101
                case 4: newRow[x] = true;  break; // 100
                case 3: newRow[x] = true;  break; // 011
                case 2: newRow[x] = true;  break; // 010
                case 1: newRow[x] = true;  break; // 001
                case 0: newRow[x] = false; break; // 000
            }
        }
        
        // Insert the new row into the grid.
        if (activeRow < gridSize - 1) {
            for (int x = 0; x < gridSize; x++) {
                currentState[activeRow + 1][x] = newRow[x];
            }
            activeRow++; // Advance active row.
        } else {
            // If at the bottom, scroll the grid upward.
            for (int row = 0; row < gridSize - 1; row++) {
                currentState[row] = currentState[row + 1];
            }
            currentState[gridSize - 1] = newRow;
        }
        
        if (gameStatus == GameStatus.NEW) {
            setGameStatus(GameStatus.STARTED);
        }
        history.push(copyGrid(currentState));
        repaint();
    }
    
    /* ======================================================
       Utility Methods
       ====================================================== */
    
 // Returns the total number of alive cells in the grid.
    public int getAliveCount() {
        int count = 0;
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                if (currentState[y][x]) {
                    count++;
                }
            }
        }
        return count;
    }

    // Getter for the current grid size.
    public int getGridSize() {
        return gridSize;
    }
    
    
    
    // Counts the number of live neighbors for a given cell.
    private int countLiveNeighbors(int x, int y) {
        int liveNeighbors = 0;
        // Check all 8 surrounding neighbors.
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue; // Skip self
                
                int neighborX = x + i;
                int neighborY = y + j;
                
                if (neighborX >= 0 && neighborX < gridSize && neighborY >= 0 && neighborY < gridSize) {
                    if (currentState[neighborY][neighborX]) {
                        liveNeighbors++;
                    }
                }
            }
        }
        return liveNeighbors;
    }
    
    // Randomizes the grid cells (alive or dead) for a new initial state.
    public void initializeRandomGrid() {
        Random rand = new Random();
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                currentState[y][x] = rand.nextBoolean();
            }
        }
        gameStatus = GameStatus.STARTED;
        repaint();
    }
    
    // Reverts the grid to the previous simulation step.
    public void previousStep() {
        if (!history.isEmpty()) {
            history.pop();  // Remove current state
            if (!history.isEmpty()) {
                currentState = history.peek();  // Retrieve previous state
                repaint();
            }
        }
    }
    
    // Resets the grid to its initial state (all cells dead).
    public void resetGrid() {
        initializeStates();
    }
}
