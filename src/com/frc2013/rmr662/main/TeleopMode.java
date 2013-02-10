package com.frc2013.rmr662.main;

import com.frc2013.rmr662.climber.NewClimber;
import com.frc2013.rmr662.drive.Drive;
import com.frc2013.rmr662.system.generic.Component;
import com.frc2013.rmr662.system.generic.RobotMode;

/**
 * @author Dan Mercer
 *
 */
public class TeleopMode extends RobotMode {
    public static final int XBOX_JOYSTICK_PORT = 3;
    
    private Component[] components = new Component[2];

    public TeleopMode() {
	super("TeleopMode");
//	components[0] = new Drive();
        components[1] = new NewClimber();
    }
    
    protected void onBegin() {
        for (int i = 0; i < components.length; i++) {
	    if (components[i] != null) {
		components[i].start();
	    }
        }
    }

    protected void loop() {
	// STUFF HERE
    }
    
    protected void onEnd() {
        for (int i = 0; i < components.length; i++) {
            if (components[i] != null) {
		components[i].end();
	    }
        }
    }
}
