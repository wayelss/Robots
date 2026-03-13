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

import javax.swing.*;

import log.Logger;

import static java.awt.SystemColor.desktop;


public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    WindowStateManager windowStateManager = new WindowStateManager(desktopPane);
    private RobotModel robotModel = new RobotModel();

    public MainApplicationFrame() {
        UI.setUpRussianLanguage();
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
        addCoordinateWindow(); // Добавляем новое окно
        windowStateManager.loadWindowStates();
    }

    private void addGameWindow() {
        GameWindow gameWindow = new GameWindow(robotModel); // Передаем модель
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);
    }

    private void addCoordinateWindow() {
        CoordinateWindow coordWindow = new CoordinateWindow(robotModel);
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
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
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

        JMenuItem exitPoint = closeApplication();
        exitPoint.setMaximumSize(exitPoint.getPreferredSize());

        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        menuBar.add(openWindows);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(exitPoint);

        return menuBar;
    }

    private JMenuItem createCrossplatformLookAndFeel(){
        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });

        return crossplatformLookAndFeel;
    }

    private JMenuItem createSystemLookAndFeel(){
        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });

        return systemLookAndFeel;
    }

    private JMenuItem createAddLogMessageItem(){
        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });

        return addLogMessageItem;
    }

    private JMenu createLookAndFeelMenu(){
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        lookAndFeelMenu.add(createSystemLookAndFeel());
        lookAndFeelMenu.add(createCrossplatformLookAndFeel());

        return lookAndFeelMenu;
    }

    private JMenu createOpenWindowsMenu(){
        JMenu openWindowsMenu = new JMenu("Открыть окна");
        openWindowsMenu.setMnemonic(KeyEvent.VK_V);

        openWindowsMenu.add(createOpenGameWindow());
        openWindowsMenu.add(createOpenLogWindow());

        return openWindowsMenu;
    }

    private JMenuItem createOpenGameWindow(){
        JMenuItem createOpenGameWindow = new JMenuItem("Открыть игру");
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
            Logger.debug("Игры открылась!");
        }
        });
        return createOpenGameWindow;
    }

    private JMenuItem createOpenLogWindow(){
        JMenuItem createOpenGameWindow = new JMenuItem("Открыть логи");
        createOpenGameWindow.addActionListener(e -> {
            boolean windowExists = false;
            for(JInternalFrame frame : desktopPane.getAllFrames()){
                if (frame instanceof GameWindow) {
                    windowExists = true;
                    break;
                }
            }
            if(!windowExists){
                addLogWindow();
                Logger.debug("Логи открылись!");
            }
        });
        return createOpenGameWindow;
    }

    private JMenu createTestMenu(){
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        testMenu.add(createAddLogMessageItem());

        return testMenu;
    }

    private JMenuItem closeApplication(){
        JMenuItem exitPoint = new JMenuItem("Закрыть приложение");
        exitPoint.addActionListener(e -> closeYesNoOption());
        return exitPoint;
    }

    private void closeYesNoOption(){
        int result = JOptionPane.showConfirmDialog(null, "Вы действительно хотите выйти?",
                "",JOptionPane.YES_NO_OPTION);
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