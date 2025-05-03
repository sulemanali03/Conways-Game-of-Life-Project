package cp317.gui.components;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cp317.gui.MainFrame;


public class ImportExportPanel extends TabParentClass {
    /*
     * Description: Tab class that is responsible for allowing 
     * the user to set a file name and path and either import or
     * export state and rule data to that xml file. The import
     * export panel contains logic to mediate between xml data
     * and the states of various components within the applicaiton.
     */
    
    // Attributes
    private String file_path;
    private String file_name;

    JTextPane message = new JTextPane();

    // Contructor
    public ImportExportPanel(MainFrame mainFrame) {
        title = "Import/export";

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Create an import and export button panel
        JPanel ieButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton importButton = new JButton("Import");
        JButton exportButton = new JButton("Export");
        ieButtonPanel.add(importButton);
        ieButtonPanel.add(exportButton);
        this.add(ieButtonPanel);
        this.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing

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
        this.add(fileBrowserPanel);

        // Messaging panel used to give user status messages and error feedback
        JPanel messagingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        browsePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextPane message = new JTextPane();
        message.setContentType("text/html");
        message.setEditable(false);
        message.setBackground(null); // Use parent background color
        message.setPreferredSize(new Dimension(500, 50));
        messagingPanel.add(message, BorderLayout.CENTER);

        this.add(messagingPanel);

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
                if (mainFrame.is_active == true) {
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
                            int rule_number = Integer.parseInt(getTagValue("ruleNumber", settings));
                            mainFrame.settingsPanel.ruleSpinner.setValue(rule_number);
                            mainFrame.settingsPanel.rb1D.doClick();
                        } else {
                            String life_string = getTagValue("lifeString", settings);
                            String death_string = getTagValue("deathString", settings);
                            mainFrame.settingsPanel.lifeText.setText(life_string);
                            mainFrame.settingsPanel.deathText.setText(death_string);
                            mainFrame.settingsPanel.rb2D.doClick();
                        }

                        // Get grid size
                        int gridSize = Integer.parseInt(getTagValue("gridSize", settings));
                        mainFrame.settingsPanel.gridSizeSlider.setValue(gridSize);
                        mainFrame.stateGrid.setGridSize(gridSize);

                        // Get simulation speed
                        int simSpeed = Integer.parseInt(getTagValue("simSpeed", settings));
                        mainFrame.settingsPanel.speedSlider.setValue(simSpeed);

                        // Get display format
                        String dispFormat = getTagValue("dispFormat", settings);
                        if (dispFormat.equals("Black and White")) {
                            mainFrame.settingsPanel.rbBlackAndWhite.doClick();
                        } else {
                            mainFrame.settingsPanel.rbNumberFormat.doClick();
                        }
                    }

                    // Parse the statistics
                    NodeList statsList = root.getElementsByTagName("stats");
                    if (statsList.getLength() > 0) {
                        Element stats = (Element) statsList.item(0);

                        // Get and set the alive count
                        int alive_count = Integer.parseInt(getTagValue("aliveCells", stats));
                        mainFrame.statisticsPanel.aliveCellsLabel.setText(String.format("Alive Cells: %d", alive_count));

                        // Get and set the dead count
                        int dead_count = Integer.parseInt(getTagValue("deadCells", stats));
                        mainFrame.statisticsPanel.deadCellsLabel.setText(String.format("Dead Cells: %d", dead_count));

                        // Get and set the population
                        int population = Integer.parseInt(getTagValue("population", stats));
                        mainFrame.statisticsPanel.populationLabel.setText(String.format("Population: %d", population));

                        // Get and set the percentage
                        int percentage = Integer.parseInt(getTagValue("gridPercentage", stats));
                        mainFrame.statisticsPanel.percentageLabel.setText(String.format("Percentage: %d", percentage));
                    }

                    // Parse state grid
                    NodeList stateGridList = root.getElementsByTagName("stategrid");
                    if (stateGridList.getLength() > 0) {
                        Element stateGrid = (Element) stateGridList.item(0);
                        Element matrix = (Element) stateGrid.getElementsByTagName("matrix").item(0);
                        NodeList rows = matrix.getElementsByTagName("row");

                        int size = mainFrame.stateGrid.getGridSize();
                        int[][] newStateMatrix = new int[size][size];

                        for (int i = 0; i < rows.getLength(); i++) {
                            Node row = rows.item(i);
                            String[] values = row.getTextContent().split(",");
                            for (int j = 0; j < values.length; j++) {
                                // newstatematrix[i][j] = values[j].trim().equals("1");
                                newStateMatrix[i][j] = (int)Integer.parseInt(values[j].trim());
                            }
                        }

                        mainFrame.stateGrid.setStateMatrix(newStateMatrix);
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
                if (mainFrame.is_active == true) {
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
                    int dim = mainFrame.settingsPanel.rb1D.isSelected() ? 1 : 2;
                    writer.write(String.format("    <dimension>%d</dimension>", dim));
                    writer.newLine();
                    if (dim == 1) {
                        // Get the 1D rule number
                        int rule_number = (int) mainFrame.settingsPanel.ruleSpinner.getValue();
                        writer.write(String.format("    <ruleNumber>%d</ruleNumber>", rule_number));
                        writer.newLine();
                    } else {
                        // Get the life and death lists
                        String life_string = mainFrame.settingsPanel.lifeText.getText();
                        String death_string = mainFrame.settingsPanel.deathText.getText();

                        writer.write(String.format("    <lifeString>%s</lifeString>", life_string));
                        writer.newLine();
                        writer.write(String.format("    <deathString>%s</deathString>", death_string));
                        writer.newLine();

                    }

                    int grid_size = mainFrame.stateGrid.getGridSize();
                    writer.write(String.format("    <gridSize>%d</gridSize>", grid_size));
                    writer.newLine();
                    int speed = mainFrame.settingsPanel.speedSlider.getValue();
                    writer.write(String.format("    <simSpeed>%d</simSpeed>", speed));
                    writer.newLine();
                    String display_fmt = mainFrame.settingsPanel.rbBlackAndWhite.isSelected() ? "Black and White" : "Numbers";
                    writer.write(String.format("    <dispFormat>%s</dispFormat>", display_fmt));
                    writer.newLine();
                    writer.write("    <ruleNumber>30</ruleNumber>"); // Not yet functioning since rule setting has not been implmented
                    writer.newLine();
                    writer.write("</settings>");
                    writer.newLine();

                    // Write statistics
                    writer.write("<stats>");
                    writer.newLine();
                    int alive_count = mainFrame.statisticsPanel.calculateAliveCells(mainFrame.stateGrid);
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
                    int[][] state_matrix = mainFrame.stateGrid.copyStateMatrix();

                    for (int i = 0; i < grid_size; i++) {
                        writer.write("        <row>");
                        // Join the row values with commas
                        StringBuilder rowData = new StringBuilder();
                        for (int j = 0; j < grid_size; j++) {
                            if (j > 0) rowData.append(",");
                            int value = state_matrix[i][j];
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
    }


    // Private methods
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
