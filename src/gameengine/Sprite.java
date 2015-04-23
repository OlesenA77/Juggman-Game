package gameengine;

import  java.awt.*;
import  java.awt.geom.*;


/**
 * class Sprite -- parent class for each component in the game.
 *
 * @author  (Brian Brookwell)
 * @version (20100426)
 */
/*  Attribute       Description
 *
 *  location        2D Location for the center of the sprite
 *  velocity        2D velocity vector for the sprite
 *  acceleration    2D acceleration for the sprite
 *  action          Index of the action being performed by Sprite
 *  type            Motion type of the Sprite (see Constants)
 *  lastLocation    Last display location of the sprite
 *  radius          Collision radius of the sprite
 *  drag            Fluid drag on the sprite
 *  layer           Layer that contains this Sprite
 */
public class Sprite {
    protected Point2D.Double     location, velocity, acceleration;
    protected AnimationSet       set[];
    protected boolean            visible, collidable;
    protected int                action, type;
    protected Point2D.Double     lastLocation;
    protected double             radius, drag;
    protected Layer              layer;
    protected Rectangle2D.Double BoundingBox;
    protected boolean            StillAliveFlag;
/**
 * Constructor for objects of class Sprite
 */
    public Sprite() {}

/**
 * initialize -- sets up a sprite's internal data for an immobile
 *               sprite.
 *
 * @param   s       Array of Animation Set objects for the Sprite
 * @param   loc     Location for the sprite
 * @param   r       Radius of circle defining collision hull
 * @param   coll    Allow collision detection
 */
    synchronized public void initialize (AnimationSet s[],
                                Point2D.Double loc, double r,
                                boolean coll) {
        set      = s;
        location = loc;
        lastLocation = PointFactory.create(0,0);
        radius   = r;
        type = Constants.STATIC;
        BoundingBox = new Rectangle2D.Double(location.x-(.5*getWidth()), location.y-(.5*getHeight()),getWidth(),getHeight());
        collidable = coll;
        StillAliveFlag=true;
        }

/**
 * initialize -- sets up a sprite's internal data for a fixed speed
 *               sprite.
 *
 * @param   s       Array of Animation Set objects for the Sprite
 * @param   loc     Location for the sprite
 * @param   vel     Velocity for the sprite
 * @param   r       Radius of circle defining collision hull
 * @param   coll    Allow collision detection
 * @param   units    UNITS_PER_FRAME or UNITS_PER_SECOND;
 */

    synchronized public void initialize (AnimationSet s[],
                                Point2D.Double loc, Point2D.Double vel,
                                double r, boolean coll, int units) {
        set = s;

        location = loc;
        lastLocation = PointFactory.create(0,0);
        velocity = vel;
        if (units == Constants.UNITS_PER_SECOND) {
            velocity.x *= Constants.secondsPerUpdate;
            velocity.y *= Constants.secondsPerUpdate;
            }
        radius   = r;

        type = Constants.MOVING;
        BoundingBox = new Rectangle2D.Double(location.x-(.5*getWidth()), location.y-(.5*getHeight()),getWidth(),getHeight());
        collidable = coll;
        StillAliveFlag=true;
        }
/**
 * initialize -- sets up a sprite's internal data for a fixed speed
 *               sprite.
 *
 * @param   s       Array of Animation Set objects for the Sprite
 * @param   loc     Location for the sprite
 * @param   vel     Velocity for the sprite
 * @param   acc     Acceleration for the sprite
 * @param   r       Radius of circle defining collision hull
 * @param   drg     Fluid drag on sprite
 * @param   coll    Allow collision detection
 * @param   units    UNITS_PER_FRAME or UNITS_PER_SECOND;
 */

    synchronized public void initialize (AnimationSet s[],
                                Point2D.Double loc, Point2D.Double vel,
                                Point2D.Double acc, double drg, double r,
                                boolean coll, int units) {
        set = s;

        location = loc;
        lastLocation = PointFactory.create(0,0);
        velocity = vel;
        acceleration = acc;
        if (units == Constants.UNITS_PER_SECOND) {
            velocity.x *= Constants.secondsPerUpdate;
            velocity.y *= Constants.secondsPerUpdate;

            acceleration.x *= Constants.secondsPerUpdate;
            acceleration.y *= Constants.secondsPerUpdate;
            }
        radius = r;

        type = Constants.IN_FLUID;
        BoundingBox = new Rectangle2D.Double(location.x-(.5*getWidth()), location.y-(.5*getHeight()),getWidth(),getHeight());
        collidable = coll;

        drag = drg;
        StillAliveFlag=true;
        }
/**
 * initialize -- sets up a sprite's internal data for a fixed speed
 *               sprite.
 *
 * @param   s       Array of Animation Set objects for the Sprite
 * @param   loc     Location for the sprite
 * @param   vel     Velocity for the sprite
 * @param   acc     Acceleration for the sprite
 * @param   r       Radius of circle defining collision hull
 * @param   coll    Allow collision detection
 * @param   units    UNITS_PER_FRAME or UNITS_PER_SECOND;
 */


