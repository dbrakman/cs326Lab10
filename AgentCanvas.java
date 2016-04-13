import javax.swing.*;      // for all the Swing stuff (JWhatevers)
import java.util.*;        // for ArrayList
import java.awt.*;         // for Graphics, Graphics2D
import java.awt.event.*;   // for MouseListener
import java.awt.geom.*;    // for Rectangle2D

/**
 * This class implements the agent canvas for our agent-based simulation,
 * specifically drawing the grid and (eventually) drawing the agents
 * that will run rampant thereupon.  A canvas must appear in the main GUI
 * window (JFrame) -- see AgentGUI.
 */
class AgentCanvas extends JPanel implements MouseListener
{
    private int viewportX;     // where in the viewport to start drawing image/grid
    private int viewportY;     // (we want the image centered)

    private int gridWidth;     // width of grid in cells
    private int gridHeight;    // height of grid in cells

    private int cellGUISize;  // maximum drawn size of a cell

    private SimulationManager simulation; // a reference to the simulation object

    /**************************************************************************
    //* Constructor for the agent canvas.
     *
     * @param  theSimulation  a SimulationManager reference to the simulation
     * @param  gridRows       the number of rows to be drawn in the environment
     * @param  gridCols       the number of cols to be drawn in the environment
     * @param  cellGUISize    the size of a cell, and maximum size of an agent in a cell
     **************************************************************************/
    public AgentCanvas(SimulationManager theSimulation, 
                       int gridRows, int gridCols, int cellGUISize)
    {
        this.simulation = theSimulation;

        addMouseListener(this);  // to handle mouse clicks

        this.cellGUISize = cellGUISize;
        this.gridHeight   = gridRows;
        this.gridWidth    = gridCols;

        updateGrid();
    }

    /**************************************************************************
     * Accessor method returning the grid width (number of columns).
     * @return  an integer representing the grid width (number of columns)
     **************************************************************************/
    public int getGridWidth()  { return gridWidth; }

    /**************************************************************************
     * Accessor method returning the grid height (number of rows).
     * @return  an integer representing the grid height (number of rows)
     **************************************************************************/
    public int getGridHeight() { return gridHeight; }

    /**************************************************************************
     * Method to redraw the canvas, simply by call repaint
     **************************************************************************/
    public void updateGrid()
    {
        // call repaint to redisplay the new background, then agents & grid
        repaint();  // calls the defined paintComponent method
    }

    /**************************************************************************
     * Method implementing everything that should happen when the agent canvas
     * is (re)drawn.  This method makes use of an ArrayList<AgentInterface>
     * via the getListOfAgents() method that any agent class must implement.
     *
     * @param   g  a Graphics component corresponding to this JPanel
     **************************************************************************/
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        // safest to create a copy of the graphics component -- one must
        // ensure that no changes are made to the original
        Graphics2D    graphics = (Graphics2D) g.create();

        JViewport   viewport = null;
        JScrollPane scrollPane = null;
        Insets      borders = null;

        int viewportWidth  = 0;
        int viewportHeight = 0;
        int imageWidth     = gridWidth * cellGUISize;
        int imageHeight    = gridHeight * cellGUISize;

        // make sure that we're grabbing onto the viewport of the scroll pane
        Component ancestor = getParent();
        if (ancestor == null || !(ancestor instanceof JViewport))
        {
            //Exception e = new Exception(
            //    "AgentCanvas instance must be within JScrollPane instance");
            //e.printStackTrace();
            //return;

            viewportWidth  = imageWidth;
            viewportHeight = imageHeight;

            borders = new Insets(0,0,0,0);
        }
        else
        {
            // presumably we have the viewport of scroll pane containing 'this'
            viewport  = (JViewport) ancestor;

            viewportWidth  = viewport.getWidth();
            viewportHeight = viewport.getHeight();

            scrollPane = (JScrollPane) viewport.getParent();
            borders = scrollPane.getInsets();
        }

        // Note that drawImage automatically scales the image to fit that 
        // rectangle.
        int renderWidth  = gridWidth * cellGUISize;
        int renderHeight = gridHeight * cellGUISize;

