package com.frc2013.rmr662.climber;

import com.frc2013.rmr662.main.TeleopMode;
import com.frc2013.rmr662.system.generic.Component;
import com.frc2013.rmr662.wrappers.RMRDigitalInput;
import com.frc2013.rmr662.wrappers.RMRSolenoid;

import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;

// TODO: set constants and fill in returnAxisVal()
public class Climber extends Component {
	
	private static class EmergencyException extends Exception {
		private static final long serialVersionUID = -7491961259340232519L;
		// ^ This is just here to make Eclipse happy.
	}
	
	// Constants
	public static final int MOTOR_DIRECTION_MULT = 1;
	public static final int PISTON_PORT = 1;
	public static final boolean INVERTED_PISTON = true;
	
	public static final boolean INVERTED_0 = false;
	public static final boolean INVERTED_1 = false;
	public static final boolean INVERTED_2 = false;
	public static final boolean INVERTED_3 = false;
	public static final boolean INVERTED_4 = false;
	public static final boolean INVERTED_5 = false;
	
	public static final int SENSOR0 = 1;
	public static final int SENSOR1 = 2;
	public static final int SENSOR2 = 3;
	public static final int SENSOR3 = 4;
	public static final int SENSOR4 = 5;
	public static final int SENSOR5 = 6;
	
	public static final int MOTOR_PORT = 3;
	public static final int JOYSTICK_BUTTON_INDEX_START_AUTO = 1;
	public static final int JOYSTICK_BUTTON_INDEX_START_MAN = 2;
	public static final int JOYSTICK_BUTTON_DOWN_AND_UP = 3;
	public static final int JOYSTICK_PORT = TeleopMode.XBOX_JOYSTICK_PORT;
	
	// Fields
	private RMRSolenoid piston;
	private Jaguar motor;
	private RMRDigitalInput[] sensors = new RMRDigitalInput[6];
	private Joystick joystick;
	/**
	 * true if the tilt pneumatics have been fired.
	 */
	private boolean isFired;
	
	// sensors[0] is bottom limit
	// sensors[1] is top limit
	// sensors[2] is stationary middle hook
	// sensors[3] is stationary top hook
	// sensors[4] is bottom carriage hook
	// sensors[5] is top carriage hook
	public Climber() {
		// initialize member variables
		piston = new RMRSolenoid(PISTON_PORT, INVERTED_PISTON);
		sensors[0] = new RMRDigitalInput(SENSOR0, INVERTED_0);
		sensors[1] = new RMRDigitalInput(SENSOR1, INVERTED_1);
		sensors[2] = new RMRDigitalInput(SENSOR2, INVERTED_2);
		sensors[3] = new RMRDigitalInput(SENSOR3, INVERTED_3);
		sensors[4] = new RMRDigitalInput(SENSOR4, INVERTED_4);
		sensors[5] = new RMRDigitalInput(SENSOR5, INVERTED_5);
		motor = new Jaguar(MOTOR_PORT);
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
	
	private boolean sensor(int number) {
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
	
	private boolean notOutOfBounds() { // TODO: remove this, optimize for direction of travel
		return (!sensor(0) && !sensor(1));
	}
	
	// whatever we decide to use for emergency control (triggers or stick things
	// or d pad) return value from -1 to 1
	private double returnAxisVal() {
		return 0.0; // TODO: return something relevant to emergency control
	}
	
	private void emergencyControl() {
		// allows for operator to move the carriage up and down *slowly*
		while (!isEnding()) {
			final double speed = returnAxisVal() * .25 * MOTOR_DIRECTION_MULT;
			if (speed == 0 || (speed > 0 && !sensor(1))
					|| (speed < 0 && !sensor(0))) {
				motor.set(speed);
			}
		}
	}
	
	private void moveUpALevel() throws EmergencyException {
		// while top stationary is hooked but top carriage is not, move carriage
		// up
		motor.set(0.5 * MOTOR_DIRECTION_MULT);
		while (sensor(3) && !sensor(5) && notOutOfBounds()) {
			checkEmergencyButton();
		}
		motor.set(0); 
		// TODO: is it necessary stop the motors before going the other direction?
		// while middle stationary is not hooked but top carriage is hooked,
		// move carriage down, robot up
		motor.set(-0.5 * MOTOR_DIRECTION_MULT);
		while (!sensor(2) && sensor(5) && notOutOfBounds()) {
			checkEmergencyButton();
		}
		motor.set(0);
		// while middle stationary is hooked but bottom carriage is not, move
		// carriage up
		motor.set(0.5 * MOTOR_DIRECTION_MULT);
		while (sensor(2) && !sensor(4) && notOutOfBounds()) {
			checkEmergencyButton();
		}
		motor.set(0);
		// while top stationary is not hooked but top carriage is hooked, move
		// carriage down, robot up
		motor.set(-0.5 * MOTOR_DIRECTION_MULT);
		while (!sensor(3) && sensor(4) && notOutOfBounds()) {
			checkEmergencyButton();
		}
		motor.set(0);
	}
	
	private void autoClimb() throws EmergencyException {
		// tilt robot into position
		piston.set(true);
		// wait until the top hook is locked in
		while (!sensor(3)) {
			// (do nothing)
		}
		piston.set(false); // TODO: is it necessary to retract piston?
		moveUpALevel();
		// TODO: something here?
		moveUpALevel();
		
		// code to prevent robot touching second rung
		// while top stationary is hooked but top carriage is not, move carriage
		// up
		motor.set(0.5 * MOTOR_DIRECTION_MULT);
		while (sensor(3) && !sensor(5) && notOutOfBounds()) {
			checkEmergencyButton();
		}
		motor.set(0);
		// while middle stationary is not hooked but top carriage is hooked,
		// move carriage down, robot up
		motor.set(-0.5 * MOTOR_DIRECTION_MULT);
		while (!sensor(2) && sensor(5) && notOutOfBounds()) {
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
	}
}