    synchronized public void initialize (AnimationSet s[],
                                Point2D.Double loc, Point2D.Double vel,
                                Point2D.Double acc, double r,
                                boolean coll, int units) {
        set = s;

        location = loc;
        lastLocation = PointFactory.create(0,0);
        velocity = vel;
        acceleration = acc;
        if (units == Constants.UNITS_PER_SECOND) {
            velocity.x *= Constants.secondsPerUpdate;
            velocity.y *= Constants.secondsPerUpdate;

            acceleration.x *= Constants.secondsPerUpdate;
            acceleration.y *= Constants.secondsPerUpdate;
            }
        radius = r;
       BoundingBox = new Rectangle2D.Double(location.x-(.5*getWidth()), location.y-(.5*getHeight()),getWidth(),getHeight());
        type = Constants.ACCELERATED;

        collidable = coll;
        StillAliveFlag=true;
        }

    /**
     * Inertial and Gravity Sprite
     * Designed to maintain momentum
     */
     synchronized public void initializeM(AnimationSet s[], Point2D.Double loc, double Xacceleration, double Yacceleration, double r, boolean coll, int units){
         set = s;
         radius = r;
         collidable = coll;
         location = loc;
         lastLocation = PointFactory.create(0, 0);
         if(collidable==true){
            BoundingBox = new Rectangle2D.Double(location.x-(.5*getWidth()), location.y-(.5*getHeight()),getWidth(),getHeight());
         }

         velocity = PointFactory.create(0, 0);
         velocity.x = Xacceleration;

         velocity.y = Yacceleration;

         if (units == Constants.UNITS_PER_SECOND) {
            velocity.x *= Constants.secondsPerUpdate;
            velocity.y *= Constants.secondsPerUpdate;
            }

            StillAliveFlag=true;
         type = Constants.MOMENTUM;
     }

/**
 * display -- displays the current sprite's image at it's current location
 *
 * @param   g   Graphics object to display the sprite image to
 */
    synchronized protected void display (Graphics2D g, Point offset) {
    if(StillAliveFlag==true){
            set[action].nextImage();

            lastLocation.x = (int)(0.5 + location.x + offset.x -
                                    0.5 * set[action].getWidth());
            lastLocation.y = (int)(0.5 + location.y + offset.y -
                                    0.5 * set[action].getHeight());

        set[action].display (g, (int)lastLocation.x, (int)lastLocation.y);
        }
    }

/**
 * update -- updates the sprite's image and and position
 */
    synchronized protected void update() {
        //Update Collision Hulls for all Collidable Sprites
        if(StillAliveFlag==true){
        lastLocation.x = location.x;
        lastLocation.y = location.y;
        
        if(collidable == true){
        BoundingBox = new Rectangle2D.Double(location.x-(.5*getWidth()), location.y-(.5*getHeight()),getWidth(),getHeight());
        }
        switch (type) {

        case Constants.IN_FLUID:
                    double vSquared = velocity.x * velocity.x +
                                        velocity.y * velocity.y;
                    double vAcc = drag * vSquared;
                    double aX, aY;
                    double v = 1.0 / Math.sqrt (vSquared);

                    aX = acceleration.x - vAcc * velocity.x * v;
                    aY = acceleration.y - vAcc * velocity.y * v;

                    location.x += velocity.x + 0.5 * aX;
                    location.y += velocity.y + 0.5 * aY;

                    velocity.x += aX;
                    velocity.y += aY;
                    break;

        case Constants.ACCELERATED:
                    velocity.x = velocity.x + acceleration.x*Math.abs(acceleration.x);
                    velocity.y = velocity.y + acceleration.y*Math.abs(acceleration.y);

                    location.x = location.x + velocity.x;
                    location.y = location.y + velocity.y;


                    break;

        case Constants.MOVING:
                    location.x += velocity.x;
                    location.y += velocity.y;

        case Constants.MOMENTUM:
                    location.x = location.x + velocity.x;
                    location.y = location.y + velocity.y;

        default:    break;
        }
        }
    }
/**
 * hasHit -- determines whether two Sprites have collided.
 *
 * @param   other   Sprite being tested against this one
 */
   /* protected boolean hasHit (Sprite other) {
        if (collidable && other.collidable) {
            double dX = other.location.x - location.x;
            double dY = other.location.y - location.y;
            double r = other.radius + radius;
            return dX * dX + dY * dY < r * r;
            }

        return false;
        }*/
    protected boolean hasHit (Sprite other) {
        if (collidable && other.collidable) {
            if(BoundingBox.intersects(other.BoundingBox)){
                return true;
            }
        }

        return false;
        }

/**
 * getWidth -- returns the current width of the Sprite's image
 */
    protected int getWidth() {
        return set[action].getWidth();

        }

/**
 * getHeight -- returns the current height of the Sprite's image
 */
    protected int getHeight() {
        return set[action].getHeight();
        }

/**
 * dispose -- frees all main and volatile memory for a Sprite
 */
    synchronized protected void dispose() {
        switch (type) {
        case Constants.IN_FLUID:
        case Constants.ACCELERATED: PointFactory.dispose (acceleration);
        case Constants.MOVING:      PointFactory.dispose (velocity);
        case Constants.STATIC:      PointFactory.dispose (location);
            }
        

        for (int i=0;i < set.length;i++)
            set[i].dispose();

        set = null;
        location = null;
        velocity = null;
        acceleration = null;
        StillAliveFlag=false;
        collidable=false;
        }
    }