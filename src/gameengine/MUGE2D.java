package gameengine;

/**
 *
 * @author Alex Olesen
 */
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.File;
import java.net.URL;

/**
 * **************************READ THIS FIRST************************************
 * -----------------------------------------------------------------------------
 * 'd' to move forward
 * 'a' to move backward
 * 'SPACEBAR' to jump
 * CONCRETE WALLS ARE DARK GREY THESE ARE BREAKABLE
 * STEEL WALLS ARE LIGHTER AND HAVE BOLTS IN THEM, THEY CANNOT BE BROKEN
 * IN THE UNLIKELY EVENT OF GETTING STUCK IN A WALL, HOLD EITHER A OR D, YOU WILL GET OUT
 * FASTEST TIME HAS BEEN 17 SECONDS
 * ROCKS ARE SURFACES, THEY CAN BE RAN ON. 
 */

/**
 * class MUGE2D -- Primary driver class for game.  It consists of the main
 * program for the game plus the primary display loop
 *
 * @author  (Brian Brookwell)
 * @version (2010-04-26)
 */
/*  Attribute       Description
 *
 *  sFactory        Factory for all Sprites in the Game
 *  pFactory        Factory for all Points in the Game
 *  miFactory       Factory for all Managed Images in the Game
 *  stillRunning    Flag used to control execution of the game Thread
 *  layers          List of Layers that make up the game (At least one)
 *  timer           Running thread handling the display of the game
 *  Xvelocity       The Player Characters X direction velocity
 *  Yvelocity       The Player Characters Y direction velocity
 *  HasCollided     Flag to only allow one collision
 *  CollideCounter  Counter to reset Hit Count every third of a second
 *  FrCount         Counter of frames to get one second
 *  SecCount        Counter of Seconds
 *  MinCount        Counter of Minutes
 *  FinishLine      The End of the Level
 */
public class MUGE2D extends Frame implements Runnable, KeyListener, MouseListener, MouseMotionListener {
    protected static AudioClip          WallSmash;
    protected static AudioClip          WallHit;

    protected SpriteFactory             sFactory;
    protected PointFactory              pFactory;
    protected ManagedImageFactory       miFactory;
    protected boolean                   stillRunning = false;
    protected java.util.List<Layer>     layers;
    protected Thread                    timer;
    protected Point                     location;

    protected int                       Xvelocity;
    protected int                       Yvelocity;


    protected AnimationSet[]            StandJuggman;
    protected AnimationSet[]            RunJuggman;
    protected AnimationSet[]            JumpJuggman;
    protected AnimationSet[]            FallJuggman;
    protected AnimationSet[]            FloorTex;
    protected AnimationSet[]            PlatformTex;
    protected AnimationSet[]            ConcreteWall;
    protected AnimationSet[]            SteelWall;
    protected AnimationSet[]            RubbleSpin;

    protected ColorLayer                MainLayer;

    protected Sprite                    Juggman;
    protected Sprite[]                  Rubble;
    protected Sprite[]                  FloorTexture;
    protected Sprite[]                  Platforms;
    protected Sprite[]                  ConcreteWalls;
    protected Sprite[]                  SteelWalls;

    protected Surface[]                 Floors;

    protected int                       SurfaceCount;
    protected int                       ConcreteWallCount;
    protected int                       PlatformCount;
    protected int                       SteelWallCount;
    protected int                       JumpCount;
    protected int                       RubbleTime;

    protected boolean                   HasCollided;
    protected int                       CollideCounter;

    protected Point2D.Double            RubbleStart;

    protected int                       CameraControlFlag=1;
    
    protected int                       CountDown=120;

    protected int                       FinishLine=18000;

    protected int                       FrCount;

    protected int                       SecCount=0;

    protected int                       MinCount=0;

