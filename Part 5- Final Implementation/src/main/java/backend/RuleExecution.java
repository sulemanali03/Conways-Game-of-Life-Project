package cp317.backend;

import java.util.ArrayList;
import java.util.List;

import cp317.gui.MainFrame;

public class RuleExecution {
    /*
     * Description: Backend class responsible for creating, setting,
     * checking, and executing a cellular automata rules. This class
     * contains a separate multithreaded execution loop that computs
     * the next step every time interval if the simulation state in 
     * the main frame is set to active.
     */

    private final MainFrame mainFrame; // store a local reference to the main frame for internal use
    private volatile CARule currentRule;
    private volatile int delay = 5;
    private volatile boolean loop = true;
    private final Object lock = new Object();
    
    public RuleExecution(MainFrame mf) {
        mainFrame = mf;
        int default_rule = 30;
        createNewRule(30);
        new Thread(() -> {executionLoop();}).start();
    }

    // Control flow methods
    public final void executionLoop() {
        while (loop) {
            int current_delay;
            synchronized (lock) {
                current_delay = getDelay();
                if (mainFrame.is_active) {
                    nextStep();
                }
            }

            try {
                Thread.sleep(current_delay);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void nextStep() {
        synchronized(lock) {
            currentRule.nextStep();
        }
    }

    public void prevStep() {
        synchronized(lock) {
            currentRule.prevStep();
        }
    }

    public final void createNewRule(int rule_number) {
        synchronized(lock) {
            // Creates a new rule based on the options set in the settings tab
            currentRule = new CA1Drule(mainFrame.stateGrid);
            currentRule.setRule(rule_number);
        }
    }

    public final void createNewRule(String life_string, String death_string, int[][] neighborhood) {
        synchronized(lock) {
            // At the moment we default to setting Conway's game of life
            int[] life_list = parseStringList(life_string);
            int[] death_list = parseStringList(death_string);

            if (life_list.length == 0 && death_list.length == 0) {
                System.err.println("Error: Must have at least one element in life or death list");
                return;
            }
            if (neighborhood == null || neighborhood.length == 0) {
                System.err.println("Error: Cannot set neighbourhood of null or zero length");
            }

            currentRule = new CA2Drule(mainFrame.stateGrid, neighborhood);
            currentRule.setRule(life_list, death_list);
        }

    }

    public void clearHistory() {
        currentRule.history.clear();
    }

    public int getDelay() {
        synchronized(lock) {
            return delay;
        }
    }

    public void setDelay(int new_delay) {
        synchronized(lock) {
            this.delay = new_delay;
        }
    } 

    private int[] parseStringList(String s) {

        List<Integer> numberList = new ArrayList<>();
        String[] numbers = s.replaceAll("[^0-9,]", "").split(",");

        for (String num : numbers) {
            if (!num.isEmpty()) {
                int value = Integer.parseInt(num);
                numberList.add(value);
            }
        }

        return numberList.stream().mapToInt(Integer::intValue).toArray();
    }
}
