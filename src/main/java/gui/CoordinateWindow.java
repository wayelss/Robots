package gui;

import java.awt.geom.Point2D;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CoordinateWindow extends JInternalFrame implements PropertyChangeListener {
    private final JLabel coordinatesLabel;
    private RobotModel robotModel;

    public CoordinateWindow(RobotModel model) {
        super(Localization.getResourceBundle().getString(
                "window.coord.title"),
                true, true, true, true);
        model.addPropertyChangeListener(this);
        robotModel = model;

        coordinatesLabel = new JLabel(Localization.getResourceBundle()
                .getString("label.coordinates"));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(coordinatesLabel);

        getContentPane().add(panel);
        pack();
    }

    public void updateText(){
        ResourceBundle resourceBundle = Localization.getResourceBundle();
        setTitle(resourceBundle.getString("window.coord.title"));

        String pattern = resourceBundle.getString("label.coordinates");

        double currentX = robotModel.getX();
        double currentY = robotModel.getY();

        String formattedText = MessageFormat.format(pattern, currentX, currentY);
        coordinatesLabel.setText(formattedText);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("robotPosition".equals(evt.getPropertyName())) {
            Point2D pos = (Point2D) evt.getNewValue();
            String pattern = Localization.getResourceBundle().getString("label.coordinates");
            String formattedText = MessageFormatCache.checkPattern(pattern, pos.getX(), pos.getY());

            coordinatesLabel.setText(formattedText);
        }
    }
}