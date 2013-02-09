package com.frc2013.rmr662.manipulator;

import com.frc2013.rmr662.main.TeleopMode;
import com.frc2013.rmr662.system.HardwarePool;
import com.frc2013.rmr662.system.generic.Component;
import com.frc2013.rmr662.wrappers.Button;
import com.frc2013.rmr662.wrappers.RMRSolenoid;
import edu.wpi.first.wpilibj.Joystick;

public class PneumaticManipulator extends Component {
    
    // Constants
    private static final int WING_CHANNEL = 0;
    
    private static final int DUMPER_CHANNEL = 1;
    private static final int DUMPER_BUTTON = 2;
    
    private static final int TILT_CHANNEL = 2;
    private static final int TILT_BUTTON = 3;
    
    // Fields
    private final Joystick xBoxController;
    private final Button tiltButton;
    
    private final RMRSolenoid winglenoid;
    private boolean wingTarget = false;
    private boolean wingState = false;
    
    private final RMRSolenoid dumplenoid;
    private boolean dumpState = false;
    
    private final RMRSolenoid tiltlenoid;

    // Constructor
    public PneumaticManipulator() {
	final HardwarePool pool = HardwarePool.getInstance();
	winglenoid = pool.getSolenoid(WING_CHANNEL, false);
	dumplenoid = pool.getSolenoid(DUMPER_CHANNEL, false);
	tiltlenoid = pool.getSolenoid(TILT_CHANNEL, false);
	
	xBoxController = new Joystick(TeleopMode.XBOX_JOYSTICK_PORT);
	tiltButton = new Button(xBoxController, TILT_BUTTON);
    }
    
    // Implemented (from superclass) methods
    protected void onBegin() {
	winglenoid.set(false);
	dumplenoid.set(false);
    }

    protected void update() { // Called repeatedly

	// Wing
	if (wingTarget && !wingState) {
	    winglenoid.set(wingTarget);
	    wingState = true;
	}

	// Dumping
	boolean dumperTarget = xBoxController.getRawButton(DUMPER_BUTTON);
	if (dumpState != dumperTarget) {
	    dumplenoid.set(dumperTarget);
	    dumpState = dumperTarget;
	}
	
	// Tilt
	if (tiltButton.wasPressed()) {
	    // Toggle piston state
	    tiltlenoid.set(!tiltlenoid.get());
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