# Outline for gui prototype

## Purpose:
> Give the client a sensation of how the application will work and function before spending large amounts of time implementing full application code. This is not intended to be a first iteration of the project, but just provide something tangible for clients, investors, and potential user to see.

## Prototype Status
> Demonstrates the all the features that the first iteration is intended to have.
> Shows how the the interactive state grid will function work, but does not actually run a full cellular automata simulation since this is only a demonstration of frontend features.
> Has event listeners that print to the console when clicked to give indication of an interaction while not requiring the full backend to be implemented.
> Show some tabs, widgets and customizations that will be included in the first project iteration.

## Running the project
> The main entry point is included in the src/GUI_Prototype.java file. This can be run with any jdk version >= 22.

## Usage
> In the application is divided into 2 main sections
> ### State grid
> > - This component is where all the simulations and initial conditions will be set. 
> > - The state grid in the prototpye is fixed at a 58 x 58 size, but will be variable in the final application.
> > - The entire grid is initialized to white, but each cell when clicked will change its start from white to black or vise versa.
> > - This demonstrates how the inital conditions will be set in the final application.
> ### Options
> > Has event listeners to log interaction with each widget in the options section
> > - Parameters tab: Contains simulation settings
> > - Visualization: Sample color scheme setting
> > - Statistics: Demonstrates some of the stats that will be tracked throughout the progress of the simulation
> > - Import/Export: For saving set of rules and initial conditions.