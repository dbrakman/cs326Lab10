import javax.swing.*;       // for all the Swing stuff (JWhatevers)

import java.awt.*;          // for Graphics, Graphics2D
import java.awt.geom.*;     // for Rectangle2D
import java.util.ArrayList; // for ArrayList

public class DialogCanvas extends JPanel
{
    // provide the (x,y) locations for the corners of the rectangle
    // that comprise the graph
    private final int GRAPH_UPPER_LEFT_X  =  75;
    private final int GRAPH_UPPER_LEFT_Y  =  25;
    private final int GRAPH_LOWER_RIGHT_X = 475;
    private final int GRAPH_LOWER_RIGHT_Y = 325;

    private double horizontalStep;  // one step along horizontal, scaled to max
    private double verticalStep;    // one step along vertical, scaled to max

    private TimeSeriesDialog dialog;  // the containing JFrame

    public DialogCanvas(TimeSeriesDialog container)
    {
        dialog = container;

        // determine what one step in the horizontal and vertical directions
        // means by taking the total distance and dividing it by the maximum
        // associated with that direction
        horizontalStep = (GRAPH_LOWER_RIGHT_X - GRAPH_UPPER_LEFT_X) 
                        / (double) dialog.maxTime;

        verticalStep   = (GRAPH_LOWER_RIGHT_Y - GRAPH_UPPER_LEFT_Y) 
                        / (double) dialog.maxAgents;
    }

    //======================================================================
    //* public void paintComponent(Graphics g)
    //* What happens whenever the time series window is (re)drawn.
    //======================================================================
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        // safest to create a copy of the graphics component -- one must
        // ensure that no changes are made to the original
        Graphics2D graphics = (Graphics2D) g.create();

        graphics.clearRect(0, 0, dialog.WINDOW_WIDTH, dialog.WINDOW_HEIGHT);
        drawTimeSeries(graphics);
        drawAxes(graphics);

        revalidate();

        // get rid of the graphics copy
        graphics.dispose();

    } // end paintComponent()

    //======================================================================
    //* private void drawTimeSeries(Graphics2D graphics)
    //* This method draws time-series curves by plotting three 2x2 
    //* rectangles at each integer time point: a green one to indicate
    //* the number of macrophages; a red one to indicate
    //* the number of bacteria.
    //======================================================================
    private void drawTimeSeries(Graphics2D graphics)
    {
        // first draw the macrophages
        graphics.setPaint(Color.green);
        for (int i = 0; i < dialog.macrophagesCounts.size(); i++)
        {
            graphics.fillRect( 
               GRAPH_UPPER_LEFT_X + (int)((i + 1) * horizontalStep),
               GRAPH_LOWER_RIGHT_Y - 
                  (int)(dialog.macrophagesCounts.get(i).intValue() * verticalStep),
               2, 2 );  // a 2x2 rectangle
        }

        // then draw the bacteria
        graphics.setPaint(Color.red);
        for (int i = 0; i < dialog.bacteriaCounts.size(); i++)
        {
            graphics.fillRect( 
               GRAPH_UPPER_LEFT_X + (int)((i + 1) * horizontalStep),
               GRAPH_LOWER_RIGHT_Y - 
                  (int)(dialog.bacteriaCounts.get(i).intValue() * verticalStep),
               2, 2 );  // a 2x2 rectangle
        }

    } // end drawTimeSeries()

    //======================================================================
    //* private void drawAxes(Graphics2D graphics)
    //* This method draws the vertical and horizontal axes, tick marks,
    //* and labels for the time series graphic.
    //======================================================================
    private void drawAxes(Graphics2D graphics)
    {
        graphics.setPaint(Color.black);

        // the left axis
        graphics.drawLine( GRAPH_UPPER_LEFT_X, GRAPH_UPPER_LEFT_Y,
                           GRAPH_UPPER_LEFT_X, GRAPH_LOWER_RIGHT_Y );

        // the right axis
        graphics.drawLine( GRAPH_UPPER_LEFT_X, GRAPH_LOWER_RIGHT_Y,
                           GRAPH_LOWER_RIGHT_X, GRAPH_LOWER_RIGHT_Y );

        // the static labels etc.
        graphics.drawString( "Agents", 
            10,
            GRAPH_UPPER_LEFT_Y + (GRAPH_LOWER_RIGHT_Y - GRAPH_UPPER_LEFT_Y) / 2 );

        graphics.drawString( "0", 
            GRAPH_UPPER_LEFT_X - 10, 
            GRAPH_LOWER_RIGHT_Y + 10 );

        graphics.drawString( "Time", 
            GRAPH_UPPER_LEFT_X + (GRAPH_LOWER_RIGHT_X - GRAPH_UPPER_LEFT_X) / 2,
            GRAPH_LOWER_RIGHT_Y + 30 );


        // put label for max number of agents near upper left
        FontMetrics font  = graphics.getFontMetrics();
        String      label = String.valueOf( dialog.maxAgents );
        Rectangle2D rect  = font.getStringBounds(label, graphics);

        graphics.drawString( label, 
            GRAPH_UPPER_LEFT_X - 10 - (int)(rect.getWidth()),
            GRAPH_UPPER_LEFT_Y + ((int)(rect.getHeight()) / 2) );

        // put label for max time near upper left
        label = String.valueOf( dialog.maxTime );
        rect  = font.getStringBounds(label, graphics);

        graphics.drawString( label, 
            GRAPH_LOWER_RIGHT_X + 10,
            GRAPH_LOWER_RIGHT_Y + ((int)(rect.getHeight()) / 2) );

        // put tick marks on the left axis -- ten of 'em each just for estimating
        int tickSeparation = (GRAPH_LOWER_RIGHT_Y - GRAPH_UPPER_LEFT_Y) / 10;
        for (int i = 0; i < 10; i++)
        {
            graphics.drawLine( GRAPH_UPPER_LEFT_X - 5, 
                               GRAPH_UPPER_LEFT_Y + (i * tickSeparation),
                               GRAPH_UPPER_LEFT_X,      
                               GRAPH_UPPER_LEFT_Y + (i * tickSeparation) );
        }

        // put tick marks on the left axis -- ten of 'em each just for estimating
        tickSeparation = (GRAPH_LOWER_RIGHT_X - GRAPH_UPPER_LEFT_X) / 10;
        for (int i = 0; i < 10; i++)
        {
            graphics.drawLine( GRAPH_LOWER_RIGHT_X - (i * tickSeparation),
                               GRAPH_LOWER_RIGHT_Y,
                               GRAPH_LOWER_RIGHT_X - (i * tickSeparation),
                               GRAPH_LOWER_RIGHT_Y + 5 );
        }

    } // end drawAxes()
}

