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
    public Simulation(int numCells,   int guiCellWidth, double initResourceMean,
            double initResourceSD, double regrowthRateMean, double regrowthRateSD,
            double maxResourceMean, double maxResourceSD, int numMacrophages,
            double macSpeed, int numBacteria, double bacSpeed, int bacDivShape, 
            double bacDivScale, double consumptionRateMean, double consumptionRateSD,
            long seed, double maxTime)
    {
        // call the SimulationManager constructor, which itself makes sure to
        // construct and store an AgentGUI object for drawing
        super(numCells, guiCellWidth, maxTime);
        rng = new Random(seed);
        sim_clock = 0;
        this.numCells = numCells;
        this.guiCellWidth = guiCellWidth;
        this.maxTime = maxTime;
        macrophageList = new ArrayList<Agent>();
        bacteriaList   = new ArrayList<Agent>();
        landscape = new Cell[numCells][numCells]; //now the landscape is init'd, but..
        for(int i=0; i<numCells; i++){
            for(int j=0;j<numCells;j++){
                //intialize cells with normally distributed resource parameters, but positive?
                double res = -1;
                while( res < 0 ){
                    res = Agent.normal(initResourceMean, initResourceSD, rng); //keep trying I guess
                }
                double rate = -1;
                while( rate < 0){
                    rate = Agent.normal(regrowthRateMean, regrowthRateSD, rng);
                }
                double max = -1;
                while( max < 0)
                {
                    max = Agent.normal(maxResourceMean, maxResourceSD, rng);
                }
                landscape[i][j] = new Cell(i,j, res, rate, max);
            }
        }

        // construct the initial macrophages and and add them at random to the
        //  landscape
        int row = 0, col = 0;
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
          //System.out.printf("Randy/numCols = %d, Randy%%numCols = %d%n",randy/numCols,randy%numCols);
          if(id < numMacrophages){
            Agent a = new Macro(macSpeed,rng);
            (landscape[randy/numCols][randy%numCols]).occupy(a);
            macrophageList.add(a);
          } else {
            //initialize bacs with normally distributed (positive) consumptionRate and 0 resource
            double consumptionRate = -1;
            while( consumptionRate < 0){
                consumptionRate = Agent.normal(consumptionRateMean, consumptionRateSD, rng);
            }
            Agent a = new Bact(bacSpeed,bacDivShape,bacDivScale,0,consumptionRate, rng);
            Cell cl = landscape[randy/numCols][randy%numCols];
            cl.occupy(a);
            //OK, maybe I see that the occupy method could've handled this. but eh
            //When a bac is placed in the landscape, it needs to acquire all the
            // resources from that cell
            double t0 = sim_clock;
            ((Bact) a).consume(cl,t0);
            bacteriaList.add(a);
            //check if bac is DOA
            Agent.considerStarving((Bact)a,t0,landscape,bacteriaList);
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
        //System.out.printf("mac list size = %d, bac list size = %d%n",macrophageList.size(),bacteriaList.size());
        for( Agent m : macrophageList ){
            System.out.printf("Macro #%d at (%d, %d) %ss at time %f%n",m.getID(),m.getRow(),m.getCol(),m.getNextEv().type.name(),m.getNextEv().time);
            if( nextEv.time > m.getNextEv().time )
                nextEv = m.getNextEv();
            }
            for( Agent b : bacteriaList ){
                System.out.printf("Bac #%d at (%d,%d) w/ resource lvl %.2f %ss at time %.2f%n",b.getID(),b.getRow(),b.getCol(),((Bact) b).getResource(),b.getNextEv().type.name(),b.getNextEv().time);
                if( nextEv.time > b.getNextEv().time )
                    nextEv = b.getNextEv();
            }
            //Now nextEv holds the next thing that's supposed to happen. Does it?
            System.out.printf("Evt Owner Type: %s%n",nextEv.owner.getType().name());
            System.out.printf("So the next event is a(n) %s by %s #%d at cell (%d,%d) and time %f%n",nextEv.type.name(),nextEv.owner.getType().name(),nextEv.owner.getID(),nextEv.owner.getRow(),nextEv.owner.getCol(), nextEv.time);
            sim_clock = nextEv.time;

        while (sim_clock < maxTime){
            System.out.println("Let's see what's next");
            if(nextEv.type == Event.EventType.MOVE){
                nextEv.owner.move(landscape, rng);
            } else if(nextEv.type == Event.EventType.EAT){
                nextEv.owner.eat(landscape, bacteriaList);
            } else if(nextEv.type == Event.EventType.STARVE){
                nextEv.owner.starve(landscape, bacteriaList);
            } else if(nextEv.type == Event.EventType.DIVIDE){
                nextEv.owner.divide(landscape, bacteriaList, rng);
            } else {
                throw new RuntimeException("Undefined Event Type");
            }
            nextEv.time = Double.MAX_VALUE; //reset the nextEv so we don't keep repeating the same event
            for( Agent m : macrophageList ){
            System.out.printf("Macro #%d at (%d, %d) %ss at time %.2f%n",m.getID(),m.getRow(),m.getCol(),m.getNextEv().type.name(),m.getNextEv().time);
            if( nextEv.time > m.getNextEv().time )
                nextEv = m.getNextEv();
            }
            for( Agent b : bacteriaList ){
                System.out.printf("Bac #%d at (%d,%d) w/ resource lvl %.2f %ss at time %.2f%n",b.getID(),b.getRow(),b.getCol(),((Bact) b).getResource(),b.getNextEv().type.name(),b.getNextEv().time);
                if( nextEv.time > b.getNextEv().time )
                    nextEv = b.getNextEv();
            }
            //Now nextEv holds the next thing that's supposed to happen. Does it?
            System.out.printf("So the next event is a(n) %s by %s #%d at cell (%d,%d) and time %f%n",nextEv.type.name(),nextEv.owner.getType().name(),nextEv.owner.getID(),nextEv.owner.getRow(),nextEv.owner.getCol(), nextEv.time);
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
