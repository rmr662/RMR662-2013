/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frc2013.rmr662.climber;
import com.frc2013.rmr662.main.TeleopMode;
import com.frc2013.rmr662.system.HardwarePool;
import com.frc2013.rmr662.system.generic.Component;
import com.frc2013.rmr662.wrappers.RMRDigitalInput;
import com.frc2013.rmr662.wrappers.RMRSolenoid;

import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// TODO: fill in returnAxisVal()
// TODO: abort in the checkEmergency and emergencyControl methods, should be able to abort in auto directly
// TODO: refactor so that numbers are constants for names
// TODO: refactor so that sensor() method is getSensor() in all places
// TODO: replace notoutofbounds with limitswitch checking, to be specific to each case, up or down
public class Climber extends Component {
	
	private static class EmergencyException extends Exception {
		private static final long serialVersionUID = -7491961259340232519L;
		// ^ This is just here to make Eclipse happy.
	}
	
	// Constants
	public static final int MOTOR_PORT = 3;
	public static final int MOTOR_DIRECTION_MULT = 1;
	public static final int PISTON_PORT = 1;
	public static final int SERVO_PORT = 0;
	
	public static final double SERVO_UNLOCK = 0.0;
	public static final double SERVO_LOCK = 1.0;
	
	public static final int SENSOR_LIMIT_BOTTOM = 0;
	public static final int SENSOR_LIMIT_TOP = 1;
	public static final int SENSOR_FIXED_LEFT = 2;
	public static final int SENSOR_FIXED_RIGHT = 3;
	public static final int SENSOR_CARRIAGE_LEFT = 4;
	public static final int SENSOR_CARRIAGE_RIGHT = 5;
	
	// Fields
	private final Joystick controller;
	private final RMRSolenoid piston;
	private final Jaguar motor;
	private final Servo servo;
	
	private final RMRDigitalInput topLimit;
	private final RMRDigitalInput bottomLimit;
	private final RMRDigitalInput leftFixed;
	private final RMRDigitalInput rightFixed;
	private final RMRDigitalInput leftCarriage;
	private final RMRDigitalInput rightCarriage;
	
	public Climber() {
		// initialize member variables
		final HardwarePool pool = HardwarePool.getInstance();
		piston = pool.getSolenoid(PISTON_PORT, false);
		motor = pool.getJaguar(MOTOR_PORT);
		servo = pool.getServo(SERVO_PORT);
		
		topLimit = pool.getDigitalInput(SENSOR_LIMIT_TOP, false);
		bottomLimit = pool.getDigitalInput(SENSOR_LIMIT_BOTTOM, false);
		leftFixed = pool.getDigitalInput(SENSOR_FIXED_LEFT, false);
		rightFixed = pool.getDigitalInput(SENSOR_FIXED_RIGHT, false);
		leftCarriage = pool.getDigitalInput(SENSOR_CARRIAGE_LEFT, false);
		rightCarriage = pool.getDigitalInput(SENSOR_CARRIAGE_RIGHT, false);
		
		controller = new Joystick(TeleopMode.XBOX_JOYSTICK_PORT);
	}
	
	private void emergencyControl() {
		// allows for operator to move the carriage up and down *slowly*
		while (!isEnding()) {
			
		}
	}
	
	private void moveUpALevel() {
		
	}
	
	private void autoClimb() {
		
	}
	
	protected void update() {
	    updateHookStatusIndicators();
	}
	
	private void updateHookStatusIndicators() {
	    SmartDashboard.putBoolean("hook_fixed_right", rightFixed.get());
	    SmartDashboard.putBoolean("hook_fixed_left", leftFixed.get());
	    SmartDashboard.putBoolean("hook_carriage_right", rightCarriage.get());
	    SmartDashboard.putBoolean("hook_carriage_left", leftCarriage.get());
	}

	/**
	 * Neutralize all hardware states here. (i.e. stop motor, reset solenoids, etc.)
	 * @author Dan
	 */
	protected void onEnd() {
		motor.set(0.0);
//		servo.set(SERVO_LOCK); May break something if hooks are folded in (passing a bar)
	}
}
