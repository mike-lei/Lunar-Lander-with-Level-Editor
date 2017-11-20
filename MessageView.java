import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class MessageView extends JPanel implements Observer {
    GameModel model;
    // status messages for game
    JLabel fuel = new JLabel("fuel");
    JLabel speed = new JLabel("speed");
    JLabel message = new JLabel("message");

    public MessageView(GameModel model_) {
        model = model_;
        model.addObserver(this);
        setFocusable(false);
        // want the background to be black
        setBackground(Color.BLACK);

        setLayout(new FlowLayout(FlowLayout.LEFT));

        add(fuel);
        add(speed);
        add(message);

        for (Component c: this.getComponents()) {
            c.setForeground(Color.WHITE);
            c.setPreferredSize(new Dimension(100, 20));
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        if (model.ship.getFuel() < 10){
            fuel.setForeground(Color.RED);
        } else {
            fuel.setForeground(Color.WHITE);
        }
        if (model.ship.getSpeed() < model.ship.getSafeLandingSpeed()){
            speed.setForeground(Color.GREEN);
        } else {
            speed.setForeground(Color.WHITE);
        }
        fuel.setText("Fuel: "+String.format("%.0f", model.ship.getFuel()));
        speed.setText("Speed: "+String.format("%.2f", model.ship.getSpeed()));
        if (model.getMessage() == 0){
            message.setText("");
        } else if (model.getMessage() == 1)  {
            message.setText("LANDED!");
        } else if (model.getMessage() == 2){
            message.setText("CRASH");
        } else {
            message.setText("[Paused]");
        }
    }
}