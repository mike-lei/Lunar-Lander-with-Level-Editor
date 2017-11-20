
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import javax.swing.undo.*;
import javax.vecmath.*;
import java.util.Random;

public class GameModel extends Observable {
    Rectangle2D.Double worldBounds;
    Polygon Terrain;
    int Ylowerbound, Xupperbound;
    int padLastX, padLastY, terrainLastY;
    int padNewX, padNewY, terrainNewY;
    int[] XP, YP;
    UndoManager undoManager;
    Rectangle LandingPad;
    public Ship ship;
    boolean enableUndo, enableRedo;
    int shipMessage = 3;
    public GameModel(int fps, int width, int height, int peaks) {
        undoManager = new UndoManager();
        ship = new Ship(60, width/2, 50);
        worldBounds = new Rectangle2D.Double(0, 0 ,width, height);
        LandingPad = new Rectangle(330,100,40,10);
        // anonymous class to monitor ship updates
        Ylowerbound = (int)worldBounds.getMaxY();
        Xupperbound = (int)worldBounds.getMaxX();
        XP = generateTerrainX(worldBounds);
        YP = generateTerrainY(worldBounds);
        Terrain = new Polygon(XP, YP, peaks+2);
        ship.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                //shipHittest();
                setChangedAndNotify();
            }
        });
    }

    // World
    // - - - - - - - - - - -
    public final Rectangle2D getWorldBounds() {
        return worldBounds;
    }

    // Edit View
    // - - - - - - - - - - -
    public void setLastXY(int type, int x, int y, int index){
        // Record the position of terrain or landingpad before dragged
        if (type == 1){ // pad
            padLastX = x;
            padLastY = y;
        } else { // terrain
            terrainLastY = YP[index];
        }
    }

    public void setNewXY(int type, int x, int y, int index){ // Record the final position of terrain or landingpad
        if (type == 1){ // pad
            padNewX = x;
            padNewY = y;
        } else { // terrain
            terrainNewY = YP[index];
        }
    }



    public void setLandingPadPos(int x, int y, boolean final_){
       if(final_){
           // final: the dragged LandingPad has reached its final position
           System.out.println("Model: set X to " + x);
           System.out.println("Model: set Y to " + y);

           // create undoable edit
           UndoableEdit undoableEdit = new AbstractUndoableEdit() {

               // capture variables for closure
               final int oldX = padLastX;
               final int oldY = padLastY;
               final int newX = padNewX;
               final int newY = padNewY;

               // Method that is called when we must redo the undone action
               public void redo() throws CannotRedoException {
                   super.redo();
                   LandingPad.setLocation(newX, newY);
                   System.out.println("Model: redo X to " + x);
                   System.out.println("Model: redo Y to " + y);
                   setChangedAndNotify();
               }

               public void undo() throws CannotUndoException {
                   super.undo();
                   LandingPad.setLocation(oldX, oldY);
                   System.out.println("Model: undo X to " + x);
                   System.out.println("Model: undo Y to " + y);
                   setChangedAndNotify();
               }
           };

           // Add this undoable edit to the undo manager
           undoManager.addEdit(undoableEdit);
       }

        LandingPad.setLocation(x, y);
        setChangedAndNotify();
    }

    public Rectangle getLandingPad(){return LandingPad;}
    public boolean LandingPadHittest(double x, double y){return LandingPad.contains(x,y); }
    public  int getLandingPadX() {return (int)LandingPad.getX();}
    public  int getLandingPadY() {return (int)LandingPad.getY();}
    public int getLandingPadW() {return (int)LandingPad.getWidth();}
    public  int getLandingPadH() {return (int)LandingPad.getHeight();}
    public int[] getXP() {return XP;}
    public int[] getYP() {return YP;}
    public void setYP(int index, int value, boolean final_){
        if (final_){  // final: the dragged peak has reached its final position
            UndoableEdit undoableEdit = new AbstractUndoableEdit() {

                // capture variables for closure
                final int oldValue = terrainLastY;
                final int newValue = terrainNewY;

                // Method that is called when we must redo the undone action
                public void redo() throws CannotRedoException {
                    super.redo();
                    YP[index] = newValue;
                    Terrain.reset();
                    Terrain = new Polygon(XP, YP, 22);
                    System.out.println("Model: redo value to " + value);
                    setChangedAndNotify();
                }

                public void undo() throws CannotUndoException {
                    super.undo();
                    YP[index] = oldValue;
                    Terrain.reset();
                    Terrain = new Polygon(XP, YP, 22);
                    System.out.println("Model: undo value to " + value);
                    setChangedAndNotify();
                }
            };
            // Add this undoable edit to the undo manager
            undoManager.addEdit(undoableEdit);
        }

        YP[index] = value;
        Terrain.reset();
        Terrain = new Polygon(XP, YP, 22);
        setChangedAndNotify();
    }

    public Polygon getTerrain() {return Terrain;}

    public int[] generateTerrainY(Rectangle2D bound){
        Random r = new Random();
        int[] Y_Points = new int[22];
       int Low =  Ylowerbound/2;
        int High = Ylowerbound;
        int Result;
        Y_Points[0] = High;
        Y_Points[21] = High;
        for (int i = 1; i < 21; i++){
            Result = r.nextInt(High-Low) + Low;
            //System.out.print("Terrain: Y Point Generated at Y = "+Result +"\n");
            Y_Points[i] = Result;
        }
        return Y_Points;
    }

    public int[] generateTerrainX(Rectangle2D bound){
        int[] X_Points = new int[22];
        int X_Interval = Xupperbound/19;
        X_Points[21] =  Xupperbound;
        X_Points[20] =  Xupperbound;
        X_Points[0] = 0;
        X_Points[1] = 0;
        for (int i = 2; i < 20; i++){
            X_Points[i] = (i-1) * X_Interval;
            //System.out.print("Terrain: X Point Generated at X = "+X_Points[i] +"\n");
        }
        return X_Points;
    }


    // Ship
    // - - - - - - - - - - -

    public int getMessage(){return shipMessage;}
    public void setMessage(int i){shipMessage = i;}
    //public Ship ship;

    // Observerable
    // - - - - - - - - - - -

    // helper function to do both
    void setChangedAndNotify() {
        setChanged();
        notifyObservers();
    }

    // undo and redo methods
    // - - - - - - - - - - - - - -

    public void undo() {
        if (canUndo())
            undoManager.undo();
    }

    public void redo() {
        if (canRedo())
            undoManager.redo();
    }

    public boolean canUndo() {
        return undoManager.canUndo();
    }

    public boolean canRedo() {
        return undoManager.canRedo();
    }

}



