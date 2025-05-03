package cp317.gui.components;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import cp317.gui.MainFrame;

public class SettingsPanel extends TabParentClass {

    /*
     * Description: Tab class that contains all the controls
     * for the user to create and set rules, step through the 
     * simulation, and change the state grid. 
     */
    
    // Attributes
    public int rule_dimension = 1;
    public JLabel simulationStateLabel;

    private MainFrame mainFrame;
    public JSpinner ruleSpinner; // For 1D rule settings
    public JTextField lifeText;
    public JTextField deathText;
    private JPanel OneDPanel;
    private JPanel TwoDPanel;
    public JRadioButton rb1D;
    public JRadioButton rb2D;
    public JSlider gridSizeSlider;
    public JSlider speedSlider;
    public JRadioButton rbBlackAndWhite;
    public JRadioButton rbNumberFormat;

    // Added the process and restict the 2D rule settings strings to only
    // numbers and commas
    private class NumberCommaFilter extends DocumentFilter {
        /*
         * Description: Class that provides event handling to the 
         * 2D rule setting life and death lists. It also limits any 
         * input to only commas and numbers to avoid errors within the
         * rule parsing.
         */

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
            throws BadLocationException {
                String newText = fb.getDocument().getText(0, fb.getDocument().getLength())
                    .substring(0, offset) + text + 
                    fb.getDocument().getText(offset + length, 
                    fb.getDocument().getLength() - offset - length);
                    
                if (newText.matches("^[0-9,]*$")) {
                    super.replace(fb, offset, length, text, attrs);
                    setNewRule();
                }
            }

        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) 
                throws BadLocationException {
            replace(fb, offset, 0, text, attr);
        }
    }

    // Contructor
    public SettingsPanel(MainFrame mf) {
        title = "Settings";

        mainFrame = mf;

        // Button Panel: Simulation Control Buttons
        JPanel buttonPanel = new JPanel();
        JButton randomButton = new JButton("Initialize Random");
        JButton prevButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");
        JButton playPauseButton = new JButton("Play/Pause");
        JButton resetButton = new JButton("Reset");
        buttonPanel.add(randomButton);
        buttonPanel.add(playPauseButton);
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(resetButton);
        this.add(buttonPanel);

        // Button listners
        resetButton.addActionListener((ActionEvent e) -> {
            // If the state is not active reset it
            if(mainFrame.is_active) {
                System.out.println("Action denied: Grid cannot be reset while grid is active");
            } else {
                mainFrame.stateGrid.resetGrid();
                mainFrame.model.clearHistory();
            }
        });

        randomButton.addActionListener((ActionEvent e) -> {
            // If the state is not active reset it
            if(mainFrame.is_active) {
                System.out.println("Action denied: Grid cannot be initialized while simulation is active");
            } else {
                mainFrame.stateGrid.initializeRandomGrid();
            }
        });

        nextButton.addActionListener((ActionEvent e) -> {
            // If the state is not active reset it
            if(mainFrame.is_active) {
                System.out.println("Action denied: Grid cannot be stepped next while simulation is active");
            } else {
                mainFrame.model.nextStep();
            }
        });

        prevButton.addActionListener((ActionEvent e) -> {
            // If the state is not active reset it
            if(mainFrame.is_active) {
                System.out.println("Action denied: Grid cannot be stepped next while grid is active");
            } else {
                mainFrame.model.prevStep();
            }
        });

        playPauseButton.addActionListener((ActionEvent e) -> {
            mainFrame.toggleAcitveState();
        });

        // Status Panel: Grid & Simulation State
        JPanel statusPanel = new JPanel(new GridLayout(2, 1));
        // gridStatusLabel = new JLabel("Grid Status: NEW");
        simulationStateLabel = new JLabel("Simulation State: Paused");
        // statusPanel.add(gridStatusLabel);
        statusPanel.add(simulationStateLabel);
        this.add(statusPanel);

        // Grid Dimension Selection Panel (1D vs 2D)
        JPanel ruleSettingsPanel = new JPanel();
        ruleSettingsPanel.setLayout(new BoxLayout(ruleSettingsPanel, BoxLayout.Y_AXIS));
        ruleSettingsPanel.setBorder(BorderFactory.createTitledBorder("Rule Settings"));
        JPanel radioPanel = new JPanel();
        rb1D = new JRadioButton("1D Grid", true);
        rb2D = new JRadioButton("2D Grid", false);
        ButtonGroup gridDimensionsGroup = new ButtonGroup();
        gridDimensionsGroup.add(rb1D);
        gridDimensionsGroup.add(rb2D);
        radioPanel.add(rb1D);
        radioPanel.add(rb2D);
        radioPanel.setAlignmentX(CENTER_ALIGNMENT);
        ruleSettingsPanel.add(radioPanel);

        // Add 1D CA rule dec - inc box
        OneDPanel = new JPanel();
        JLabel ruleLabel = new JLabel("1D rule number: ");
        SpinnerNumberModel ruleModel = new SpinnerNumberModel(30, 0, 255, 1);
        ruleSpinner = new JSpinner(ruleModel);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(ruleSpinner, "#");
        ruleSpinner.setEditor(editor);

        OneDPanel.add(ruleLabel);
        OneDPanel.add(ruleSpinner);
        ruleSettingsPanel.add(OneDPanel);
        OneDPanel.setVisible(rule_dimension == 1);

        ruleSpinner.addChangeListener((ChangeEvent e) -> {
            int new_rule = (int) ruleSpinner.getValue();
            if (new_rule < 0) {
                new_rule = 0;
                ruleSpinner.setValue(new_rule);
            } else if (new_rule > 255) {
                new_rule = 255;
                ruleSpinner.setValue(new_rule);
            }
            setNewRule();
        });

        // Add 2D CA rule list entries for alive and dead list
        TwoDPanel = new JPanel();
        JLabel lifeTitle = new JLabel("Live List");
        lifeText = new JTextField("3");
        JLabel deathTitle = new JLabel("Dead List");
        deathText = new JTextField("1,4,5,6,7,8");

        TwoDPanel.add(lifeTitle);
        TwoDPanel.add(lifeText);
        TwoDPanel.add(deathTitle);
        TwoDPanel.add(deathText);

        ruleSettingsPanel.add(TwoDPanel);
        TwoDPanel.setVisible(rule_dimension == 2);

        // Then in your constructor, replace the document listeners with:
        DocumentFilter numberFilter = new NumberCommaFilter();
        ((AbstractDocument) lifeText.getDocument()).setDocumentFilter(numberFilter);
        ((AbstractDocument) deathText.getDocument()).setDocumentFilter(numberFilter);

        this.add(ruleSettingsPanel);

        // Action Listeners for Grid Dimension
        rb1D.addActionListener((ActionEvent e) -> {
            setRuleDimension(1);
        });
        rb2D.addActionListener((ActionEvent e) -> {
            setRuleDimension(2);
        });

        // Grid Size Selection Panel with Slider
        JPanel gridSizePanel = new JPanel();
        gridSizePanel.setBorder(BorderFactory.createTitledBorder("Grid Size"));
        gridSizeSlider = new JSlider(5, 100, 5);
        // start the slider at the state grid default
        gridSizeSlider.setValue(mainFrame.stateGrid.getGridSize());
        gridSizeSlider.setPaintLabels(true);
        gridSizeSlider.setPaintTicks(true);
        gridSizeSlider.setMajorTickSpacing(10);
        gridSizePanel.add(gridSizeSlider);
        this.add(gridSizePanel);

        // Listener for Grid Size Slider
        gridSizeSlider.addChangeListener((ChangeEvent e) -> {
            int newGridSize = gridSizeSlider.getValue();
            System.out.println("Grid size slider value: " + newGridSize);
            if (mainFrame.is_active) {
                System.out.println("Action denied: Grid size cannot be changed while simulation status is active.");
                gridSizeSlider.setValue(mainFrame.stateGrid.getGridSize()); // reset back to original size
            } else {
                System.out.println("Changing grid size to: " + newGridSize);
                mainFrame.stateGrid.setGridSize(newGridSize);
            }
        });

        // Simulation Speed Panel with Slider and Display Field
        JPanel speedPanel = new JPanel();
        speedPanel.setBorder(BorderFactory.createTitledBorder("Simulation Speed"));
        speedSlider = new JSlider(1, 15, 5);
        speedSlider.setPaintTicks(true);
        speedSlider.setMajorTickSpacing(2);
        speedSlider.setPaintLabels(true);
        JLabel speedLabel = new JLabel("Speed: " + speedSlider.getValue());
        speedSlider.addChangeListener((ChangeEvent e) -> {
            speedLabel.setText("Speed: " + speedSlider.getValue());
        });
        speedPanel.add(speedSlider);
        speedPanel.add(speedLabel);
        this.add(speedPanel);
        
        // Additional Listener to adjust simulation delay based on slider
        speedSlider.addChangeListener((ChangeEvent e) -> {
            int sliderValue = speedSlider.getValue();
            mainFrame.model.setDelay(1000 / sliderValue);
            speedLabel.setText("Speed: " + sliderValue);
            System.out.println("Simulation Speed set to: " + sliderValue 
                    + " -> Delay: " + mainFrame.model.getDelay() + " ms");
        });

        mainFrame.model.setDelay(1000 / speedSlider.getValue());

        // Cell Display Format Panel (Black & White vs Number)
        JPanel displayFormatPanel = new JPanel();
        displayFormatPanel.setBorder(BorderFactory.createTitledBorder("Cell Display Format"));
        rbBlackAndWhite = new JRadioButton("Black and White", true);
        rbNumberFormat = new JRadioButton("Number Format");
        ButtonGroup displayFormatGroup = new ButtonGroup();
        displayFormatGroup.add(rbBlackAndWhite);
        displayFormatGroup.add(rbNumberFormat);
        displayFormatPanel.add(rbBlackAndWhite);
        displayFormatPanel.add(rbNumberFormat);
        this.add(displayFormatPanel);

        // Action Listeners for Cell Display Format
        rbBlackAndWhite.addActionListener((ActionEvent e) -> {
            if (mainFrame.is_active) {
                System.out.println("Action denied: Cell display format cannot be changed while simulation status is active.");
                rbBlackAndWhite.setSelected(false);
            } else {
                mainFrame.stateGrid.setDisplayFormat(StateGrid.DisplayFormat.BLACK_WHITE);
                System.out.println("Cell display format set to Black and White.");
            }
        });
        rbNumberFormat.addActionListener((ActionEvent e) -> {
            if (mainFrame.is_active) {
                System.out.println("Action denied: Cell display format cannot be changed while simulation status is active.");
                rbNumberFormat.setSelected(false);
            } else {
                mainFrame.stateGrid.setDisplayFormat(StateGrid.DisplayFormat.NUMBERS);
                System.out.println("Cell display format set to Number Format.");
            }
        });
    }

    public void setNewRule() {
        switch (rule_dimension) {
            case 1:
                int new_rule = (int) ruleSpinner.getValue(); 
                System.out.printf("Setting rule to %d\n", new_rule);
                mainFrame.model.createNewRule(new_rule);
                break;
            case 2:
                // This is the simple conway's neighbourhood, but further neighbourhood 
                // settings is a good possible future feature
                int[][] neighborhood= new int[][] {
                    {0,1},
                    {0,-1},
                    {1,1},
                    {1,-1},
                    {1,0},
                    {-1,0},
                    {-1,1},
                    {-1,-1}
                };
                
                String life_string = lifeText.getText();
                String death_string = deathText.getText();
                System.out.printf("New 2D rule with: LL = %s and DL = %s\n", life_string, death_string);
                mainFrame.model.createNewRule(life_string, death_string, neighborhood);
                break;
            default:
                throw new AssertionError();
        }

    }

    public void setRuleDimension(int dim) {
        if (dim == 1 || dim == 2) {
            rule_dimension = dim;
            setNewRule();
            OneDPanel.setVisible(rule_dimension == 1);
            TwoDPanel.setVisible(rule_dimension == 2);
            rb1D.setSelected(rule_dimension == 1);
            rb2D.setSelected(rule_dimension == 2);
        } else {
            System.err.println("Error: rule dimension must be 1 or 2");
        }

    }

    // Private methods
    
}
