package com.frc2013.rmr662.eastereggs;

import com.frc2013.rmr662.system.generic.Component;
import com.frc2013.rmr662.system.generic.RobotMode;
import com.frc2013.rmr662.climber.Climber;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Jaguar;

public class DanceMode extends RobotMode {
	
	private static class DanceDrive extends Component {
		private static final int MOTOR_CHANNEL_LEFT = 1;
		private static final int MOTOR_CHANNEL_RIGHT = 2;
		private static final double MOTOR_SPEED = 0.5;
		
		private final Jaguar leftMotor, rightMotor;
		
		public DanceDrive() {
			leftMotor = new Jaguar(MOTOR_CHANNEL_LEFT);
			rightMotor = new Jaguar(MOTOR_CHANNEL_RIGHT);
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
	
	private static class DanceManipulator extends Component {
		private final Jaguar motor = new Jaguar(Climber.MOTOR_PORT);
		private final DigitalInput top = new DigitalInput(Climber.SENSOR0);
		private final DigitalInput bottom = new DigitalInput(Climber.SENSOR1);
		
		public DanceManipulator() {
			motor.set(.5 * Climber.MOTOR_DIRECTION_MULT);
		}
		
		protected void update() {
			if (bottom.get() != Climber.INVERTEDS[0]) {
				motor.set(.5 * Climber.MOTOR_DIRECTION_MULT);
			} else if (top.get() != Climber.INVERTEDS[1]) {
				motor.set(-.5 * Climber.MOTOR_DIRECTION_MULT);
			}
		}
		
		public void onEnd() {
			motor.set(0);
		}
	}
	
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
