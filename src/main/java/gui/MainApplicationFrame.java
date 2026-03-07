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

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();


    public MainApplicationFrame() {
        setUpRussianLanguage();
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
        loadWindowStates();
    }

    private void addGameWindow() {
        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);
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

//    protected JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
//
//        //Set up the lone menu.
//        JMenu menu = new JMenu("Document");
//        menu.setMnemonic(KeyEvent.VK_D);
//        menuBar.add(menu);
//
//        //Set up the first menu item.
//        JMenuItem menuItem = new JMenuItem("New");
//        menuItem.setMnemonic(KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("new");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
//
//        //Set up the second menu item.
//        menuItem = new JMenuItem("Quit");
//        menuItem.setMnemonic(KeyEvent.VK_Q);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("quit");
    ////        menuItem.addActionListener(this);
//        menu.add(menuItem);
//
//        return menuBar;
//    }

    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu lookAndFeelMenu = createLookAndFeelMenu();

        {
            JMenuItem systemLookAndFeel = createSystemLookAndFeel();
            JMenuItem crossplatformLookAndFeel = createCrossplatformLookAndFeel();
            lookAndFeelMenu.add(crossplatformLookAndFeel);
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        JMenu testMenu = createTestMenu();

        {
            JMenuItem addLogMessageItem = createAddLogMessageItem();
            testMenu.add(addLogMessageItem);
        }

        JMenuItem exitPoint = closeApplication();
        exitPoint.setMaximumSize(exitPoint.getPreferredSize());

        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
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

        return lookAndFeelMenu;
    }

    private JMenu createTestMenu(){
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

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
                saveWindowStates();
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

    private void setUpRussianLanguage(){
        UIManager.put("InternalFrame.maxButtonToolTip", "Развернуть");
        UIManager.put("InternalFrame.closeButtonToolTip", "Закрыть");
        UIManager.put("InternalFrame.iconButtonToolTip", "Свернуть");
        UIManager.put("InternalFrameTitlePane.restoreButtonText", "Восстановить");
        UIManager.put("InternalFrameTitlePane.moveButtonText", "Переместить");
        UIManager.put("InternalFrameTitlePane.sizeButtonText", "Размер");
        UIManager.put("InternalFrameTitlePane.minimizeButtonText", "Свернуть");
        UIManager.put("InternalFrameTitlePane.maximizeButtonText", "Развернуть");
        UIManager.put("InternalFrameTitlePane.closeButtonText", "Закрыть");
        UIManager.put("OptionPane.yesButtonText"   , "Да"    );
        UIManager.put("OptionPane.noButtonText"    , "Нет"   );
        UIManager.put("OptionPane.close","Закрыть");
    }

    private void saveWindowStates() {
        Properties props = new Properties();
        JInternalFrame[] frames = desktopPane.getAllFrames();
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
        try (FileOutputStream out = new FileOutputStream(getStateFile())) {
            props.storeToXML(out, "Window states");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadWindowStates() {
        File file = getStateFile();
        if (!file.exists()) return;
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(file)) {
            props.loadFromXML(in);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        JInternalFrame[] frames = desktopPane.getAllFrames();
        for (int i = 0; i < frames.length; i++) {
            String prefix = "window." + i + ".";
            JInternalFrame frame = frames[i];
            try {
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
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private File getStateFile() {
        String userHome = System.getProperty("user.home");
        File dir = new File(userHome, ".robots");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, "windows.xml");
    }
}