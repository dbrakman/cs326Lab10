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
    private double sim_clock;
    private int numCells;
    private int guiCellWidth;
    private Random rng;
    private double maxTime;
    private Cell[][] landscape;
    /**************************************************************************
     * Constructs a Simulation object.  This should just perform initialization
     * and setup.  Later use the object to .run() the simulation.
     *
     * @param numCells       number of rows and columns in the environment
     * @param guiCellWidth   width of each cell drawn in the gui
     * @param numMacrophages number of initial macrophages in the environment
     * @param numBacteria    number of initial bacteria in the environment
     **************************************************************************/
    public Simulation(int numCells,   int guiCellWidth, int numMacrophages,
                      double macSpeed, int numBacteria, double bacSpeed, 
                      int bacDivShape, double bacDivScale, long seed, double maxTime)
    {
        // call the SimulationManager constructor, which itself makes sure to
        // construct and store an AgentGUI object for drawing
        super(numCells, guiCellWidth, maxTime);

        sim_clock = 0;
        this.numCells = numCells;
        this.guiCellWidth = guiCellWidth;
        this.maxTime = maxTime;
        macrophageList = new ArrayList<Agent>();
        bacteriaList   = new ArrayList<Agent>();
        landscape = Cell[numCells][numCells];

        // as a simple example, construct the initial macrophages and
        // bacteria and add them "at random" (not really, here) to the
        // landscape
        int row = 0, col = 0;
        rng = new Random(seed);
        HashSet<Integer> hs = new HashSet<Integer>();
        while(hs.size() < numMacrophages + numBacteria)
        {
          hs.add(rng.nextInt(numCells*numCells));
        } //generate random ints, w/o replacement, from numCells^2
        int id = 0;
        int numCols= numCells;
        for (int randy : hs) //place macs/bacs in the landscape
        //according to the linearized order of their random ints
        {
          if(id < numMacrophages){
            Agent a = new Macro(rng);
            landscape[randy/numCols][randy%numCols].occupy(a);
            macrophageList.add(a);
          } else {
            Agent a = new Bact(rng);
            landscape[randy/numCols][randy%numCols].occupy(a);
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

        Event nextEv = new Event(null, Double.MAX_VALUE, Event.EventType.UNDEF);
        for( Agent m : macrophageList ){
            System.out.printf("Macro at (%d, %d) has ev_type %s at time %f%n",m.getRow(),m.getCol(),m.getNextEv().type.name(),m.getNextEv().time);
            if( nextEv.time > m.getNextEv().time )
                nextEv = m.getNextEv();
            }
            for( Agent b : bacteriaList ){
                System.out.printf("Bac at (%d,%d) has ev_type %s at time %f%n",b.getRow(),b.getCol(),b.getNextEv().type.name(),b.getNextEv().time);
                if( nextEv.time > b.getNextEv().time )
                    nextEv = b.getNextEv();
            }
            //Now nextEv holds the next thing that's supposed to happen. Does it?
            System.out.printf("So the next event is a (%d,%d) %s's %s at time %f%n",nextEv.owner.getRow(),nextEv.owner.getCol(), nextEv.owner.getType().name(), nextEv.type.name(), nextEv.time);
            sim_clock = nextEv.time;

        while (sim_clock < maxTime){
            System.out.println("Let's see what's next");
            if(nextEv.type == Event.EventType.MOVE){
                nextEv.owner.move(landscape, rng);
            } else if(nextEv.type == Event.EventType.EAT){
                nextEv.owner.eat(landscape, bacteriaList);
            } else {
                nextEv.owner.divide(landscape, bacteriaList, rng);
            }
            nextEv.time = Double.MAX_VALUE; //reset the nextEv so we don't keep repeating the same event
            for( Agent m : macrophageList ){
            System.out.printf("Macro at (%d, %d) has ev_type %s at time %f%n",m.getRow(),m.getCol(),m.getNextEv().type.name(),m.getNextEv().time);
            if( nextEv.time > m.getNextEv().time )
                nextEv = m.getNextEv();
            }
            for( Agent b : bacteriaList ){
                System.out.printf("Bac at (%d,%d) has ev_type %s at time %f%n",b.getRow(),b.getCol(),b.getNextEv().type.name(),b.getNextEv().time);
                if( nextEv.time > b.getNextEv().time )
                    nextEv = b.getNextEv();
            }
            //Now nextEv holds the next thing that's supposed to happen. Does it?
            System.out.printf("So the next event is a (%d,%d) %s's %s at time %f%n",nextEv.owner.getRow(),nextEv.owner.getCol(), nextEv.owner.getType().name(), nextEv.type.name(), nextEv.time);
            sim_clock = nextEv.time;

            gui.update(guiDelay);
            System.out.println("Simulation: I'm updating the GUI!");
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
    public double getTime()        { return(sim_clock); }

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
