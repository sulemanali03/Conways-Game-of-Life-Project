import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.io.File;
import java.security.MessageDigest;

   //GUI_FINAL Class
public class GUI_FINAL {
	
	// Updates the statistics tab labels based on the current grid state.
	private void updateStatistics(CellularAutomataGrid gridPanel, 
	                              JLabel deadCellsLabel, 
	                              JLabel aliveCellsLabel, 
	                              JLabel populationLabel, 
	                              JLabel percentageLabel) {
	    int gridSize = gridPanel.getGridSize();
	    int totalCells = gridSize * gridSize;
	    int aliveCount = gridPanel.getAliveCount();
	    int deadCount = totalCells - aliveCount;
	    double percentage = (aliveCount / (double) totalCells) * 100;
	    
	    deadCellsLabel.setText("Dead Cells: " + deadCount);
	    aliveCellsLabel.setText("Alive Cells: " + aliveCount);
	    populationLabel.setText("Population: " + aliveCount);
	    percentageLabel.setText(String.format("Percentage of Grid Used: %.2f%%", percentage));
	}
	
	
    /* -------------------------------
       Global Variables
       ------------------------------- */
    private volatile boolean isPlaying = false;
    private int simulationDelay = 500; // Delay in milliseconds (adjustable via speed slider)
    private JLabel gridStatusLabel;
    private JLabel simulationStateLabel;

    /* -------------------------------
       Helper Methods
       ------------------------------- */
    // Updates the status labels based on the current grid state and simulation activity.
    private void updateStatusLabels(CellularAutomataGrid gridPanel) {
        gridStatusLabel.setText("         Grid Status: " + gridPanel.getGameStatus());
        simulationStateLabel.setText("            Simulation State: " + (isPlaying ? "Active" : "Paused"));
    }

    /* -------------------------------
       Inner Classes
       ------------------------------- */
    // Redirects output to a JTextArea, ensuring thread safety.
    public class TextAreaOutputStream extends OutputStream {
        private final JTextArea textArea;

        public TextAreaOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException {
            // Append the character on the EDT for thread safety
            SwingUtilities.invokeLater(() -> {
                textArea.append(String.valueOf((char) b));
                // Auto-scroll to the bottom
                textArea.setCaretPosition(textArea.getDocument().getLength());
            });
        }
    }

