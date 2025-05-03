package cp317.gui.components;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import cp317.gui.MainFrame;

public class OutputLog extends TabParentClass {
    /*
     * Description: Tab class that contains all the warning or
     * system messages that are given by either System.err or 
     * System.out.
     */
    
    /* -------------------------------
       Inner Classes
       ------------------------------- */
    public class TextAreaOutputStream extends OutputStream{
        /*
         * Redirects output to a JTextArea, ensuring thread safety.
         */
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

    // Attributes

    // Contructor
    public  OutputLog(MainFrame mainFrame) {
        title = "Output Log";
        setLayout(new BorderLayout());
        JTextArea logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logTextArea);
        this.add(logScrollPane, BorderLayout.CENTER);

        // Redirect System.out to Execution Log 
        System.setOut(new java.io.PrintStream(new TextAreaOutputStream(logTextArea)));

    }

    // Public methods

    // Private methods
    
    
}
