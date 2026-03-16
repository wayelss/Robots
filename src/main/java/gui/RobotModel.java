package gui;

import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Timer;
import java.util.TimerTask;


public class RobotModel {
    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;
    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.005;


    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private final Timer m_timer = new Timer("model update timer", true);

    public RobotModel() {
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateModel();
            }
        }, 0, 10);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void setTargetPosition(Point p) {
        m_targetPositionX = p.x;
        m_targetPositionY = p.y;
    }

    public double getX() { return m_robotPositionX; }
    public double getY() { return m_robotPositionY; }
    public double getDirection() { return m_robotDirection; }
    public int getTargetX() { return m_targetPositionX; }
    public int getTargetY() { return m_targetPositionY; }

    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY)
    {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    private void updateModel() {
        double distance = distance(m_targetPositionX, m_targetPositionY, m_robotPositionX, m_robotPositionY);
        if (distance < 68.0) return;

        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);

        double angleDifference = angleToTarget - m_robotDirection;
        while (angleDifference < -Math.PI) angleDifference += 2 * Math.PI;
        while (angleDifference > Math.PI) angleDifference -= 2 * Math.PI;

        double velocity = maxVelocity * Math.max(0, Math.cos(angleDifference));

        double angularVelocity = angleDifference * 0.02;

        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);

        moveRobot(velocity, angularVelocity, 10);

        support.firePropertyChange("robotPosition", null, new Point((int)m_robotPositionX, (int)m_robotPositionY));
    }

    private static double applyLimits(double value, double min, double max)
    {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    private static double asNormalizedRadians(double angle)
    {
        while (angle < 0)
        {
            angle += 2*Math.PI;
        }
        while (angle >= 2*Math.PI)
        {
            angle -= 2*Math.PI;
        }
        return angle;
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        {
            velocity = applyLimits(velocity, 0, maxVelocity);
            angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);
            double newX = m_robotPositionX + velocity / angularVelocity *
                    (Math.sin(m_robotDirection + angularVelocity * duration) -
                            Math.sin(m_robotDirection));
            if (!Double.isFinite(newX)) {
                newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
            }
            double newY = m_robotPositionY - velocity / angularVelocity *
                    (Math.cos(m_robotDirection + angularVelocity * duration) -
                            Math.cos(m_robotDirection));
            if (!Double.isFinite(newY)) {
                newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
            }
            m_robotPositionX = newX;
            m_robotPositionY = newY;
            double newDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);
            m_robotDirection = newDirection;

        }
    }
}