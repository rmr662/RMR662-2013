package com.frc2013.rmr662.climber;
import com.frc2013.rmr662.main.TeleopMode;
import com.frc2013.rmr662.system.generic.Component;
import com.frc2013.rmr662.wrappers.RMRDigitalInput;
import com.frc2013.rmr662.wrappers.RMRJaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber extends Component {
	// Constants
	
	// different modes in auto()
	public static final int MOVE_UP_FIRST_BAR = 0;
	public static final int MOVE_UP_SECOND_BAR = 1;
	public static final int HANG_ON_THIRD_BAR = 2;
	public static final int END_OF_AUTO = 3;
	
	// we should be concerned if only one, and not both, of a set of hooks is still gripping a bar after this amount of time has passed
	public static final int TIME_AT_WHICH_WE_SHOULD_BE_CONCERNED = 100;
	
	public static final double CARRIAGE_SPEED_FAST = 0.5;
	public static final double CARRIAGE_SPEED_SLOW = 0.25;
	
	// different modes in update()
	public static final int PRE_CLIMB = 0;
	public static final int AUTO_MODE = 1;
	public static final int OPERATOR_MODE = 2;
	
	// ports for the different hardware stuff -- get these numbers from wiring 
	public static final int MOTOR_PORT = 3;
	public static final int PISTON_PORT = 1;
	public static final int SERVO_PORT = 0;
	
	// will be -1 if motors are inverted -- positive should move carriage up, negative should move carriage down
	public static final int MOTOR_DIRECTION_MULT = 1;
	
	// double values for the servo to unlock and lock the top carriage hooks
	public static final double SERVO_UNLOCK = 0.0;
	public static final double SERVO_LOCK = 1.0;
	
	// different sensor ports -- get these numbers from wiring
	public static final int SENSOR_LIMIT_BOTTOM = 0;
	public static final int SENSOR_LIMIT_TOP = 1;
	// not ored with others, since the bottom has no sensors
	public static final int SENSOR_FIXED_LEFT = 2;
	public static final int SENSOR_FIXED_RIGHT = 3;
	// ored together
	public static final int SENSOR_CARRIAGE_LEFT = 4;
	public static final int SENSOR_CARRIAGE_RIGHT = 5;
	// add to the automode case 3 for the final movements
	
	// abort button
	public static final int ABORT_BUTTON = 1;
	// operator button
	public static final int OPERATOR_BUTTON = 2;
	// auto button
	public static final int AUTO_BUTTON = 3;
	// servo button
	public static final int SERVO_BUTTON = 4;
	// stop servo button
	public static final int NOT_SERVO_BUTTON = 5;
	// Fields
	private final Joystick joystick;
	private final RMRJaguar motor;
	private final Servo servo;
	
	// sensors
	private final RMRDigitalInput topLimit;
	private final RMRDigitalInput bottomLimit;
	private final RMRDigitalInput leftFixed;
	private final RMRDigitalInput rightFixed;
	private final RMRDigitalInput leftCarriage;
	private final RMRDigitalInput rightCarriage;
	
	// if true, choose auto mode, if false, choose operator
	private boolean startAuto = true; // assuming we default want to do auto
	// for largest case statement (in update() )
	private int mode;
	// for the automode cases
	private int autoMode;
	// for the case statements involved in moving up during automode
	private int moveUpALevelMode;
	
	// used to hold the system time for checking something after a certain time passed
	long t0 = 0;
	
	public Climber(/*PneumaticsManipulator for wing*/) {
		// initialize member variables
		
		motor = new RMRJaguar(MOTOR_PORT, 1.0); // Carriage motor
		servo = new Servo(SERVO_PORT); // Top carriage hook locking servo
		
		// Limit switches
		topLimit = new RMRDigitalInput(SENSOR_LIMIT_TOP, false);
		bottomLimit = new RMRDigitalInput(SENSOR_LIMIT_BOTTOM, false);
		
		// Vertical pairs of fixed hooks (ORed together)
		leftFixed = new RMRDigitalInput(SENSOR_FIXED_LEFT, false);
		rightFixed = new RMRDigitalInput(SENSOR_FIXED_RIGHT, false);
		
		// Top carriage hooks (bottom have no useful input)
		leftCarriage = new RMRDigitalInput(SENSOR_CARRIAGE_LEFT, false);
		rightCarriage = new RMRDigitalInput(SENSOR_CARRIAGE_RIGHT, false);
		
		// Xbox controller (joystick)
		joystick = new Joystick(TeleopMode.XBOX_JOYSTICK_PORT);
		
		mode = 0;
		autoMode = 0;
		moveUpALevelMode = 0;
	}
	
	// Sends hook states to SmartDashboard
	private void sendHookStatus() {
	    SmartDashboard.putBoolean("hook_fixed_right", rightFixed.get());
	    SmartDashboard.putBoolean("hook_fixed_left", leftFixed.get());
	    SmartDashboard.putBoolean("hook_carriage_right", rightCarriage.get());
	    SmartDashboard.putBoolean("hook_carriage_left", leftCarriage.get());
	}
	
	// Sets the climbing control mode (auto or operator control)
	private void setAuto(boolean auto) {
		startAuto = auto;
		SmartDashboard.putBoolean("climb_mode_is_auto", auto);
	}
	
	
	// return button press information, accounts for possible inversion
	private boolean isAutoButtonPressed() {
		return joystick.getRawButton(AUTO_BUTTON);
	}
	
	// return button press infoprmation to start operator mode, accounts for inversion
	private boolean isOperatorButtonPressed() {
		return joystick.getRawButton(OPERATOR_BUTTON);
	}
	
	//returns true if abort button is pressed
	private boolean isAbortPressed() {
		return joystick.getRawButton(ABORT_BUTTON);
	}
	
	// return the joystick value
	private double getJoystickAxis() {
		return joystick.getRawAxis(3); // Constant
	}

//servo locking maually stuff == bad	
	// servo button pressed
//	private boolean servoPressed() {
//		return joystick.getRawButton(SERVO_BUTTON);
//	}
//	// servo stop button pressed
//	private boolean servoNotPressed() {
//		return joystick.getRawButton(NOT_SERVO_BUTTON);
//	}
	
	// Returns true if at least one carriage hook is pressed
	private boolean oneCarriagePressed() {
		// at least one top carriage hook sensor is on the bar
		if (leftCarriage.get() || rightCarriage.get()) { // return <boolean expression>
			return true;
		} else {
			return false;
		}
	}

	private boolean bothCarriagePressed() {
		// both top carriage hook sensors are on the bar
		if (leftCarriage.get() && rightCarriage.get()) { 
			return true;
		} else {
			return false;
		}
	}

	private boolean oneStationaryPressed() {
		// a stationary hook sensor on either left or right is at least on the bar
		if (leftFixed.get() || rightFixed.get()) {
			return true;
		} else {
			return false;
		}
	}

	private boolean bothStationaryPressed() {
		// stationary hook sensor on both left and right are on the bar
		if (leftFixed.get() && rightFixed.get()) {
			return true;
		} else {
			return false;
		}
	}

	// ...
	private void moveUpALevel() {

		switch (moveUpALevelMode) {
			case 0: // moves the carriage all the way up
				motor.set(CARRIAGE_SPEED_FAST);
				if (topLimit.get()) {
					motor.set(0.0);
					servo.set(SERVO_LOCK); // lock the top carriage hooks
					moveUpALevelMode = 1;
				}
				break;
			case 1: // moves the carriage down until a top carriage hook grips to the bar 
				motor.set(-CARRIAGE_SPEED_SLOW);
				if (oneCarriagePressed()) {
					t0 = System.currentTimeMillis(); // get system time
					moveUpALevelMode = 2;
				} else if (bottomLimit.get()) {
					motor.set(0.0);
				}
				break;
			case 2:
				// just in case both middle stationary hooks do not grip to bar after 100ms, 
				// go into operator mode because something is not right
				if (bothCarriagePressed()) {
					moveUpALevelMode = 3;
				} else if (System.currentTimeMillis() - t0 > TIME_AT_WHICH_WE_SHOULD_BE_CONCERNED) {
					motor.set(0.0); 
					mode = OPERATOR_MODE;
				}
				break;
			case 3: // move carriage all the way down, moving robot up
				motor.set(-CARRIAGE_SPEED_FAST);
				if (bottomLimit.get()) {
					motor.set(0.0);
					moveUpALevelMode = 4;
				}
				break;
			case 4: // move carriage up, moving robot down, until a middle stationary hook grips to bar
				motor.set(CARRIAGE_SPEED_SLOW);
				if (oneStationaryPressed()) {
					t0 = System.currentTimeMillis();
					moveUpALevelMode = 5;
				} else if (topLimit.get()) {
					motor.set(0.0);
				}
				break;
			case 5:
				// just in case both middle stationary hooks do not grip to bar after 100ms, 
				// go into operator mode because something is not right
				if (bothStationaryPressed()) {
					moveUpALevelMode = 6;					
				} else if (System.currentTimeMillis() - t0 > TIME_AT_WHICH_WE_SHOULD_BE_CONCERNED) {
					motor.set(0.0);
					mode = OPERATOR_MODE;
				}
				break;
			case 6: // move carriage all the way up
				servo.set(SERVO_UNLOCK); // Move this to case 5 if block
				motor.set(CARRIAGE_SPEED_FAST);
				if (topLimit.get()) {
					motor.set(0.0);
					moveUpALevelMode = 7;
				}
				break;
			case 7: // move carriage down, causing bottom carriage hooks to grip to bar, moving the robot up
				// THERE IS NOTHING TO MAKE SURE THE BOTTOM CARRIAGE HOOKS ARE PRESSED!
				motor.set(-CARRIAGE_SPEED_FAST);
				if (bottomLimit.get()) {
					motor.set(0.0);
					moveUpALevelMode = 8;
				}
				break;
			case 8: // move carriage up, moving robot down, until a top stationary hook grips to bar
				motor.set(CARRIAGE_SPEED_SLOW);
				if (oneStationaryPressed()) {
					t0 = System.currentTimeMillis();
					moveUpALevelMode = 9;
				}
				else if (topLimit.get()) {
					motor.set(0.0);
				}
				break;
			case 9:
				// just in case both middle stationary hooks do not grip to bar after 100ms, 
				// go into operator mode because something is not right
				if (bothStationaryPressed()) {
					// Should advance autoMode!
					moveUpALevelMode = 0;
					autoMode++;
					break;
				} else if (System.currentTimeMillis() - t0 > TIME_AT_WHICH_WE_SHOULD_BE_CONCERNED) {
					motor.set(0.0);
					mode = OPERATOR_MODE;
				}
				break;
		}
	}
	private void hangOnThirdLevel() {
		// intended to hang middle stationary on top bar (90 inch)
		switch (moveUpALevelMode) {
			case 0: // moves the carriage all the way up
				motor.set(CARRIAGE_SPEED_FAST);
				if (topLimit.get()) {
					motor.set(0.0);
					servo.set(SERVO_LOCK); // SERVO_LOCK the top carriage hooks
					moveUpALevelMode = 1;
				}
				break;
			case 1: // moves the carriage down until a top carriage hook grips to the bar 
				motor.set(-CARRIAGE_SPEED_SLOW);
				if (oneCarriagePressed()) {
					t0 = System.currentTimeMillis(); // get system time
					moveUpALevelMode = 2;
				}
				else if (bottomLimit.get()) {
					motor.set(0.0);
				}
				break;
			case 2:
				// just in case both middle stationary hooks do not grip to bar after 100ms, 
				// go into operator mode because something is not right
				if (bothCarriagePressed()) {
					moveUpALevelMode = 3;
				} else if (System.currentTimeMillis() - t0 > TIME_AT_WHICH_WE_SHOULD_BE_CONCERNED) {
					motor.set(0.0);
					mode = OPERATOR_MODE;
				}
				break;
			case 3: // move carriage all the way down, moving robot up
				motor.set(-CARRIAGE_SPEED_FAST);
				if (bottomLimit.get()) {
					motor.set(0.0);
					moveUpALevelMode = 4;
				}
				break;
			case 4: // move carriage up, moving robot down, until a middle stationary hook grips to bar
				motor.set(CARRIAGE_SPEED_SLOW);
				if (oneStationaryPressed()) {
					t0 = System.currentTimeMillis();
					moveUpALevelMode = 5;
				}
				else if (topLimit.get()) {
					motor.set(0.0);			
				}
				break;
			case 5:
				// just in case both middle stationary hooks do not grip to bar after 100ms, 
				// go into operator mode because something is not right
				if (bothStationaryPressed()) {
					moveUpALevelMode = 0;
					autoMode++;
					break;
				} else if (System.currentTimeMillis() - t0 > TIME_AT_WHICH_WE_SHOULD_BE_CONCERNED) {
					motor.set(0.0);
					mode = OPERATOR_MODE;
				}
				break;
		}
	}	
	private void auto() {
//		if (isOperatorButtonPressed()) {
//			mode = 2;
//		}
		
		switch (autoMode) {
			// move up first level, resulting in top stationary on the second bar
			case MOVE_UP_FIRST_BAR:
				moveUpALevel();
				break;
			// move up second level, resulting in top stationary on the third bar 
			case MOVE_UP_SECOND_BAR:
				moveUpALevel();
				break;
				
			// make it not touch the second bar
			case HANG_ON_THIRD_BAR:
				hangOnThirdLevel();
				break;
			case END_OF_AUTO:
				// checks for operator button in update
				break;
			// if something unexpected happens, abort
			default:
				end();
				break;
		}
	}
	private void operator() {
		// the point: give joystick control *slowly*
		// TODO: give driver feedback // need Dan for this maybe
		double speed = getJoystickAxis() * CARRIAGE_SPEED_SLOW;
		if (!oneCarriagePressed() && ((speed / Math.abs(speed)) == 1)) {
			// top carriage hook (if for some reason only one is on) or hooks are not on bar and carriage is moving up
			servo.set(SERVO_UNLOCK);
		}
		else {
			// top carriage hooks are on bar or carriage is moving down
			servo.set(SERVO_LOCK);
		}
		// limit switch checking
		if (bottomLimit.get() || topLimit.get()) {
			motor.set(0.0);
		}
		motor.set(speed);

	}
	
	protected void update() {
		sendHookStatus(); // Sends hook status to dashboard
		// Only update status when changed.
		
		switch (mode) {
			case PRE_CLIMB: // before the climb starts
				if (isAutoButtonPressed()) {
					// make it so that it will continue to auto later
					setAuto(true);
				}
				if (isOperatorButtonPressed()) {
					// make it so that it will continue to operator later
					setAuto(false);
				}
				
				// once the top stationary latches in change the mode to auto or operator
				if (leftFixed.get() && rightFixed.get()) {
					// auto
					if (startAuto) {
						mode = 1;
					}
					// operator
					else {
						mode = 2;
					}
				}
				break;
			
			case AUTO_MODE: // automatic climb
				// abort hasn't been pressed
				if (isOperatorButtonPressed()) {
					// change to operator
					mode = 2;
					break;
				}
				if (! isAbortPressed()) {
					auto();
				} else {
					// go to default case, which aborts
					motor.set(0.0);
					mode = 3;
					break;
				}
				break;
			
			case OPERATOR_MODE: // manual climb
				// abort hasn't been pressed
				if (! isAbortPressed()) {
					operator();
				}
				else {
					// go to default case
					motor.set(0.0);
					mode = 3;
					break;
				}
				break;
			
			case 3: // abort
				motor.set(0.0);
				end();
				break;
			default:
				end();
				break;
		}
	}
		
	/**
	 * Neutralize all dangerous hardware states here.
	 * @author Dan
	 */
	public void onEnd() {
		motor.set(0.0);
	}
}
