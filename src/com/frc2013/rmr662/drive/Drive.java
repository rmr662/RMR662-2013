package com.frc2013.rmr662.drive;

import com.frc2013.rmr662.main.TeleopMode;
import com.frc2013.rmr662.system.HardwarePool;
import com.frc2013.rmr662.system.generic.Component;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;

/** Drive Component */
public class Drive extends Component {
    
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    
    //public static final int LEFT_MOTOR = 0;
    //public static final int RIGHT_MOTOR = 1;
    
    private Jaguar[] m_motors = {null, null};
    private Joystick[] m_joysticks = {null, null};
    private double[] m_speeds = {0d , 0d};
    private double[] arcadeAxes = {0d , 0d};
    private boolean m_changed;
    private boolean pidEnabled = false;
    
    public Drive () {
	m_motors[LEFT] = HardwarePool.getInstance().getJaguar(LEFT+1);
	m_motors[RIGHT] = HardwarePool.getInstance().getJaguar(RIGHT+1);
	
	m_joysticks[LEFT] = new Joystick(TeleopMode.XBOX_JOYSTICK_PORT);
	m_joysticks[RIGHT] = new Joystick(RIGHT+1);
    }
    
    public Drive(Jaguar[] motors) {
	m_motors = motors;
	m_changed = false;
	//m_joysticks = joysticks;
    }
    
    // @Override
    public synchronized void update() {
	setAxisValues(m_joysticks);
	System.out.println("x = " + arcadeAxes[LEFT] + " y = " + arcadeAxes[RIGHT]);
	arcadeDrive(arcadeAxes[LEFT], arcadeAxes[RIGHT]);
    }
    
    /**
     * Sets the speeds to send to each side motor
     * 
     * @param left The speed to send to the left motors
     * @param right  The speed to send to the right motors
     */
    public synchronized void setSpeeds(double left, double right) {
	m_speeds[LEFT] = left;
	m_speeds[RIGHT] = right;
	m_changed = true;
    }
    
    /**
     * Drives the robot arcade-style by calculating from two axes
     * 
     * @param xAxis The x-axis to use
     * @param yAxis The y-axis to use
     */
    public synchronized void arcadeDrive(double yAxis, double xAxis) {
	if (pidEnabled == false) {
	    if (yAxis > 0.0) {
		if (xAxis > 0.0) {
		    setSpeeds(yAxis-xAxis, Math.max(yAxis, xAxis));
		}
		else {
		    setSpeeds(Math.max(yAxis, -xAxis), yAxis+xAxis);
		}
	    }
	    else {
		if (xAxis > 0.0) {
		    setSpeeds(-Math.max(-yAxis, xAxis), yAxis+xAxis);
		}
		else {
		    setSpeeds(yAxis-xAxis, -Math.max(-yAxis, -xAxis));
		}
	    }
	}
	m_motors[LEFT].set(m_speeds[LEFT]);
	m_motors[RIGHT].set(m_speeds[RIGHT]);
    }
    
    public void setAxisValues(Joystick[] joysticks) {
	arcadeAxes[LEFT] = joysticks[LEFT].getRawAxis(2);
	arcadeAxes[RIGHT] = joysticks[LEFT].getRawAxis(4);
    }

	protected void onEnd() {
		final Jaguar[] motors = m_motors;
		final int length = motors.length;
		
		for (int i = 0; i < length; i++) {
			motors[i].set(0.0);
		}
	}
}
