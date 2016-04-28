import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class Bact extends Agent implements AgentInterface
{
    private static int IDBASE = 0;
    private double bacSpeed;
    private int bacDivShape;
    private double bacDivScale;
    private double resource;
    private double consumptionRate;

    public Bact(double startTime, double bacSpeed, int bacDivShape,
    			 double bacDivScale, double res, double consumptionRate,
                 Random rng)
    {
        //Construct a Bact at startTime w/ movement speed macSpeed
        type=Agent.AgentType.BACTERIUM;
        ID = IDBASE++;
        this.bacSpeed = bacSpeed;
        this.bacDivShape = bacDivShape;
        this.bacDivScale = bacDivScale;
        this.resource = res;
        this.consumptionRate = consumptionRate;
        cal[0] = new Event(this,startTime+Agent.exponential(bacSpeed, rng),Event.EventType.MOVE);
        cal[1] = new Event(this,Double.MAX_VALUE,Event.EventType.EAT);
        cal[2] = new Event(this,startTime+Agent.erlang(bacDivShape,bacDivScale, rng),Event.EventType.DIVIDE);
        cal[3] = new Event(this,Double.MAX_VALUE,Event.EventType.UNDEF);
        row = col = -1; //should be overwritten as soon as the Mac is placed in the landscape
    }

    public Bact(double bacSpeed, int bacDivShape, double bacDivScale, double res,
        double consumptionRate, Random rng){ 
    	this(0.0, bacSpeed, bacDivShape, bacDivScale, res, consumptionRate, rng); 
    }

    //move
    public void move(Cell[][] landscape, Random rng)
    {
        //Alright, so we're about to leave our current cell.
        // 1) Pick up the resources g that have grown since we moved in
        Cell cl = landscape[row][col];
        double t0 = cl.getTimeLastDepleted();
        double t1 = cal[0].time;
        cl.setResource( (t1-t0)*cl.getRate() ); //calculate resources g
        consume(cl,t1);  //consume resources g
        // 2) Reduce internal resources by consumption amt c
        resource = resource - (t1-t0)*consumptionRate;
            //2b) If we should've starved, throw error
            if(resource < 0){ throw new RuntimeException("shoulda starved");}
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
            landscape[row][col].removeBacterium(); //take this object out of its current spot
            landscape[dest_point.x][dest_point.y].occupy(this); //put me in the dest spot
            if( landscape[dest_point.x][dest_point.y].hasMacrophage() )
            {
                Macro m = landscape[dest_point.x][dest_point.y].getMacrophage();
                Event e = new Event(m,cal[0].time,Event.EventType.EAT);
                m.putEvent(e);
                System.out.println("Whoops! I moved into a Macrophage and need to be eaten!");
            }
 		}
        // Now we've entered a new cell:
            // 1) Pick up its resources
                // 1b) Update the cell's timeLastDepleted
        cl = landscape[row][col];
        consume(cl,t1);
            // 2) Calculate time of next would-be move
        double t2 = t1+Agent.exponential(bacSpeed,rng);
            // 3) Calculate cell's resource growth g over that time
        double aPlusR = resource; //we've already added r to a in "consume(c,t1)"
        double g = (t2-t1)*cl.getRate();
            // 4) Calculate internal consumption c over that time
        double amtC = (t2-t1)*consumptionRate;
            // 5) Decide if we need to starve first
        if(aPlusR + g - amtC <= 0)
        {
            //schedule a starve event
            double tX = ((t2-t1)/(amtC-g))*resource + t1;
            cal[3] = new Event(this,tX,Event.EventType.STARVE);
        }
        cal[0] = new Event(this,t2,Event.EventType.MOVE);
        //^^schedule another movement
    }

    //Override Agent's eat method, even though bacts can't eat:
    public void eat(Cell[][] landscape, ArrayList<Agent> bacList)
    {
        throw new RuntimeException("Bacs can't eat, dummy!");
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

        	Bact daughter = new Bact(cal[2].time,bacSpeed,bacDivShape,bacDivScale,
                resource/2.0, consumptionRate, rng);
            landscape[dest_point.x][dest_point.y].occupy(daughter);
        	bacList.add(daughter);
        }
        resource = resource/2.0; //halve this bact's resources during division
        cal[2] = new Event(this,cal[2].time+Agent.erlang(bacDivShape,bacDivScale,rng),Event.EventType.DIVIDE);
    }

    public void consume(Cell c, double time)
    {
        resource = resource + c.getResource();  //consume all the resource in a cell
        c.setResource(0);
        c.setTimeLastDepleted(time);
    }

    public void starve(Cell[][] landscape, ArrayList<Agent> bacList)
    {
        //remove this bac from the landscape and bacList
        landscape[row][col].removeBacterium();
        bacList.remove(this);
    }
}
