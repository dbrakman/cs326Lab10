import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class Bact extends Agent implements AgentInterface
{
	//type=Agent.AgentType.BACTERIUM;

	private static int IDBASE = 0;
	private double bacSpeed;
    //constructor

    public Bact(double startTime, double bacSpeed, int bacDivShape,
    			 double bacDivScale, Random rng)
    {
        //Construct a Macrophage at startTime w/ movement speed macSpeed
        ID = IDBASE++;
        this.bacSpeed = bacSpeed;
        cal[0] = new Event(this,startTime+Agent.exponential(bacSpeed, rng),Event.EventType.MOVE);
        cal[1] = new Event(this,Double.MAX_VALUE,Event.EventType.EAT);
        cal[2] = new Event(this,startTime+Agent.erlang(bacDivShape,bacDivScale, rng),Event.EventType.DIVIDE);
        cal[3] = new Event(this,Double.MAX_VALUE,Event.EventType.UNDEF);
        row = col = -1; //should be overwritten as soon as the Mac is placed in the landscape
    }

    public Bact(int bacDivShape, double bacDivScale, Random rng){ 
    	super(0.0,bacDivShape, bacDivScale,rng); 
    }

    //move
    public void move(Cell[][] landscape, Random rng)
    {
        //number of possible destinations: 8 - neighbors
        ArrayList<Point> nobac_coord = new ArrayList<Point>();
        for(int i=-1; i <= 1; i++)
        {   
            for( int j=-1; j<=1; j++)
            {
                if( i==0 && j==0 ){ continue; }//don't consider the current pos as a move dest.
                int r = (row + i + landscape.length) % landscape.length;
                int c = (col + j + landscape[0].length) % landscape[0].length;
                if( !landscape[r][c].hasBacterium() )
                    nobac_coord.add(new Point(r,c));
            }
        }
        if( nobac_coord.size() > 0 ){//if there's spots w/ no bac in our Moore neighborhood
            int dest = rng.nextInt(nobac_coord.size()); //pick one at random
            Point dest_point = nobac_coord.get(dest);
            landscape[dest_point.x][dest_point.y].occupy(this); //move there
 		}
        cal[0] = new Event(this,cal[0].time+Agent.exponential(bacSpeed,rng),Event.EventType.MOVE);
        //^^schedule another movement
    }
    //divide
    public void divide(Cell[][] landscape, ArrayList<Agent> bacList, Random rng)
    {
    	ArrayList<Point> nobac_coord = new ArrayList<Point>();
    	for(int i=-1; i <= 1; i++)
        {   
            for( int j=-1; j<=1; j++)
            {
            	if( i==0 && j==0 ){ continue; }//don't consider the current pos
                int r = (row + i + landscape.length) % landscape.length;
                int c = (col + j + landscape[0].length) % landscape[0].length;
                if( !landscape[r][c].hasBacterium() )
                	nobac_coord.add(new Point(r,c));
            }
        }
        if( nobac_coord.size() > 0){        	
        	int dest = rng.nextInt(nobac_coord.size()); //pick one at random
            Point dest_point = nobac_coord.get(dest);

        	Bact daughter = new Bact(cal[2].time,bacSpeed,bacDivShape,bacDivScale,rng);
            landscape[dest_point.x][dest_point.y].occupy(daughter);
        	bacList.add(daughter);
        }
        cal[2] = new Event(this,cal[2].time+Agent.erlang(bacDivShape,bacDivScale,rng),Event.EventType.DIVIDE);
    }
}
