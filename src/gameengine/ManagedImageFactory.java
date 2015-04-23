package gameengine;

import java.util.TreeMap;
import java.io.IOException;
import java.util.*;

/**
 * class BufferedImageFactory -- a binary tree based map structure used to
 *         ensure that each BufferedImage is read in once and only once.
 *
 * @author (Brian Brookwell)
 * @version (2010-10-01)
 *
 * Test Class:  TestBufferedImageFactory
 */
public class ManagedImageFactory {
    static protected TreeMap<String, ManagedImage>  search;
    static protected ArrayList<ManagedImage>        list;

/**
 * Constructor for objects of class ManagedImageFactory.  This is called
 * ONCE in a game before the primary game loop timer is started.  The
 * created object need not be saved as an instance variable because the
 * methods and associated search tree are STATIC
 */
//  BRB     2010-09-30

    public ManagedImageFactory(int sz) {
        search = new TreeMap<String, ManagedImage>();

        list = new ArrayList<ManagedImage> (sz);

        for (int i=0;i < sz;i++)
            list.add (new ManagedImage());
        }

/**
 * get -- retrieves a ManagedImage file either from the search tree (if it has been read in prior)
 * or by reading it from the file system or resource JAR
 *
 * @param   iFile       File name excluding the images/ directory which is prepended before the file
 *                      is accessed
 * @throws IOException  An error has occurred in reading the image file
 */
//  BRB     2010-10-01

    synchronized static public ManagedImage get (String iFile)
                                                    throws IOException {
        ManagedImage mImg = search.get (iFile);

        if (mImg == null) {
            if (list.size() != 0)
                mImg = list.remove (list.size() - 1);
            else
                mImg = new ManagedImage ();

            mImg.initialize (iFile);
            search.put (iFile, mImg);
            }
        else
            mImg.count++;

        return mImg;
        }

/**
 * dispose -- Gets rid of the ManagedImage and removes it from the search
 *            tree if it's the last one and adds it to the free list.
 *
 * @param   mImg    ManagedImage being deleted
 */
//  BRB     2010-09-29

    synchronized static public void dispose (ManagedImage mImg) {
        mImg.dispose();

        if (mImg.count == 0)
            search.remove (mImg.fileName);

        list.add (mImg);
        }

/**
 * dispose -- Removes all entries from the free ManagedImage list and
 *            deletes all active ManagedImages from the search tree.  All
 *            active ManagedImages are disposed prior to being removed.
 */
//  BRB 2010-09-30

    synchronized static public void dispose () {
        list.clear();

        while (!search.isEmpty()) {
            String key = search.firstKey();
            ManagedImage mImg = search.remove (key);
            mImg.dispose();
            }
        }
    }