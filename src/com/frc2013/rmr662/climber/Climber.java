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
	public static final int PISTON_PORT = 1;
	public static final boolean INVERTED_PISTON = false; // if hardware is inverted

// port numbers
// need actual digital ports from wiring	
	public static final int SENSOR0 = 1;
	public static final int SENSOR1 = 2;
	public static final int SENSOR2 = 3;
	public static final int SENSOR3 = 4;
	public static final int SENSOR4 = 5;
	public static final int SENSOR5 = 6;
	public static final int MOTOR_PORT = 3;
// carriage motor 
// change to negative if inverted	
	public static final int MOTOR_DIRECTION_MULT = 1;
// figure out buttons	
	public static final int JOYSTICK_BUTTON_INDEX_START_AUTO = 1;
	public static final int JOYSTICK_BUTTON_INDEX_START_MAN = 2;
	public static final int JOYSTICK_BUTTON_DOWN_AND_UP = 3;
	public static final int JOYSTICK_PORT = TeleopMode.XBOX_JOYSTICK_PORT;
// ports for the servos
// get from wiring
	public static final int SERVO0 = 1;
	public static final int SERVO1 = 2;
	public static final int SERVO2 = 3;
	public static final int SERVO3 = 4;
// figure out what values stop the hook, and what values allow the hook to move	
	public static final double SERVO_FALSE = 0.0;
	public static final double SERVO_TRUE = 1.0;
	
	// Fields
	private RMRSolenoid piston;
	private Jaguar motor;
	private RMRDigitalInput[] sensors = new RMRDigitalInput[6];
	private Joystick joystick;
	private Servo[] servos = new Servo[4];
	// code that starts climb is in the update() method
	// update method is called many times
	// 
	private boolean isFired;
	
	public static final int BOTTOM_LIMIT = 0;
	public static final int TOP_LIMIT = 1;
	public static final int MIDDLE_STATIONARY = 2;
	public static final int TOP_STATIONARY = 3;
	public static final int BOTTOM_CARRIAGE = 4;
	public static final int TOP_CARRIAGE = 5;
	
	// sensors[0] is bottom limit
	// sensors[1] is top limit
	// sensors[2] is stationary middle hook
	// sensors[3] is stationary top hook
	// sensors[4] is bottom carriage hook
	// sensors[5] is top carriage hook
	public Climber() {
		// initialize member variables

		piston = HardwarePool.getInstance().getSolenoid(PISTON_PORT, INVERTED_PISTON);
		sensors[0] = HardwarePool.getInstance().getDigitalInput(SENSOR0, false);
		sensors[1] = HardwarePool.getInstance().getDigitalInput(SENSOR1, false);
		sensors[2] = HardwarePool.getInstance().getDigitalInput(SENSOR2, false);
		sensors[3] = HardwarePool.getInstance().getDigitalInput(SENSOR3, false);
		sensors[4] = HardwarePool.getInstance().getDigitalInput(SENSOR4, false);
		sensors[5] = HardwarePool.getInstance().getDigitalInput(SENSOR5, false);
		motor = HardwarePool.getInstance().getJaguar(MOTOR_PORT);

		servos[0] = new Servo(SERVO0);
		servos[1] = new Servo(SERVO1);
		servos[2] = new Servo(SERVO2);
		servos[3] = new Servo(SERVO3);
		// servos[0] is the servo for the stationary middle hook
		// servos[1] is the servo for the stationary top hook
		// servos[2] is the servo for the carriage bottom hook
		// servos[3] is the servo for the carriage top hook
		
		joystick = new Joystick(JOYSTICK_PORT);
		isFired = false;
	}
	
	/**
	 * If the emergency button is pressed, stops the motor and throws an
	 * EmergencyException.
	 * 
	 * @throws EmergencyException
	 */
	private void checkEmergencyButton() throws EmergencyException {
		if (joystick.getRawButton(JOYSTICK_BUTTON_INDEX_START_MAN)) {
			motor.set(0.0);
			throw new EmergencyException();
		}
	}
	// returns 
	private boolean getSensor(int number) {
		return sensors[number].get();
		// THIS CODE IS DEPRECATED!
		// if (number == 0) {
		// return sensors[2].get() != INVERTEDS[2];
		// } else if (number == 1) {
		// return sensors[1].get() != INVERTEDS[1] && sensors[2].get() ==
		// INVERTEDS[2];
		// } else if (number == 2) {
		// return sensors[1].get() != INVERTEDS[1] && sensors[2].get() !=
		// INVERTEDS[2];
		// } else if (number == 3) {
		// return sensors[0].get() != INVERTEDS[0] && sensors[1].get() ==
		// INVERTEDS[1] && sensors[2].get() == INVERTEDS[2];
		// } else if (number == 4) {
		// return sensors[0].get() != INVERTEDS[0] && sensors[1].get() ==
		// INVERTEDS[1] && sensors[2].get() != INVERTEDS[2];
		// } else {
		// return sensors[0].get() != INVERTEDS[0] && sensors[1].get() !=
		// INVERTEDS[1] && sensors[2].get() == INVERTEDS[2];
		// }
	}
	// check 
	private boolean notOutOfBounds() { // TODO: remove this, optimize for direction of travel
		return (!getSensor(BOTTOM_LIMIT) && !getSensor(TOP_LIMIT));
	}
	
	// whatever we decide to use for emergency control (triggers or stick things
	// or d pad) return value from -1 to 1
	private double returnAxisVal() {
		return 0.0; // TODO: return something relevant to emergency control
	}
	
	private void emergencyControl() {
		// allows for operator to move the carriage up and down *slowly*
		boolean servoOn0 = false;
		boolean servoOn1 = false;
		boolean servoOn2 = false;
		boolean servoOn3 = false;
		while (!isEnding()) {
			final double speed = returnAxisVal() * .25 * MOTOR_DIRECTION_MULT;
//			if ((speed == 0 && !sensor(0) && !sensor(1)) || (speed > 0 && !sensor(1))
//					|| (speed < 0 && !sensor(0))) {
//				motor.set(speed);
//			}
			// do not allow the carriage to go past limit switches

			if ((speed > 0 && !getSensor(TOP_LIMIT))
					|| (speed < 0 && !getSensor(BOTTOM_LIMIT))) {
				motor.set(speed);
			}
			// use servos to lock in the hook
			if (getSensor(MIDDLE_STATIONARY)) {
				servos[0].set(SERVO_TRUE);
				servoOn0 = true;		
			}
			else if (servoOn0) {
				servos[0].set(SERVO_FALSE);
				servoOn0 = false;
			}
			
			if (getSensor(TOP_STATIONARY)) {
				servos[1].set(SERVO_TRUE);
				servoOn1 = true;
			}
			
			else if (servoOn1) {
				servos[1].set(SERVO_FALSE);
				servoOn1 = false;
			}
			
			if (getSensor(BOTTOM_CARRIAGE)) {
				servos[2].set(SERVO_TRUE);
				servoOn2 = true;
			}
			else if (servoOn2) {
				servos[2].set(SERVO_FALSE);
				servoOn2 = false;
			}
			
			if (getSensor(TOP_CARRIAGE)) {
				servos[3].set(SERVO_TRUE);
				servoOn3 = true;
			}
			else if (servoOn3) {
				servos[3].set(SERVO_FALSE);
				servoOn3 = false;
			}
		}
	}
	
	private void moveUpALevel() throws EmergencyException {
		// moveUpAlevel can be used two times to move up the first two levels.
		// while top stationary is hooked but top carriage is not, move carriage
		// up
		servos[1].set(SERVO_TRUE); // lock in the top stationary hooks
		motor.set(0.5 * MOTOR_DIRECTION_MULT);
		while (getSensor(TOP_STATIONARY) && !getSensor(TOP_CARRIAGE) && notOutOfBounds()) {
			checkEmergencyButton();
		}
		servos[3].set(SERVO_TRUE); // lock in the top carriage hooks
		servos[1].set(SERVO_FALSE); // free the top stationary hooks
		motor.set(0); 
		// TODO: is it necessary stop the motors before going the other direction?
		// while middle stationary is not hooked but top carriage is hooked,
		// move carriage down, robot up
		motor.set(-0.5 * MOTOR_DIRECTION_MULT);
		
		while (!getSensor(MIDDLE_STATIONARY) && getSensor(TOP_CARRIAGE) && notOutOfBounds()) {
			checkEmergencyButton(); //TODO: sleep about 50 ms in every one of these
//			Thread.sleep(50);
		}
		servos[0].set(SERVO_TRUE); // lock in the bottom stationary hooks
		servos[3].set(SERVO_FALSE); // free the top carriage hooks
		motor.set(0);
		// while middle stationary is hooked but bottom carriage is not, move
		// carriage up
		motor.set(0.5 * MOTOR_DIRECTION_MULT);
		while (getSensor(MIDDLE_STATIONARY) && !getSensor(BOTTOM_CARRIAGE) && notOutOfBounds()) {
			checkEmergencyButton();
		}
		servos[2].set(SERVO_TRUE); // lock in the bottom carriage hooks
		servos[0].set(SERVO_FALSE); // free the bottom stationary hooks
		motor.set(0);
		// while top stationary is not hooked but bottom carriage is hooked, move
		// carriage down, robot up
		motor.set(-0.5 * MOTOR_DIRECTION_MULT);
		while (!getSensor(TOP_STATIONARY) && getSensor(BOTTOM_CARRIAGE) && notOutOfBounds()) {
			checkEmergencyButton();
		}
		servos[1].set(SERVO_TRUE); // lock in the top stationary hooks
		servos[2].set(SERVO_FALSE); // free the bottom carriage hooks
		motor.set(0);
	}
	
	private void autoClimb() throws EmergencyException {
		// tilt robot into position
		piston.set(true);
		// wait until the top hook is locked in
		while (!getSensor(TOP_STATIONARY)) {
			// (do nothing)
		}
		//piston.set(false); // TODO: is it necessary to retract piston?
		moveUpALevel();
		// TODO: something here?
		moveUpALevel();
		
		// code to prevent robot touching second rung
		// while top stationary is hooked but top carriage is not, move carriage
		// up
		motor.set(0.5 * MOTOR_DIRECTION_MULT);
		while (getSensor(TOP_STATIONARY) && !getSensor(TOP_CARRIAGE) && notOutOfBounds()) {
			checkEmergencyButton();
		}
		motor.set(0);
		// while middle stationary is not hooked but top carriage is hooked,
		// move carriage down, robot up
		motor.set(-0.5 * MOTOR_DIRECTION_MULT);
		while (!getSensor(MIDDLE_STATIONARY) && getSensor(TOP_CARRIAGE) && notOutOfBounds()) {
			checkEmergencyButton();
		}
		motor.set(0);
	}
	
	
	protected void update() {
		if (!isFired && joystick.getRawButton(JOYSTICK_BUTTON_INDEX_START_AUTO)) {
			// make it so that this code will not execute again
			isFired = true;
			try {
				autoClimb();
			} catch (EmergencyException e) {
				emergencyControl();
			}
			
			// ends thread
			end();
		}
		else if (!isFired && joystick.getRawButton(JOYSTICK_BUTTON_INDEX_START_MAN)) {
			piston.set(true);
			emergencyControl();
			isFired = true;
		}
		// in case we think of something to do after the robot has started climbing
		//else {
			
		//}
	}
}
