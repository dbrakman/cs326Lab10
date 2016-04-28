import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

package finals; //Kluge; declared in same package as Tester so that Tester can access these fields

public abstract class Agent implements AgentInterface
{

    protected AgentType type; //is either mac or bac.
    public AgentType getType() { return(type); }

    //private static final int NUM_NEIGHBORS = 8;//Moore neighborhood

    protected int ID;
    public int getID() { return(ID); }

    //Recall order: [MOVE, EAT, DIVIDE, UNDEF]
    protected Event[] cal = new Event[Event.EventType.values().length];
    //^^A list of all possible events, comprising a calendar. 
    public Event getNextEv() 
    {// returns the next (most imminent) event in this Agent's calendar
        Event e = new Event();
        e.time = Double.MAX_VALUE;
        for( Event ev : cal ){
            if( ev.time < e.time )
                e = ev;
        }
        return e;
    }
    public void putEvent( Event e )
    {//Adds event e to this agent's calendar, overwriting the event in it's place
        cal[e.type.ordinal()] = e;
    }

    protected int row;
    protected int col;
    public int getRow() { return(row); }
    public int getCol() { return(col); }
    public void setRowCol(int row, int col)
    { //these values must equal the (r,c) position in the landscape
      //stored here for efficient lookup
        this.row = row;
        this.col = col;
    }

    public static double exponential(double m, Random rng)
/* =========================================================
 * Returns an exponentially distributed positive real number. 
 * NOTE: use m > 0.0
 * =========================================================
 */
    {
	return (-m * Math.log(1.0 - rng.nextDouble()));
    }

    public static double erlang(long n, double b, Random rng)
/* ================================================== 
 * Returns an Erlang distributed positive real number.
 * NOTE: use n > 0 and b > 0.0
 * ==================================================
 */
    { 
	long   i;
	double x = 0.0;
	
	for (i = 0; i < n; i++) 
	    x += exponential(b, rng);
	return (x);
    }

    public static double normal(double m, double s, Random rng)
/* ========================================================================
 * Returns a normal (Gaussian) distributed real number.
 * NOTE: use s > 0.0
 *
 * Uses a very accurate approximation of the normal idf due to Odeh & Evans, 
 * J. Applied Statistics, 1974, vol 23, pp 96-97.
 * ========================================================================
 */
    { 
	final double p0 = 0.322232431088;     final double q0 = 0.099348462606;
	final double p1 = 1.0;                final double q1 = 0.588581570495;
	final double p2 = 0.342242088547;     final double q2 = 0.531103462366;
	final double p3 = 0.204231210245e-1;  final double q3 = 0.103537752850;
	final double p4 = 0.453642210148e-4;  final double q4 = 0.385607006340e-2;
	double u, t, p, q, z;
	
	u   = rng.nextDouble();
	if (u < 0.5)
	    t = Math.sqrt(-2.0 * Math.log(u));
	else
	    t = Math.sqrt(-2.0 * Math.log(1.0 - u));
	p   = p0 + t * (p1 + t * (p2 + t * (p3 + t * p4)));
	q   = q0 + t * (q1 + t * (q2 + t * (q3 + t * q4)));
	if (u < 0.5)
	    z = (p / q) - t;
	else
	    z = t - (p / q);
	return (m + s * z);
    }
  /*
    public void setNextEvent(double time, Event.EventType type){
        next_time = time;
        next_event = type;
        if(type == Event.EventType.MOVE){time_move = time;}
        else if(type == Event.EventType.EAT){time_eat = time;}
        else {time_divide = time;}
    }
    */
    /*
    public void pushTimes(double time)
    {
        //Made to bring newly hatched bacteria to the current time
        if(time_move != Double.MAX_VALUE){ time_move+=time; }
        if(time_eat != Double.MAX_VALUE){ time_eat+=time; }
        if(time_divide != Double.MAX_VALUE){ time_divide+=time; }
    }
    */

    public abstract void move(Cell[][] landscape, Random rng);
    public abstract void eat(Cell[][] landscape, ArrayList<Agent> bacList);
    public abstract void divide(Cell[][] landscape, ArrayList<Agent> bacList, Random rng);
    public abstract void starve(Cell[][] landscape, ArrayList<Agent> bacList);
    public static boolean considerStarving(Bact b, double t0, 
            Cell[][] landscape, ArrayList<Agent> bacList)
    {
        Cell cl = landscape[b.getRow()][b.getCol()];
        //We still need to check if bacterium is DOA: starving before 1st move
        // 2) Calculate time of next would-be action
        double t1 = b.getNextEv().time;
        // 3) Calculate cell's resource growth g over that time
        double aPlusR = b.getResource(); //we've already added r to a in "consume()"
        double g = (t1 - t0) * cl.getRate();
        // 4) Calculate internal consumption c over that time
        double amtC = (t1 - t0) * b.getConsumptionRate();
        // 5) Decide if we need to starve first
        if (aPlusR + g - amtC <= 0) {
            //schedule a starve event
            double tX = ((t1 - t0) / (amtC - g)) * aPlusR + t0;
            b.putEvent(new Event(b, tX, Event.EventType.STARVE));
            return true;
        }
        return false;
    }
}
