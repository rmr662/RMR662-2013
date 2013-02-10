package com.frc2013.rmr662.drive;

import com.frc2013.rmr662.system.generic.Component;
import com.frc2013.rmr662.wrappers.RMRJaguar;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;

/** Drive Component */
public class Drive extends Component {
    
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    
    private static final double[] KP = {0.20, 0.20};
    private static final double[] KI = {0.0, 0.0};
    private static final double[] KD = {0.21, 0.21};
    private static final int[] MOTOR_CHANNELS = {1, 2};
    private static final int[] ENCODER_CHANNELS_A = {1, 3};
    private static final int[] ENCODER_CHANNELS_B = {2, 4};
    
    private static final double DISTANCE_PER_PULSE = 0.001198473;
    private static final double MAX_SPEED = 1;

    //public static final int LEFT_MOTOR = 0;
    //public static final int RIGHT_MOTOR = 1;
    
    private RMRJaguar[] motors = new RMRJaguar[MOTOR_CHANNELS.length];
    private Joystick[] joysticks = new Joystick[MOTOR_CHANNELS.length];
    private Encoder[] encoders = new Encoder[ENCODER_CHANNELS_A.length];
    private PIDController[] controllers = new PIDController[MOTOR_CHANNELS.length];
    
    
    private double[] targetValues = {0d , 0d};

    private double[] arcadeAxes = {0d , 0d};
    
    private boolean[] isPressed = {false, false, false, false, false, false};
    //private boolean m_changed;
    
    private double relativePTuning = 0.01;
    private double relativeITuning = 0.0001;
    private double relativeDTuning = 0.01;
    
    private boolean pidEnabled = false;
    private boolean tuningEnabled = false;
    
    public Drive () {
	for(int i = 0; i < MOTOR_CHANNELS.length; i++) {
	    motors[i] = new RMRJaguar(MOTOR_CHANNELS[i], 1.0);
	    //encoders[i] = pool.getEncoder(ENCODER_CHANNELS_A[i], ENCODER_CHANNELS_B[i]);
	    encoders[i] = new Encoder(ENCODER_CHANNELS_A[i], ENCODER_CHANNELS_B[i]);
	    encoders[i].setDistancePerPulse(DISTANCE_PER_PULSE);
	    encoders[i].setPIDSourceParameter(Encoder.PIDSourceParameter.kRate);
	    controllers[i] = new PIDController(KP[i], KI[i], KD[i], encoders[i], motors[i]);
	    encoders[i].start();
	    controllers[i].setInputRange(-MAX_SPEED, MAX_SPEED);
	    controllers[i].setOutputRange(-MAX_SPEED, MAX_SPEED);
	    if (pidEnabled) {
		controllers[i].enable();
	    }
	}
	encoders[LEFT].setReverseDirection(true);
	joysticks[LEFT] = new Joystick(3);
	joysticks[RIGHT] = new Joystick(RIGHT+1);
    }
    
    // @Override
    public synchronized void update() {
	setAxisValues(joysticks);
	
	System.out.println("Y axis: " +  arcadeAxes[LEFT] + " X axis: " + arcadeAxes[RIGHT]);
	
	if (tuningEnabled) {
	    relativePIDTuning(joysticks);
	    //System.out.println(isPressed[0] + " " + isPressed[1] + " " + isPressed[2] + " " + isPressed[3]  + " " + isPressed[4] + " " + isPressed[5]);
	}
	//System.out.println("left = " + encoders[LEFT].getRate() + " right = " + encoders[RIGHT].getRate());
	if (pidEnabled) {
	    //System.out.println("lPID = " + controllers[LEFT].getSetpoint() + " rPID = " + controllers[RIGHT].getSetpoint());
	    System.out.println("P:" + getP() + " I:" + getI() + " D:" + getD());
	}
	arcadeDrive(arcadeAxes[LEFT], arcadeAxes[RIGHT]);
    }
    
    /**
     * Sets the speeds to send to each side motor
     * 
     * @param left The speed to send to the left motors
     * @param right  The speed to send to the right motors
     */
    public synchronized void setTargetValues(double left, double right) {
	targetValues[LEFT] = left;
	targetValues[RIGHT] = right;
	//m_changed = true;
    }
    