    /* -------------------------------
       Main Method
       ------------------------------- */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI_FINAL().createGUI());
    }

    /* -------------------------------
       GUI Creation Method
       ------------------------------- */
    private void createGUI() {
        /* --- Frame Setup --- */
        JFrame frame = new JFrame("Conways Game of Life");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1220, 600);
        frame.setLayout(new BorderLayout());

        /* --- Grid Display & Initialization --- */
        CellularAutomataGrid gridPanel = new CellularAutomataGrid();
        frame.add(gridPanel, BorderLayout.WEST);

        // Listen for changes in grid state and update labels accordingly.
        gridPanel.addPropertyChangeListener("gameStatus", evt -> updateStatusLabels(gridPanel));

        /* --- Tabbed Pane Setup --- */
        JTabbedPane tabbedPane = new JTabbedPane();

        /* =====================================================
           Tab 1: Parameters
           ===================================================== */
        JPanel parametersPanel = new JPanel();
        parametersPanel.setLayout(new BoxLayout(parametersPanel, BoxLayout.Y_AXIS));

        // Button Panel: Simulation Control Buttons
        JPanel buttonPanel = new JPanel();
        JButton randomButton = new JButton("Initialize Random");
        JButton nextButton = new JButton("Next");
        JButton prevButton = new JButton("Previous");
        JButton playPauseButton = new JButton("Play/Pause");
        JButton resetButton = new JButton("Reset");
        buttonPanel.add(randomButton);
        buttonPanel.add(playPauseButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(prevButton);
        buttonPanel.add(resetButton);
        parametersPanel.add(buttonPanel);

        // Status Panel: Grid & Simulation State
        JPanel statusPanel = new JPanel(new GridLayout(2, 1));
        gridStatusLabel = new JLabel("Grid Status: NEW");
        simulationStateLabel = new JLabel("Simulation State: Paused");
        statusPanel.add(gridStatusLabel);
        statusPanel.add(simulationStateLabel);
        parametersPanel.add(statusPanel);

        // Grid Dimension Selection Panel (1D vs 2D)
        JPanel gridDimensionPanel = new JPanel();
        gridDimensionPanel.setBorder(BorderFactory.createTitledBorder("Grid Dimension"));
        JRadioButton rb1D = new JRadioButton("1D Grid", false);
        JRadioButton rb2D = new JRadioButton("2D Grid", true);
        ButtonGroup gridDimensionGroup = new ButtonGroup();
        gridDimensionGroup.add(rb1D);
        gridDimensionGroup.add(rb2D);
        gridDimensionPanel.add(rb1D);
        gridDimensionPanel.add(rb2D);
        parametersPanel.add(gridDimensionPanel);

        // Action Listeners for Grid Dimension
        rb1D.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gridPanel.getGameStatus().equals("NEW")) {
                    System.out.println("Action denied: Grid mode can only be changed when the grid state is NEW.");
                    rb2D.setSelected(true); // Revert to 2D if state is not NEW.
                } else {
                    System.out.println("1D Grid selected");
                    gridPanel.setOneDMode(true);
                }
            }
        });
        rb2D.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gridPanel.getGameStatus().equals("NEW")) {
                    System.out.println("Action denied: Grid mode can only be changed when the grid state is NEW.");
                    rb2D.setSelected(true); // Force 2D if state is not NEW.
                } else {
                    System.out.println("2D Grid selected");
                    gridPanel.setOneDMode(false);
                }
            }
        });

        // Grid Size Selection Panel with Slider
        JPanel gridSizePanel = new JPanel();
        gridSizePanel.setBorder(BorderFactory.createTitledBorder("Grid Size"));
        JSlider gridSizeSlider = new JSlider(5, 100, 5);
        gridSizeSlider.setPreferredSize(new Dimension(400, 50));
        gridSizeSlider.setPaintLabels(true);
        gridSizeSlider.setPaintTicks(true);
        gridSizeSlider.setMajorTickSpacing(10);
        gridSizePanel.add(gridSizeSlider);
        parametersPanel.add(gridSizePanel);

        // Listener for Grid Size Slider
        gridSizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int newGridSize = gridSizeSlider.getValue();
                System.out.println("Grid size slider value: " + newGridSize);
                if (!gridPanel.getGameStatus().equals("NEW")) {
                    System.out.println("Action denied: Grid size can only be changed when the grid state is NEW.");
                } else {
                    System.out.println("Changing grid size to: " + newGridSize);
                    gridPanel.setGridSize(newGridSize);
                }
            }
        });

        // Simulation Speed Panel with Slider and Display Field
        JPanel speedPanel = new JPanel();
        speedPanel.setBorder(BorderFactory.createTitledBorder("Simulation Speed"));
        JSlider speedSlider = new JSlider(1, 10, 5);
        speedSlider.setPaintTicks(true);
        speedSlider.setMajorTickSpacing(1);
        speedSlider.setPaintLabels(true);
        JTextField speedTextField = new JTextField("Speed: " + speedSlider.getValue());
        speedTextField.setEditable(false);
        speedTextField.setHorizontalAlignment(JTextField.CENTER);
        speedTextField.setPreferredSize(new Dimension(100, 30));
        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                speedTextField.setText("Speed: " + speedSlider.getValue());
            }
        });
        speedPanel.add(speedSlider);
        speedPanel.add(speedTextField);
        parametersPanel.add(speedPanel);
        
        // Additional Listener to adjust simulation delay based on slider
        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int sliderValue = speedSlider.getValue();
                simulationDelay = 1100 - (sliderValue * 100);
                speedTextField.setText("Speed: " + sliderValue);
                System.out.println("Simulation Speed set to: " + sliderValue 
                        + " -> Delay: " + simulationDelay + " ms");
            }
        });

        // Cell Display Format Panel (Black & White vs Number)
        JPanel displayFormatPanel = new JPanel();
        displayFormatPanel.setBorder(BorderFactory.createTitledBorder("Cell Display Format"));
        JRadioButton rbBlackAndWhite = new JRadioButton("Black and White", true);
        JRadioButton rbNumberFormat = new JRadioButton("Number Format");
        ButtonGroup displayFormatGroup = new ButtonGroup();
        displayFormatGroup.add(rbBlackAndWhite);
        displayFormatGroup.add(rbNumberFormat);
        displayFormatPanel.add(rbBlackAndWhite);
        displayFormatPanel.add(rbNumberFormat);
        parametersPanel.add(displayFormatPanel);

        // Action Listeners for Cell Display Format
        rbBlackAndWhite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gridPanel.getGameStatus().equals("NEW")) {
                    System.out.println("Action denied: Cell display format can only be changed when the grid state is NEW.");
                    rbBlackAndWhite.setSelected(false);
                } else {
                    gridPanel.setDisplayFormat(CellularAutomataGrid.DisplayFormat.BLACK_WHITE);
                    System.out.println("Cell display format set to Black and White.");
                }
            }
        });
        rbNumberFormat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gridPanel.getGameStatus().equals("NEW")) {
                    System.out.println("Action denied: Cell display format can only be changed when the grid state is NEW.");
                    rbNumberFormat.setSelected(false);
                } else {
                    gridPanel.setDisplayFormat(CellularAutomataGrid.DisplayFormat.NUMBER);
                    System.out.println("Cell display format set to Number Format.");
                }
            }
        });

        // Add Parameters tab to the tabbed pane.
        tabbedPane.addTab("Parameters", parametersPanel);

        /* =====================================================
           Tab 3: Visualization
           ===================================================== */
        JPanel visualizationPanel = new JPanel();
        visualizationPanel.setLayout(new BoxLayout(visualizationPanel, BoxLayout.Y_AXIS));

        // Panel for selecting the alive cell color.
        JPanel aliveColorPanel = new JPanel();
        aliveColorPanel.setBorder(BorderFactory.createTitledBorder("Alive Cell Color"));
        JColorChooser aliveColorChooser = new JColorChooser(gridPanel.getAliveColor());
        // Listen for changes and update the grid.
        aliveColorChooser.getSelectionModel().addChangeListener(e -> {
            Color newColor = aliveColorChooser.getColor();
            gridPanel.setAliveColor(newColor);
        });
        aliveColorPanel.add(aliveColorChooser);
        visualizationPanel.add(aliveColorPanel);

        // Panel for selecting the dead cell color.
        JPanel deadColorPanel = new JPanel();
        deadColorPanel.setBorder(BorderFactory.createTitledBorder("Dead Cell Color"));
        JColorChooser deadColorChooser = new JColorChooser(gridPanel.getDeadColor());
        deadColorChooser.getSelectionModel().addChangeListener(e -> {
            Color newColor = deadColorChooser.getColor();
            gridPanel.setDeadColor(newColor);
        });
        deadColorPanel.add(deadColorChooser);
        visualizationPanel.add(deadColorPanel);

        tabbedPane.addTab("Visualization", visualizationPanel);


        /* =====================================================
           Tab 4: Statistics
           ===================================================== */
        JPanel statisticsPanel = new JPanel();
        statisticsPanel.setLayout(new BoxLayout(statisticsPanel, BoxLayout.Y_AXIS));
        JLabel deadCellsLabel = new JLabel("Dead Cells: 0");
        JLabel aliveCellsLabel = new JLabel("Alive Cells: 0");
        JLabel populationLabel = new JLabel("Population: 0");
        JLabel percentageLabel = new JLabel("Percentage of Grid Used: 0%");
        // Add padding between labels.
        deadCellsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        aliveCellsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        populationLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        percentageLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        statisticsPanel.add(deadCellsLabel);
        statisticsPanel.add(aliveCellsLabel);
        statisticsPanel.add(populationLabel);
        statisticsPanel.add(percentageLabel);
        tabbedPane.addTab("Statistics", statisticsPanel);

        
        /* =====================================================
           Tab 5: Import/Export
           ===================================================== */
        JPanel importExportPanel = new JPanel();
        importExportPanel.setLayout(new BoxLayout(importExportPanel, BoxLayout.Y_AXIS));

        // Create an import and export button panel
        JPanel ieButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton importButton = new JButton("Import");
        JButton exportButton = new JButton("Export");
        ieButtonPanel.add(importButton);
        ieButtonPanel.add(exportButton);
        importExportPanel.add(ieButtonPanel);
        importExportPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing

        // Create the file browser panel
        JPanel fileBrowserPanel = new JPanel();
        fileBrowserPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // File name panel
        JPanel fileNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fileNamePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel nameLabel = new JLabel("File Name:");
        JTextField fileName = new JTextField();
        Dimension fnameSize = new Dimension(300, 25);
        fileName.setPreferredSize(fnameSize);
        fileName.setMaximumSize(fnameSize);
        fileNamePanel.add(nameLabel);
        fileNamePanel.add(fileName);

        // File path panel
        JPanel pathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pathPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel pathLabel = new JLabel("File Directory:");
        JTextField pathField = new JTextField();
        Dimension pathFieldSize = new Dimension(500, 25);
        pathField.setPreferredSize(pathFieldSize);
        pathField.setMaximumSize(pathFieldSize);
        pathPanel.add(pathLabel);
        pathPanel.add(pathField);

        // Browse button panel
        JPanel browsePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        browsePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton browseButton = new JButton("Browse");
        browsePanel.add(browseButton);


        // Add components to fileBrowserPanel with spacing
        fileBrowserPanel.add(fileNamePanel);
        fileBrowserPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        fileBrowserPanel.add(pathPanel);
        fileBrowserPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        fileBrowserPanel.add(browsePanel);

        // Add fileBrowserPanel to importExportPanel
        importExportPanel.add(fileBrowserPanel);

        // Messaging panel used to give user status messages and error feedback
        JPanel messagingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        browsePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextPane message = new JTextPane();
        message.setContentType("text/html");
        message.setEditable(false);
        message.setBackground(null); // Use parent background color
        message.setPreferredSize(new Dimension(500, 50));
        messagingPanel.add(message, BorderLayout.CENTER);

        importExportPanel.add(messagingPanel);

        // Add action listener for the browse button
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    pathField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        // Add event listener for the import button
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String file_path = pathField.getText();
                String file_name = fileName.getText();

                // Ensure that the simulation is not active
                if (isPlaying == true) {
                    writeWrappedString("ERROR: please pause the simulation before exporting", message);
                    return;
                }

                String xmlPath = String.format("%s/%s", file_path, file_name);
                if (!isValidPath(xmlPath) || !isValidFileName(file_name)) {
                    writeWrappedString(String.format("ERROR: invalid path: %s ", xmlPath), message);
                    return;
                }

                try {
                    // Create the document builder
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();

                    // Parse the XML file
                    Document doc = builder.parse(new File(xmlPath));
                    doc.getDocumentElement().normalize();

                    // Get root element
                    Element root = doc.getDocumentElement();
                    if (!root.getTagName().equals("automata")) {
                        throw new Exception("Invalid XML structure: root element must be 'automata'");
                    }

                    // Parse settings
                    NodeList settingsList = root.getElementsByTagName("settings");
                    if (settingsList.getLength() > 0) {
                        Element settings = (Element) settingsList.item(0);
                        
                        // Get dimension
                        int dimension = Integer.parseInt(getTagValue("dimension", settings));
                        if (dimension == 1) {
                            rb1D.setSelected(true);
                            gridPanel.setOneDMode(true);
                        } else {
                            rb2D.setSelected(true);
                            gridPanel.setOneDMode(false);
                        }

                        // Get grid size
                        int gridSize = Integer.parseInt(getTagValue("gridSize", settings));
                        gridSizeSlider.setValue(gridSize);
                        gridPanel.setGridSize(gridSize);

                        // Get simulation speed
                        int simSpeed = Integer.parseInt(getTagValue("simSpeed", settings));
                        speedSlider.setValue(simSpeed);

                        // Get display format
                        String dispFormat = getTagValue("dispFormat", settings);
                        if (dispFormat.equals("Black and White")) {
                            rbBlackAndWhite.setSelected(true);
                            gridPanel.setDisplayFormat(CellularAutomataGrid.DisplayFormat.BLACK_WHITE);
                        } else {
                            rbNumberFormat.setSelected(true);
                            gridPanel.setDisplayFormat(CellularAutomataGrid.DisplayFormat.NUMBER);
                        }
                    }

                    // Parse the statistics
                    NodeList statsList = root.getElementsByTagName("stats");
                    if (statsList.getLength() > 0) {
                        Element stats = (Element) statsList.item(0);

                        // Get and set the alive count
                        int alive_count = Integer.parseInt(getTagValue("aliveCells", stats));
                        aliveCellsLabel.setText(String.format("Alive Cells: %d", alive_count));

                        // Get and set the dead count
                        int dead_count = Integer.parseInt(getTagValue("deadCells", stats));
                        deadCellsLabel.setText(String.format("Dead Cells: %d", dead_count));

                        // Get and set the population
                        int population = Integer.parseInt(getTagValue("population", stats));
                        populationLabel.setText(String.format("Population: %d", population));

                        // Get and set the percentage
                        int percentage = Integer.parseInt(getTagValue("gridPercentage", stats));
                        percentageLabel.setText(String.format("Percentage: %d", percentage));
                    }

                    // Parse state grid
                    NodeList stateGridList = root.getElementsByTagName("stategrid");
                    if (stateGridList.getLength() > 0) {
                        Element stateGrid = (Element) stateGridList.item(0);
                        Element matrix = (Element) stateGrid.getElementsByTagName("matrix").item(0);
                        NodeList rows = matrix.getElementsByTagName("row");

                        

                        boolean[][] newStateMatrix = new boolean[gridPanel.getGridSize()][gridPanel.getGridSize()];

                        for (int i = 0; i < rows.getLength(); i++) {
                            Node row = rows.item(i);
                            String[] values = row.getTextContent().split(",");
                            for (int j = 0; j < values.length; j++) {
                                newStateMatrix[i][j] = values[j].trim().equals("1");
                            }
                        }

                        gridPanel.setStateMatrix(newStateMatrix);
                    }

                    writeWrappedString("XML file successfully parsed", message);
                } catch (Exception err) {
                    writeWrappedString("Error parsing XML file: " + err.getMessage(), message);
                    System.out.println(err.getMessage());
                }

            }
        });

        // Add event listener for export button
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dir_path = pathField.getText();
                String file_name = fileName.getText();

                // Ensure that the simulation is not active
                if (isPlaying == true) {
                    writeWrappedString("ERROR: please pause the simulation before exporting", message);
                    return;
                }

                String xmlPath = String.format("%s/%s", dir_path, file_name);
                if (!isValidPath(dir_path)) {
                    writeWrappedString(String.format("ERROR: directory does not exist: %s ", dir_path), message);
                    return;
                }
                if (isValidPath(xmlPath)) {
                    writeWrappedString("ERROR: file already exisits please create a new one", message);
                    return;
                }
                if (!isValidFileName(file_name)) {
                    writeWrappedString(String.format("ERROR: invalid file name %s", file_name), message);
                    return;
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(xmlPath))) {

                    // Write the xml header
                    writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                    writer.newLine();
                    writer.write("<automata>");
                    writer.newLine();

                    // Write settings
                    writer.write("<settings>");
                    writer.newLine();
                    int dim = rb1D.isSelected() ? 1 : 2;
                    writer.write(String.format("    <dimension>%d</dimension>", dim));
                    writer.newLine();
                    int grid_size = gridPanel.getGridSize();
                    writer.write(String.format("    <gridSize>%d</gridSize>", grid_size));
                    writer.newLine();
                    int speed = speedSlider.getValue();
                    writer.write(String.format("    <simSpeed>%d</simSpeed>", speed));
                    writer.newLine();
                    String display_fmt = rbBlackAndWhite.isSelected() ? "Black and White" : "Numbers";
                    writer.write(String.format("    <dispFormat>%s</dispFormat>", display_fmt));
                    writer.newLine();
                    writer.write("    <ruleNumber>30</ruleNumber>"); // Not yet functioning since rule setting has not been implmented
                    writer.newLine();
                    writer.write("</settings>");
                    writer.newLine();

                    // Write statistics
                    writer.write("<stats>");
                    writer.newLine();
                    int alive_count = gridPanel.getAliveCount();
                    int dead_count = (grid_size * grid_size) - alive_count;
                    writer.write(String.format("    <deadCells>%d</deadCells>", dead_count));
                    writer.newLine();
                    writer.write(String.format("    <aliveCells>%d</aliveCells>", alive_count));
                    writer.newLine();
                    writer.write("    <population>0</population>");
                    writer.newLine();
                    writer.write("    <gridPercentage>0</gridPercentage>");
                    writer.newLine();
                    writer.write("</stats>");
                    writer.newLine();

                    // Write state grid
                    writer.write("<stategrid>");
                    writer.newLine();
                    writer.write("    <matrix>");
                    writer.newLine();
                    
                    // Example of writing matrix rows
                    boolean[][] state_matrix = gridPanel.getStateMatrix();

                    for (int i = 0; i < grid_size; i++) {
                        writer.write("        <row>");
                        // Join the row values with commas
                        StringBuilder rowData = new StringBuilder();
                        for (int j = 0; j < grid_size; j++) {
                            if (j > 0) rowData.append(",");
                            int value = state_matrix[i][j] ? 1 : 0;
                            rowData.append(value);
                        }
                        writer.write(rowData.toString());
                        writer.write("</row>");
                        writer.newLine();
                    }
                    
                    writer.write("    </matrix>");
                    writer.newLine();
                    writer.write("</stategrid>");

                    writer.newLine();
                    writer.write("</automata>");

                    writeWrappedString(String.format("Results successfully written to %s", xmlPath), message);
                } catch (IOException err) {
                    writeWrappedString(String.format("ERROR: Failed to write to file: %s", err.getMessage()), message);
                }

            }
        });

        
        tabbedPane.addTab("Import/Export", importExportPanel);


        /* =====================================================
           Tab 6: Execution Log
           ===================================================== */
        JPanel executionLogPanel = new JPanel(new BorderLayout());
        JTextArea logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logTextArea);
        executionLogPanel.add(logScrollPane, BorderLayout.CENTER);
        tabbedPane.addTab("Execution Log", executionLogPanel);

        // Set preferred size for the tabbed pane and add it to the frame.
        tabbedPane.setPreferredSize(new Dimension(600, frame.getHeight()));
        frame.add(tabbedPane, BorderLayout.EAST);

        /* --- Redirect System.out to Execution Log --- */
        System.setOut(new java.io.PrintStream(new TextAreaOutputStream(logTextArea)));

        /* -------------------------------
           Action Listeners for Simulation Control Buttons
           ------------------------------- */
        randomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPlaying) {
                    System.out.println("Initializing random grid");
                    gridPanel.initializeRandomGrid();
                    updateStatusLabels(gridPanel);
                    updateStatistics(gridPanel, deadCellsLabel, aliveCellsLabel, populationLabel, percentageLabel);
                } else {
                    System.out.println("Action denied: Cannot initialize random grid while simulation is active (Paused required).");
                }
            }
        });

        nextButton.addActionListener(new ActionListener() {
        	@Override
            public void actionPerformed(ActionEvent e) {
                if (!isPlaying) {
                    System.out.println("Next step in simulation");
                    gridPanel.nextGeneration();
                    updateStatusLabels(gridPanel);
                    updateStatistics(gridPanel, deadCellsLabel, aliveCellsLabel, populationLabel, percentageLabel);
                } else {
                    System.out.println("Action denied: Cannot step to next generation while simulation is active (Paused required).");
                }
            }
        });

        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPlaying) {
                    System.out.println("Previous step in simulation");
                    gridPanel.previousStep();
                    updateStatusLabels(gridPanel);
                    updateStatistics(gridPanel, deadCellsLabel, aliveCellsLabel, populationLabel, percentageLabel);
                } else {
                    System.out.println("Action denied: Cannot revert to previous step while simulation is active (Paused required).");
                }
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPlaying) {
                    System.out.println("Resetting simulation");
                    gridPanel.resetGrid();
                    updateStatusLabels(gridPanel);
                    updateStatistics(gridPanel, deadCellsLabel, aliveCellsLabel, populationLabel, percentageLabel);
                } else {
                    System.out.println("Action denied: Cannot reset grid while simulation is active (Paused required).");
                }
            }
        });

        playPauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Toggle playing status
                isPlaying = !isPlaying;
                if (isPlaying) {
                    // Start simulation in a new thread
                    new Thread(() -> {
                        while (isPlaying) {
                            SwingUtilities.invokeLater(() -> {
                                gridPanel.nextGeneration();
                                updateStatusLabels(gridPanel);
                                updateStatistics(gridPanel, deadCellsLabel, aliveCellsLabel, populationLabel, percentageLabel);
                            });
                            try {
                                Thread.sleep(simulationDelay);
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }).start();
                }
                updateStatusLabels(gridPanel);
                System.out.println("Play/Pause toggled: " + (isPlaying ? "Playing" : "Paused"));
            }
        });
        
        
        
        
        // Finalize & Show Frame
        frame.setVisible(true);
    }

    // Import / Export auxiliary functions
    private Boolean isValidPath(String path) {
        File f = new File(path);

        return f.exists();
    }

    private Boolean isValidFileName(String fname) {
        // Check if file name is empty or null
        if (fname == null || fname.isEmpty()) {
            return false;
        }

        // Check if file has .xml extension
        if (!fname.endsWith(".xml")) {
            return false;
        }

        // Define invalid characters
        String invalidChars = "\\/:*?\"<>|";
        
        // Check for invalid characters
        for (char c : invalidChars.toCharArray()) {
            if (fname.indexOf(c) != -1) {
                return false;
            }
        }

        return true;
    }

    private String getTagValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag);
            if (nodeList != null && nodeList.getLength() > 0) {
                Node node = nodeList.item(0);
                if (node.hasChildNodes()) {
                    // Get the first child node and trim any whitespace
                    String content = node.getFirstChild().getNodeValue().trim();
                    return content;
                }
            }
            throw new RuntimeException("Tag not found or empty: " + tag);
        } catch (Exception e) {
            System.err.println("Error reading tag " + tag + ": " + e.getMessage());
            return "";
        }
    }

    private void writeWrappedString(String content, JTextPane outputText) {
        outputText.setText(String.format("<html><div style='width: 450px'>%s</div></html>", content));
    }
}
