import javax.swing.*;       // for all the Swing stuff (JWhatevers)

import java.awt.*;          // for Graphics, Graphics2D
import java.awt.geom.*;     // for Rectangle2D
import java.util.ArrayList; // for ArrayList

//======================================================================
//* This class provides a simple graphical interface that will display
//* the time-series information of number of macrophages and bacteria
//* across time at each integer time step.
//*
//* The constructor expects the maximum time and maximum number of agents
//* as arguments.
//*
//* The updateCounts() method will be called from within the simulation
//* code each time the simulation time crosses an integer boundary.
//*
//* Author:  Barry Lawson (blawson)
//* Date:    23 September 2009
//* Updated: 29 Mar 2016
//======================================================================
public class TimeSeriesDialog extends JFrame
{
    //*************************************************************************

    // define how big the entire window will be
    protected final int WINDOW_WIDTH  = 550;
    protected final int WINDOW_HEIGHT = 400;

    private DialogCanvas canvas;  // for drawing 

    // these are needed to properly scale the axes
    protected int maxAgents;
    protected int maxTime;
    protected int lastUpdateTime;

    // arrays of ints for the counts for display time series info
    protected ArrayList<Integer> macrophagesCounts;
    protected ArrayList<Integer> bacteriaCounts;

    //======================================================================
    //* public TimeSeriesDialog(int agents, int time)
    //* Constructor for the class.  Just creates three new array lists to
    //* hold the counts for displaying the time-series info, and sets up
    //* a few instance variables used when drawing the time-series curves.
    //* Finally, pops up a new window right before your very eyes!
    //======================================================================
    public TimeSeriesDialog(int maxAgents, int maxTime)
    {
        super();

        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setTitle("Time Series");

        this.maxAgents      = maxAgents;
        this.maxTime        = maxTime;
        this.lastUpdateTime = 0;

        this.canvas = new DialogCanvas(this);
        this.getContentPane().add(this.canvas);

        setVisible(true);

        macrophagesCounts = new ArrayList<Integer>();
        bacteriaCounts    = new ArrayList<Integer>();

    } // end TimeSeriesDialog constructor

    //======================================================================
    //* public void updateCounts( int numMacrophages, int numBacteria)
    //* This method will be invoked by the simulation code to update the
    //* counts at each integer time step.  It will then repaint the window,
    //* displaying the updated information.
    //======================================================================
    public void updateCounts( int numMacrophages, int numBacteria )
    {
        macrophagesCounts.add( new Integer(numMacrophages) );
        bacteriaCounts.add( new Integer(numBacteria) );

        this.canvas.repaint();  // rediplay with new counts on screen
        
    } // end updateCounts()


} // end class TimeSeriesDialog
