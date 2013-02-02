package com.frc2013.rmr662.manipulator;

import com.frc2013.rmr662.main.TeleopMode;
import com.frc2013.rmr662.system.HardwarePool;
import com.frc2013.rmr662.system.generic.Component;
import com.frc2013.rmr662.wrappers.RMRSolenoid;
import edu.wpi.first.wpilibj.Joystick;

public class PneumaticManipulator extends Component {
    
    // Constants
    private static final int WING_CHANNEL = 0;
    private static final int DUMPER_CHANNEL = 1;
    private static final int DUMPER_BUTTON = 2;
    
    // Fields
    private final RMRSolenoid winglenoid;
    private boolean wingTarget = false;
    private boolean wingState = false;
    private final RMRSolenoid dumplenoid;
    private boolean dumpState = false;
    private final Joystick xBoxController;

    // Constructor
    public PneumaticManipulator() {
	winglenoid = HardwarePool.getInstance().getSolenoid(WING_CHANNEL, false);
	dumplenoid = HardwarePool.getInstance().getSolenoid(DUMPER_CHANNEL, false);
	xBoxController = new Joystick(TeleopMode.XBOX_JOYSTICK_PORT);
    }
    
    // Implemented (from superclass) methods
    protected void onBegin() {
	winglenoid.set(false);
	dumplenoid.set(false);
    }

    protected void update() {
	// Called repeatedly

	if (wingTarget && !wingState) {
	    winglenoid.set(wingTarget);
	    wingState = true;
	}

	boolean dumperTarget = xBoxController.getRawButton(DUMPER_BUTTON);

	if (dumpState != dumperTarget) {
	    dumplenoid.set(dumperTarget);
	    dumpState = dumperTarget;
	}
    }

    protected void onEnd() {
	// Called when robot is disabled. 
	// Be sure to set hardware elements to safe states.
	dumplenoid.set(false);
    }
    
    // Public method(s)

    /**
     * Call this to extend the wings. Should be called from Climber or
     * something.
     */
    public synchronized void extendWings() {
	wingTarget = true;
    }
}