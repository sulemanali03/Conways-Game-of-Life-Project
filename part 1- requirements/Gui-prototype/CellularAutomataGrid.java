import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CellularAutomataGrid {
    private static final int GRID_SIZE = 64; // Reduced grid size
    private static final int CELL_SIZE = 8;  // Increased cell size for visibility

    private JFrame frame;
    private JPanel gridPanel;
    private boolean[][] currentState;

    public CellularAutomataGrid() {
        initializeGUI();
        initializeStates();
    }

    private void initializeGUI() {
        frame = new JFrame("Cellular Automata Grid");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Grid Panel
        gridPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGrid(g);
            }
        };
        
        gridPanel.setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE));
        gridPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleGridClick(e);
            }
        });

        frame.add(gridPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    private void initializeStates() {
        currentState = new boolean[GRID_SIZE][GRID_SIZE];
    }

    private void drawGrid(Graphics g) {
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                if (currentState[y][x]) {
                    g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else {
                    g.clearRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }

    private void handleGridClick(MouseEvent e) {
        int x = e.getX() / CELL_SIZE;
        int y = e.getY() / CELL_SIZE;

        if (x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE) {
            currentState[y][x] = !currentState[y][x];
            gridPanel.repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CellularAutomataGrid::new);
    }
}
