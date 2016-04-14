import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class Macro extends Agent implements AgentInterface
{
    //type=Agent.AgentType.MACROPHAGE;

    private static int IDBASE = 0;
    private Rvgs rvg;
    private double macSpeed;
    
    public Macro(double startTime, double macSpeed, Random rng)
    {
        //Construct a Macrophage at startTime w/ movement speed macSpeed
        ID = IDBASE++;
        rvg = new Rvgs(rng);
        this.macSpeed = macSpeed;
        cal[0] = new Event(this,startTime+rvg.exponential(macSpeed),Event.EventType.MOVE);
        cal[1] = new Event(this,Double.MAX_VALUE,Event.EventType.EAT);
        cal[2] = new Event(this,Double.MAX_VALUE,Event.EventType.DIVIDE);
        cal[3] = new Event(this,Double.MAX_VALUE,Event.EventType.UNDEF);
        row = col = -1; //should be overwritten as soon as the Mac is placed in the landscape
    }

    public Macro(double macSpeed, Random rng){ super(0.0,macSpeed,rng); }

    public void move(Cell[][] landscape, Random rng)
    {
        //number of possible destinations: 8 - neighbors
        ArrayList<Point> bac_coord = new ArrayList<Point>();
        ArrayList<Point> nomacro_coord = new ArrayList<Point>();
        for(int i=-1; i <= 1; i++)
        {   
            for( int j=-1; j<=1; j++)
            {
                if( i==0 && j==0 ){ continue; }//don't consider the current pos as a move dest.
                int r = (row + i + landscape.length) % landscape.length;
                int c = (col + j + landscape[0].length) % landscape[0].length;
                if( landscape[r][c].hasBacterium() )
                    bac_coord.add(new Point(r,c));
                if( !landscape[r][c].hasMacrophage())
                    nomacro_coord.add(new Point(r,c));
            }
        }
        if( bac_coord.size() > 0 ){//if there's bacs in our Moore neighborhood
            int dest = rng.nextInt(bac_coord.size()); //pick one at random
            Point dest_point = bac_coord.get(dest);
            landscape[dest_point.x][dest_point.y].occupy(this); //move there
            cal[1] = new Event(this,cal[0].time,Event.EventType.EAT);
        } else if( nomacro_coord.size() > 0){//else if there's a spot w/ no macs
            int dest = rng.nextInt(nomacro_coord.size()); //pick one at random
            Point dest_point = nomacro_coord.get(dest);
            landscape[dest_point.x][dest_point.y].occupy(this); //move there
        }
        cal[0] = new Event(this,cal[0].time+rvg.exponential(macSpeed),Event.EventType.MOVE);
        //^^schedule another movement
    }

    public void eat(Cell[][] landscape, ArrayList<Agent> bacList){
        if( landscape[row][col].hasBacterium() ){
            bacList.remove(landscape[row][col].removeBacterium());
            //^^remove the bact from the landscape and the bacList
        } else {
            throw Exception("attempted to eat absent bacterium");
        }
    }

}
