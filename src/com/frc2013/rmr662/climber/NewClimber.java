
package com.frc2013.rmr662.climber;

import com.frc2013.rmr662.main.TeleopMode;
import com.frc2013.rmr662.system.generic.Component;
import com.frc2013.rmr662.wrappers.Button;
import com.frc2013.rmr662.wrappers.RMRDigitalInput;
import com.frc2013.rmr662.wrappers.RMRJaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author jackson
 */
public class NewClimber extends Component {
	private static final int TIME_AT_WHICH_WE_CAN_BE_SATISFIED_THAT_THE_HOOKS_HAVE_STOPPED_BOUNCING = 1000;
	private static final int STOP_COUNT = 6;
	
	// Motor constants
	private final int MOTOR_CHANNEL = 3;
	private final double MOTOR_MULTIPLIER = 0.5;
	private final double MOTOR_UP = 1.0;
	private final double MOTOR_DOWN = 1.0;
	
	// Servo constants
	private static final int SERVO_CHANNEL = 4;
	private static final double SERVO_UNLOCK = 1.0;
	private static final double SERVO_LOCK = 0.0;
	
	// Hook input channels
	private final int LEFT_CHANNEL = 1;
	private final int RIGHT_CHANNEL = 2;
	private final int TOP_CHANNEL = 14;
	private final int BOTTOM_CHANNEL = 12;
	
	// button indices
	private final int MODE_BUTTON = 1;
	private final int ABORT_BUTTON = 2;
	private final int SERVO_BUTTON = 3;
	private final int OPERATOR_CONTROL_AXIS = 3;
	
	// Hardware stuff
	private final RMRDigitalInput left;
	private final RMRDigitalInput right;
	private final RMRDigitalInput top;
	private final RMRDigitalInput bottom;
	private final RMRJaguar motor;
	private final Servo servo;
	
	private final Joystick xbox;
	private final Button modeButton;
	private final Button servoButton;
	
	// Last hook states
	private boolean leftOnLast = false;
	private boolean rightOnLast = false;
	private boolean hooksOnLast = false;
	
	// Motor state
	private boolean motorGoingUp = false;
	
	// Servo state
	private boolean servoLocked = false;
	
	// Code states
	private boolean climbing = false;
	private boolean stopped = false;
	private boolean isInAutoMode = true;
	
	// Hook press counter
	private int counter = 0;
	private long lastHookEngageTime = 0;
	
	public NewClimber() {
		motor = new RMRJaguar(MOTOR_CHANNEL, MOTOR_MULTIPLIER);
		
		left = new RMRDigitalInput(LEFT_CHANNEL, false);
		right = new RMRDigitalInput(RIGHT_CHANNEL, false);
		
		top = new RMRDigitalInput(TOP_CHANNEL, false);
		bottom = new RMRDigitalInput(BOTTOM_CHANNEL, false);
		
		xbox = new Joystick(TeleopMode.XBOX_JOYSTICK_PORT);
		modeButton = new Button(xbox, MODE_BUTTON);
		
		servo = new Servo(SERVO_CHANNEL);
		servoButton = new Button(xbox, SERVO_BUTTON);
	}
	
	// ========================== UTILITY METHODS ========================
	
	// Toggle between auto and manual mode
	private void toggleMode() {
		isInAutoMode = !isInAutoMode;
		SmartDashboard.putBoolean("climb_mode_is_auto", isInAutoMode);
	}
	
	private void toggleServoState() {
		setServoState(!servoLocked);
	}
	
	private void setServoState(boolean locked) {
		if (locked && !servoLocked) {
			SmartDashboard.putBoolean("servo_is_locked", true);
			System.out.println("servo_locked = true");
			servo.set(SERVO_LOCK);
			servoLocked = true;
			
		} else if (!locked && servoLocked) {
			SmartDashboard.putBoolean("servo_is_locked", false);
			System.out.println("servo_locked = true");
			servo.set(SERVO_UNLOCK);
			servoLocked = false;
			
		}
	}
	
	// ========================== MOTOR STUFF =======================
	
	private void updateMotor() {
		if (motorGoingUp && top.get()) { // Motor is at top
			motor.set(MOTOR_DOWN);
			motorGoingUp = false;
			toggleServoState();
			
		} else if (!motorGoingUp && bottom.get()) { // Motor is at bottom
			motor.set(MOTOR_UP);
			motorGoingUp = true;
		}
	}
	
	// ======================== HOOK COUNTER STUFF ================
	
	// Check if both sides are on
	private boolean areBothHooksOn() {
		final boolean leftOn = left.get();
		final boolean rightOn = right.get();
		if (leftOn != leftOnLast) {
			SmartDashboard.putBoolean("left_hook_state", leftOn);
			System.out.println("left = " + leftOn);
			leftOnLast = leftOn;
		}
		if (rightOn != rightOnLast) {
			SmartDashboard.putBoolean("right_hook_state", rightOn);
			System.out.println("right = " + rightOn);
			rightOnLast = rightOn;
		}
		return leftOn && rightOn;
	}
	
	private void updateHookCounter() {
		final boolean hooksOn = areBothHooksOn();
		if (!hooksOnLast && hooksOn) {
			counter++;
			hooksOnLast = true;
			lastHookEngageTime = System.currentTimeMillis();
			
		} else if (hooksOnLast && !hooksOn) {
			if (System.currentTimeMillis() - lastHookEngageTime > TIME_AT_WHICH_WE_CAN_BE_SATISFIED_THAT_THE_HOOKS_HAVE_STOPPED_BOUNCING) {
				hooksOnLast = false;
			}
		}
		if (counter == STOP_COUNT) {
			stopped = true;
			motor.set(0.0);
		}
	}
	
	// =========================== OPERATOR CONTROL ========================
	
	private void operator() {
		if (servoButton.wasPressed()) {
			toggleServoState();
		}
		motor.set(xbox.getRawAxis(OPERATOR_CONTROL_AXIS));		
	}
	
	// ======================== COMPONENT METHODS =====================
	
	protected void update() {
		
		if (xbox.getRawButton(ABORT_BUTTON)) {
		    System.out.println("ABORT!");
			motor.set(0.0);
			end();
			return;
		}
		
		if (modeButton.wasPressed()) {
			toggleMode();
		    System.out.println("Toggle mode. isAutoMode = " + isInAutoMode);
		}
		
		if (!climbing && areBothHooksOn()) {
			servo.set(SERVO_UNLOCK); // make sure hooks won't break when carriage goes up at very beginning
			motor.set(MOTOR_UP);
			motorGoingUp = true;
			climbing = true;
		}
		if (climbing) {
			if (isInAutoMode && !stopped) {
				updateMotor();
				updateHookCounter();
			} else if (! isInAutoMode) {
				operator();
				// Operator control
			}
		}
		
	}

	protected void onEnd() {
		motor.set(0.0);
		motor.free();
		servo.free();
		top.free();
		bottom.free();
		left.free();
		right.free();
	}
	
}
