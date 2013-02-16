package com.frc2013.rmr662.drive;

import com.frc2013.rmr662.main.TeleopMode;
import com.frc2013.rmr662.system.generic.Component;
import com.frc2013.rmr662.wrappers.RMRJaguar;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;

/** Drive Component */
public class SimpleDrive extends Component {
    
    public static final int LEFT_PORT = 1;
    public static final int RIGHT_PORT = 2;
    
    private RMRJaguar left, right;
    private Joystick xbox;
    
    private RobotDrive robotDrive;
    
    public SimpleDrive () {
	left = new RMRJaguar(LEFT_PORT, 1.0);
	right = new RMRJaguar(RIGHT_PORT, 1.0);
	
	xbox = new Joystick(TeleopMode.XBOX_JOYSTICK_PORT);
	
	robotDrive = new RobotDrive(left, right);
    }
    
    public synchronized void update() {
        robotDrive.arcadeDrive(xbox, 4, xbox, 2, true);
    }
    
    protected void onEnd() {
        left.set(0);
        right.set(0);
	left.free();
	right.free();
	robotDrive.free();
    }
    
}
