package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import log.Logger;

public class MainApplicationFrame extends JFrame {
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

    protected LogWindow createLogWindow() {
        LogWindow window = new LogWindow(Logger.getDefaultLogSource());
        window.setLocation(10, 10);
        window.setSize(300, 800);
        Logger.debug(Localization.getResourceBundle().getString("log.protocol_running"));
        return window;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private JMenuBar generateMenuBar() {
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

    private JMenu createChangeLanguage() {
        JMenuItem russianLanguage = MenuBuilder.createJMenuItem(
                Localization.getResourceBundle().getString("menu.russianLanguage"),
                () -> {
                    Localization.setUpRussianLanguage();
                    updateLanguage();
                }
        );

        JMenuItem englishLanguage = MenuBuilder.createJMenuItem(
                Localization.getResourceBundle().getString("menu.englishLanguage"),
                () -> {
                    Localization.setUpEnglishLanguage();
                    updateLanguage();
                }
        );

        return MenuBuilder.createJMenu(
                Localization.getResourceBundle().getString("menu.language"),
                "",
                russianLanguage, englishLanguage
        );
    }

    private JMenu createLookAndFeelMenu() {
        JMenuItem createSystemLookAndFeel = MenuBuilder.createJMenuItem(
                Localization.getResourceBundle().getString("menu.lookAndFeel.system"),
                () -> {
                    setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    this.invalidate();
                }
        );
        JMenuItem createCrossplatformLookAndFeel = MenuBuilder.createJMenuItem(
                Localization.getResourceBundle().getString("menu.lookAndFeel.crossplatform"),
                () -> {
                    setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    this.invalidate();
                }
        );
        return MenuBuilder.createJMenu(
                Localization.getResourceBundle().getString("menu.lookAndFeel"),
                Localization.getResourceBundle().getString("menu.lookAndFeel.description"),
                createSystemLookAndFeel, createCrossplatformLookAndFeel
        );
    }

    private JMenu createTestMenu() {
        JMenuItem logMessageItem = MenuBuilder.createJMenuItem(
                Localization.getResourceBundle().getString("menu.tests.addLog"),
                () -> Logger.debug(Localization.getResourceBundle().getString("log.new_line"))
        );
        return MenuBuilder.createJMenu(
                Localization.getResourceBundle().getString("menu.tests"),
                Localization.getResourceBundle().getString("menu.tests.description"),
                logMessageItem
        );
    }

    private JMenu createOpenWindowsMenu() {
        JMenuItem openGameWindow = MenuBuilder.createJMenuItem(
                Localization.getResourceBundle().getString("menu.windows.game"),
                () -> {
                    boolean windowExists = false;
                    for (JInternalFrame frame : desktopPane.getAllFrames()) {
                        if (frame instanceof GameWindow) {
                            windowExists = true;
                            break;
                        }
                    }
                    if (!windowExists) {
                        addGameWindow();
                        Logger.debug(Localization.getResourceBundle().getString("log.game_opened"));
                    }
                }
        );

        JMenuItem openLogWindow = MenuBuilder.createJMenuItem(
                Localization.getResourceBundle().getString("menu.windows.log"),
                () -> {
                    boolean windowExists = false;
                    for (JInternalFrame frame : desktopPane.getAllFrames()) {
                        if (frame instanceof LogWindow) {
                            windowExists = true;
                            break;
                        }
                    }
                    if (!windowExists) {
                        addLogWindow();
                        Logger.debug(Localization.getResourceBundle().getString("log.logs_opened"));
                    }
                }
        );

        JMenuItem openCoordinatesWindow = MenuBuilder.createJMenuItem(
                Localization.getResourceBundle().getString("menu.windows.coord"),
                () -> {
                    boolean windowExists = false;
                    for (JInternalFrame frame : desktopPane.getAllFrames()) {
                        if (frame instanceof CoordinateWindow) {
                            windowExists = true;
                            break;
                        }
                    }
                    if (!windowExists) {
                        addCoordinateWindow();
                        Logger.debug(Localization.getResourceBundle().getString("log.coords_opened"));
                    }
                }
        );

        return MenuBuilder.createJMenu(
                Localization.getResourceBundle().getString("menu.windows"),
                "",
                openGameWindow, openLogWindow, openCoordinatesWindow
        );
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

    private JMenuItem closeApplication() {
        return MenuBuilder.createJMenuItem(
                Localization.getResourceBundle().getString("menu.exit"),
                this::closeYesNoOption
        );
    }

    private void closeYesNoOption() {
        int result = JOptionPane.showConfirmDialog(
                null,
                Localization.getResourceBundle().getString("dialog.exit.message"),
                Localization.getResourceBundle().getString("dialog.exit.title"), JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            Window[] windows = Window.getWindows();
            for (Window window : windows) {
                windowStateManager.saveWindowStates();
                window.dispose();
                System.exit(0);
            }
        }
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
        }
    }
}