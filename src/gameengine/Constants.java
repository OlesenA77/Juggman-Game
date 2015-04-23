package gameengine;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * class Constants -- Common constants used by all components in the game
 *
 * @author  (Brian Brookwell)
 * @version (20100426)
 */
/*  Attribute       Description
 *
 *  device          Graphic display device attached to the monitor
 *  env             Graphic environmnet of the system
 *  gc              Configuration constants for the current display
 *  oldDisplayMode  Original display mode before the game starts.  It's used to
 *                  reset the system back to the original state after the game
 *                  completes
 *  bounds          Bounding rectangle for whole display
 *  bufferStrategy  Current startegy for multi-buffering of the display
 *  fuzzZone        Thickness of the invisible Surface components
 *
 *  Constant        Description
 *
 *  Sprite Constants
 *
 *  STATIC          Used for sprites that remain fixed in position
 *  MOVING          Used for Sprites that move with a fixed velocity
 *  ACCELERATED     Used for Sprites whose position depends on a velocity and an
 *                  acceleration.
 *  IN_FLUID        Simulates a simple fluid drag on a Sprite.  Fluid drag is
 *                  always exactly opposite the motion of the body.
 *
 *  UNITS_PER_FRAME  Speed and acceleration in pixels / frame
 *  UNITS_PER_SECOND Speed and acceleration in pixels / second
 *
 *  CYCLIC          The Animation sequence continuously repeats
 *  SEQUENCE        The Animation occurs once
 *
 *  Display Constants
 *
 *  BACKGROUND      New image buffer is filled with background color
 *  COPIED          New image buffer is exact copy of current image buffer
 *  PRIOR           New image buffer is the image from 2 buffers ago
 *  UNDEFINED       Image buffer initialization type not set yet.
 *
 *  Tuning Constants
 *  MAX_JUMP        The catch to ensure that Juggman can only jump for so long SET AT 1.5 seconds at 30fps
 *
 */
public class Constants {
    protected static GraphicsDevice         device;
    protected static GraphicsEnvironment    env;
    protected static GraphicsConfiguration  gc;
    protected static DisplayMode            oldDisplayMode;
    protected static BufferStrategy         bufferStrategy;
    protected static int                    screenWidth,
                                            screenHeight,
                                            centerX,
                                            centerY,
                                            centering,
                                            strategy;
    protected static double                 secondsPerUpdate     = 0.025;
    protected static int                    fuzzZone = 25;
    protected static boolean                isFullScreen;

    protected static final int BACKGROUND   = 0;
    protected static final int COPIED       = 1;
    protected static final int PRIOR        = 2;
    protected static final int UNDEFINED    = -1;

    protected static boolean   leftMouse, rightMouse, altDown, metaDown, ctrlDown, shiftDown;
    protected static BitSet    keyDown;
    protected static Point     mouse;

    public static final int STATIC          = 0;
    public static final int MOVING          = 1;
    public static final int ACCELERATED     = 2;
    public static final int IN_FLUID        = 3;
    public static final int MOMENTUM        = 4;

    public static final int UNITS_PER_FRAME = 0;
    public static final int UNITS_PER_SECOND= 1;

    public static final boolean CYCLIC      = true;
    public static final boolean SEQUENCE    = false;

    public static final int NOT_PLAYER_CENTERED = 0x00;
    public static final int PLAYER_CENTERED_X   = 0x01;
    public static final int PLAYER_CENTERED_Y   = 0x02;



    //========TUNEABLE CONSTANTS=======================
    /**
     * MAX_JUMP             Counter for The maximum time The player can jump
     * TERMINAL_VELOCITY    Maximum fall speed
     * CENTRE_STAGE          Where the screen centres to.
     */
    public static final int      MAX_JUMP = 10;
    public static final double   TERMINAL_VELOCITY =  1000;
    public static final int      CENTRE_STAGE = 550;
    public static final int      MAX_VELOCITY = 2250;
    public static final int      RAMMING_SPEED = 2000;

    //-------------------------------------------------
    public Constants() {
        env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        gc  = env.getDefaultScreenDevice().getDefaultConfiguration();
        device = env.getDefaultScreenDevice();
        oldDisplayMode = device.getDisplayMode();

        screenWidth = oldDisplayMode.getWidth();
        screenHeight = oldDisplayMode.getHeight();

        centerX = screenWidth >> 1;
        centerY = screenHeight >> 1;

        isFullScreen = device.isFullScreenSupported();

        keyDown = new BitSet();
        mouse = new Point (0, 0);
        }
    }