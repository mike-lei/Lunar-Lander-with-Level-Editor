import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.awt.geom.Rectangle2D;

// the editable view of the terrain and landing pad
public class EditView extends JPanel implements Observer {
    GameModel model;
    int draggingObject = 0; // LandingPad = 1, Terrain = 2, Default = 0;
    int Ylowerbound;
    int Xupperbound;
    int peakID;
    public EditView(GameModel model_) {
        setFocusable(false);
        model = model_;
        Ylowerbound = (int)model.getWorldBounds().getMaxY();
        Xupperbound = (int)model.getWorldBounds().getMaxX();
        model.addObserver(this);
        setBackground(Color.lightGray);

        this.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e) {
                System.out.print("Mouse Clicked @ " + e.getX() + ", " + e.getY() + "\n");
                if (e.getClickCount() == 2){
                    model.setLandingPadPos(e.getX()-20,e.getY()-5, true);
                    repaint();
                }

            }
            public void mouseDragged(MouseEvent e){
                mouseDraggedHandler(e);
            }
            public void mouseReleased(MouseEvent e){
                int x, y;
                if (draggingObject == 1){
                    x = (int)e.getX()-20;
                    y = (int)e.getY()-5;
                    model.setNewXY(1, x, y, 0);
                    model.setLandingPadPos(x, y, true);
                } else if ((draggingObject == 2)&&(peakID!=-1)){
                    y =  model.getYP()[peakID];
                    model.setNewXY(2, 0, y, peakID);
                    model.setYP(peakID, y, true);
                }

                draggingObject = 0;
                System.out.print("Now dragging nothing \n");
            }

        });
        this.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e){
                mouseDraggedHandler(e);
            }

        });
    }

    @Override
    public void update(Observable o, Object arg) {
        repaint();
    }


    public void mouseDraggedHandler(MouseEvent e){
        peakID = peak_hit_test(e.getX(),e.getY());
        if ((draggingObject != 2) &&
                (model.LandingPadHittest(e.getX(),e.getY()))){
            // LandingPad dragging handler
            // cannot drag landing pad when dragging terrain
            if (draggingObject == 0){
                model.setLastXY(1, model.getLandingPadX(), model.getLandingPadY(), 0);
            }
            draggingObject = 1;
            double preciseX = e.getX() - 20;
            double preciseY = e.getY() - 5;
            if (preciseY+10 > Ylowerbound){
                preciseY = Ylowerbound-10;
            }
            if (preciseY < 0){
                preciseY = 0;
            }
            if (preciseX < 0) {
                preciseX = 0;
            }
            if (preciseX+40 >  Xupperbound){
                preciseX = Xupperbound-40;
            }
            model.setLandingPadPos((int)preciseX,(int)preciseY, false);

        } else if ((draggingObject != 1)&&(peakID != -1)){
            // Terrain dragging handler
            // cannot drag terrain when dragging landing pad
            int value = (int)e.getY();
            if (draggingObject == 0){
                model.setLastXY(2, 0, 0,peakID);
            }
            draggingObject = 2;
            if ( value < 0){
                value = 0;
            }
            if ( value > Ylowerbound){
                value = Ylowerbound;
            }
            model.setYP(peakID, value, false);
        } else {

        }
    }

    public void drawCenteredCircle(Graphics2D g, int r) {
        int x, y;
        for (int i = 1; i < 21; i++){
           x = model.getXP()[i]-r/2;
            y = model.getYP()[i]-r/2;
            g.drawOval(x,y,r,r);
        }
    }

    public int peak_hit_test(double x, double y){
        double distance;
        for (int i = 1; i < 21; i++){
            distance = Math.hypot(x-(model.getXP())[i], y-(model.getYP())[i]);
            if (distance < 15){
                return i;
            }
        }
        return -1;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; // cast to get 2D drawing methods
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  // antialiasing look nicer
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.darkGray);
        g2.fillPolygon(model.getTerrain());
        g2.setColor(Color.GRAY);
        drawCenteredCircle(g2, 15);
        g2.setColor(Color.RED);
        g2.fillRect(model.getLandingPadX(), model.getLandingPadY(), model.getLandingPadW(), model.getLandingPadH());

    }
}
