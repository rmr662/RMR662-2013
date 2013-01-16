 package com.frc2013.rmr662.system.generic;

import com.frc2013.rmr662.system.Fluffy;
import com.frc2013.rmr662.system.JoystickThread;

import edu.wpi.first.wpilibj.SimpleRobot;

public abstract class Robot extends SimpleRobot {
	private static final int[] JOYSTICK_PORTS = null;
	
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
		// Init
		final TeleopMode mode = getTeleOpMode();
		this.mode = mode;
		this.joystickThread = new JoystickThread(mode.getJoystickPorts());
		joystickThread.setMode((TeleopMode) mode);
		
		// Start
		joystickThread.start();
		mode.start();
		
		// Loop
		final Fluffy fluffy = Fluffy.getInstance();
		while (isOperatorControl()) {
			fluffy.update(); 
		}
		
		// Shut down
		joystickThread.end();
		joystickThread = null;
		this.mode.end();
		this.mode = null;
	}
	
	/**
	 * @return A {@link TeleopMode} to use for teleop mode
	 */
	protected abstract TeleopMode getTeleOpMode();

}
