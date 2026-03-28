package gui;

import log.Logger;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GameVisualizer extends JPanel {
    private final Timer m_timer = initTimer();
    private final RobotModel model;
    private Image targetImage;

    private Image robotImage;

    private static Timer initTimer() {
        return new Timer("events generator", true);
    }

    public GameVisualizer(RobotModel model) {
        this.model = model;

        try {
            robotImage = ImageIO.read(new File("tolsty.png"));
        } catch (IOException e) {
            System.err.println("Не удалось загрузить картинку толстого");
            e.printStackTrace();
        }

        try {
            targetImage = ImageIO.read(new File("mac-combo.png"));
        } catch (IOException e) {
            System.err.println("Не удалось загрузить картинку бургера");
            e.printStackTrace();
        }

        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                EventQueue.invokeLater(GameVisualizer.this::repaint);
            }
        }, 0, 50);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                model.setTargetPosition(e.getPoint());
                String coordinates = MessageFormatCache.checkPattern(
                        Localization.getResourceBundle().getString("log.target_set")
                        ,e.getX()
                        ,e.getY()
                );
                Logger.debug(coordinates);
                repaint();
            }
        });
        setDoubleBuffered(true);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        drawRobot(g2d, (int)Math.round(model.getX()), (int)Math.round(model.getY()), model.getDirection());
        drawTarget(g2d, (int)model.getTargetX(), (int)model.getTargetY());
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction)
    {
        AffineTransform oldTransform = g.getTransform();

        g.rotate(direction, x, y);

        if (robotImage != null) {

            int imgWidth = robotImage.getWidth(null);
            int imgHeight = robotImage.getHeight(null);

            g.drawImage(robotImage, x - imgWidth / 2, y - imgHeight / 2, null);
        } else {

            g.setColor(Color.MAGENTA);
            fillOval(g, x, y, 30, 10);
            g.setColor(Color.BLACK);
            drawOval(g, x, y, 30, 10);
        }

        g.setTransform(oldTransform);
    }

    private void drawTarget(Graphics2D g, int x, int y)
    {
        if (targetImage != null) {

            int imgWidth = targetImage.getWidth(null);
            int imgHeight = targetImage.getHeight(null);

            g.drawImage(targetImage, x - imgWidth / 2, y - imgHeight / 2, null);
        } else {

            g.setColor(Color.GREEN);
            fillOval(g, x, y, 5, 5);
            g.setColor(Color.BLACK);
        }   drawOval(g, x, y, 5, 5);
    }
}