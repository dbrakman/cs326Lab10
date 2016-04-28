import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

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
/*
    public void moveMacro(int numCells, ArrayList<Agent> macroList, ArrayList<Agent> bacList,
                     Random rng)
    {
          ArrayList<Point> bac_coord = new ArrayList<Point>();
          ArrayList<Point> nomacro_coord = new ArrayList<Point>();

          //Checks neighbors for bacteria and saves its coordinate in the landscape.
          System.out.println("Macro is looking in its Moore neighborhood");
          for(int i = -1; i <=1; i++) {
            for(int j = -1; j<=1; j++) {
              if(i==j && j==0){ continue;} //don't consider the current position as a move dest.
              int r = (this.row + i + numCells) % numCells;
              int c = (this.col + j + numCells) % numCells; //look at every adjacent (Moore) cell (r,c) in a toroidal landscape
              System.out.println("Looking at cell (" + r + "," + c +")");
              for(int k = 0; k < bacList.size(); k++) { //check if any bacterium is at the current position
                Agent bac = bacList.get(k);
                if(bac.getRow() == r && bac.getCol() == c) {
                  Point p = new Point(r,c); //i.e. there is a bac at (r,c)
                  System.out.println("We found a bac there");
                  bac_coord.add(p);
                  break; //we don't need to check for any other bacs in the same space
                }
              }
              boolean noMac = true;//now check if any mac is at the current position
              for(int k = 0; k < macroList.size(); k++) { 
                Agent macro = macroList.get(k);
                if(macro.getRow() == r && macro.getCol() == c) {
                  noMac = false; //if there is, set a flag saying we can't move to (r,c)
                  System.out.println("We found a mac there");
                  break; //and stop looking for other macs in the same space
                }
              }
              if(noMac){ //if we didn't find a mac at (r,c), it's a potential destination
                  Point p = new Point(r,c);
                  nomacro_coord.add(p);
              }
            }
          }
          if(bac_coord.size() > 0) {//if there's bacs in our Moore neighborhood, eat 1 at random
            int dest = rng.nextInt(bac_coord.size()); //pick one at random
            Point dest_point = bac_coord.get(dest);
            this.row = dest_point.x; //move there
            this.col = dest_point.y;
            System.out.println("We're moving to (" + row + "," + col + ")");
            this.time_eat = this.time_move; //schedule an immediate eat
            this.next_event = Event.EventType.EAT;
          }else {
            int dest = rng.nextInt(nomacro_coord.size()); //find a random place w/ no mac
            Point dest_point = nomacro_coord.get(dest);
            this.row = dest_point.x; //move there
            this.col = dest_point.y;
            System.out.println("We're moving to (" + row + "," + col + ")");
            this.next_event = Event.EventType.MOVE; //well, we're not eating...
          }
          this.time_move += java_rexp(MAC_SPEED,rng); //no matter what happens, schedule next move
          updateNextTime();
    }
    */
