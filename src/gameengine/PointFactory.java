package gameengine;

import java.util.ArrayList;
import  java.awt.geom.Point2D;
import  java.awt.geom.Point2D.Double;

/**
 * class PointFactory -- allocates a block of 2D Point objects for use in
 * the game.  It handles all deletions and allocations of these objects as
 * the game progresses to minimize the use of JAVA's Garbage Collector.
 *
 * @author (Brian Brookwell)
 * @version (2010-10-01)
 */
public class PointFactory {
    static protected ArrayList<Point2D.Double> available;

/**
 * Constructor for objects of class PointFactory.  Sets up the ArrayList
 * that holds the allocated 2D Points and then allocates the number of
 * points passed as a parameter
 *
 * @param   start   Number of elements in ArrayList at the start
 */
//  BRB     2010-10-01

    public PointFactory(int start) {
        available = new ArrayList<Point2D.Double>(start);

        for (int i=0;i < start;i++)
            available.add (new Point2D.Double(0.0, 0.0));
        }

/**
 * create -- Generates a Point from the factory
 *
 * @param   x   Horizontal value of the point
 * @param   y   Vertical value of the point
 */
//  BRB     2010-10-01

    synchronized static public Point2D.Double create (double x, double y){
        Point2D.Double temp;
        if (available.size() == 0)
            temp = new Point2D.Double();
        else
            temp = available.remove(available.size() - 1);

        temp.x = x;
        temp.y = y;

        return temp;
        }

/**
 * dispose -- returns a point to the available point ArrayList
 *
 * @param   pt  Point being disposed
 */
//  BRB     2010-10-01

    synchronized static public void dispose (Point2D.Double pt) {
        available.add (pt);
        }

/**
 * dispose -- frees all allocated points and the associated ArrayList
 */
//  BRB     2010-10-01

    synchronized public void dispose() {
        while (!available.isEmpty())
            available.remove(available.size() - 1);

        available = null;
        }
    }