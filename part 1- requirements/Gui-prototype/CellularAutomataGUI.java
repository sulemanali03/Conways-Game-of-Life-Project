import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CellularAutomataGUI {
    private static final int GRID_SIZE = 256; // 256x256 grid for 1D cellular automata
    private static final int CELL_SIZE = 4;  // Size of each cell in pixels

    private JFrame frame;
    private JPanel gridPanel;
    private JButton startButton;
    private JButton pauseButton;
    private JButton resetButton;
    private JButton nextStepButton;
    private JSpinner ruleSpinner;
    private Timer timer;

    private boolean[][] currentState;
    private boolean[][] nextState;

    public CellularAutomataGUI() {
        initializeGUI();
        initializeStates();
    }

    private void initializeGUI() {
        frame = new JFrame("1D Cellular Automata");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Control Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        ruleSpinner = new JSpinner(new SpinnerNumberModel(30, 0, 255, 1));
        controlPanel.add(new JLabel("Rule:"));
        controlPanel.add(ruleSpinner);

        startButton = new JButton("Start");
        controlPanel.add(startButton);

        pauseButton = new JButton("Pause");
        controlPanel.add(pauseButton);

        resetButton = new JButton("Reset");
        controlPanel.add(resetButton);

        nextStepButton = new JButton("Next Step");
        controlPanel.add(nextStepButton);

        frame.add(controlPanel, BorderLayout.NORTH);

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

        frame.add(new JScrollPane(gridPanel), BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);

        // Event Listeners
        startButton.addActionListener(e -> startSimulation());
        pauseButton.addActionListener(e -> pauseSimulation());
        resetButton.addActionListener(e -> resetSimulation());
        nextStepButton.addActionListener(e -> nextStep());
    }

    private void initializeStates() {
        currentState = new boolean[GRID_SIZE][GRID_SIZE];
        nextState = new boolean[GRID_SIZE][GRID_SIZE];
        resetSimulation();
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

        if (y == 0 && x >= 0 && x < GRID_SIZE) { // Only allow clicking on the first row
            currentState[y][x] = !currentState[y][x];
            gridPanel.repaint();
        }
    }

    private void startSimulation() {
        timer = new Timer(100, e -> nextStep());
        timer.start();
    }

    private void pauseSimulation() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void resetSimulation() {
        pauseSimulation();
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                currentState[y][x] = false;
            }
        }
        gridPanel.repaint();
    }

    private void nextStep() {
        int rule = (int) ruleSpinner.getValue();

        for (int y = 1; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                int left = (x == 0) ? 0 : (currentState[y - 1][x - 1] ? 1 : 0);
                int center = currentState[y - 1][x] ? 1 : 0;
                int right = (x == GRID_SIZE - 1) ? 0 : (currentState[y - 1][x + 1] ? 1 : 0);

                int neighborhood = (left << 2) | (center << 1) | right;
                nextState[y][x] = (rule & (1 << neighborhood)) != 0;
            }
        }

        for (int y = 0; y < GRID_SIZE; y++) {
            System.arraycopy(nextState[y], 0, currentState[y], 0, GRID_SIZE);
        }

        gridPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CellularAutomataGUI::new);
    }
}
