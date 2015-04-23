package gameengine;

import gameengine.ManagedImage;
import gameengine.ManagedImageFactory;
import  java.awt.*;
import  java.io.IOException;

/**
 * class AnimationSet -- defines the set of images required by a single
 * action of a Sprite.  Sprites must have at least one action and will
 * likely have more than one.  The AnimationSet also keeps track of which
 * image is the current one
 *
 * @author (Brian Brookwell)
 * @version (2010-10-01)
 */
public class AnimationSet {
    protected ManagedImage  image[];
    protected int           current;
    protected boolean       cyclic;

/**
 * Constructor for objects of class AnimationSet.  This version reads a
 * set of files having the form: XXXXXX.NN.png.   XXXXXX = Base file name
 * of the set, NN = number of the image in the image set.  NN starts at
 * 00 and goes up to 99.
 *
 * @param   name    XXXXXX portion of the filename
 * @param   length  Number of images in the sequence (0 .. length-1)
 * @throws  IOException     If the file name read fails
 */
//  BRB     2010-10-01

    public AnimationSet(String name, int length, boolean cyc)
                                                    throws IOException {
        image = new ManagedImage[length];
        cyclic = cyc;

        for (int i=0;i < length;i++)
            image[i] = ManagedImageFactory.get (
                            name + "." + ((i / 10) % 10) +
                                "" + (i % 10) + ".png");
        current = 0;
        }

/**
 * Constructor for objects of class AnimationSet.  This version connects
 * the images in the an array in the new AnimationSet.  Each ManagedImage
 * has it's count incremented to indicate another AnimationSet is using
 * the image
 *
 * @param   existing    The AnimationSet whose images are being used.
 */
//  BRB     2010-10-01

    public AnimationSet (AnimationSet existing, boolean cyc) {
        image = new ManagedImage[existing.image.length];
        cyclic = cyc;

        for (int i=0;i < existing.image.length;i++) {
            image[i] = existing.image[i];
            image[i].count++;
            }

        current = 0;
        }

/**
 * Constructor for objects of class AnimationSet.  This version retrieves
 * files given by an array of file name Strings that can't be built using
 * the XXXXXX.NN.png format
 *
 * @param   file    Array if image file names.
 * @throws  IOException     If the file name read fails
 */
//  BRB     2010-10-01

    public AnimationSet (String file[], boolean cyc)
                                                    throws IOException {
        image = new ManagedImage[file.length];
        cyclic = cyc;

        for (int i=0;i < file.length;i++)
            image[i] = ManagedImageFactory.get (file[i]);

        current = 0;
        }

/**
 * nextImage -- switches to the next image in the AnimationSet
 */
//  BRB     2010-10-01

    protected void nextImage () {
        current++;
        if (current >= image.length)
            if (cyclic)
                current = 0;
            else
                current = image.length - 1;
        }

/**
 * backImage -- switches to the previous image in the AnimationSet
 */
//  BRB     2010-10-01

    protected void backImage () {
        current--;
        if (current < 0)
            if (cyclic)
                current = image.length - 1;
            else
                current = 0;
        }

/**
 * firstImage -- switches to the first image in the AnimationSet
 */
//  BRB     2010-10-01

    protected void firstImage () {
        current = 0;
        }

/**
 * lastImage -- switches to the last image in the AnimationSet
 */
//  BRB     2010-10-01

    protected void lastImage () {
        current = image.length - 1;
        }

/**
 * display -- displays the current image in the AnimationSet
 *
 * @param   g   Graphics2D object to draw image to
 * @param   x   Horizontal location of the image
 * @param   y   Vertical location of the image
 */
//  BRB     2010-10-01

    protected void display (Graphics2D g, int x, int y) {
        image[current].display (g, x, y);
        }

/**
 * dispose -- disposes of all ManagedImage objects in the AnimationSet
 * and disposes of the associated array to reduce memory leaks
 */
//  BRB     2010-10-01

    protected void dispose() {
        for (int i=0;i < image.length;i++)
            image[i].dispose();

        image = null;
        }

/**
 * getWidth -- returns the width of the current AnimationSet Image
 */
//  BRB     2010-10-01

    protected int getWidth() {
        return image[current].bImage.getWidth();
        }

/**
 * getHeight -- returns the height of the current AnimationSet Image
 */
//  BRB     2010-10-01

    protected int getHeight() {
        return image[current].bImage.getHeight();
        }
    }