package com.frc2013.rmr662.main;

import com.frc2013.rmr662.drive.Drive;
import com.frc2013.rmr662.pneumatic.PneumaticSystem;
import com.frc2013.rmr662.system.generic.RobotMode;

/**
 * @author Dan Mercer
 *
 */
public class TeleopMode extends RobotMode {
    public static final int XBOX_JOYSTICK_PORT = 3;
    
    private final Drive drive;
//    private final PneumaticSystem pneumaticSystem;

    public TeleopMode() {
	super("DemoTeleopMode");
	drive = new Drive();
//        pneumaticSystem = new PneumaticSystem();
    }
    
    protected void onBegin() {
	drive.start();
//        pneumaticSystem.start();
    }

    protected void loop() {
	// STUFF HERE
    }
    
    protected void onEnd() {
	drive.end();
//        pneumaticSystem.end();
    }
}
