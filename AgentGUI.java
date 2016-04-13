import javax.swing.*;  // for all the JWhatevers
import java.awt.*;     // for BorderLayout
import java.util.Random;

/**
 * This class implements the GUI window for our agent-based simulation,
 * specifically drawing the grid and (eventually) drawing the agents
 * that will run rampant thereupon.
 */
class AgentGUI extends WindowManager
{
    private SimulationManager simulation;     // a reference to the simulation object
    private AgentCanvas       canvas;         // for drawing the agents
    private TimeSeriesDialog  dialog;         // for drawing time series
    private int               lastUpdateTime; // for updating dialog

    /**************************************************************************
     * Constructor for the agent GUI window.
     *
     * @param  theSimulation  a SimulationManager reference to the simulation object
     * @param  gridSize       number of rows (same as columns) in the environment
     * @param  guiCellWidth   width of each cell drawn in the gui
     **************************************************************************/
    public AgentGUI(SimulationManager theSimulation, int gridSize, int guiCellWidth)
    {
        // call the WindowManager constructor, and add a BorderLayout
        super("Agent-Based Simulation", 600, 600);
        this.setLayout(new BorderLayout());

        // hang on to a reference to the SimulationManager object
        this.simulation = theSimulation;

        // create the AgentCanvas for drawing, and then add to window's center
        this.canvas = new AgentCanvas(theSimulation, 
                                      gridSize, gridSize, guiCellWidth);
        this.add(new JScrollPane(this.canvas), BorderLayout.CENTER);

        int maxAgents = gridSize * gridSize;
        this.dialog = new TimeSeriesDialog(maxAgents, (int)theSimulation.getMaxTime());

        this.lastUpdateTime = 0;
    }

    /**************************************************************************
     * Accessor method returning the number of rows (same as columns) in the
     * environment.
     * @return an integer representing the number of rows (columns) in the environment
     **************************************************************************/
    public int getGridSize() { return(canvas.getGridWidth()); }

    /**************************************************************************
     * Method to redraw the canvas in the GUI window.  This method calls the
     * updateGrid() method in AgentCanvas which updates the GUI landsacpe by
     * querying the simulation object.
     *
     * @param   delayInSecs  a double representing the delay between draws
     **************************************************************************/
    public void update(double delayInSecs) throws InterruptedException
    { 
        // update the main GUI
        canvas.updateGrid(); 
        long msecs = (long)(delayInSecs * 1000); 
        Thread.sleep(msecs);

        // and update the time series dialog -- only on integer time steps
        double t = simulation.getTime();
        if ((int)t > lastUpdateTime)
        {
            this.dialog.updateCounts(simulation.getNumMacrophages(), 
                                     simulation.getNumBacteria());
            lastUpdateTime++;
        }
    }
    
}
