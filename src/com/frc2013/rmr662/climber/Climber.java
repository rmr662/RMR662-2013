package com.frc2013.rmr662.climber;
import com.frc2013.rmr662.main.TeleopMode;
import com.frc2013.rmr662.system.HardwarePool;
import com.frc2013.rmr662.system.generic.Component;
import com.frc2013.rmr662.wrappers.RMRDigitalInput;
import com.frc2013.rmr662.wrappers.RMRJaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber extends Component {
	// Constants
	// TODO: constant for time value waiting
	public static final int MOTOR_PORT = 3;
	public static final int MOTOR_DIRECTION_MULT = 1;
	public static final int PISTON_PORT = 1;
	public static final int SERVO_PORT = 0;
	
	public static final double SERVO_UNLOCK = 0.0;
	public static final double SERVO_LOCK = 1.0;
	
	public static final int SENSOR_LIMIT_BOTTOM = 0;
	public static final int SENSOR_LIMIT_TOP = 1;
	// not ored with others, since the bottom has no sensors
	public static final int SENSOR_FIXED_LEFT = 2;
	public static final int SENSOR_FIXED_RIGHT = 3;
	// ored together
	public static final int SENSOR_CARRIAGE_LEFT = 4;
	public static final int SENSOR_CARRIAGE_RIGHT = 5;
	
	// TODO: fill out moveUpALevel
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
	private boolean startAuto = true; // TODO: change to false?
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
		final HardwarePool pool = HardwarePool.getInstance();

		motor = pool.getJaguar(MOTOR_PORT, 1.0); // Carriage motor
		servo = pool.getServo(SERVO_PORT); // Top carriage hook locking servo
		
		// Limit switches
		topLimit = pool.getDigitalInput(SENSOR_LIMIT_TOP, false);
		bottomLimit = pool.getDigitalInput(SENSOR_LIMIT_BOTTOM, false);
		
		// Vertical pairs of fixed hooks (ORed together)
		leftFixed = pool.getDigitalInput(SENSOR_FIXED_LEFT, false);
		rightFixed = pool.getDigitalInput(SENSOR_FIXED_RIGHT, false);
		
		// Top carriage hooks (bottom have no useful input)
		leftCarriage = pool.getDigitalInput(SENSOR_CARRIAGE_LEFT, false);
		rightCarriage = pool.getDigitalInput(SENSOR_CARRIAGE_RIGHT, false);
		
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
	
	// servo button pressed
	private boolean servoPressed() {
		return joystick.getRawButton(SERVO_BUTTON);
	}
	// servo stop button pressed
	private boolean servoNotPressed() {
		return joystick.getRawButton(NOT_SERVO_BUTTON);
	}
	
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
		// 0 is ok the real value is in an if statement that will always execute

		switch (moveUpALevelMode) {
			// TODO increment automode at the final case
			case 0: // moves the carriage all the way up
				motor.set(0.5); // TODO: make constant
				if (topLimit.get()) {
					motor.set(0.0);
					servo.set(SERVO_LOCK); // lock the top carriage hooks
					moveUpALevelMode = 1;
				}
				break;
			case 1: // moves the carriage down until a top carriage hook grips to the bar 
				motor.set(-0.25);
				if (oneCarriagePressed()) {
					t0 = System.currentTimeMillis(); // get system time
					moveUpALevelMode = 2;
				} // TODO: else if bottom limit ... for similar cases
				break;
			case 2:
				// just in case both hooks do not grip to bar after 100 ms, move carriage back up and retry
				if (bothCarriagePressed()) {
					moveUpALevelMode = 3;
				} else if (System.currentTimeMillis() - t0 > 100) {
					motor.set(0.0); // TODO: go to operator control? (for all similar cases: 2, 5, 9)
					moveUpALevelMode = 0;
				}
				break;
			case 3: // move carriage all the way down, moving robot up
				motor.set(-0.5);
				if (bottomLimit.get()) {
					motor.set(0.0);
					moveUpALevelMode = 4;
				}
				break;
			case 4: // move carriage up, moving robot down, until a middle stationary hook grips to bar
				motor.set(0.25);
				if (oneStationaryPressed()) {
					t0 = System.currentTimeMillis();
					moveUpALevelMode = 5;
				}
				break;
			case 5:
				// just in case both middle stationary hooks do not grip to bar after 100ms, move carriage back down and retry
				if (bothStationaryPressed()) {
					moveUpALevelMode = 6;					
				} else if (System.currentTimeMillis() - t0 > 100) {
					motor.set(0.0);
					moveUpALevelMode = 3;
				}
				break;
			case 6: // move carriage all the way up
				servo.set(SERVO_UNLOCK); // Move this to case 5 if block
				motor.set(0.5);
				if (topLimit.get()) {
					motor.set(0.0);
					moveUpALevelMode = 7;
				}
				break;
			case 7: // move carriage down, causing bottom carriage hooks to grip to bar, moving the robot up
				// THERE IS NOTHING TO MAKE SURE THE BOTTOM CARRIAGE HOOKS ARE PRESSED!
				motor.set(-0.5);
				if (bottomLimit.get()) {
					motor.set(0.0);
					moveUpALevelMode = 8;
				}
				break;
			case 8: // move carriage up, moving robot down, until a top stationary hook grips to bar
				motor.set(0.25);
				if (oneStationaryPressed()) {
					t0 = System.currentTimeMillis();
					moveUpALevelMode = 9;
				}
				break;
			case 9:
				// just in case both top stationary hooks do not grip to bar after 100ms, move carriage back down and retry
				if (bothStationaryPressed()) {
					// Should advance autoMode!
					break;
				} else if (System.currentTimeMillis() - t0 > 100) {
					motor.set(0.0);
					moveUpALevelMode = 7;
				}
				break;
		}
	}
	private void hangOnThirdLevel() {
		// 0 is ok, because the if statement that the real initializer is in will be evaluated no matter what
		// intended to hang middle stationary on top bar (90 inch)
		//TODO: same problem with operator mode (the timer stuff)
		switch (moveUpALevelMode) {
			case 0: // moves the carriage all the way up
				motor.set(0.5);
				if (topLimit.get()) {
					motor.set(0.0);
					servo.set(SERVO_LOCK); // SERVO_LOCK the top carriage hooks
					moveUpALevelMode = 1;
				}
				break;
			case 1: // moves the carriage down until a top carriage hook grips to the bar 
				motor.set(-0.25);
				if (oneCarriagePressed()) {
					t0 = System.currentTimeMillis(); // get system time
					moveUpALevelMode = 2;
				}
				break;
			case 2:
				// just in case both hooks do not grip to bar after 100 ms, move carriage back up and retry
				if (bothCarriagePressed()) {
					moveUpALevelMode = 3;
				} else if (System.currentTimeMillis() - t0 > 100) {
					motor.set(0.0);
					moveUpALevelMode = 0;
				}
				break;
			case 3: // move carriage all the way down, moving robot up
				motor.set(-0.5);
				if (bottomLimit.get()) {
					motor.set(0.0);
					moveUpALevelMode = 4;
				}
				break;
			case 4: // move carriage up, moving robot down, until a middle stationary hook grips to bar
				motor.set(0.25);
				if (oneStationaryPressed()) {
					t0 = System.currentTimeMillis();
					moveUpALevelMode = 5;
				}
				break;
			case 5:
				// just in case both middle stationary hooks do not grip to bar after 100ms, move carriage back down and retry
				if (bothStationaryPressed()) {
					break;
				} else if (System.currentTimeMillis() - t0 > 100) {
					motor.set(0.0);
					moveUpALevelMode = 3;
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
			case 0:
				moveUpALevel();
				break;
				
			// allow it to move up a level again	
				// combine 1 and 2
			case 1:
				// put this in move up a level
				moveUpALevelMode = 0;
				autoMode++;
				break;
				
			// move up second level, resulting in top stationary on the third bar 
			case 2:
				moveUpALevel();
				// needs to add one to auto mode, works with chnage from previous case
				break;
				
			// make it not touch the second bar
			case 3:
				// increment automode in hangonthirdlevel
				// add case 4 for only checking operator button
				// dont add operator checking, already done in update method
				hangOnThirdLevel();
				break;
			// if something unexpected happens, abort
			default:
				end();
				break;
		}
	}
	private void operator() {
		// TODO: need to add things for auto top servo locking
		// give joystick control *slowly*
		//make sure that it is impossible to have a locked servo claw move up
		// add limit switch checking
		// give driver feedback
		motor.set(getJoystickAxis() * .5);
		if (servoPressed()) {
			servo.set(SERVO_LOCK);
		}
		if (servoNotPressed()) {
			servo.set(SERVO_UNLOCK);
		}
	}
	
	protected void update() {
		sendHookStatus(); // Sends hook status to dashboard
		// Only update status when changed.
		
		switch (mode) {
			case 0: // pre-climb // Should be constant
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
			
			case 1: // auto // Should be constant
				// abort hasn't been pressed
				if (isOperatorButtonPressed()) {
					// change to operator
					mode = 2;
					//break; add this here
				}
				if (! isAbortPressed()) {
					auto();
				} else {
					// go to default case, which aborts
					motor.set(0.0);
					mode = 3;
				}
				break;
			
			case 2: // operator // Should be constants
				// abort hasn't been pressed
				if (! isAbortPressed()) {
					operator();
				}
				else {
					// go to default case
					motor.set(0.0);
					mode = 3;
				}
				break;
			
			case 3: // abort
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
