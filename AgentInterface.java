/**
 * Interface defining the minimum set of methods any implementing agent class
 * must contain.  This interface also contains an enumerated definition, of
 * public access, to the types of agents.
 */

public interface AgentInterface
{
    // define (as public, so accessible throughout) the types of agents
    public enum AgentType { MACROPHAGE, BACTERIUM };

    /**************************************************************************
     * Accessor method returning the integer id of the corresponding agent.
     * @return an integer representing the id of the agent
     **************************************************************************/
    public int getID();

    /**************************************************************************
     * Accessor method returning the current row of the corresponding agent.
     * @return an integer representing the current row of the agent
     **************************************************************************/
    public int getRow();

    /**************************************************************************
     * Accessor method returning the current column of the corresponding agent.
     * @return an integer representing the current columnn of the agent
     **************************************************************************/
    public int getCol();

    /**************************************************************************
     * Accessor method returning the type of the corresponding agent.  Types
     * are defined as an enumerated type and accessible, for example, throughout 
     * your software implementation using AgentInterface.AgentType.MACROPHAGE,
     * or should you have a class Agent implementing AgentInterface as
     * Agent.AgentType.MACROPHAGE
     * 
     * @return an AgentType representing the type of agent, either MACROPHAGE or BACTERIUM
     **************************************************************************/
    public AgentType getType();


    /**************************************************************************
     * Mutator method to set the (new) row and column of the agent.
     * @param  row  the new row of the corresponding agent
     * @param  col  the new column of the corresponding agent
     **************************************************************************/
    public void setRowCol(int row, int col);
}