        // determine the starting (x,y) in the viewport where the image
        // will be drawn
        viewportX = (int) Math.max((viewportWidth - renderWidth)  / 2,  0);
        viewportY = (int) Math.max((viewportHeight - renderHeight) / 2, 0);

        // in case there was a previous image, clear things out
        graphics.clearRect(0, 0, viewportWidth, viewportHeight);

        // now draw the agents
        ArrayList<AgentInterface> agentsList = simulation.getListOfAgents();
        for (int i = 0; i < agentsList.size(); i++)
        {
            AgentInterface a = agentsList.get(i);

            // make sure not to draw any agent outside the image boundaries;
            // remember that graphics x corresponds to column and graphics y
            // corresponds to row
            if ((a.getRow() >= 0) && (a.getCol() >= 0) &&
                ((a.getRow() * cellGUISize) + cellGUISize <= renderHeight) &&
                ((a.getCol() * cellGUISize) + cellGUISize <= renderWidth))
            {
                int guiX = viewportX + (a.getCol() * cellGUISize);
                int guiY = viewportY + (a.getRow() * cellGUISize);

                if (a.getType() == AgentInterface.AgentType.MACROPHAGE)
                {
                    graphics.setPaint(new Color(0,150,0)); // dark green
                    graphics.fillRect(guiX, guiY, cellGUISize, cellGUISize);
                }
                else
                {
                    // draw the bacteria in red, and slightly smaller
                    graphics.setPaint(Color.red);
                    int size   = (int)(cellGUISize*0.6);
                    int offset = (int)(cellGUISize*0.2); // 0.2 + 0.6 + 0.2 = 1.0
                    graphics.fillRect(guiX + offset, guiY + offset, size + 1, size + 1);
                }

                // draw the agent's ID on top of colored agent box
                String agentID    = "" + a.getID();
                FontMetrics font  = graphics.getFontMetrics();
                Rectangle2D rect  = font.getStringBounds(agentID, graphics);
                int textWidth     = (int)(rect.getWidth());
                int textHeight    = (int)(rect.getHeight());
                int startX        = (cellGUISize - textWidth) / 2;
                int startY        = (cellGUISize - textHeight) / 2;
        
                graphics.setPaint(Color.white);
                // the x,y provided to draw string corresponds to lower LHS;
                // hence, add textHeight to the y component
                graphics.drawString(agentID, guiX + startX, 
                                             guiY + startY + textHeight - 1);
            }
        }

        // draw the grid last so that it will overlay the agent squares 
        drawGrid(graphics, viewportX, viewportY, renderWidth, renderHeight);

        // show the number of infected/uninfected agents
        drawAgentInfo(graphics, viewportX, viewportY, 
                renderWidth, renderHeight, borders);

        revalidate();

