package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import log.Logger;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;


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
        if (gameWindow != null && !gameWindow.isClosed()) {
            try {
                gameWindow.setSelected(true);
            } catch (java.beans.PropertyVetoException e) {
                e.printStackTrace();
            }
            return;
        }

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
        JMenu gameSettings = createGameSettingsMenu();

        JMenuItem exitPoint = closeApplication();
        exitPoint.setMaximumSize(exitPoint.getPreferredSize());

        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        menuBar.add(openWindows);
        menuBar.add(changeLanguage);
        menuBar.add(gameSettings);
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

    private void chooseAndSetImage(boolean isRobot) {

        if (gameWindow == null) {
            showWarningDialog();
            return;
        }

        File selectedFile = chooseImageFile();
        if (selectedFile == null) {
            return;
        }

        Image newImage = processImageFile(selectedFile);

        if (newImage != null) {
            applyImageToGame(newImage, isRobot);
        }
    }

    private File chooseImageFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(Localization.getResourceBundle().getString("dialog.fileChooser.title"));

        FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG, JPG, GIF", "png", "jpg", "jpeg", "gif");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    private Image processImageFile(File file) {
        try {
            Image newImage = ImageIO.read(file);

            if (newImage == null) {
                // Если Java не поняла формат (например, WEBP)
                showErrorDialog("Не удалось распознать формат изображения.");
                return null;
            }

            return scaleImageIfTooLarge(newImage, 100, 100);

        } catch (IOException ex) {
            Logger.debug(Localization.getResourceBundle().getString("dialog.fileChooser.error"));
            showErrorDialog(Localization.getResourceBundle().getString("dialog.fileChooser.error"));
            return null;
        }
    }

    private void applyImageToGame(Image image, boolean isRobot) {
        if (isRobot) {
            gameWindow.setRobotImage(image);
            Logger.debug(Localization.getResourceBundle().getString("log.robot_image_changed"));
        } else {
            gameWindow.setTargetImage(image);
            Logger.debug(Localization.getResourceBundle().getString("log.target_image_changed"));
        }
    }

    private void showWarningDialog() {
        JOptionPane.showMessageDialog(this,
                Localization.getResourceBundle().getString("dialog.fileChooser.warning"),
                Localization.getResourceBundle().getString("dialog.fileChooser.warningTitle"),
                JOptionPane.WARNING_MESSAGE);
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                Localization.getResourceBundle().getString("dialog.fileChooser.errorTitle"),
                JOptionPane.ERROR_MESSAGE);
    }

    private Image scaleImageIfTooLarge(Image img, int maxWidth, int maxHeight) {
        if (img == null) return null;

        int width = img.getWidth(null);
        int height = img.getHeight(null);

        if (width <= maxWidth && height <= maxHeight && width > 0) {
            return img;
        }

        double ratio = Math.min((double) maxWidth / width, (double) maxHeight / height);
        int newWidth = (int) (width * ratio);
        int newHeight = (int) (height * ratio);

        java.awt.image.BufferedImage scaledBI = new java.awt.image.BufferedImage(
                newWidth, newHeight, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaledBI.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return scaledBI;
    }

    private JMenu createGameSettingsMenu() {
        JMenuItem changeRobotImage = MenuBuilder.createJMenuItem(
                Localization.getResourceBundle().getString("menu.gameSettings.changeRobot"),
                () -> chooseAndSetImage(true)
        );

        JMenuItem changeTargetImage = MenuBuilder.createJMenuItem(
                Localization.getResourceBundle().getString("menu.gameSettings.changeTarget"),
                () -> chooseAndSetImage(false)
        );

        JMenuItem resetRobotImage = MenuBuilder.createJMenuItem(
                Localization.getResourceBundle().getString("menu.gameSettings.resetRobot"),
                () -> {
                    if (gameWindow != null) gameWindow.resetRobotImage();
                }
        );

        JMenuItem resetTargetImage = MenuBuilder.createJMenuItem(
                Localization.getResourceBundle().getString("menu.gameSettings.resetTarget"),
                () -> {
                    if (gameWindow != null) gameWindow.resetTargetImage();
                }
        );

        JMenu menu = MenuBuilder.createJMenu(
                Localization.getResourceBundle().getString("menu.gameSettings"),
                Localization.getResourceBundle().getString("menu.gameSettings.description"),
                changeRobotImage, changeTargetImage
        );

        menu.addSeparator();
        menu.add(resetRobotImage);
        menu.add(resetTargetImage);

        return menu;
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