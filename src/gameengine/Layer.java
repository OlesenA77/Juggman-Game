package gameengine;

import  java.awt.*;
import  java.util.*;
import  java.io.*;

/**
 * class Layer -- a single display layer in the game
 *
 * @author (your name)
 * @version (a version number or a date)
 */
abstract public class Layer {
    protected Point                     offset;
    protected java.util.List<Sprite>    sprite;
    protected int                       width, height;
    protected Sprite                    player;
    protected Surface[]                 surfaces;
    protected int                       SurfaceCount;

/**
 * Constructor for objects of class Layer
 */
    public Layer(int wC, int hC) {
        sprite = Collections.synchronizedList (new ArrayList<Sprite> (10));

        offset = new Point(0, 0);
        surfaces = new Surface[50];
        SurfaceCount = 0;
        }

    public Layer(Point startPt, int wC, int hC) {
        sprite = Collections.synchronizedList (new ArrayList<Sprite> (10));

        offset = startPt;
        surfaces = new  Surface[50];
        SurfaceCount = 0;
        }

    abstract protected void loadImages (String fName, int maxRow, int maxColumn)
                                                            throws IOException;

    protected void setLocation (Point newPt) {
        offset = newPt;
        }

    protected void setLocation (int x, int y) {
        offset.x = x;
        offset.y = y;
        }

    protected void setPlayer (Sprite pl) {
        player = pl;
        }

    protected void addSprite (Sprite newSprite) {
        sprite.add (newSprite);
        newSprite.layer = this;

//        if (newSprite instanceof SpriteSurface)
//            surfaces.add (((SpriteSurface)(newSprite)).surface);
        }

    protected void removeSprite (Sprite oldSprite) {
        oldSprite.layer = null;
        sprite.remove (oldSprite);

 //       if (oldSprite instanceof SpriteSurface)
 //          surfaces.remove (((SpriteSurface)(oldSprite)).surface);
        }

    protected void addSurface (Surface newSurface) {
        surfaces[SurfaceCount] = (newSurface);
        SurfaceCount++;
        }

    abstract protected void displayBackground (Graphics2D g);
    abstract protected void displayEntireBackground (Graphics2D g);
    abstract protected void display (Graphics2D g);
    }