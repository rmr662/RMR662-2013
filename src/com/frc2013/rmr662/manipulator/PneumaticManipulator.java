package com.frc2013.rmr662.manipulator;

import com.frc2013.rmr662.main.TeleopMode;
import com.frc2013.rmr662.system.generic.Component;
import com.frc2013.rmr662.wrappers.Button;
import com.frc2013.rmr662.wrappers.RMRSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;

public class PneumaticManipulator extends Component {
    
    // Constants
    private static final int WING_CHANNEL_EXTEND = 0;
    private static final int WING_CHANNEL_RETRACT = 0; 
    
    private static final int DUMPER_CHANNEL = 1;
    
    private static final int TILT_CHANNEL_EXTEND = 4;
    private static final int TILT_CHANNEL_RETRACT = 5;
    
    private static final int WING_BUTTON = 5;
    private static final int DUMPER_BUTTON = 2;
    private static final int TILT_BUTTON = 3;
    
    // Fields
    private final Joystick xBoxController;
    private final Button tiltButton;
    
    private final DoubleSolenoid winglenoid;
    private boolean wingTarget = false;
    private boolean wingState = false;
    
    private final RMRSolenoid dumplenoid;
    private boolean dumpState = false;
    
    private final DoubleSolenoid tiltlenoid;

    // Constructor
    public PneumaticManipulator() {
	winglenoid = new DoubleSolenoid(WING_CHANNEL_EXTEND, WING_CHANNEL_RETRACT);
	dumplenoid = new RMRSolenoid(DUMPER_CHANNEL, false);
	tiltlenoid = new DoubleSolenoid(TILT_CHANNEL_EXTEND, TILT_CHANNEL_RETRACT);
	
	xBoxController = new Joystick(TeleopMode.XBOX_JOYSTICK_PORT);
	tiltButton = new Button(xBoxController, TILT_BUTTON);
    }
    
    // Implemented (from superclass) methods
    protected void onBegin() {
	winglenoid.set(DoubleSolenoid.Value.kOff);
	dumplenoid.set(false);
    }

    protected void update() { // Called repeatedly

	// Wing
	wingTarget = xBoxController.getRawButton(WING_BUTTON);
	
	if (wingTarget && !wingState) {
	    winglenoid.set(DoubleSolenoid.Value.kForward);
	    wingState = true;
	} else if (!wingTarget && wingState) {
	    winglenoid.set(DoubleSolenoid.Value.kReverse);
	    wingState = false;
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
	    DoubleSolenoid.Value currentState = tiltlenoid.get();
	    if (currentState == DoubleSolenoid.Value.kForward) {
		tiltlenoid.set(DoubleSolenoid.Value.kReverse);
	    } else {
		tiltlenoid.set(DoubleSolenoid.Value.kForward);
	    }
	}
	
    }

    protected void onEnd() {
	// Called when robot is disabled. 
	// Be sure to set hardware elements to safe states.
	dumplenoid.set(false);
	dumplenoid.free();
	tiltlenoid.free();
	winglenoid.free();
    }
}