    protected int                       ClockFlag=1;

/**
 * Constructor for objects of class MUGE2D that generate a ColorLayer as the primary Layer
 *
 * @param   backColor   Color to use as the background
 * @param   tRows       Number of rows in the SurfaceTree
 * @param   tCols       Number of columns in the SurfaceTree
 * @param   sprites     Initial number of sprites in the SpriteFactory
 * @param   points      Initial number of points in the PointFactory
 * @param   images      Initial number of Managed Images in the ManagedImageFactory
 * @param   layerCount  Initial number of Layers in the Game
 */
    public MUGE2D (Color backColor, int tRows, int tCols, int sprites, int points,
                            int images, int layerCount) throws Exception {
        super();

        // Acquire system constants
        new Constants();

        // Create object factories and image layer list
        layers = Collections.synchronizedList (new ArrayList<Layer> (layerCount));
        layers.add (new ColorLayer (tCols, tRows, backColor));

        setUpScreen(sprites, points, images);
        }

/**
 * Constructor for objects of class MUGE2D that generates an ImageLayer as the primary layer
 *
 * @param   backFile    File containing the image or image set for the layer
 * @param   tRows       Number of rows in the SurfaceTree
 * @param   tCols       Number of columns in the SurfaceTree
 * @param   sprites     Initial number of sprites in the SpriteFactory
 * @param   points      Initial number of points in the PointFactory
 * @param   images      Initial number of Managed Images in the ManagedImageFactory
 * @param   layerCount  Initial number of Layers in the Game
 */
    public MUGE2D (String backFile, int tRows, int tCols, int sprites, int points,
                            int images, int layerCount) throws Exception {
        super();

        // Acquire system constants
        new Constants();
        setUpScreen(sprites, points, images);
        
//        ImageLayer panel = new ImageLayer (tCols, tRows);
//        panel.loadImages (backFile, 0, 0);

        // Create object factories and image layer list
        layers = Collections.synchronizedList (new ArrayList<Layer> (layerCount));
 //       layers.add (panel);


        stillRunning = true;
        timer = new Thread (this);
        timer.start();
        }

/**
 * setUpScreen -- sets up all the game factories and the screen
 *
 * @param   sprites     Initial number of sprites in the SpriteFactory
 * @param   points      Initial number of points in the PointFactory
 * @param   images      Initial number of Managed Images in the ManagedImageFactory
 */
    protected void setUpScreen(int sprites, int points, int images) {
        Constants.centering = Constants.NOT_PLAYER_CENTERED;
        sFactory = new SpriteFactory (sprites);
        pFactory = new PointFactory (points);
        miFactory = new ManagedImageFactory (images);
        addKeyListener(this);


        setUndecorated (Constants.isFullScreen);
        setResizable (!Constants.isFullScreen);
        setIgnoreRepaint (true);

        if (Constants.isFullScreen)
            Constants.device.setFullScreenWindow (this);

        // Choose the best display mode
        if (Constants.device.isDisplayChangeSupported ())
            chooseBestDisplayMode ();

        // Set up double Buffering
        createBufferStrategy (2);
        Constants.bufferStrategy = getBufferStrategy();

        BufferCapabilities.FlipContents flipContents =
                                Constants.bufferStrategy.getCapabilities().getFlipContents();

        if (flipContents == BufferCapabilities.FlipContents.BACKGROUND)
            Constants.strategy = Constants.BACKGROUND;
        else if (flipContents == BufferCapabilities.FlipContents.COPIED)
            Constants.strategy = Constants.COPIED;
        else if (flipContents == BufferCapabilities.FlipContents.PRIOR)
            Constants.strategy = Constants.PRIOR;
        else
            Constants.strategy = Constants.UNDEFINED;
        }


/**
 * chooseBestDisplayMode -- sets the screen to the size with the best bit depth
 * of the maximum size.  This will choose a greater bit depth over size.  Thus,
 * a 1280 x 1024 screen in 16 bits would not be selected if a a 1024 x 800 in
 * 32 bits were available.
 */
/*  Variable        Description
 *
 *  mode            Array of display modes associated with the system
 *  bestMode        Display mode having the greatest size possible at the
 *                  highest bit depth
 */

