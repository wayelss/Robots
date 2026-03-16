package gui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CoordinateWindow extends JInternalFrame implements PropertyChangeListener {
    private final JLabel coordinatesLabel;

    public CoordinateWindow(RobotModel model) {
        super("Координаты робота", true, true, true, true);
        model.addPropertyChangeListener(this);

        coordinatesLabel = new JLabel("X: 100, Y: 100");
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(coordinatesLabel);

        getContentPane().add(panel);
        pack();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("robotPosition".equals(evt.getPropertyName())) {
            java.awt.Point pos = (java.awt.Point) evt.getNewValue();
            coordinatesLabel.setText(String.format("X: %d, Y: %d", pos.x, pos.y));
        }
    }
}