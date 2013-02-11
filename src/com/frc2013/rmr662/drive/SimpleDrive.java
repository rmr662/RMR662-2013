package com.frc2013.rmr662.drive;

import com.frc2013.rmr662.main.TeleopMode;
import com.frc2013.rmr662.system.generic.Component;
import com.frc2013.rmr662.wrappers.RMRJaguar;

import edu.wpi.first.wpilibj.Joystick;

/** Drive Component */
public class SimpleDrive extends Component {
    
    public static final int LEFT_PORT = 1;
    public static final int RIGHT_PORT = 2;
    
    private RMRJaguar left, right;
    private Joystick xbox;
    
    public SimpleDrive () {
	left = new RMRJaguar(LEFT_PORT, 1.0);
	right = new RMRJaguar(RIGHT_PORT, 1.0);
	
	xbox = new Joystick(TeleopMode.XBOX_JOYSTICK_PORT);
    }
    
    public synchronized void update() {
        final double yAxis = xbox.getRawAxis(2);
        final double xAxis = xbox.getRawAxis(4);
        
	left.set(-(yAxis + xAxis) / 3);
        right.set(-(yAxis - xAxis) / 3);
    }
    
    protected void onEnd() {
        left.set(0);
        left.free();
        right.set(0);
        right.free();
    }
    
}
