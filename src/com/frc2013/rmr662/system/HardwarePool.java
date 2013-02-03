package com.frc2013.rmr662.system;

import com.frc2013.rmr662.wrappers.Invertable;
import com.frc2013.rmr662.wrappers.RMRDigitalInput;
import com.frc2013.rmr662.wrappers.RMRJaguar;
import com.frc2013.rmr662.wrappers.RMRSolenoid;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Encoder;

import edu.wpi.first.wpilibj.Servo;

/**
 * A pool of hardware components (anything that uses IO channels). 
 */
public class HardwarePool {
    // Singleton stuff
    private static HardwarePool instance;

    public synchronized static HardwarePool getInstance() {
	if (instance == null) {
	    instance = new HardwarePool();
	}
	return instance;
    }
    
    // Constants
    private static final int INITIAL_POOL_SIZE = 12;
    
    // Fields
    private Object[] hardwares;

    private HardwarePool() {
	hardwares = new Object[INITIAL_POOL_SIZE];
    }
    
    /**
     * Gets a Jaguar on the given channel
     *
     * @param channel
     * @return
     */
    public synchronized RMRJaguar getJaguar(int channel) {
	return getJaguar(channel, 1.0);
    }

    /**
     * Gets a Jaguar on the given channel
     *
     * @param channel
     * @param multiplier The multiplier to apply to speed input
     * @return
     */
    public synchronized RMRJaguar getJaguar(int channel, double multiplier) {
	RMRJaguar o = (RMRJaguar) getObject(channel,
		RMRJaguar.class);

	if (o == null) { // Need to create a new Jaguar

	    o = new RMRJaguar(channel, multiplier);
	    hardwares[channel] = o;
	} else if (((RMRJaguar) o).multiplier != multiplier) {
	    System.err.println("The Jaguar already initialized on " + channel + " does not have the same multiplier.");
	}
	return o;
    }

    /**
     * Gets an RMRSolenoid on the given channel
     *
     * @param channel
     * @param inverted
     * @return
     */
    public synchronized RMRSolenoid getSolenoid(int channel, boolean inverted) {
	RMRSolenoid o = (RMRSolenoid) getObject(channel,
		RMRSolenoid.class, inverted);

	if (o == null) { // Need to create a new RMRSolenoid

	    o = new RMRSolenoid(channel, inverted);
	    hardwares[channel] = o;
	}
	return o;
    }

    public synchronized RMRDigitalInput getDigitalInput(int channel,
	    boolean inverted) {
	RMRDigitalInput o = (RMRDigitalInput) getObject(channel,
		RMRDigitalInput.class, inverted);

	if (o == null) { // Need to create a new RMRDigitalInput

	    o = new RMRDigitalInput(channel, inverted);
	    hardwares[channel] = o;

	}
	return o;
    }
    
    public synchronized Compressor getCompressor(int channelA, int channelB) {
	Compressor o = (Compressor) getObject(channelA,
		Compressor.class);

	if (o == null) { // Need to create a new Compressor
	    
	    o = new Compressor(channelA, channelB);
	    hardwares[channelA] = o;
	}
	return o;
    }
    
    public synchronized Servo getServo(int channel) {
	Servo o = (Servo) getObject(channel,
		Servo.class);

	if (o == null) { // Need to create a new Servo

	    o = new Servo(channel);
	    hardwares[channel] = o;

	}
	return o;
    }
    
    public synchronized Encoder getEncoder(int channelA, int channelB) {
	Encoder o = (Encoder) getObject(channelA, Encoder.class);

	if (o == null) { // Need to create a new Encoder
	    
	    if (hardwares[channelB] != null) {
		throw new IllegalStateException("A hardware component already exists on channelB (" + channelB + ")");
	    }

	    o = new Encoder(channelA, channelB);
	    hardwares[channelA] = o;
	    hardwares[channelB] = o;

	}
	return o;
    }

    private Object getObject(int channel, Class c) {
	return getObject(channel, c, false);
    }

    private Object getObject(int channel, Class c, boolean inverted) {
	// Ensure array is big enough.
	final int tempLength = hardwares.length;
	if (tempLength <= channel) {
	    final Object[] temp = hardwares;
	    hardwares = new Object[channel + 1];
	    System.arraycopy(temp, 0, hardwares, 0, tempLength);
	}
	
	// Get Object
	final Object o = hardwares[channel];
	if (o != null) {
	    if (o.getClass() == c) {
		if (o instanceof Invertable
			&& ((Invertable) o).isInverted() != inverted) {
		    System.err.println("The " + c.getName() + " (" + channel
			    + ") does not match the requested inversion.");
		}
		return o;
	    } else {
		throw new IllegalStateException("A " + o.getClass().getName()
			+ " has already been initialized on channel " + channel);
	    }
	} else {
	    return o;
	}
    }    
}
