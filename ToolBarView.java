import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

// the edit toolbar
public class ToolBarView extends JPanel implements Observer {
    GameModel model;
    JButton undo = new JButton("Undo");
    JButton redo = new JButton("Redo");

    public ToolBarView(GameModel model_) {
        model = model_;
        model.addObserver(this);
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setFocusable(false);
        // prevent buttons from stealing focus
        undo.setFocusable(false);
        redo.setFocusable(false);
        undo.setEnabled(false);
        redo.setEnabled(false);
        add(undo);
        add(redo);

        // controllers for undo menu item
        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                model.undo();
            }
        });
        // controller for redo menu item
        redo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                model.redo();
            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        undo.setEnabled(model.canUndo());
        redo.setEnabled(model.canRedo());
    }
}
