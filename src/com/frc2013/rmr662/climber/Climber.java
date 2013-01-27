package com.frc2013.rmr662.climber;

import com.frc2013.rmr662.system.generic.Component;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;

// for use on old code
// TODO: set constants and fill in returnAxisVal()
public class Climber extends Component {
    // constants

    public static final int MOTOR_DIRECTION_MULT = 1;
    public static final int PISTON_PORT = 1;
    public static final boolean FIRED_IS1 = true;
    public static final boolean[] INVERTEDS = {false, false, false, false, false, false};
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
    public static final int JOYSTICK_PORT = 3;
    // member declarations
    Solenoid piston;
    Jaguar motor;
    DigitalInput[] sensors = new DigitalInput[6];
    Joystick joystick;
    /**
     * true if the tilt pneumatics have been fired.
     */
    boolean isfired;
    boolean isInEmergencyControl;

    // sensors[0] is bottom limit
    // sensors[1] is top limit
    // sensors[2] is stationary middle hook
    // sensors[3] is stationary top hook
    // sensors[4] is bottom carriage hook
    // sensors[5] is top carriage hook
    public Climber() {
	// initialize member variables
	piston = new Solenoid(PISTON_PORT);
	sensors[0] = new DigitalInput(SENSOR0);
	sensors[1] = new DigitalInput(SENSOR1);
	sensors[2] = new DigitalInput(SENSOR2);
	sensors[3] = new DigitalInput(SENSOR3);
	sensors[4] = new DigitalInput(SENSOR4);
	sensors[5] = new DigitalInput(SENSOR5);
	motor = new Jaguar(MOTOR_PORT);
	joystick = new Joystick(JOYSTICK_PORT);
	isfired = false;
    }

    public void checkEmergencyButton() {
	isInEmergencyControl = joystick.getRawButton(JOYSTICK_BUTTON_INDEX_START_MAN);
    }

    public boolean sensor(int number) {
	return (sensors[number].get() != INVERTEDS[number]);
//	if (number == 0) {
//	    return sensors[2].get() != INVERTEDS[2];
//	} else if (number == 1) {
//	    return sensors[1].get() != INVERTEDS[1] && sensors[2].get() == INVERTEDS[2];
//	} else if (number == 2) {
//	    return sensors[1].get() != INVERTEDS[1] && sensors[2].get() != INVERTEDS[2];
//	} else if (number == 3) {
//	    return sensors[0].get() != INVERTEDS[0] && sensors[1].get() == INVERTEDS[1] && sensors[2].get() == INVERTEDS[2];
//	} else if (number == 4) {
//	    return sensors[0].get() != INVERTEDS[0] && sensors[1].get() == INVERTEDS[1] && sensors[2].get() != INVERTEDS[2];
//	} else {
//	    return sensors[0].get() != INVERTEDS[0] && sensors[1].get() != INVERTEDS[1] && sensors[2].get() == INVERTEDS[2];
//	}
    }

    public boolean notOutOfBounds() {
	return (!sensor(0) && !sensor(1));
    }
    
    // whatever we decide to use (triggers or stick things or d pad) return value from -1 to 1
    public double returnAxisVal() {
	return 0.0;//returnWhateverWeShouldHere
    }

    public void emergencyControl() {
	// allows for operator to move the carriage up and down *slowly*
	while (!isEnding()) {
	    final double speed = returnAxisVal() * .25 * MOTOR_DIRECTION_MULT;
	    if (speed == 0 || (speed > 0 && ! sensor(1)) || (speed < 0 && ! sensor(0))) {
		motor.set(speed);
	    }
	}
    }

    public void moveUpALevel() {
	// while top stationary is hooked but top carriage is not, move carriage up
	motor.set(0.5 * MOTOR_DIRECTION_MULT);
	while (sensor(3) && !sensor(5) && notOutOfBounds()) {
	    checkEmergencyButton();
	    if (isInEmergencyControl) {
		break;
	    }
	}
	motor.set(0);
	if (isInEmergencyControl) {
	    return;
	}
	// while middle stationary is not hooked but top carriage is hooked, move carriage down, robot up
	motor.set(-0.5 * MOTOR_DIRECTION_MULT);
	while (!sensor(2) && sensor(5) && notOutOfBounds()) {
	    checkEmergencyButton();
	    if (isInEmergencyControl) {
		break;
	    }
	}
	motor.set(0);
	if (isInEmergencyControl) {
	    return;
	}
	// while middle stationary is hooked but bottom carriage is not, move carriage up
	motor.set(0.5 * MOTOR_DIRECTION_MULT);
	while (sensor(2) && !sensor(4) && notOutOfBounds()) {
	    checkEmergencyButton();
	    if (isInEmergencyControl) {
		break;
	    }
	}
	motor.set(0);
	if (isInEmergencyControl) {
	    return;
	}
	// while top stationary is not hooked but top carriage is hooked, move carriage down, robot up
	motor.set(-0.5 * MOTOR_DIRECTION_MULT);
	while (!sensor(3) && sensor(4) && notOutOfBounds()) {
	    checkEmergencyButton();
	    if (isInEmergencyControl) {
		break;
	    }
	}
	motor.set(0);
    }

    public void autoClimb() {
	// make it so that this code will not execute again
	isfired = true;
	// tilt robot into position
	piston.set(FIRED_IS1);
	// wait until the top hook is locked in
	while (!sensor(3)) {
	    // (do nothing)
	}
	moveUpALevel();
	if (isInEmergencyControl) {
	    return;
	}
	moveUpALevel();
	if (isInEmergencyControl) {
	    return;
	}
	// code to prevent robot touching second rung
	// while top stationary is hooked but top carriage is not, move carriage up
	motor.set(0.5 * MOTOR_DIRECTION_MULT);
	while (sensor(3) && !sensor(5) && notOutOfBounds()) {
	    checkEmergencyButton();
	    if (isInEmergencyControl) {
		break;
	    }
	}
	motor.set(0);
	if (isInEmergencyControl) {
	    return;
	}
	// while middle stationary is not hooked but top carriage is hooked, move carriage down, robot up
	motor.set(-0.5 * MOTOR_DIRECTION_MULT);
	while (!sensor(2) && sensor(5) && notOutOfBounds()) {
	    checkEmergencyButton();
	    if (isInEmergencyControl) {
		break;
	    }
	}
	motor.set(0);
    }

    public void update() {
	if (!isfired && joystick.getRawButton(JOYSTICK_BUTTON_INDEX_START_AUTO)) {
	    autoClimb();
	    if (isInEmergencyControl) {
		emergencyControl();
	    }
	    //ends thread
	    end();
	}
    }
}
