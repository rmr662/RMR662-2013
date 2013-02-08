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
	
	// TODO: fill out moveUpALevel
	
	// abort button
	public static final int ABORT_BUTTON = 1;
	// operator button
	public static final int OPERATOR_BUTTON = 2;
	// auto button
	public static final int AUTO_BUTTON = 3;
	
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
	private boolean startAuto = true;
	// for largest case statement (in update() )
	private int mode;
	// for the automode cases
	private int autoMode;
	// for the case statements involved in moving up during automode
	private int moveUpALevelMode;
	
	public Climber() {
		// initialize member variables
		final HardwarePool pool = HardwarePool.getInstance();
		motor = pool.getJaguar(MOTOR_PORT, 1.0);
		servo = pool.getServo(SERVO_PORT);
		
		topLimit = pool.getDigitalInput(SENSOR_LIMIT_TOP, false);
		bottomLimit = pool.getDigitalInput(SENSOR_LIMIT_BOTTOM, false);
		leftFixed = pool.getDigitalInput(SENSOR_FIXED_LEFT, false);
		rightFixed = pool.getDigitalInput(SENSOR_FIXED_RIGHT, false);
		leftCarriage = pool.getDigitalInput(SENSOR_CARRIAGE_LEFT, false);
		rightCarriage = pool.getDigitalInput(SENSOR_CARRIAGE_RIGHT, false);
		
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
	
	private void setAuto(boolean auto) {
		startAuto = auto;
		SmartDashboard.putBoolean("climb_mode", auto);
	}
	
	// return button press information, accounts for possible inversion
	private boolean startAutoButtonPressed() {
		return joystick.getRawButton(AUTO_BUTTON);
	}
	
	// return button press infoprmation to start operator mode, accounts for inversion
	private boolean startOperatorButtonPressed() {
		return joystick.getRawButton(OPERATOR_BUTTON);
	}
	
	//returns true if abort button is pressed
	private boolean abortPressed() {
		return joystick.getRawButton(ABORT_BUTTON);
	}
	
	// return the joystick value
	private double getJoystickAxis() {
		return joystick.getRawAxis(3);
	}
	
	// when this method is done with its stuff it should increment autoMode
	private void moveUpALevel() {
		// moveUpAlevel can be used two times to move up the first two levels.
	}
	
	private void auto() {
		switch (autoMode) {
			// move up first level, resulting in top stationary on the second bar
			case 0:
				moveUpALevel();
				break;
			// allow it to move up a level again	
			case 1:
				moveUpALevelMode = 0;
				autoMode++;
				break;
			// move up second level, resulting in top stationary on the third bar 
			case 2:
				moveUpALevel();
				break;
			// make it not touch the second bar
			case 3:
				break;
			// if something unexpected happens, abort
			default:
				end();
				break;
		}
	}
	private void operator() {
		// need to add things for auto top servo locking
		// give joystick control *slowly*
		motor.set(getJoystickAxis() * .5);
	}
	
	protected void update() {
		sendHookStatus();
		switch (mode) {
			
			case 0: // pre-climb
				if (startAutoButtonPressed()) {
					// make it so that it will continue to auto later
					setAuto(true);
				}
				if (startOperatorButtonPressed()) {
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
			
			case 1: // auto
				// abort hasn't been pressed
				if (startOperatorButtonPressed()) {
					// change to operator
					mode = 2;
				}
				if (! abortPressed()) {
					auto();
				}
				else {
					// go to default case, which aborts
					motor.set(0.0);
					mode = 3;
				}
				break;
			
			case 2: // operator
				// abort hasn't been pressed
				if (! abortPressed()) {
					operator();
				}
				else {
					// go to default case
					motor.set(0.0);
					mode = 3;
				}
				break;
			
			default: // abort
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
