import java.util.ArrayList;

public class Macro extends Agent implements AgentInterface
{
    type= MACROPHAGE;
    
    public Macro(int row, int col({
        this.row = row;
        this.col = col;
    }

    public void move(int[][] landscape, ArrayList<Agent> agents, Random rng)
    {
        //number of possible destinations: 8 - neighbors
        for(int i=-1; i <= 1; i++)
        {   
            for( int j=-1; j<=1; j++)
            {
                int r = (row + i + landscape.length) % landscape.length;
                int c = (col + j + landscape[0].length) % landscape[0].length;
            }
        }
    }
}
