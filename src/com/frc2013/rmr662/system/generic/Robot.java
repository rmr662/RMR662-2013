 package com.frc2013.rmr662.system.generic;

import com.frc2013.rmr662.system.JoystickThread;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SimpleRobot;

public abstract class Robot extends SimpleRobot {
	private RobotMode mode;
	private JoystickThread joystickThread;

//	@Override
	public final void autonomous() {
		this.mode = getAutoMode();
		mode.start();
	}

	/**
	 * @return A {@link RobotMode} to use for autonomous mode
	 */
	protected abstract RobotMode getAutoMode();

//	@Override
	public final void operatorControl() {
		final TeleopMode mode = getTeleOpMode();
		this.mode = mode;
		this.joystickThread = new JoystickThread(getJoysticks());
		joystickThread.setMode(mode);
		joystickThread.start();
		mode.start();
		while (isOperatorControl()) {
			Watchdog.getInstance().check(); // TODO left off here
			wait(100);
		}
	}
	
	/**
	 * @return An array of {@link Joystick}s to use for input
	 */
	protected abstract Joystick[] getJoysticks();

	/**
	 * @return A {@link TeleopMode} to use for autonomous mode
	 */
	protected abstract TeleopMode getTeleOpMode();

//	@Override
	protected final void disabled() {
		if (joystickThread != null) {
			joystickThread.end();
		}
		onDisabled();
	}

	/**
	 * Override this to do something here.
	 */
	private void onDisabled() {
		// Override this to do something here.
	}

}
