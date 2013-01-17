package com.frc2013.rmr662.system.generic;

import com.frc2013.rmr662.system.Fluffy;

import edu.wpi.first.wpilibj.SimpleRobot;

public abstract class Robot extends SimpleRobot {
	
	// @Override
	public final void autonomous() {
		// Init
		final RobotMode mode = getAutoMode();
		mode.start();
		
		// Loop
		final Fluffy fluffy = Fluffy.getInstance();
		while (isEnabled() && isAutonomous()) {
			fluffy.update();
		}
		
		// Shut down
		mode.end();
	}
	
	/**
	 * @return A {@link RobotMode} to use for autonomous mode
	 */
	protected abstract RobotMode getAutoMode();
	
	// @Override
	public final void operatorControl() {
		// Init
		final RobotMode mode = getTeleopMode();
		mode.start();
		
		// Loop
		final Fluffy fluffy = Fluffy.getInstance();
		while (isEnabled() && isOperatorControl()) {
			fluffy.update();
		}
		
		// Shut down
		mode.end();
	}
	
	/**
	 * @return A {@link RobotMode} to use for teleop mode
	 */
	protected abstract RobotMode getTeleopMode();
	
}
