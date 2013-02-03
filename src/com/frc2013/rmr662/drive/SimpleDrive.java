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
        final double yAxis = xbox.getRawAxis(2);
        final double xAxis = xbox.getRawAxis(4);
        
	left.set(-(yAxis + xAxis) / 3);
        right.set(-(yAxis - xAxis) / 3);
    }
    
    protected void onEnd() {
        left.set(0);
        right.set(0);
    }
    
}
