/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gameengine;

/**
 *
 * @author Alex Olesen
 */
public class Main {
    public static void main (String param[]) throws Exception {
        MUGE2D game = new MUGE2D( "Background.png" , 100, 100, 50, 200, 50, 5);
        game.setVisible (true);

        }
}