        // get rid of the graphics copy
        graphics.dispose();
    }

    /**************************************************************************
     * Method to draw a grid on top of (any) background and agents.
     * @param  graphics  a Graphics2D version of this JPanel
     * @param  x         horizontal position to start drawing from 
     * @param  y         vertical position to start drawing from 
     * @param  width     width of grid to be drawn
     * @param  height    heightof grid to be drawn
     **************************************************************************/
    private void drawGrid(Graphics2D graphics, int x, int y,
                          int width, int height)
    {
        graphics.setPaint(Color.black);

        // the columns
        for (int row = 0; row < width / cellGUISize; row++)
            graphics.drawLine(x + (row * cellGUISize), y, 
                              x + (row * cellGUISize), y + height - 1);

        // the rows
        for (int col = 0; col < height / cellGUISize; col++)
            graphics.drawLine(x, y + (col * cellGUISize),
                              x + width - 1, y + (col * cellGUISize));

        // the border
        graphics.drawLine(x, y, x, y + height);
        graphics.drawLine(x, y, x + width, y);
        graphics.drawLine(x + width, y, x + width, y + height);
        graphics.drawLine(x, y + height, x + width, y + height);
    }

    //======================================================================
    //* private void drawAgentInfo(Graphics2D graphics, int x, int y,
    //======================================================================
    /**************************************************************************
     * Method to draw all the agents in the simulation.
     *
     * @param  graphics  a Graphics2D version of this JPanel
     * @param  x         horizontal position to start drawing from 
     * @param  y         vertical position to start drawing from 
     * @param  width     width of agent to be drawn
     * @param  height    height of agent to be drawn
     * @param  borders   reference to the borders of the container
     **************************************************************************/
    private void drawAgentInfo(Graphics2D graphics, int x, int y,
                               int width, int height, Insets borders)
    {
        final int verticalSpaceBeforeText = 20;

        // a string to appear at the bottom listing current counts
        String agentInfo = "Macrophages: " + simulation.getNumMacrophages() 
            + "    " + "Bacteria: " + simulation.getNumBacteria();

        // Find the size of string in the font being used by the current
        // Graphics2D context.
        FontMetrics font = graphics.getFontMetrics();
        Rectangle2D rect = font.getStringBounds(agentInfo, graphics);

        int textWidth     = (int)(rect.getWidth());
        int textHeight    = (int)(rect.getHeight());
        int startStringAt = (width - textWidth)  / 2;

        // center text horizontally (max sure left side at least draws w/in
        // the viewport window -- i.e., x at least 0)
        graphics.drawString(agentInfo, Math.max(x + startStringAt, 0), 
                                       y + height + verticalSpaceBeforeText);

        // Make sure the image plus text (which may be a new one loaded in) is
        // visible in the scroll pane.  If this isn't somewhere, scrollbars 
        // won't work in the main screen's encompassing JScrollPane.
        setPreferredSize(
            new Dimension(
                    Math.max(width + borders.left + borders.right, textWidth),
                     height + borders.top + borders.bottom 
                            + verticalSpaceBeforeText + textHeight));

    }

    /**************************************************************************
     * Method that takes physical (x,y) and converts it to logical (x,y) in the
     * context of the agent/grid size.  Recall that x and y are reversed with
     * respect to GUI and 2D arrays -- i.e., in the GUI, (0,0) appears in the 
     * bottom left.
     *
     * @param   x  the physical x value from a mouse click 
     * @param   y  the physical y value from a mouse click 
     * @return  a Point corresponding to the converted (array-logical) coordinate pair
     **************************************************************************/
    private Point convertToLogical(int x, int y)
    {
        // convert to next lowest multiple of cellGUISize using int division
        int newX = (x - viewportX) / cellGUISize;
        int newY = (y - viewportY) / cellGUISize;

        return new Point(newX, newY);
    }

    /**************************************************************************
     * Method (currently not used) that can allow for individual inspection of
     * an agent appearing at a logical (in array terms) point on the screen.
     *
     * @param   p  a Point corresponding to the array-logical conversion of a GUI (x,y) mouse click
     **************************************************************************/
    private void inspectAgentAt(Point p)
    {
/*
        // remember canvas x,y are reveresed from row,col
        Agent agent = simulation.getAgentAt((int) p.getY(), (int) p.getX());

        if (agent != null)
        {
            // use hash map to see if the window is already created, and if so,
            // just focus; otherwise, create a new inspector window
            double time = simulation.getTime();
            String id = agent.getRow() + "," + agent.getCol() + " " + time;
            AgentInspector window = AgentInspector.windowList.get(id);
            if (window == null)
            {
                window = new AgentInspector(agent, time, id);
                AgentInspector.windowList.put(id, window);
            }
            window.setVisible(true);
        }
*/
    }

    //************************************************************
    //* The following methods are for implementing MouseListener.
    //* We're really only interested in mouseClicked().
    //************************************************************
    public void mouseClicked(MouseEvent e)
    {
        // grab the physical (x,y) of the mouse click, convert to
        // "array logical", and then inspect the agent there...
        Point p = convertToLogical(e.getX(), e.getY());
        inspectAgentAt(p);
    }

    public void mousePressed(MouseEvent e)  {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e)  {}
    public void mouseExited(MouseEvent e)   {}
}
