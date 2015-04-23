package gameengine;

import java.awt.geom.Rectangle2D;

/**
 * Abstract class Surface - A Surface is an invisible object that
 *          is solid (i.e. Sprites can stand on it)
 *
 * @author  (Brian Brookwell)
 * @version (2011-01-07)
 */
public abstract class Surface {
    protected abstract void                  shiftToSurface (Sprite current);
    protected abstract boolean               onSurface (Sprite current);
    protected abstract boolean               hasHit (Sprite current);
    protected abstract Rectangle2D.Double    getBounds();
    protected abstract void                  translate (double x, double y);
    protected abstract void                  translateX (double x);
    }
