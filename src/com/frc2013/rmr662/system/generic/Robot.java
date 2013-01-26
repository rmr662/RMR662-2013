package com.frc2013.rmr662.system.generic;

import com.frc2013.rmr662.system.WatchdogAggregator;

import edu.wpi.first.wpilibj.SimpleRobot;

public abstract class Robot extends SimpleRobot {

    // @Override
    public final void autonomous() {
	// Init
	final RobotMode mode = getAutoMode();
	final boolean hasMode = (mode != null);

	if (hasMode) {
	    // Start mode
	    mode.start();

	    // Loop
	    final WatchdogAggregator fluffy = WatchdogAggregator.getInstance();
	    while (isEnabled() && isAutonomous()) {
		fluffy.update();
	    }

	    // Shut down
	    mode.end();

	} else {
	    System.err.println("getAutoMode() returned null!");
	    while (isEnabled() && isAutonomous()) {
		// Nothing to do!
	    }
	}
    }

    /**
     * @return A {@link RobotMode} to use for autonomous mode
     */
    protected abstract RobotMode getAutoMode();

    // @Override
    public final void operatorControl() {
	// Init
	final RobotMode mode = getTeleopMode();
	final boolean hasMode = (mode != null);

	if (hasMode) {
	    // Start mode
	    mode.start();

	    // Loop
	    final WatchdogAggregator fluffy = WatchdogAggregator.getInstance();
	    while (isEnabled() && isOperatorControl()) {
		fluffy.update();
	    }

	    // Shut down
	    mode.end();
	} else {
	    System.err.println("getTeleopMode() returned null!");
	    while (isEnabled() && isOperatorControl()) {
		// Nothing to do!
	    }
	}
    }

    /**
     * @return A {@link RobotMode} to use for teleop mode
     */
    protected abstract RobotMode getTeleopMode();
}