    private void chooseBestDisplayMode() {
        DisplayMode mode[] = Constants.device.getDisplayModes();
        DisplayMode bestMode = null;

        for (int x = 0; x < mode.length; x++) {
            if (bestMode == null)
                bestMode = mode[x];
            else if (mode[x].getBitDepth() > bestMode.getBitDepth())
                bestMode = mode[x];
            else if (mode[x].getBitDepth() == bestMode.getBitDepth()) {
                if (mode[x].getWidth() > bestMode.getWidth())
                    bestMode = mode[x];
                else if (mode[x].getWidth() == bestMode.getWidth() &&
                         mode[x].getHeight() > bestMode.getHeight())
                    bestMode = mode[x];
                }
            }

        Constants.device.setDisplayMode(bestMode);
        }


/**
 * run -- handles the update of the screen, alternating between 'flip' buffers
 * to create smooth animation
 */
/*  Variable        Description
 *
 *  g               Graphics display object used to update the current back buffer
 */
    public void run() {


       /**
        * Setting up Animation Sets
        */
       StandJuggman = new AnimationSet[1];
       JumpJuggman = new AnimationSet[1];
       RunJuggman = new AnimationSet[1];
       FallJuggman = new AnimationSet[1];
       
       FloorTex = new AnimationSet[1];
       PlatformTex = new AnimationSet[1];
       
       ConcreteWall = new AnimationSet[1];
       SteelWall = new AnimationSet[1];
       
       RubbleSpin = new AnimationSet[1];
       
       try {
            StandJuggman[0] = new AnimationSet("Standing", 1, true);
            } catch (Exception ex) {
                System.out.println ("" + ex);
                }

       try {
            JumpJuggman[0] = new AnimationSet("Jump", 18, true);
            } catch (Exception ex) {
                System.out.println ("" + ex);
                }

       try {
            RunJuggman[0] = new AnimationSet("Running", 12, true);
            } catch (Exception ex) {
                System.out.println ("" + ex);
                }

       try {
            FallJuggman[0] = new AnimationSet("Fall", 8, true);
            } catch (Exception ex) {
                System.out.println ("" + ex);
            }
       
       try {
            FloorTex[0] = new AnimationSet("FloorTexture", 1, true);
            } catch (Exception ex) {
                System.out.println ("" + ex);
            }
       
       try {
            PlatformTex[0] = new AnimationSet("PlatformTexture", 1, true);
            } catch (Exception ex) {
                System.out.println ("" + ex);
            }
       
       try {
           ConcreteWall[0] = new AnimationSet("ConcreteWallTexture", 1, true);
       } catch (Exception ex){
           System.out.println("" + ex);
       }

       try {
           SteelWall[0] = new AnimationSet("SteelWallTexture", 1, true);
       } catch (Exception ex){
           System.out.println("" + ex);
       }
       
       try {
           RubbleSpin[0] = new AnimationSet("Rubble", 3, true);
       } catch (Exception ex){
           System.out.println("" + ex);
       }
       
        /**
         * Making the main Sprite
         */

        Juggman = new Sprite();
        Juggman.initialize( StandJuggman, pFactory.create(300, 650), 25, true);

       /**
        * Making the Rubble Sprite(s)
        */
        RubbleStart = pFactory.create(0,0);
        
        Rubble = new Sprite[10];

        for(int i = 0; i < 10; i++){
            Rubble[i] = new Sprite();
        }
        
       /**
        * Making the main floor
        */
         Floors = new SurfaceFlat[50];
         Floors[0] = new SurfaceFlat(0, 950, 20000, 45);
         FloorTexture = new Sprite[11];
         for(int i=0; i<11; i++){
           FloorTexture[i]= new Sprite();
         }

         FloorTexture[0].initialize(FloorTex, pFactory.create(1000, 990), 0, false);
         FloorTexture[1].initialize(FloorTex, pFactory.create(3000, 990), 0, false);
         FloorTexture[2].initialize(FloorTex, pFactory.create(5000, 990), 0, false);
         FloorTexture[3].initialize(FloorTex, pFactory.create(7000, 990), 0, false);
         FloorTexture[4].initialize(FloorTex, pFactory.create(7000, 990), 0, false);
         FloorTexture[5].initialize(FloorTex, pFactory.create(9000, 990), 0, false);
         FloorTexture[6].initialize(FloorTex, pFactory.create(11000, 990), 0, false);
         FloorTexture[7].initialize(FloorTex, pFactory.create(13000, 990), 0, false);
         FloorTexture[8].initialize(FloorTex, pFactory.create(15000, 990), 0, false);
         FloorTexture[9].initialize(FloorTex, pFactory.create(17000, 990), 0, false);
         FloorTexture[10].initialize(FloorTex, pFactory.create(19000, 990), 0, false);
         SurfaceCount = 1;
/*************************************************************************************************/
        /********************************************************************************/
         /************************ASSEMBLY LINES**************************************/
         
     
        /*******************PLATFORM BUILDING******************************/
        /******************************************************************/
        /**
         * Making the platform Sprites
         */ 
         SurfaceCount = 8;
         PlatformCount = 7;
         
         Platforms = new Sprite[50];
         for(int i = 0; i<PlatformCount; i++){Platforms[i] = new Sprite();}

         //High Platform Above Player
         Platforms[0].initialize(PlatformTex, pFactory.create(250, 540), 0, false);
         Floors[1] = new SurfaceFlat(0, 500, 500, 45);

         //Platform above the set of concrete walls
         Platforms[1].initialize(PlatformTex, pFactory.create(4000, 840), 0, false);
         Floors[2] = new SurfaceFlat(3750, 800, 500, 45);

         //Platforms after the HELL WALL
         Platforms[2].initialize(PlatformTex, pFactory.create(6000, 840), 0, false);
         Floors[3] = new SurfaceFlat(5750, 800, 500, 45);
         Platforms[3].initialize(PlatformTex, pFactory.create(6300, 840), 0, false);
         Floors[4] = new SurfaceFlat(6050, 800, 500, 45);

         //Platforms after the HELL WALL
         Platforms[4].initialize(PlatformTex, pFactory.create(9000, 840), 0, false);
         Floors[5] = new SurfaceFlat(8750, 800, 500, 45);
         Platforms[5].initialize(PlatformTex, pFactory.create(9300, 840), 0, false);
         Floors[6] = new SurfaceFlat(9050, 800, 500, 45);

         //Platforms in between the walls
         Platforms[5].initialize(PlatformTex, pFactory.create(12750, 640), 0, false);
         Floors[6] = new SurfaceFlat(12500, 600, 500, 45);
         Platforms[6].initialize(PlatformTex, pFactory.create(12750, 440), 0, false);
         Floors[7] = new SurfaceFlat(12500, 400, 500, 45);
   /*******************WALL BUILDING*******************************************/
   /**************************************************************************/
         /**
         * Making the Destructable Wall Sprites
         */
         ConcreteWallCount=15;
         ConcreteWalls = new Sprite[25];
         for(int i = 0; i<ConcreteWallCount; i++){ConcreteWalls[i] = new Sprite();}

         ConcreteWalls[0].initialize(ConcreteWall, pFactory.create(1200, 900), 100, true);

         //CONCRETE wall 3
         ConcreteWalls[1].initialize(ConcreteWall, pFactory.create(2800, 900), 100, true);
         ConcreteWalls[2].initialize(ConcreteWall, pFactory.create(2800, 775), 100, true);
         ConcreteWalls[3].initialize(ConcreteWall, pFactory.create(2800, 900), 100, true);

         //Wall 4 and 5, smaller
         ConcreteWalls[4].initialize(ConcreteWall, pFactory.create(3400, 900), 100, true);
         ConcreteWalls[5].initialize(ConcreteWall, pFactory.create(4000, 900), 100, true);

         ConcreteWalls[9].initialize(ConcreteWall, pFactory.create(4100, 900), 100, true);
         ConcreteWalls[10].initialize(ConcreteWall, pFactory.create(4200, 900), 100, true);
         
         //Wall 6 mid height
         ConcreteWalls[6].initialize(ConcreteWall, pFactory.create(4800, 775), 100, true);
         ConcreteWalls[7].initialize(ConcreteWall, pFactory.create(4800, 650), 100, true);
         //Wall 7 low
         ConcreteWalls[8].initialize(ConcreteWall, pFactory.create(5200, 900), 100, true);
         //wall 9 low
         ConcreteWalls[9].initialize(ConcreteWall, pFactory.create(11500,900), 100, true);
         //wall 10 low
         ConcreteWalls[10].initialize(ConcreteWall, pFactory.create(15500,900), 100, true);
         //wall 11 mid
         ConcreteWalls[11].initialize(ConcreteWall, pFactory.create(16500,775), 100, true);
         ConcreteWalls[12].initialize(ConcreteWall, pFactory.create(16500,650), 100, true);
         //wall 12 high
         ConcreteWalls[13].initialize(ConcreteWall, pFactory.create(17500,525), 100, true);

         
         /**
         * Making the INDestructable Wall Sprites
         */ 
         SteelWallCount=34;
         SteelWalls = new Sprite[40];
         for(int i = 0; i<SteelWallCount; i++){SteelWalls[i] = new Sprite();}
         //back wall
         SteelWalls[0].initialize(SteelWall, pFactory.create(0, 900), 100, true);
         SteelWalls[1].initialize(SteelWall, pFactory.create(0, 775), 100, true);
         SteelWalls[2].initialize(SteelWall, pFactory.create(0, 650), 100, true);
         SteelWalls[3].initialize(SteelWall, pFactory.create(0, 525), 100, true);
         //wall 1
         SteelWalls[4].initialize(SteelWall, pFactory.create(1200, 775), 100, true);
         SteelWalls[5].initialize(SteelWall, pFactory.create(1200, 650), 100, true);
         //hurdle 2
         SteelWalls[6].initialize(SteelWall, pFactory.create(1600, 1000), 100, true);
         //Wall 6 mid break zone
         SteelWalls[7].initialize(SteelWall, pFactory.create(4800, 900), 100, true);
         SteelWalls[8].initialize(SteelWall, pFactory.create(4800, 525), 100, true);
         //wall 7
         SteelWalls[9].initialize(SteelWall, pFactory.create(5200, 775), 100, true);

         //wall 8
         SteelWalls[10].initialize(SteelWall, pFactory.create(7500, 900), 100, true);
         SteelWalls[11].initialize(SteelWall, pFactory.create(7500, 775), 100, true);

         //wall 9
         SteelWalls[12].initialize(SteelWall, pFactory.create(11500, 775), 100, true);
         SteelWalls[13].initialize(SteelWall, pFactory.create(11500, 650), 100, true);

         //wall 10
         SteelWalls[14].initialize(SteelWall, pFactory.create(12500, 775), 100, true);
         SteelWalls[15].initialize(SteelWall, pFactory.create(12500, 650), 100, true);
         SteelWalls[16].initialize(SteelWall, pFactory.create(12500, 525), 100, true);
         SteelWalls[17].initialize(SteelWall, pFactory.create(12500, 450), 100, true);
         SteelWalls[18].initialize(SteelWall, pFactory.create(12500, 325), 100, true);
         SteelWalls[19].initialize(SteelWall, pFactory.create(12500, 200), 100, true);
         SteelWalls[20].initialize(SteelWall, pFactory.create(12500,  75), 100, true);

         //wall 11
         SteelWalls[21].initialize(SteelWall, pFactory.create(13000, 900), 100, true);
         SteelWalls[22].initialize(SteelWall, pFactory.create(13000, 775), 100, true);
         SteelWalls[23].initialize(SteelWall, pFactory.create(13000, 650), 100, true);
         SteelWalls[24].initialize(SteelWall, pFactory.create(13000, 525), 100, true);
         SteelWalls[25].initialize(SteelWall, pFactory.create(13000, 450), 100, true);
         SteelWalls[26].initialize(SteelWall, pFactory.create(13000, 325), 100, true);
         SteelWalls[27].initialize(SteelWall, pFactory.create(13000, 200), 100, true);

         //wall 12
         SteelWalls[28].initialize(SteelWall, pFactory.create(15500, 775), 100, true);
         //wall 13
         SteelWalls[29].initialize(SteelWall, pFactory.create(16500, 900), 100, true);
         //wall 14
         SteelWalls[30].initialize(SteelWall, pFactory.create(17500, 900), 100, true);
         SteelWalls[31].initialize(SteelWall, pFactory.create(17500, 775), 100, true);
         SteelWalls[32].initialize(SteelWall, pFactory.create(17500, 650), 100, true);
   /***************************************************************************/
   /**************************************************************************/


         /**
         * Making a Layer
         */
        MainLayer= new ColorLayer(new Point(20, 20),200,200,new Color(0,0,0,0));
        
        /**
         * Adding a Sprite and Surface to the Layer
         */
         for(int i = 0; i<PlatformCount; i++){MainLayer.addSprite(Platforms[i]);}

         for(int i = 0; i<ConcreteWallCount; i++){ MainLayer.addSprite(ConcreteWalls[i]);}
         for(int i = 0; i<SteelWallCount; i++){MainLayer.addSprite(SteelWalls[i]);}

         for(int i = 0; i<SurfaceCount; i++){MainLayer.addSurface(Floors[i]);}
         
         MainLayer.addSprite(Juggman);



        for(int i = 0; i < 10; i++){
            MainLayer.addSprite(Rubble[i]);
        }
         
        for (int i=0; i<11; i++){
         MainLayer.addSprite(FloorTexture[i]);
        }
         
         

         
         MainLayer.setPlayer(Juggman);
        /**
         * Adding the MainLayer to the Layer Group
         */
        layers.add(MainLayer);

        Xvelocity=0;
        Yvelocity=0;

        RubbleTime=31;
        
        


        /**
         * AUDIO
         */
        try {
            File currentDir = new File(".");
            URL currentDirURL = currentDir.toURL();
            URL url = new URL(currentDirURL, "Sounds/WallSmash.wav");
            WallSmash = Applet.newAudioClip(url);
            url = new URL(currentDirURL, "Sounds/WallHit.wav");
            WallHit =  Applet.newAudioClip(url);
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
                }
/************************************GAME EVENT LOOP********************/
        while (stillRunning){

            


            if(RubbleTime<=21){
                for(int i = 0; i < 10; i++){
                Rubble[i].update();
                }
                RubbleTime++;
            }
            String temp = "";
            String temp2 = "";
            if(RubbleTime==21){
                for(int i = 0; i < 10; i++){
                Rubble[i].initialize(RubbleSpin, pFactory.create(0,0), 0, false);
                Rubble[i].visible = false;
                }
            }

            for(int i = 0; i < ConcreteWallCount; i++){
                if(ConcreteWalls[i].StillAliveFlag==true){
                   ConcreteWalls[i].update();}
               
            }

            for(int i = 0; i < SteelWallCount; i++){
                SteelWalls[i].update();
            }
            Graphics2D g = (Graphics2D)(Constants.bufferStrategy.getDrawGraphics());
            
            
            


            g.setColor (Color.blue);
            g.fillRect(0, 0, Constants.screenWidth, Constants.screenHeight);

            /******HUD*********************************/
            //Players Clock
            if(ClockFlag==1){
               FrCount++;    
            }
            if(FrCount==30){
                FrCount=0;
                SecCount++;
            }
            if(SecCount==60){
                SecCount=0;
                MinCount++;
            }
            /******** Speed ***************/
            temp2 = (temp2+Math.abs(Xvelocity)*100/Constants.MAX_VELOCITY);
            if(SecCount<=9)
            {
            temp= temp+MinCount+":"+0+SecCount;
            }
            else{
                temp= temp+MinCount+":"+SecCount;
            }
            g.setColor (Color.BLACK);
            g.setFont(new Font("STENCIL",Font.PLAIN,35));
            g.drawString("TIME", 1500, 25);
            g.drawString(temp, 1600, 25);
            g.drawString("SPEED", 1500, 50);
            g.drawString(temp2, 1625, 50);
            layers.get(0).display(g);
          
            layers.get(0).displayEntireBackground(g);
            
            //
            for(int i = 0; i < SteelWallCount; i++){
                SteelWalls[i].update();
            }
            g = (Graphics2D)(Constants.bufferStrategy.getDrawGraphics());
                
            Constants.bufferStrategy.show();
            //Disposing of Graphics Object, recreated next loop
            g.dispose();
            
            /*****CLOCK****************************************************/
            long startTime = System.nanoTime();
            try {
                //Setting Sleep time to get as close to 30 fps as possible
                int time=(int)((33333333L - System.nanoTime() + startTime + 500000) / 1000000);
                if(time>0){timer.sleep(time);}
            }
            catch (InterruptedException ex) {
                    stillRunning = false;
            }

           /*===================COMMAND AND CONTROL ROUTINES===1.2=============================*/
            

            /**
             * Exit Command "ESC"
             */
            if(Constants.keyDown.get(27))
                {
            stillRunning = false;
                }
            if(Juggman.location.y>920){
                Juggman.location.y=920;
                Yvelocity=0;
            }
            
             /*JUMPING*/
           
            //Jumping straight up
            if(Constants.keyDown.get(' ') && JumpCount <= Constants.MAX_JUMP)
            {
                if(Yvelocity>-120){Yvelocity=-270;}
                Yvelocity=(int)(-1+Yvelocity*1.2);
                Juggman.initializeM(JumpJuggman, Juggman.location, Xvelocity, Yvelocity, 34, true, Constants.UNITS_PER_SECOND);

                JumpCount++;
            }
                
             /*FALLING*/
            
            //Falling Straight Down
            else if(SurfaceCheck(Floors, Juggman, SurfaceCount)==false)
             {
                if (Yvelocity<0){
                     Yvelocity=(int)(Yvelocity/1.5+5);
                }
                if (Yvelocity==0){Yvelocity=40;}
                if (Yvelocity<Constants.TERMINAL_VELOCITY){
                    Yvelocity= (int)(1+Yvelocity*1.2);
                 }
                 Juggman.initializeM(FallJuggman, Juggman.location, Xvelocity, Yvelocity, 34, true, Constants.UNITS_PER_SECOND);

                 JumpCount++;
             }
            
             
              /*RUNNING*/
             //Moving Left
            else if (Constants.keyDown.get('a'))
                {
                Yvelocity=0;
                    if (Xvelocity>0){//brakes;
                        Xvelocity = (int)(Xvelocity/1.2-20);
                    }
                    if (Xvelocity==0){Xvelocity=-30;}
                    if (Xvelocity<=0){
                        Xvelocity = (int)(-1+Xvelocity*1.1);
                    }
                    Juggman.initializeM(RunJuggman, Juggman.location, Xvelocity, Yvelocity, 34, true, Constants.UNITS_PER_SECOND);
                    //Juggman.update();
                    JumpCount=0;
                }
             //Moving Right
            else if (Constants.keyDown.get('d'))
                {
                    Yvelocity=0;
                    if (Xvelocity<0){//brakes;
                        Xvelocity = (int)(Xvelocity/1.2+20);
                    }
                    if (Xvelocity==0){Xvelocity=30;}
                    if (Xvelocity>0){
                    Xvelocity = (int)(1+Xvelocity*1.1);
                    }
                    
                    Juggman.initializeM( RunJuggman, Juggman.location, Xvelocity, Yvelocity, 34, true, Constants.UNITS_PER_SECOND);
                    //Juggman.update();
                    JumpCount=0;
                }

           /*STANDING*/
            else {
                Yvelocity=0;
                JumpCount=0;
                if(Xvelocity<=30&&Xvelocity>=(-30)){
                    Xvelocity=0;
                      Juggman.initialize( StandJuggman, Juggman.location, 34, true);
                    }       
                else{
                    Juggman.initializeM(RunJuggman, Juggman.location, Xvelocity, Yvelocity, 34, true, Constants.UNITS_PER_SECOND);
                    //Juggman.update();
                    Xvelocity = (int)(Xvelocity/1.15);
                }
                
            }
            //Keeping the Camera Centred on the Player
            if(CameraControlFlag==1)
            CameraControl((int)(Xvelocity*Constants.secondsPerUpdate));
            //Collision Detection
             CollisionDetection();
            //Juggman
            Juggman.update();
     //High Speed Limit
     if(Xvelocity>=Constants.MAX_VELOCITY) {Xvelocity=Constants.MAX_VELOCITY;}
     if(Xvelocity<=-Constants.MAX_VELOCITY){Xvelocity=-Constants.MAX_VELOCITY;}
    

     //VICTORY CONDITION
     if(Juggman.location.x>FinishLine){
        ClockFlag=0;
        CameraControlFlag=0;
        CountDown--;
        if(CountDown==0){stillRunning = false;}
     }
     }//end of Running Game

        //resetting to old display mode
        Constants.device.setDisplayMode (Constants.oldDisplayMode);
        Constants.device.setFullScreenWindow (null);

        
        //Killing the Thread
        dispose();
        timer = null;
        }

/**
 * keyPressed -- method used to handle KeyEvents for key press events
 *
 * @param   e   KeyEvent being processed
 */
    public void keyPressed(KeyEvent e) {
        Constants.keyDown.set (e.getKeyChar());
        handleMetas (e);
        }

/**
 * keyReleased -- method used to handle KeyEvents for key release events
 *
 * @param   e   Keyevent being processed
 */
    public void keyReleased(KeyEvent e) {
        Constants.keyDown.clear (e.getKeyChar());
        handleMetas (e);
        }

/**
 * keyType -- method used to handle KeyEvents for key click events (unused)
 *
 * @param   e   Keyevent being processed
 */
    public void keyTyped(KeyEvent e) {}

/**
 * handleMouseButtons -- method used to process the left/right button mask bit to
 *              determine which button(s) are currently down
 *
 * @param   e   MouseEvent being processed
 */
    protected void handleMouseButtons(MouseEvent e) {
        Constants.leftMouse    = (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0;
        Constants.rightMouse   = (e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0;
        }

/**
 * handleMetas -- method used to process the key mask bits to
 *              determine which meta keys are currently down
 *
 * @param   e   InputEvent being processed
 */
    protected void handleMetas(InputEvent e) {
        Constants.altDown     = (e.getModifiersEx() & MouseEvent.ALT_DOWN_MASK) != 0;
        Constants.ctrlDown    = (e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0;
        Constants.shiftDown   = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0;
        Constants.metaDown    = (e.getModifiersEx() & MouseEvent.META_DOWN_MASK) != 0;
        }

/**
 * mouseClicked -- handles a mouse click event (Press + Release)
 *
 * @param   e   MouseEvent being processed
 */
    public void mouseClicked(MouseEvent e) {}

/**
 * mouseEntered -- handles a mouse moving into the frame (unused)
 *
 * @param   e   MouseEvent being processed
 */
    public void mouseEntered(MouseEvent e) {
        handleMouseButtons (e);
        handleMetas (e);
        }

/**
 * mouseExited -- handles a mouse moving out of the frame (unused)
 *
 * @param   e   MouseEvent being processed
 */
    public void mouseExited(MouseEvent e) {}

/**
 * mousePressed -- handles a mouse button being pressed
 *
 * @param   e   MouseEvent being processed
 */
    public void mousePressed(MouseEvent e) {
        handleMouseButtons (e);
        handleMetas (e);
        }

/**
 * mouseReleased -- handles a mouse button being released
 *
 * @param   e   MouseEvent being processed
 */
    public void mouseReleased(MouseEvent e) {
        handleMouseButtons (e);
        handleMetas (e);
        }

/**
 * mouseDragged -- handles a mouse drag event.
 *
 * @param   e   MouseEvent being processed
 */

    public void mouseDragged(MouseEvent e) {
        Constants.mouse.x = e.getX();
        Constants.mouse.y = e.getY();

        handleMouseButtons (e);
        handleMetas (e);
        }

/**
 * mouseMoved -- handles a mouse move event.
 *
 * @param   e   MouseEvent being processed
 */

    public void mouseMoved(MouseEvent e) {
        Constants.mouse.x = e.getX();
        Constants.mouse.y = e.getY();

        handleMouseButtons (e);
        handleMetas (e);
        }

    /**
     * Used to Check a Sprite against the surfaces in the game
     * @param Surfaces The Array of Surfaces in the game
     * @param Toon The Sprite to be compared against the surfaces
     * @param number number of surfaces in the array
     * @return whether or not the sprite has collided with any surface in the Array
     */
    public static boolean SurfaceCheck(Surface[] Surfaces, Sprite Toon, int number){
        boolean Contact = false;
        for (int i=0; i < number; i++){
         if (Surfaces[i].onSurface(Toon)==true){Contact=true;}
        }
        return Contact;
    }

    /**
     * Used to see if any walls are colliding with a Sprite
     * @param Walls The Array of Walls to be compared with the Sprite
     * @param Toon The Sprite to be compared with the Walls
     * @param number The number of walls in the Array
     * @return Whether any wall in the Array has been Collided by the Sprite
     */
    public static boolean  CollisionCheck(Sprite[] Walls, Sprite Toon, int number){
        boolean collide = false;
        for (int i=0; i < number; i++){
            if (Walls[i].hasHit(Toon)==true){collide=true;}
        }
        return collide;
    }
    /**
     * Used to determine Which wall is destroyed by collision at high enough velocity
     * @param Walls The Array of Walls To be compared
     * @param Toon The Sprite being compared to the Wall Array
     * @param number The number of walls in the Array
     * @return Which wall was hit
     */
    public static int WhichWall(Sprite[] Walls, Sprite Toon, int number){
        int whichwall=0;
        for (int i=0; i < number; i++){
            if (Walls[i].hasHit(Toon)==true){whichwall=i;}
        }
      return whichwall;
    }

    /**
     * Main Collision detection function, also destroys the walls that can be destroyed
     * each type of wall is compared separately, so no wall is compared twice
     * Simple collision compensation is to invert X-direction velocity
     * More complex and accurate system to be added later using locations rather
     * than velocity to resolve collisions
     */
    public void CollisionDetection(){

             if(CollideCounter==10){HasCollided=false;}
             if(CollisionCheck(SteelWalls, Juggman, SteelWallCount)==true && HasCollided==false){
                     Xvelocity = (int)(-Xvelocity*0.7);
                     Yvelocity = (int)(Yvelocity*0.7);
                     WallHit.play();

                     HasCollided = true;
                     CollideCounter = 0;
             }

             if(CollisionCheck(ConcreteWalls, Juggman, ConcreteWallCount)==true && HasCollided==false){
                
                 //Destroying Concrete Walls
                 if(Math.abs(Xvelocity)>Constants.RAMMING_SPEED){
                     RubbleStart = pFactory.create(ConcreteWalls[WhichWall(ConcreteWalls, Juggman, ConcreteWallCount)].location.x,
                                                                 ConcreteWalls[WhichWall(ConcreteWalls, Juggman, ConcreteWallCount)].location.y);
                     MainLayer.removeSprite(ConcreteWalls[WhichWall(ConcreteWalls, Juggman, ConcreteWallCount)]);
                     ConcreteWalls[WhichWall(ConcreteWalls, Juggman, ConcreteWallCount)].collidable=false;
                     ConcreteWalls[WhichWall(ConcreteWalls, Juggman, ConcreteWallCount)].StillAliveFlag=false;
                     ConcreteWalls[WhichWall(ConcreteWalls, Juggman, ConcreteWallCount)].visible=false;
                     
                     RubbleTime=0;
                     Rubble[0].initializeM(RubbleSpin, RubbleStart,Xvelocity, 150.0, 15, false, Constants.UNITS_PER_SECOND);
                     Rubble[0].visible=true;
                     WallSmash.play();

                     for(int i = 1; i < 10; i++){
                     Rubble[i].initializeM(RubbleSpin, Rubble[i-1].lastLocation, Xvelocity, 150.0, 15, false, Constants.UNITS_PER_SECOND);
                     Rubble[i].visible=true;
                     }

                 }
                 else{
                     WallHit.play();
                     Xvelocity = (int)(-Xvelocity*0.7);
                     Yvelocity = (int)(Yvelocity*0.7);
                     HasCollided = true;
                     CollideCounter = 0;
                 }
                 
                 
            }
                 
                 
        if(CollideCounter<10)CollideCounter++;
        
     }


    /**
     * Camera Control To keep the player centered on screen
     * moving all the other objects on screen to compensate for player movement
     * @param CorrectFactor
     */
    public void CameraControl(int CorrectFactor){
        if(Juggman.location.x<Juggman.lastLocation.x){                   
                    for(int i = 0; i< 10; i++){
                    FloorTexture[i].location.x = FloorTexture[i].location.x-(CorrectFactor);
                    }
                    for(int i = 0; i < PlatformCount; i++){
                    Platforms[i].location.x = Platforms[i].location.x-(CorrectFactor);
                    }
                    for(int i = 0; i< SurfaceCount; i++){
                     Floors[i].translateX((-CorrectFactor));
                    }
                    for (int i = 0; i< ConcreteWallCount; i++){
                    if(ConcreteWalls[i].StillAliveFlag==true){
                    ConcreteWalls[i].location.x=ConcreteWalls[i].location.x-(CorrectFactor);
                        }
                    }
                    for (int i = 0; i< SteelWallCount; i++){
                        if(SteelWalls[i].StillAliveFlag==true){
                        SteelWalls[i].location.x=SteelWalls[i].location.x-(CorrectFactor);
                    }
                    }
                    for (int i = 0; i< 10; i++){
                        if(Rubble[i].StillAliveFlag==true){
                        Rubble[i].location.x=Rubble[i].location.x-(CorrectFactor);
                        }
                    }
                    FinishLine=FinishLine+CorrectFactor;
                    Juggman.location.x=Juggman.location.x-(CorrectFactor);
                    Juggman.BoundingBox.x = Juggman.BoundingBox.x -(CorrectFactor);
                    }

            if(Juggman.location.x>Juggman.lastLocation.x){
                    for(int i = 0; i< 11; i++){
                    FloorTexture[i].location.x = FloorTexture[i].location.x-(CorrectFactor);
                    }
                    for(int i = 0; i< PlatformCount; i++){
                    Platforms[i].location.x = Platforms[i].location.x-(CorrectFactor);
                    }
                    for(int i = 0; i< SurfaceCount; i++){
                     Floors[i].translateX(-(CorrectFactor));
                    }
                    for (int i = 0; i< ConcreteWallCount; i++){
                        if(ConcreteWalls[i].StillAliveFlag==true){
                        ConcreteWalls[i].location.x=ConcreteWalls[i].location.x-(CorrectFactor);
                        }
                    }
                    for (int i = 0; i< SteelWallCount; i++){
                        if(SteelWalls[i].StillAliveFlag==true){
                        SteelWalls[i].location.x=SteelWalls[i].location.x-(CorrectFactor);
                        
                        }
                    }
                    for (int i = 0; i< 10; i++){
                        if(Rubble[i].StillAliveFlag==true){
                        Rubble[i].location.x=Rubble[i].location.x-(CorrectFactor);
                        }
                    }
                    FinishLine=FinishLine-CorrectFactor;
                    Juggman.location.x=Juggman.location.x-(CorrectFactor);
                    Juggman.BoundingBox.x = Juggman.BoundingBox.x -(CorrectFactor);
              }
              if(Math.abs(CorrectFactor) !=1){
                if(Juggman.location.x>Constants.CENTRE_STAGE){CameraControl(1);}
                if(Juggman.location.x<Constants.CENTRE_STAGE){CameraControl(-1);}
                }
           }



        

    
/**
 * main -- main program that sets up the main GameFrame
 *
 * @param   param   Array of parameters...ignored
 */
    
    }