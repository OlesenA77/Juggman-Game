package gameengine;

import java.util.*;

/**
 * class MapCell -- Maintains a list of Surfaces affecting one cell
 *      in a SurfaceTree
 *
 * @author  (Brian Brookwell)
 * @version (2011-01-07)
 */
public class MapCell {
    protected   ArrayList<Surface>  list;

/**
 * Constructor for objects of class MapCell
 */
    public MapCell() {
        super();

        list = new ArrayList<Surface> (5);
        }
    }
