import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import javax.vecmath.*;
import java.util.Observable;
import java.util.Observer;

// the actual game view
public class PlayView extends JPanel implements Observer {
    GameModel model;
    public PlayView(GameModel model_) {
        model = model_;
        model.addObserver(this);
        // needs to be focusable for keylistener**
        setFocusable(true);
        setBackground(Color.lightGray);
        KeyListener KL = new KeyListener() {
            boolean pressed = false;
            public void keyPressed(KeyEvent k) {
                if (!pressed){
                    int keycode = k.getKeyCode();
                    if (keycode == k.VK_SPACE){
                        if ((model.getMessage() == 1)||(model.getMessage() == 2)){ // landed or crash
                            model.setMessage(3);
                            model.ship.reset(new Point2d(350, 50));
                            model.setChangedAndNotify();
                        } else if (model.getMessage() == 0){
                            model.setMessage(3);
                            model.ship.setPaused(true);
                            model.setChangedAndNotify();
                        } else if (model.getMessage() == 3){
                            model.setMessage(0);
                            model.ship.setPaused(false);
                            model.setChangedAndNotify();
                        }
                    }
                    if (model.getMessage() == 0){
                        if (keycode == k.VK_W){
                            model.ship.thrustUp();
                        } else if (keycode == k.VK_A){
                            model.ship.thrustLeft();
                        } else if (keycode == k.VK_S){
                            model.ship.thrustDown();
                        } else if (keycode == k.VK_D){
                            model.ship.thrustRight();
                        }
                    }
                }
                pressed = true;
            }

            public void keyReleased(KeyEvent keyEvent) {
                pressed = false;
            }

            public void keyTyped(KeyEvent keyEvent) {

            }
        };
        this.addKeyListener(KL);

    }

    public void shipHittest(){
        Point2d position = model.ship.getPosition();
        if ((model.getTerrain().intersects(model.ship.getShape()))
                 ||(!model.getWorldBounds().contains(model.ship.getShape()))){
            model.setMessage(2);
            model.ship.stop();
         }
        if (model.getLandingPad().intersects(model.ship.getShape())){
            if (model.ship.getSpeed() > model.ship.getSafeLandingSpeed()){
                model.setMessage(2);
            } else {
                model.setMessage(1);
            }
            model.ship.stop();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (!model.ship.isPaused()){
            shipHittest();
        }
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; // cast to get 2D drawing methods
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  // antialiasing look nicer
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(0-(int)model.ship.getPosition().x*2, 0-(int)model.ship.getPosition().y*2);
        //g2.translate(0-model.getWorldBounds().getMaxX(), 0-model.getWorldBounds().getMaxY());
        g2.scale(3,3);
        //g2.scale(3,3);

        g2.setColor(Color.darkGray);
        g2.fillPolygon(model.getTerrain());
        g2.setColor(Color.RED);
        g2.fillRect(model.getLandingPadX(), model.getLandingPadY(), model.getLandingPadW(), model.getLandingPadH());
        g2.setColor(Color.BLUE);
        g2.fillRect((int)model.ship.getPosition().x, (int)model.ship.getPosition().y, 10, 10);
    }
}
