Description of the Actors

### **User**

This category encompasses all individuals interacting directly with the cellular automata interface, regardless of their role or expertise level. The group includes students, professors, researchers, and enthusiasts.

**Common Goals**:

* Experimenting with cellular automata.  
* Configuring and customizing grids or simulations.  
* Testing hypotheses or exploring patterns.  
* Exporting or saving results for analysis or presentation.

This approach simplifies the use case model by focusing on the shared interactions with the system, while the differences in goals can be documented elsewhere if needed for design or marketing purposes.

Use Cases

### **1\. Modification of Parameters and Cells**

**Actors**: User  
**Description**: A user can change the dimension of the grid, the speed of the simulation and the format in which the cells are displayed

**Steps**:

1. **Random Initialization**:  
   * Simple button press for generating grid with random grid dimension, simulation speed and display format that does not contain parameter conflicts (1D grid with 2D grid options)  
2. **Manual Initialization**:  
   * Choose dimension for the grid:  
     1. 1D for linear cells  
     2. 2D for rectangular/square grid  
   * Choose simulation speed:  
     1. Customized input for simulation speed through slider or manual input  
   * Choose display format:  
     1. Black and white square format  
     2. Number format through 0’s and 1’s  
3. **Dynamic Modification**:  
   * During simulation, cells can be selected and modified  
   * The rest of the grid simulation will be immediately altered accordingly  
   * Undo option will be available to revert back to the previous state  
4. Save changes in order to run simulation with the specified settings

**Extensions**:

* **Error handling**: Error prompt will require user to re design the grid state if chosen settings bring logic errors (1D dimension grids cannot have 2D dimension options)  
* **Previews**: Checks for validity of selections and gives user the option to review their selections before saving the grid state


### **2\. Pause/Play Simulation**

**Actors**: User  
**Description**: Provides users with control over the simulation’s runtime, allowing them to start, stop, and resume the cellular automaton’s evolution.

**Steps**:

1. Press the **Play** button to begin the simulation.  
2. During simulation, press **Pause** to pause the evolution.  
3. Press **Play** again to resume from the current state.

**Extensions**:

* Allow the user to step through single iterations while paused.  
* Provide the user with a visual indicator of the current state (e.g., “Paused” or “Running”).

### **3\. Reset Simulation**

**Actors**: User  
**Description**: Provides users a quick method to erase the simulation's current state and start over, with the option to keep selected settings.

**Steps**:

1. Press the **Reset** button.  
2. All cells return to their default state (e.g., 0 or white).  
3. Preserve any selected parameters (e.g., grid type, speed) unless explicitly reset.

**Extensions**:

* Provide the user with a confirmation dialog to prevent accidental resets.  
* Include an option that allows the user to clear the grid and return parameters to their initial settings.

### 

### **4\. Visualization Options**

**Actors**: User  
**Description**: Users are able to manipulate different visualization features in order to make the data displayed more informative or interactive.

**Features**:

1. **Custom Colors**:  
   * Users can specify colors that can be used to determine the state of a cell   
   * Different gradients of colors can be used to determine time of life for a given cell (brighter gradients represent longer times of life and vice versa)  
2. **Dynamic Indicators**:  
   * Overlays can be used to display patterns and key information formed during the simulation (reveal which areas of the grid experience more growth and vice versa)

**Extensions**:

* A statistics/overlays tab will be available for users to change visualization options during simulations

### **5\. Simulation Analysis**

**Actors**: User  
**Description**: Provides tools and metrics to help users quantitatively and qualitatively analyze the behavior of the cellular automaton over time. Helps users understand the behavior of the simulation.  
**Features**:

1. **Basic Statistics**:  
   * Count of alive vs. dead cells.  
   * Percentage of grid occupied.  
2. **Pattern Recognition**:  
   * Identify common structures (e.g., gliders, still lifes, oscillators in Conway’s Game of Life).  
   * Highlight or label these patterns on the grid.  
3. **Graphs and Charts**:  
   * Plot data over time (e.g., population growth, cell lifespan).  
4. **Snapshots and Comparison**:  
   * Save snapshots of grid states to compare later.  
   * Overlay grids to see differences.

**Extensions**:

* Provide the user with export options for analysis data (e.g., CSV or image files).  
* Enable the user with a "time-lapse" playback option to replay the simulation evolution.

### **6\. Preset Rules/Scenarios**

**Actors**: User  
**Description**: Provides a library of preloaded configurations and rules for ease of use or educational purposes. Offer predefined setups to help users quickly start simulations or explore known phenomena.  
**Features**:

1. **Classic Rules**:  
   * Conway’s Game of Life.  
   * Elementary cellular automata (e.g., Rule 30, Rule 110).  
   * Langton’s Ant or other well-known examples.  
2. **Thematic Scenarios**:  
   * Pre-configured grids with specific setups (e.g., symmetrical patterns, random noise).  
3. **Customizable Rules**:  
   * Allow users to modify preset rules (e.g., change birth/survival conditions in Conway’s Game of Life).

**Extensions**:

* Provide an explanation or tutorial for each preset.  
* Add a community feature to share custom rules or setups.  
* Enable random scenario generation for experimentation.

### 

### **7.Export/Import Grid State**

**Actors**: User  
**Description**: Users can export the current state of the grid (including cell states and parameters) to a file and import it later to continue the simulation or replicate scenarios. Allow users to save and reload grid configurations for future use or sharing.

**Steps**:

1. **Export**:  
   * Click the **Export** button.  
   * Select a format (e.g., JSON, CSV, or custom binary format).  
   * Save the file locally.  
2. **Import**:  
   * Click the **Import** button.  
   * Select a file containing the grid state.  
   * Load the grid and parameters into the interface.

**Extensions**:

* Allow users to name the configurations.  
* Add a library of saved configurations within the application for quick access.  
* Validate imported files to ensure compatibility.  
* Provide versioning to maintain compatibility across updates.

---

