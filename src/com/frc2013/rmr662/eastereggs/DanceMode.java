package com.frc2013.rmr662.eastereggs;

import com.frc2013.rmr662.system.generic.Component;
import com.frc2013.rmr662.system.generic.RobotMode;
import com.frc2013.rmr662.wrappers.RMRJaguar;


public class DanceMode extends RobotMode {
	
	/**
	 * The drive component for DanceMode
	 */
	private static class DanceDrive extends Component {
		private static final int MOTOR_CHANNEL_LEFT = 1;
		private static final int MOTOR_CHANNEL_RIGHT = 2;
		private static final double MOTOR_SPEED = 0.5;
		
		private final RMRJaguar leftMotor;
		private final RMRJaguar rightMotor;
		
		public DanceDrive() {
			leftMotor = new RMRJaguar(MOTOR_CHANNEL_LEFT, 1.0);
			rightMotor = new RMRJaguar(MOTOR_CHANNEL_RIGHT, 1.0);
		}
		
		protected void onBegin() {
			leftMotor.set(-MOTOR_SPEED);
			rightMotor.set(MOTOR_SPEED);
		}
		
		protected void update() {
		}
		
		protected void onEnd() {
			leftMotor.set(0.0);
			rightMotor.set(0.0);
		}
	}
	
//	/**
//	 * The manipulator component for DanceMode
//	 */
//	private static class DanceManipulator extends Component {
//		private final Jaguar motor = new Jaguar(Climber.MOTOR_PORT);
//		private final DigitalInput top = new DigitalInput(Climber.SENSOR0);
//		private final DigitalInput bottom = new DigitalInput(Climber.SENSOR1);
//		
//		protected void onBegin() {
//			motor.set(.5 * Climber.MOTOR_DIRECTION_MULT);
//		}
//		
//		protected void update() {
//			if (bottom.get() != INVERTED_BOTTOM) {
//				motor.set(.5 * Climber.MOTOR_DIRECTION_MULT);
//			} else if (top.get() != INVERTED_TOP) {
//				motor.set(-.5 * Climber.MOTOR_DIRECTION_MULT);
//			}
//		}
//		
//		public void onEnd() {
//			motor.set(0);
//		}
//	}
	
	private final DanceDrive drive;
	
	public DanceMode() {
		super("DanceMode");
		drive = new DanceDrive();
	}
	
	protected void onBegin() {
		drive.start();
	}
	
	protected void loop() {
		System.out.println("NYAN");
	}
	
	protected void onEnd() {
		drive.end();
	}
	
}
