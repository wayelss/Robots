package gui;

import java.awt.BorderLayout;
import java.util.ResourceBundle;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends JInternalFrame
{
    private final GameVisualizer m_visualizer;

    public GameWindow(RobotModel robotModel)
    {
        super(Localization.getResourceBundle().
                getString("window.game.title"),
                true, true, true, true);
        m_visualizer = new GameVisualizer(robotModel);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    public void updateText(){
        ResourceBundle resourceBundle = Localization.getResourceBundle();
        setTitle(resourceBundle.getString("window.game.title"));
    }

    public void setRobotImage(java.awt.Image image) {
        m_visualizer.setRobotImage(image);
    }

    public void setTargetImage(java.awt.Image image) {
        m_visualizer.setTargetImage(image);
    }

    public void resetRobotImage() {
        m_visualizer.resetRobotImage();
    }

    public void resetTargetImage() {
        m_visualizer.resetTargetImage();
    }
}
