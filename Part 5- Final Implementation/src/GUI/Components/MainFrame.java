package cp317.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import cp317.backend.RuleExecution;
import cp317.gui.components.ImportExportPanel;
import cp317.gui.components.OutputLog;
import cp317.gui.components.SettingsPanel;
import cp317.gui.components.StateGrid;
import cp317.gui.components.StatisticsPanel;
import cp317.gui.components.TabParentClass;
import cp317.gui.components.VisualizationPanel;

public class MainFrame extends JFrame {
    /*
     * Description: The main frame is the central class that stores
     * and access all the componentes and attributes that must
     * be shared between classes. One and only one main frame 
     * will be created in the main function. The main frame is
     * also responsibily for the initialization of all the visual
     * and backend components.
     */

    // Visual components  
    private JTabbedPane optionsPane;
    public VisualizationPanel visualizationPanel;
    public StatisticsPanel statisticsPanel;

    public SettingsPanel settingsPanel;
    public ImportExportPanel importExportPanel;
    public OutputLog outputLog;
    public StateGrid stateGrid;

    // Backend and control
    public RuleExecution model;
    public boolean is_active = false; // Used to limit or allow certain actions
    
    public MainFrame() {
        // Set up the frame
        setTitle("Cellular Automata Simulator");
        setSize(1280, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());       

        // Initialize the model
        stateGrid = new StateGrid(this);
        model = new RuleExecution(this);

        // Initialize the visual components
        initializeTabs();

        Dimension size = this.getSize();

        // Add components to frame with proportions
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        
        // Add the state grid with the width as same as the height
        gbc.weightx = size.height / size.width;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        System.out.printf("size: %d x %d\n", size.width, size.height);
        stateGrid.setMaximumSize(new Dimension(size.height, size.height));
        stateGrid.setMinimumSize(new Dimension(size.height, size.height));
        add(stateGrid, gbc);

        // NOTE: this only works if the width exceeds the height 
        // Take the rest of the space with the options pane
        gbc.weightx = 1 - size.height / size.width;
        gbc.gridx = 1;
        optionsPane.setMaximumSize(new Dimension(size.width - size.height, size.height));
        optionsPane.setMinimumSize(new Dimension(size.width - size.height, size.height));
        add(optionsPane, gbc);

        setLocationRelativeTo(null);
    }

    private void initializeTabs() {
        optionsPane = new JTabbedPane(JTabbedPane.TOP); 

        optionsPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        // Create components
        visualizationPanel = new VisualizationPanel(this);
        statisticsPanel = new StatisticsPanel(this);
        settingsPanel = new SettingsPanel(this);
        importExportPanel = new ImportExportPanel(this);
        outputLog = new OutputLog(this);

        // Now add each panel to the tabbed pane
        addTab(settingsPanel);
        addTab(visualizationPanel);
        addTab(statisticsPanel);
        addTab(importExportPanel);
        addTab(outputLog);

    }

    private void addTab(TabParentClass panel) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        optionsPane.addTab(panel.title, panel);
    }
    
    public void toggleAcitveState() {
        System.out.println("ACTION: state toggled");
        if (is_active) {
            settingsPanel.simulationStateLabel.setText("Simulation State: Paused");
            is_active = false;
        } else {
            settingsPanel.simulationStateLabel.setText("Simulation State: Active");
            is_active = true;
        }
    }
} 
