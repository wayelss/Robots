package gui;

import java.awt.Frame;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.*;

public class RobotsProgram
{
    public static void main(String[] args) {
      try {
      } catch (Exception e) {
        e.printStackTrace();
      }
      SwingUtilities.invokeLater(() -> {
        MainApplicationFrame frame = new MainApplicationFrame();
        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
      });
    }}
