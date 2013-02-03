package com.frc2013.rmr662.drive;

import com.frc2013.rmr662.main.TeleopMode;
import com.frc2013.rmr662.system.HardwarePool;
import com.frc2013.rmr662.system.generic.Component;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;

/** Drive Component */
public class SimpleDrive extends Component {
    
    public static final int LEFT_PORT = 1;
    public static final int RIGHT_PORT = 2;
    
    private Jaguar left, right;
    private Joystick xbox;
    
    public SimpleDrive () {
        final HardwarePool pool = HardwarePool.getInstance();
	left = pool.getJaguar(LEFT_PORT);
	right = pool.getJaguar(RIGHT_PORT);
	
	xbox = new Joystick(TeleopMode.XBOX_JOYSTICK_PORT);
    }
    
    public synchronized void update() {
        final double leftAxis = xbox.getRawAxis(2);
        final double rightAxis = xbox.getRawAxis(4);
//	System.out.println("x = " + leftAxis + " y = " + rightAxis);
	arcadeDrive(leftAxis, rightAxis);
    }
    
    /**
     * Drives the robot arcade-style by calculating from two axes
     * 
     * @param xAxis The x-axis to use
     * @param yAxis The y-axis to use
     */
    private void arcadeDrive(double yAxis, double xAxis) {
        double leftSpeed, rightSpeed;
        leftSpeed = (yAxis + xAxis) / 3;
        rightSpeed = (yAxis - xAxis) / 3;
        
        left.set(leftSpeed);
        right.set(rightSpeed);
    }
    
    protected void onEnd() {
        left.set(0);
        right.set(0);
    }
    
}