/*
    public void moveBac(int numCells, ArrayList<Agent> macroList, ArrayList<Agent> bacList,
                     Random rng)
    {
          ArrayList<Point> nobac_coord = new ArrayList<Point>();

          //Checks neighbors for bacteria and saves its coordinate in the landscape.
          System.out.println("Bac is looking in its Moore neighborhood");
          for(int i = -1; i <=1; i++) {
            for(int j = -1; j<=1; j++) {
              if(i==j && j==0){ continue;} //don't consider the current pos as a move dest.

              int r = (this.row + i + numCells) % numCells;
              int c = (this.col + j + numCells) % numCells; 
              //look at every adjacent (Moore) cell (r,c) in a toroidal landscape
              System.out.println("Looking at cell (" + r + "," + c +")");

              boolean noBac = true;
              for(int k = 0; k < bacList.size(); k++) { //check if any bac is at the current pos
                Agent bac = bacList.get(k);
                if(bac.getRow() == r && bac.getCol() == c) {
                  noBac = false; //if there is, set a flag saying we can't move to (r,c)
                  System.out.println("We found a bac there");
                  break; //we don't need to check for any other bacs in the same space
                }
              }
              if(noBac){ //if we didn't find a bac at (r,c), it's a potential destination
                  Point p = new Point(r,c);
                  nobac_coord.add(p);
              }
            }
          }
          if(nobac_coord.size() > 0) {//if there's any adjacent loc w/o a bac, go to 1 at random 
            int dest = rng.nextInt(nobac_coord.size()); //pick one at random
            Point dest_point = nobac_coord.get(dest);
            this.row = dest_point.x; //move there
            this.col = dest_point.y;
            System.out.println("We're moving to (" + row + "," + col + ")");
            this.time_move += java_rexp(BAC_SPEED,rng); //schedule next move
            for(int i = 0; i < macroList.size(); i++) { //check if we just walked into a mac
                Agent macro = macroList.get(i);
                if(macro.getRow() == row && macro.getCol() == col) {
                  macro.setNextEvent(next_time,Event.EventType.EAT); //if we did, schedule an immediate eat for that mac 
                  this.time_eat = Double.MAX_VALUE;
                  this.time_move = Double.MAX_VALUE;
                  this.time_divide = Double.MAX_VALUE;
                  this.next_time = Double.MAX_VALUE; //Event cancellation
                  System.out.println("Whoops, I walked into a Mac and I need to be eaten");
                  break; //and stop looking for other macs in the same space
                }
            }
          }else {
            //don't move now, but schedule next move
            System.out.println("All my neighbors are occupied! What're the odds?");
            this.time_move += java_rexp(BAC_SPEED,rng);
          }
          updateNextTime();
    }
*/
    /*public void eat(ArrayList<Agent> bacList){
        //check every bacterium. When one's found in this cell's location, eat it.
        for(int i=0; i< bacList.size(); i++){
            Agent bac = bacList.get(i);
            if (bac.getRow() == this.row && bac.getCol() == this.col){
                bacList.remove(i); //delete the bac
                System.out.println("A bac should've just been deleted");
                time_eat = Double.MAX_VALUE;
                next_time = time_move;
                next_event = Event.EventType.MOVE;
                //lunch++? killcount++?
                break;
            }
        }
        updateNextTime();
    }*/

/*public void divide(int numCells, ArrayList<Agent> macroList, ArrayList<Agent> bacList,
                       Random rng)
    {
        if (this.type == AgentType.MACROPHAGE) {System.out.println("Macro Div REEEEEE"); return;}
        //Checks neighbors for bacteria and saves its coordinate in the landscape.
        ArrayList<Point> nobac_coord = new ArrayList<Point>();
        for(int i = -1; i <=1; i++) {
          for(int j = -1; j<=1; j++) {
            int r = (this.row + i + numCells) % numCells;
            int c = (this.col + j + numCells) % numCells; //look at every adjacent (Moore) cell (r,c) in a toroidal landscape
            boolean noBac = true;
            for(int k = 0; k < bacList.size(); k++) { //check if any bacterium is at the current position
              Agent bac = bacList.get(k);
              if(bac.getRow() == r && bac.getCol() == c) {
                noBac = false; //if there is, set a flag saying we can't move to (r,c)
                break; //we don't need to check for any other bacs in the same space
              }
            }
            if(noBac){ //if we didn't find a bac at (r,c), it's a potential destination
              Point p = new Point(r,c);
              nobac_coord.add(p);
            }
          }
        }
        if(nobac_coord.size() > 0) {//if there's any adjacent square w/o a bac, go to one at random 
            Agent daughter = new Agent(AgentType.BACTERIUM,rng);
            bacList.add(daughter);
            int dest = rng.nextInt(nobac_coord.size()); //pick one at random
            Point dest_point = nobac_coord.get(dest);
            daughter.setRowCol(dest_point.x, dest_point.y);
            daughter.pushTimes(time_divide);
            System.out.println("We're popping out a baby to (" + daughter.getRow() + "," + daughter.getCol() + ")");
        }
        time_divide+= java_rgamma(DIV_SHAPE,DIV_SCALE,rng);
        updateNextTime();
    }
*/
/*    private void updateNextTime(){
        if (time_move < Math.min(time_eat, time_divide)){
            next_event = Event.EventType.MOVE;
            next_time = time_move;
        } else if(time_eat < Math.min(time_move, time_divide)){
            next_event = Event.EventType.EAT;
            next_time = time_eat;
        } else {
            next_event = Event.EventType.DIVIDE;
            next_time = time_divide;
        }
    }
    */
}
