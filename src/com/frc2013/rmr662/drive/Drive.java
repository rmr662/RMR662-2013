package com.frc2013.rmr662.drive;

import com.frc2013.rmr662.system.generic.Component;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;

/** Drive Component */
public class Drive extends Component {
    
    public static final int LEFT_SPEED = 0;
    public static final int RIGHT_SPEED = 1;
    
    public static final int LEFT_JOYSTICK = 0;
    public static final int RIGHT_JOYSTICK = 1;
    
    public static final int LEFT_MOTOR = 0;
    public static final int RIGHT_MOTOR = 1;
    
    private Jaguar[] m_motors;
    //private Joystick[] m_joysticks;
    private double[] m_speeds;
    private boolean m_changed;
    
    public Drive(Jaguar[] motors) {
	m_motors = motors;
	m_speeds = new double[2];
	m_changed = false;
	//m_joysticks = joysticks;
    }
    
    // @Override
    public synchronized void update() {
	m_motors[LEFT_MOTOR].set(m_speeds[LEFT_SPEED]);
	m_motors[RIGHT_MOTOR].set(m_speeds[RIGHT_SPEED]);
    }
    
    public synchronized void setSpeeds(double left, double right) {
	m_speeds[LEFT_SPEED] = left;
	m_speeds[RIGHT_SPEED] = right;
	m_changed = true;
    }
    
    public synchronized void arcadeDrive(double xAxis, double yAxis) {
	
    }
}
