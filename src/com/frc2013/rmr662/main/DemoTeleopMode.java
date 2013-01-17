package com.frc2013.rmr662.main;

import com.frc2013.rmr662.drive.Drive;
import com.frc2013.rmr662.system.generic.RobotMode;

/**
 * @author Dan Mercer
 *
 */
public class DemoTeleopMode extends RobotMode {
    private final Drive drive;

    public DemoTeleopMode() {
	super("DemoTeleopMode");
	drive = new Drive();
	drive.start();
    }

    /**
     * Called repeatedly while the mode is running
     *
     * @see com.frc2013.rmr662.system.generic.RobotMode#loop()
     */
    // @Override
    protected void loop() {
	// STUFF HERE
    }
    
    protected void onEnd() {
	drive.end();
    }
}
