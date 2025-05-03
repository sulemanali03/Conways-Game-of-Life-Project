package cp317.gui;

import javax.swing.SwingUtilities;

public class Main {
    /*
     * Description: Main entry point that creates and displays the main frame class
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
