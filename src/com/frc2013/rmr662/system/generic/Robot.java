 package com.frc2013.rmr662.system.generic;

import com.frc2013.rmr662.system.JoystickThread;

import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Watchdog;

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
		TeleopMode mode = getTeleOpMode();
		this.mode = mode;
		this.joystickThread = new JoystickThread(null /* TODO Initialize Joysticks */);
		joystickThread.setMode(mode);
		joystickThread.start();
		mode.start();
	}
	
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
