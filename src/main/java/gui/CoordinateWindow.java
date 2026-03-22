package gui;

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

        int currentX = (int) robotModel.getX();
        int currentY = (int) robotModel.getY();

        String formamtedText = MessageFormat.format(pattern,currentX,currentY);
        coordinatesLabel.setText(formamtedText);

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("robotPosition".equals(evt.getPropertyName())) {
            java.awt.Point pos = (java.awt.Point) evt.getNewValue();
            String pattern = Localization.getResourceBundle().getString("label.coordinates");
            String formattedText = MessageFormatCache.checkPattern(pattern,pos.x,pos.y);

            coordinatesLabel.setText(formattedText);
        }
    }
}