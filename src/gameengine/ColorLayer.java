package gameengine;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;

/**
 * class ColorLayer -- Fills the entire background with a single color
 *
 * @author  (Brian Brookwell)
 * @version (2010-11-08)
 */
/*  Variable        Description
 *
 *  background      Background color to apply
 */

public class ColorLayer extends Layer {
    protected Color background;

/**
 * Constructor for objects of class ColorLayer
 *
 * @param   wC      Width of the layer in pixels
 * @param   hC      Height of the layer in pixels
 * @param   back    Background color
 */
    public ColorLayer (int wC, int hC, Color back) {
        super(wC, hC);

        background = back;
        }

/**
 * Constructor for objects of class ColorLayer
 *
 * @param   startPt Location of the upper left corner of the layer
 * @param   wC      Width of the layer in pixels
 * @param   hC      Height of the layer in pixels
 * @param   back    Background color
 */
    public ColorLayer(Point startPt, int wC, int hC, Color back) {
        super(startPt, wC, hC);

        background = back;
        }

/**
 * displayBackground -- optimized display that only displays changed parts of the
 *      background
 *
 * @param   g       Graphics object to draw to
 * @param   bStrat
 */
    protected void displayBackground (Graphics2D g) {
        g.setColor (background);
        g.fillRect (0, 0, Constants.screenWidth, Constants.screenHeight);
        }

    protected void displayEntireBackground (Graphics2D g) {
        g.setColor (background);
        g.fillRect (0, 0, Constants.screenWidth, Constants.screenHeight);
        }

    synchronized protected void display (Graphics2D g) {
        /*displayBackground (g);

        for (Sprite current:sprite) {
            current.display (g, offset);
            }
        */
           displayBackground (g);

        switch (Constants.centering) {
        case Constants.NOT_PLAYER_CENTERED:
                    offset.x = 0;
                    offset.y = 0;
                    break;
        case Constants.PLAYER_CENTERED_X:
                    offset.x = (int)(Constants.centerX - player.location.x + 0.5);
                    offset.y = 0;
                    break;
        case Constants.PLAYER_CENTERED_Y:
                    offset.y = (int)(Constants.centerY - player.location.y + 0.5);
                    offset.x = 0;
                    break;
        case Constants.PLAYER_CENTERED_X | Constants.PLAYER_CENTERED_Y:
                    offset.x = (int)(Constants.centerX - player.location.x + 0.5);
                    offset.y = (int)(Constants.centerY - player.location.y + 0.5);
                    break;
                }

        for (Sprite current: sprite){
            current.display (g, offset);
        }
    }
    protected void loadImages (String fName, int maxRow, int maxColumn)
                                                            throws IOException {}
    }