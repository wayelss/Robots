package gui;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.*;

import log.Logger;

import static java.awt.SystemColor.desktop;


public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private WindowStateManager windowStateManager = new WindowStateManager(desktopPane);
    private RobotModel robotModel = new RobotModel();
    private CoordinateWindow coordWindow;
    private GameWindow gameWindow;
    private LogWindow logWindow;

    public MainApplicationFrame() {
        Localization.setUpEnglishLanguage();
        configureMainWindow();
        setContentPane(desktopPane);
        createAndAddWindows();
        setJMenuBar(generateMenuBar());
        configureWindowClosing();
    }


    private void configureMainWindow() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);
    }

    private void createAndAddWindows() {
        addLogWindow();
        addGameWindow();
        addCoordinateWindow();
        windowStateManager.loadWindowStates();
    }

    private void addGameWindow() {
        gameWindow = new GameWindow(robotModel);
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);
    }

    private void addCoordinateWindow() {
        coordWindow = new CoordinateWindow(robotModel);
        coordWindow.setSize(200, 100);
        coordWindow.setLocation(320, 10);
        addWindow(coordWindow);
    }

    private void configureWindowClosing() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeYesNoOption();
            }
        });
    }

    private void addLogWindow() {
        logWindow = createLogWindow();
        addWindow(logWindow);
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        Logger.debug(Localization.getResourceBundle().getString("log.protocol_running"));
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

        JMenu lookAndFeelMenu = createLookAndFeelMenu();
        JMenu testMenu = createTestMenu();
        JMenu openWindows = createOpenWindowsMenu();
        JMenu changeLanguage = createChangeLanguage();

        JMenuItem exitPoint = closeApplication();
        exitPoint.setMaximumSize(exitPoint.getPreferredSize());

        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        menuBar.add(openWindows);
        menuBar.add(changeLanguage);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(exitPoint);

        return menuBar;
    }




    private JMenu createChangeLanguage(){
        JMenu changeLanguage = new JMenu(Localization.getResourceBundle().getString("menu.language"));
        changeLanguage.setMnemonic(KeyEvent.VK_V);

        changeLanguage.add(changeRussianLanguage());
        changeLanguage.add(changeEnglishLanguage());

        return changeLanguage;
    }

    private JMenuItem changeRussianLanguage(){
        JMenuItem russianLanguage = new JMenuItem(
               Localization.getResourceBundle().getString("menu.russianLanguage"),
                KeyEvent.VK_S);
       russianLanguage.addActionListener((event) -> {
            Localization.setUpRussianLanguage();
            updateLanguage();

       });

        return russianLanguage;
    }

    private JMenuItem changeEnglishLanguage(){
        JMenuItem englishLanguage = new JMenuItem(
                Localization.getResourceBundle().getString("menu.englishLanguage"),
                KeyEvent.VK_S);
        englishLanguage.addActionListener((event) -> {
            Localization.setUpEnglishLanguage();
            updateLanguage();

        });

        return englishLanguage;
    }

    private void updateLanguage() {
        if (coordWindow != null) coordWindow.updateText();
        if (gameWindow != null) gameWindow.updateText();
        if (logWindow != null) logWindow.updateText();

        setJMenuBar(generateMenuBar());
        SwingUtilities.updateComponentTreeUI(this);

        validate();
        repaint();
    }

    private JMenuItem createCrossplatformLookAndFeel(){
        JMenuItem crossplatformLookAndFeel = new JMenuItem(
                Localization.getResourceBundle().getString("menu.lookAndFeel.crossplatform"),
                KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });

        return crossplatformLookAndFeel;
    }

    private JMenuItem createSystemLookAndFeel(){
        JMenuItem systemLookAndFeel = new JMenuItem(
                Localization.getResourceBundle().getString("menu.lookAndFeel.system"),
                KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });

        return systemLookAndFeel;
    }

    private JMenuItem createAddLogMessageItem(){
        JMenuItem addLogMessageItem = new JMenuItem(
                Localization.getResourceBundle().getString("menu.tests.addLog"),
                KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug(Localization.getResourceBundle().getString("log.new_line"));
        });

        return addLogMessageItem;
    }

    private JMenu createLookAndFeelMenu(){
        JMenu lookAndFeelMenu = new JMenu(Localization.getResourceBundle().getString("menu.lookAndFeel"));
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                Localization.getResourceBundle().getString("menu.lookAndFeel.description"));

        lookAndFeelMenu.add(createSystemLookAndFeel());
        lookAndFeelMenu.add(createCrossplatformLookAndFeel());

        return lookAndFeelMenu;
    }

    private JMenu createOpenWindowsMenu(){
        JMenu openWindowsMenu = new JMenu(Localization.getResourceBundle().getString("menu.windows"));
        openWindowsMenu.setMnemonic(KeyEvent.VK_V);

        openWindowsMenu.add(createOpenGameWindow());
        openWindowsMenu.add(createOpenLogWindow());
        openWindowsMenu.add(createOpenCoordinates());

        return openWindowsMenu;
    }

    private JMenuItem createOpenGameWindow(){
        JMenuItem createOpenGameWindow = new JMenuItem(Localization.getResourceBundle().getString("menu.windows.game"));
        createOpenGameWindow.addActionListener(e -> {
            boolean windowExists = false;
            for(JInternalFrame frame : desktopPane.getAllFrames()){
                if (frame instanceof GameWindow) {
                    windowExists = true;
                    break;
                }
            }
            if(!windowExists){
                addGameWindow();
                Logger.debug(Localization.getResourceBundle().getString("log.game_opened"));
            }
        });
        return createOpenGameWindow;
    }

    private JMenuItem createOpenLogWindow(){
        JMenuItem createOpenGameWindow = new JMenuItem(Localization.getResourceBundle().getString("menu.windows.log"));
        createOpenGameWindow.addActionListener(e -> {
            boolean windowExists = false;
            for(JInternalFrame frame : desktopPane.getAllFrames()){
                if (frame instanceof LogWindow) {
                    windowExists = true;
                    break;
                }
            }
            if(!windowExists){
                addLogWindow();
                Logger.debug(Localization.getResourceBundle().getString("log.logs_opened"));
            }
        });
        return createOpenGameWindow;
    }

    private JMenuItem createOpenCoordinates(){
        JMenuItem createOpenGameWindow = new JMenuItem(Localization.getResourceBundle().getString("menu.windows.coord"));
        createOpenGameWindow.addActionListener(e -> {
            boolean windowExists = false;
            for(JInternalFrame frame : desktopPane.getAllFrames()){
                if (frame instanceof CoordinateWindow) {
                    windowExists = true;
                    break;
                }
            }
            if(!windowExists){
                addCoordinateWindow();
                Logger.debug(Localization.getResourceBundle().getString("log.coords_opened"));
            }
        });
        return createOpenGameWindow;
    }

    private JMenu createTestMenu(){
        JMenu testMenu = new JMenu(Localization.getResourceBundle().getString("menu.tests"));
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                Localization.getResourceBundle().getString("menu.tests.description"));

        testMenu.add(createAddLogMessageItem());

        return testMenu;
    }

    private JMenuItem closeApplication(){
        JMenuItem exitPoint = new JMenuItem(Localization.getResourceBundle().getString("menu.exit"));
        exitPoint.addActionListener(e -> closeYesNoOption());
        return exitPoint;
    }

    private void closeYesNoOption(){
        int result = JOptionPane.showConfirmDialog(
                null,
                Localization.getResourceBundle().getString("dialog.exit.message"),
                Localization.getResourceBundle().getString("dialog.exit.title"),JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION){
            Window[] windows = Window.getWindows();
            for(Window window: windows){
                windowStateManager.saveWindowStates();
                window.dispose();
                System.exit(0);
            }
        }
    }

    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
               | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }

}