package com.frc2013.rmr662.dumper;

import com.frc2013.rmr662.main.TeleopMode;
import com.frc2013.rmr662.system.HardwarePool;
import com.frc2013.rmr662.system.generic.Component;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;

/**
 * The dumper component that dumps discs into the goal.
 */
public class Dumper extends Component {
    public static final int DUMPER_BUTTON = 9;
    public static final int SOLENOID_CHANNEL = 1;
    private static final boolean SOLENOID_INVERTED = false;
    
    private final Solenoid piston;
    private final Joystick xboxController;
    
    private boolean pistonState;
    
    public Dumper() {
	piston = HardwarePool.getInstance().getSolenoid(SOLENOID_CHANNEL, SOLENOID_INVERTED);
	pistonState = piston.get();
	xboxController = new Joystick(TeleopMode.XBOX_JOYSTICK_PORT);
    }
    
    protected void update() {
	boolean targetState = xboxController.getRawButton(DUMPER_BUTTON);
	if (targetState != pistonState) {
	    piston.set(targetState);
	    pistonState = targetState;
	}
    }
    
}
