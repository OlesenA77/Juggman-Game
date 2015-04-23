package gameengine;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * class SurfaceFlat -- defines an invisible solid rectanlge that
 *      Sprites can stand upon
 *
 * @author  (Brian Brookwell)
 * @version (2011-01-07)
 */
public class SurfaceFlat extends Surface {
    protected Rectangle2D.Double bounds, topSurface;
    public double    top;

/**
 * Constructor for objects of class SurfaceFlat
 *
 * @param   x, y        Upper left corner of the SurfaceFlat
 * @param   w           Rectangle width
 * @param   thickness   Thickness of the rectangle
 */
    public SurfaceFlat (int x, int y, int w, int thickness) {
        bounds = new Rectangle2D.Double (x, y, w, thickness);
        topSurface = new Rectangle2D.Double (x, y, w, (int)Constants.fuzzZone);
        }

/**
 * shiftToSurface -- Moves a Sprite that is inside the SurfaceFlat object
 *      to its upper surface
 *
 * @param   current     Sprite being moved
 */
    protected void shiftToSurface (Sprite current) {
        current.location.y = top - current.radius;
        }

/**
 * onSurface -- returns true if the Sprite is within the SurfaceFlat and
 *          close to t he upper edge
 *
 * @param   current     Sprite being moved
 */
    protected boolean onSurface (Sprite current) {
        return topSurface.contains ((int)(current.location.x + 0.5),
                        (int)(current.location.y + current.radius + 0.5));
        }
/**
 * hasHit -- Returns true if the Sprite is inside the SurfaceFlat
 *
 * @param   current     Sprite being moved
 */
    protected boolean hasHit (Sprite current) {
        return bounds.contains ((int)(current.location.x + 0.5),
                        (int)(current.location.y + current.radius + 0.5));
        }

/**
 * getBounds -- returns the rectangle that encloses the SurfaceFlat
 */
    protected Rectangle2D.Double getBounds() {
        return bounds;
        }

/**
 * translate -- Moves the SlopeSurface to a new location
 *
 * @param   x   New horizontal position
 * @param   y   New vertical position
 */
    protected void translate (double x, double y) {
        topSurface.x = x;
        topSurface.y = y;
        bounds.x = x;
        bounds.y = y;
        }
    protected void translateX (double x){
        topSurface.x = topSurface.x+x;
        bounds.x = bounds.x+x;
    }
    }