    /**
     * Drives the robot arcade-style by calculating from two axes
     * 
     * @param xAxis The x-axis to use
     * @param yAxis The y-axis to use
     */
    public void arcadeDrive(double yAxis, double xAxis) {
	if (yAxis > 0.0) {
	    if (xAxis > 0.0) {
		setTargetValues(yAxis-xAxis, Math.max(yAxis, xAxis));
	    }
	    else {
		setTargetValues(Math.max(yAxis, -xAxis), yAxis+xAxis);
	    }
	}
	else {
	    if (xAxis > 0.0) {
		setTargetValues(-Math.max(-yAxis, xAxis), yAxis+xAxis);
	    }
	    else {
		setTargetValues(yAxis-xAxis, -Math.max(-yAxis, -xAxis));
	    }
	}
	
	System.out.println("Left: " + targetValues[LEFT] + "Right: " + targetValues[RIGHT]);
	
	if (pidEnabled) {
	    controllers[LEFT].setSetpoint(targetValues[LEFT]);
	    controllers[RIGHT].setSetpoint(targetValues[RIGHT]);
	}
	else {
	    motors[LEFT].set(targetValues[LEFT]);
	    motors[RIGHT].set(targetValues[RIGHT]);
	}
    }
    
    /**
     * Set the axis values to use from the joystick for driving
     * 
     * @param joysticks The array of joysticks to get axes from
     */
    public void setAxisValues(Joystick[] joysticks) {
	arcadeAxes[LEFT] = joysticks[LEFT].getRawAxis(2);
	arcadeAxes[RIGHT] = joysticks[LEFT].getRawAxis(4);
    }
    
    /**
     * Forces a double to be no larger than 1 or smaller than -1.
     * 
     * @param num The double to limit
     * @return The limited double
     */
    private static double limit(double num) {
	if (num < -1d) {
	    return -1d;
	}
	if (num > 1d) {
	    return 1d;
	}
	return num;
    }
    
    /**
     * Add deltas to the gains on the PID controllers to tune the PID
     * 
     * @param p change in KP
     * @param i change in KI
     * @param d change in KD
     */
    public void setRelativePIDValues(double p, double i, double d) {
	for (int j = 0; j < controllers.length; ++j) {
	    controllers[j].setPID(controllers[j].getP()+p, controllers[j].getI()+i, controllers[j].getD()+d);
	}
    }
    
    /**
     * Turns PID driving on or off based on the value passed
     * 
     * @param enabled Pass true to enable PID driving, pass false to disable PID driving
     */
    public void setPID(boolean enabled) {
	pidEnabled = enabled;
	if (enabled) {
	    for (int i = 0; i< controllers.length; ++i) {
		controllers[i].enable();
	    }
	} else {
	    for (int i = 0; i< controllers.length; ++i) {
		controllers[i].disable();
	    }
	}
    }
    
    /**
     * Get the current P value for the PID
     * 
     * @return The P value for the PID
     */
    public double getP() {
	return controllers[LEFT].getP();
    }
    
    /**
     * Get the current I value for the PID
     * 
     * @return The I value for the PID
     */
     public double getI() {
	return controllers[LEFT].getI();
    }
    
    /**
     * Get the current D value for the PID
     * 
     * @return The D value for the PID
     */
    public double getD() {
	return controllers[LEFT].getD();
    }
    
    /**
     * Enables PID driving
     */
    public void enablePID() {
	setPID(true);
    }
    
    /**
     * Disables PID driving
     */
    public void disablePID() {
	setPID(false);
    }
    
    /**
     * Sets P, I, and D values based on buttons pressed on the 2nd joystick.
     * Trigger increments P, 'Button 2' decrements P
     * 'Button 3' increments I, 'Button 4' decrements I
     * 'Button 5' increments D, 'Button 6' decrements D
     * 
     * @param joysticks 
     */
    private void relativePIDTuning(Joystick[] joysticks) {
	for(int i = 0; i < this.isPressed.length; ++i) {
	    if(joysticks[RIGHT].getRawButton(i + 1) && !this.isPressed[i]) {
		switch(i + 1) {
		    case 1:
		    {
			setRelativePIDValues(relativePTuning,0,0);
			break;
		    }
		    case 2:
		    {
			setRelativePIDValues(-relativePTuning,0,0);
			break;
		    }
		    case 3:
		    {
			setRelativePIDValues(0,-relativeITuning,0);
			break;
		    }
		     case 4:
		    {
			setRelativePIDValues(0,0,-relativeDTuning);
			break;
		    }
		    case 5:
		    {
			setRelativePIDValues(0,relativeITuning,0);
			break;
		    }
		    case 6:
		    {
			setRelativePIDValues(0,0,relativeDTuning);
			break;
		    }
		}
	    }
	    this.isPressed[i] = joysticks[RIGHT].getRawButton(i + 1);
	}
    }

	protected void onEnd() { // Stop motors.
		final RMRJaguar[] localMotors = motors;
		final int length = localMotors.length;
		
		for (int i = 0; i < length; i++) {
			localMotors[i].set(0.0);
		}
	}
}
