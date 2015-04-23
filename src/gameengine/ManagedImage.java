package gameengine;

import java.awt.*;              // Basic JAVA I/O and GUI
import java.awt.image.*;        // Basic Image handling
import javax.imageio.ImageIO;   // Image input
import java.io.*;               // File handling

/**
 * class ManagedImage -- This class is used to prevent multiple copies of an
 *          image.  Each ManagedImage contains the image itself plus a key
 *          name (the fileName used to read the image).  It also maintains a
 *          count so that the resources are not freed until the image is no
 *          longer in use.
 *
 * @author (Brian Brookwell)
 * @version (2010-09-29)
 *
 * Test Classes:    TestManagedImage, TestManagedImageFactory
 */
public class ManagedImage implements Comparable {
    protected static final boolean          useResource = false;

    protected String                        fileName;
    protected BufferedImage                 bImage;
    protected int                           count;

/**
 * Constructor for objects of class ManagedImage
 */
//  BRB     2010-09-29

    public ManagedImage () {}

/**
 * initialize -- reads a file from a resource JAR or from the hard disk
 *
 * @param   filename    File to read the BufferedImage from to init the
 *                      ManagedImage
 * @throws  IOException Problems with open or reading the image file
 */
//  BRB     2010-09-29

    protected void initialize (String filename) throws IOException {
        fileName = filename;

        if (useResource) { // JAR File Resource
            ClassLoader cl = this.getClass().getClassLoader();
            bImage = ImageIO.read( cl.getResource("images/" + filename));
            }
        else // Unbundled Resource
            bImage = ImageIO.read( new File("images/" + filename) );
        count = 1;
        }

/**
 * display -- displays the volatile image to a graphics object.  If the
 *            VolatileImage has been lost, a new one is allocated and the
 *            image redrawn from the RAM version.
 *
 * @param   g   Graphics object to draw to
 * @param   x   Horizintal position of the object
 * @param   y   Vertical position of the object
 */
// BRB      2010-09-29

    protected void display (Graphics2D g, int x, int y) {
        g.drawImage (bImage, x, y, null);
        }

/**
 * dispose -- Disposes of this ManagedImage if it isn't being used any
 *            longer in the game.  The images are flushed only if the
 *            access count goes to zero.
 */
//  BRB     2010-09-29

    protected void dispose () {
        count--;

        if (count == 0) {
            bImage.flush();

            bImage = null;
            }
        }

/**
 * equals -- determines whether two objects are equal assuming the object
 *           passed as a parameter is a ManagedImage
 *
 * param    other   ManagedImage being compared to this one
 */
//  BRB     2010-09-29

    public boolean equals (Object other) {
        return fileName.equals (((ManagedImage)other).fileName);
        }

/**
 * compareTo -- determines whether two objects are less than, equal or
 *         greater than assuming the object passed as a parameter is a
 *         ManagedImage
 *
 * param    other   ManagedImage being compared to this one
 */
//  BRB     2010-09-29

    public int compareTo (Object other) {
        return fileName.compareTo (((ManagedImage)other).fileName);
        }
    }