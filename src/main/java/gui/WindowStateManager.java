package gui;

import javax.swing.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class WindowStateManager {
    private JDesktopPane desktopPane;

    public WindowStateManager(JDesktopPane desktopPane) {
        this.desktopPane = desktopPane;
    }

    protected void saveWindowStates() {
        Properties props = new Properties();
        JInternalFrame[] frames = desktopPane.getAllFrames();
        setProperties(props,frames);
        try (FileOutputStream out = new FileOutputStream(getStateFile())) {
            props.storeToXML(out, "Window states");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setProperties(Properties props,JInternalFrame[] frames){
        for (int i = 0; i < frames.length; i++) {
            JInternalFrame frame = frames[i];
            String prefix = "window." + i + ".";
            props.setProperty(prefix + "class", frame.getClass().getName());
            props.setProperty(prefix + "x", String.valueOf(frame.getX()));
            props.setProperty(prefix + "y", String.valueOf(frame.getY()));
            props.setProperty(prefix + "width", String.valueOf(frame.getWidth()));
            props.setProperty(prefix + "height", String.valueOf(frame.getHeight()));
            props.setProperty(prefix + "icon", String.valueOf(frame.isIcon()));
            props.setProperty(prefix + "maximum", String.valueOf(frame.isMaximum()));
        }
    }

    protected void loadWindowStates() {
        File file = getStateFile();
        Properties props = new Properties();
        checkValidFile(file,props);
        JInternalFrame[] frames = desktopPane.getAllFrames();
        for (int i = 0; i < frames.length; i++) {
            String prefix = "window." + i + ".";
            JInternalFrame frame = frames[i];
            try {
                getPropertiesAndSetUpWindows(props,prefix,frame);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkValidFile(File file,Properties props){
        if (!file.exists()) return;
        try (FileInputStream in = new FileInputStream(file)) {
            props.loadFromXML(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void getPropertiesAndSetUpWindows(Properties props,String prefix,JInternalFrame frame){
        int x = Integer.parseInt(props.getProperty(prefix + "x", "0"));
        int y = Integer.parseInt(props.getProperty(prefix + "y", "0"));
        int width = Integer.parseInt(props.getProperty(prefix + "width", "300"));
        int height = Integer.parseInt(props.getProperty(prefix + "height", "200"));
        boolean icon = Boolean.parseBoolean(props.getProperty(prefix + "icon", "false"));
        boolean max = Boolean.parseBoolean(props.getProperty(prefix + "maximum", "false"));

        frame.setBounds(x, y, width, height);
        try {
            if (max) {
                frame.setMaximum(true);
            } else if (icon) {
                frame.setIcon(true);
            } else {
                frame.setIcon(false);
            }
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    protected File getStateFile() {
        String userHome = System.getProperty("user.home");
        File dir = new File(userHome, ".robots");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, "windows.xml");
    }
}
