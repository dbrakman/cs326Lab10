
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class Macro extends Agent implements AgentInterface
{
    private static int IDBASE = 0;
    private double macSpeed;
    
    public Macro(double startTime, double macSpeed, double macDivRate, Random rng)
    {
        type = Agent.AgentType.MACROPHAGE;
        //Construct a Macrophage at startTime w/ movement speed macSpeed
        ID = IDBASE++;
        this.macSpeed = macSpeed;
        this.macDivRate = macDivRate;
        cal[0] = new Event(this,startTime+Agent.exponential(macSpeed,rng),Event.EventType.MOVE);
        cal[1] = new Event(this,Double.MAX_VALUE,Event.EventType.EAT);
        cal[2] = new Event(this,startTime+Agent.exponential(macDivRate,rng),Event.EventType.DIVIDE);
        cal[3] = new Event(this,Double.MAX_VALUE,Event.EventType.STARVE);
        cal[4] = new Event(this,Double.MAX_VALUE,Event.EventType.UNDEF);
        row = col = -1; //should be overwritten as soon as the Mac is placed in the landscape
    }

    public Macro(double macSpeed, Random rng){ this(0.0,macSpeed,rng); }

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
            landscape[row][col].removeMacrophage(); //take me out of the current spot
            landscape[dest_point.x][dest_point.y].occupy(this); //put me in the dest spot
            cal[1] = new Event(this,cal[0].time,Event.EventType.EAT);
        } else if( nomacro_coord.size() > 0){//else if there's a spot w/ no macs
            int dest = rng.nextInt(nomacro_coord.size()); //pick one at random
            Point dest_point = nomacro_coord.get(dest);
            landscape[row][col].removeMacrophage(); //take me out of the current spot
            landscape[dest_point.x][dest_point.y].occupy(this); //put me in the dest spot
        }

        cal[0] = new Event(this,cal[0].time+Agent.exponential(macSpeed,rng),Event.EventType.MOVE);
        //^^schedule another movement
    }

    public void eat(Cell[][] landscape, ArrayList<Agent> bacList){
        if( landscape[row][col].hasBacterium() ){
            bacList.remove(landscape[row][col].removeBacterium());
            //^^remove the bact from the landscape and the bacList
        } else {
            throw new RuntimeException("attempted to eat absent bacterium");
        }
    }

    //Override Agent's divide method, even though Macs can't divide
    public void divide(Cell[][] landscape, ArrayList<Agent> bacList, Random rng)
    {
        ArrayList<Point> nomac_coord = new ArrayList<Point>();
        for(int i=-1; i <= 1; i++)
        {   
            for( int j=-1; j<=1; j++)
            {
                if( i==0 && j==0 ){ continue; }//don't consider the current pos
                int r = (row + i + landscape.length) % landscape.length;
                int c = (col + j + landscape[0].length) % landscape[0].length;
                if( !landscape[r][c].hasMacrophage() )
                    nomac_coord.add(new Point(r,c));
            }
        }
        if( nomac_coord.size() > 0){            
            int dest = rng.nextInt(nomac_coord.size()); //pick one at random
            Point dest_point = nomac_coord.get(dest);

            Macro daughter = new Macro(cal[2].time,macSpeed,macDivRate,rng);
            Cell cl = landscape[dest_point.x][dest_point.y];
            cl.occupy(daughter);
            macList.add(daughter);
        }
        cal[2] = new Event(this,cal[2].time+Agent.exponential(macDivRate,rng),Event.EventType.DIVIDE);
    }

    //Override Agent's starve method, even though Macs can't starve
    public void starve(Cell[][] landscape, ArrayList<Agent> bacList)
    {
        throw new RuntimeException("Macs can't starve yet");
    }

}
