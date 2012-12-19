 package com.frc2013.rmr662.system.generic;

import com.frc2013.rmr662.system.Fluffy;
import com.frc2013.rmr662.system.JoystickThread;

import edu.wpi.first.wpilibj.SimpleRobot;

public abstract class Robot extends SimpleRobot {
	private static final int[] JOYSTICK_PORTS = null;
	
	private RobotMode mode;
	private JoystickThread joystickThread;

	@Override
	public final void autonomous() {
		this.mode = getAutoMode();
		mode.start();
	}

	/**
	 * @return A {@link RobotMode} to use for autonomous mode
	 */
	protected abstract RobotMode getAutoMode();

	@Override
	public final void operatorControl() {
		// Init
		this.mode = getTeleOpMode();
		this.joystickThread = new JoystickThread(JOYSTICK_PORTS);
		joystickThread.setMode((TeleopMode) mode);
		
		// Start
		joystickThread.start();
		mode.start();
		
		// Loop
		while (isOperatorControl()) {
			Fluffy.INSTANCE.update(); // TODO left off here
		}
		
		// Shut down
		joystickThread.end();
		joystickThread = null;
		mode.end();
		mode = null;
	}
	
	/**
	 * @return A {@link TeleopMode} to use for teleop mode
	 */
	protected abstract TeleopMode getTeleOpMode();

	@Override
	protected final void disabled() {
		onDisabled();
	}

	/**
	 * Override this to do something here.
	 */
	private void onDisabled() {
		// Override this to do something here.
	}

}
