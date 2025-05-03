package cp317.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JPanel;

import cp317.gui.MainFrame;


public class VisualizationPanel extends TabParentClass {
    /*
     * Description: Tab class that allows the user to set 
     * colors for the cell dead and cell alive states using
     * java's color chooser class.
     */

    // Contructor
    public  VisualizationPanel(MainFrame mainFrame) {
        title = "Visualization";

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        Dimension size = mainFrame.getSize();

        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,5,5,5);
        gbc.weightx = 1.0;

        // Panel for selecting the alive cell color.
        JPanel aliveColorPanel = new JPanel();
        aliveColorPanel.setBorder(BorderFactory.createTitledBorder("Alive Cell Color"));
        JColorChooser aliveColorChooser = new JColorChooser(mainFrame.stateGrid.aliveColor);
        aliveColorChooser.setPreferredSize(new Dimension(size.width - size.height - 10, aliveColorChooser.getPreferredSize().height));
        // Listen for changes and update the grid.
        aliveColorChooser.getSelectionModel().addChangeListener(e -> {
            Color newColor = aliveColorChooser.getColor();
            mainFrame.stateGrid.aliveColor = newColor;
            mainFrame.stateGrid.repaint();
        });

        aliveColorPanel.add(aliveColorChooser);

        gbc.gridy = 0;
        this.add(aliveColorPanel, gbc);

        // Panel for selecting the dead cell color.
        JPanel deadColorPanel = new JPanel();
        deadColorPanel.setBorder(BorderFactory.createTitledBorder("Dead Cell Color"));
        JColorChooser deadColorChooser = new JColorChooser(mainFrame.stateGrid.deadColor);
        deadColorChooser.setPreferredSize(new Dimension(size.width - size.height - 10, deadColorChooser.getPreferredSize().height));
        deadColorChooser.getSelectionModel().addChangeListener(e -> {
            Color newColor = deadColorChooser.getColor();
            mainFrame.stateGrid.deadColor = newColor;
            mainFrame.stateGrid.repaint();
        });

        gbc.gridy = 1;
        deadColorPanel.add(deadColorChooser);
        this.add(deadColorPanel);

    }

    // Public methods

    // Private methods
}