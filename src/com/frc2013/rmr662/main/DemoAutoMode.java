package com.frc2013.rmr662.main;

import com.frc2013.rmr662.drive.Drive;
import com.frc2013.rmr662.system.generic.RobotMode;

public class DemoAutoMode extends RobotMode {
	private final Drive drive;

	public DemoAutoMode() {
		super("DemoAutoMode");
		drive = new Drive();
	}
	
	protected void onBegin() {
		drive.start();
	}
	
	protected void loop() {
		// STUFF GOES HERE
	}
	
	protected void onEnd() {
		drive.end();
	}

}
