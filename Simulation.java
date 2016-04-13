import java.util.ArrayList;
import java.util.Random;
import java.util.HashSet;

/**
 * This class implements the next-event simulation engine for your agent-based
 * simulation.  You may choose to define other helper classes (e.g., Event,
 * EventList, etc.), but the main while loop of your next-event engine should
 * appear here, in the run(...) method.
 */

public class Simulation extends SimulationManager
{
    // you may choose to have two separate lists, or only one list of all
    private ArrayList<Agent> macrophageList;
    private ArrayList<Agent> bacteriaList;
    private int numCells;
    private int guiCellWidth;
    private Random rng;
    private double maxTime;
    /**************************************************************************
     * Constructs a Simulation object.  This should just perform initialization
     * and setup.  Later use the object to .run() the simulation.
     *
     * @param numCells       number of rows and columns in the environment
     * @param guiCellWidth   width of each cell drawn in the gui
     * @param numMacrophages number of initial macrophages in the environment
     * @param numBacteria    number of initial bacteria in the environment
     **************************************************************************/
    public Simulation(int numCells,       int guiCellWidth,
                      int numMacrophages, int numBacteria, long seed, double maxTime)
    {
        // call the SimulationManager constructor, which itself makes sure to
        // construct and store an AgentGUI object for drawing
        super(numCells, guiCellWidth, maxTime);

        this.numCells = numCells;
        this.guiCellWidth = guiCellWidth;
        this.maxTime = maxTime;
        macrophageList = new ArrayList<Agent>();
        bacteriaList   = new ArrayList<Agent>();

        // as a simple example, construct the initial macrophages and
        // bacteria and add them "at random" (not really, here) to the
        // landscape
        int row = 0, col = 0;
        rng = new Random(seed);
        HashSet<Integer> hs = new HashSet<Integer>();
        while(hs.size() < numMacrophages + numBacteria)
        {
          hs.add(rng.nextInt(numCells*numCells));
        }
        int id = 0;
        int numCols= numCells;
        for (int randy : hs)
        {
          if(id < numMacrophages){
            Agent a = new Agent(Agent.AgentType.MACROPHAGE, rng);
            a.setRowCol(randy/numCols, randy%numCols);
            macrophageList.add(a);
          } else {
            Agent a = new Agent(Agent.AgentType.BACTERIUM, rng);
            a.setRowCol(randy/numCols, randy%numCols);
            bacteriaList.add(a);
          }
          id++;
        }
    }

    /**************************************************************************
     * Method used to run the simulation.  This method should contain the
     * implementation of your next-event simulation engine (while loop handling
     * various event types).
     *
     * @param guiDelay  delay in seconds between redraws of the gui
     **************************************************************************/
    public void run(double guiDelay) throws InterruptedException
    {
        this.gui = new AgentGUI(this, numCells, guiCellWidth);

        double sim_clock = 0;
        Event nextEv = new Event(null, Double.MAX_VALUE, Event.EventType.UNDEF);
        for( Agent m : macrophageList ){
                System.out.printf("Macro at (%d, %d) has ev_type %s at time %f%n",m.getRow(),m.getCol(),m.getNextEvent().name(),m.getNextTime());
                if( nextEv.time > m.getNextTime() ){
                    nextEv.time = m.getNextTime();
                    nextEv.type = m.getNextEvent();
                    nextEv.owner = m;
                }
            }
            for( Agent b : bacteriaList ){
                System.out.printf("Bac at (%d,%d) has ev_type %s at time %f%n",b.getRow(),b.getCol(),b.getNextEvent().name(),b.getNextTime());
                if( nextEv.time > b.getNextTime() ){
                    nextEv.time = b.getNextTime();
                    nextEv.type = b.getNextEvent();
                    nextEv.owner = b;
                }
            }
            //Now nextEv holds the next thing that's supposed to happen. Does it?
            System.out.printf("So the next event is a (%d,%d) %s's %s at time %f%n",nextEv.owner.getRow(),nextEv.owner.getCol(), nextEv.owner.getType().name(), nextEv.type.name(), nextEv.time);
            sim_clock = nextEv.time;

        while (sim_clock < maxTime){
            System.out.println("Let's see what's next");
            if(nextEv.type == Event.EventType.MOVE){
                nextEv.owner.move(numCells, macrophageList, bacteriaList, rng);
            } else if(nextEv.type == Event.EventType.EAT){
                nextEv.owner.eat(bacteriaList);
            } else {
                nextEv.owner.divide(numCells, macrophageList,bacteriaList, rng);
            }
            nextEv.time = Double.MAX_VALUE; //reset the nextEv so we don't keep repeating the same event
            for( Agent m : macrophageList ){
                System.out.printf("Macro at (%d, %d) has ev_type %s at time %f%n",m.getRow(),m.getCol(),m.getNextEvent().name(),m.getNextTime());
                if( nextEv.time > m.getNextTime() ){
                    nextEv.time = m.getNextTime();
                    nextEv.type = m.getNextEvent();
                    nextEv.owner = m;
                }
            }
            for( Agent b : bacteriaList ){
                System.out.printf("Bac at (%d,%d) has ev_type %s at time %f%n",b.getRow(),b.getCol(),b.getNextEvent().name(),b.getNextTime());
                if( nextEv.time > b.getNextTime() ){
                    nextEv.time = b.getNextTime();
                    nextEv.type = b.getNextEvent();
                    nextEv.owner = b;
                }
            }
            //Now nextEv holds the next thing that's supposed to happen. Does it?
            System.out.printf("So the next event is a (%d,%d) %s's %s at time %f%n",nextEv.owner.getRow(),nextEv.owner.getCol(), nextEv.owner.getType().name(), nextEv.type.name(), nextEv.time);
            sim_clock = nextEv.time;

            gui.update(guiDelay);
        }
    }

    /**************************************************************************
     * Accessor method that returns the number of macrophages still present.
     * @return an integer representing the number of macrophages present
     **************************************************************************/
    public int getNumMacrophages() { return(macrophageList.size()); }

    /**************************************************************************
     * Accessor method that returns the number of bacteria still present.
     * @return an integer representing the number of bacteria present
     **************************************************************************/
    public int getNumBacteria()    { return(bacteriaList.size()); }

    /**************************************************************************
     * Accessor method that returns the current time of the simulation clock.
     * @return a double representing the current time in simulated time
     **************************************************************************/
    public double getTime()        { return(time); }

    /**************************************************************************
     * Accessor method that returns the end time of the simulation.
     * @return a double representing the end of the simulation in simulated time
     **************************************************************************/
    public double getMaxTime()        { return(maxTime); }

    /**************************************************************************
     * Method that constructs and returns a single list of all agents present.
     * This method is used by the gui drawing routines to update the gui based
     * on the number and positions of agents present.
     *
     * @return an ArrayList<AgentInterface> containing references to all macrophages and bacteria
     **************************************************************************/
    public ArrayList<AgentInterface> getListOfAgents()
    {
        // your implementation may differ depending on one or two lists...
        ArrayList<AgentInterface> returnList = new ArrayList<AgentInterface>();
        for (int i = 0; i < macrophageList.size(); i++) returnList.add( (AgentInterface)macrophageList.get(i) );
        for (int i = 0; i < bacteriaList.size(); i++)   returnList.add( (AgentInterface)bacteriaList.get(i) );
        return(returnList);
    }
}
