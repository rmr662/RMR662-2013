package com.frc2013.rmr662.system;

import com.frc2013.rmr662.wrappers.RMRDigitalInput;
import com.frc2013.rmr662.wrappers.RMRJaguar;
import com.frc2013.rmr662.wrappers.RMRSolenoid;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Solenoid;

/**
 * A pool of hardware components (anything that uses IO channels). 
 */
public class HardwarePool {
    /** The channel number to use for unused breakouts */
    private static final int UNUSED = -1;
    
    // Singleton stuff
    private static HardwarePool instance;

    public synchronized static HardwarePool getInstance() {
	if (instance == null) {
	    instance = new HardwarePool();
	}
	return instance;
    }
    
    private static class HardwareElement {
    	/** The hardware object (i.e. Jaguar, Solenoid, DigitalInput, etc.) */
    	final Object o;
	/** The 12C channel on the Digital Breakout */
	final int _12C;
	/** The PWM channel on the Digital Breakout */
	final int pwm;
	/** The Relay channel on the Digital Breakout */
	final int relay;
	/** The Digital I/O channel on the Digital Breakout */
	final int digital;
	
	/** The channel on the Analog Breakout */
	final int analog;
	/** The channel on the Solenoid Breakout */
	final int solenoid;
	
	/**
	 * @param o The hardware object (i.e. Jaguar, Solenoid, etc.)
	 * @param digital The digital channel
	 * @param pwm The PWM channel
	 * @param relay The Relay channel
	 * @param _12C The 12C channel
	 * @param analog The Analog channel
	 * @param solenoid The Solenoid channel
	 */
	HardwareElement(Object o, int digital, int pwm, int relay, int _12C, int analog, int solenoid) {
	    this.o = o;
		this.digital = digital;
	    this.pwm = pwm;
	    this.relay = relay;
	    this._12C = _12C;
	    this.analog = analog;
	    this.solenoid = solenoid;
	}
	
	boolean matches(Class c, int digital, int pwm, int relay, int _12C, int analog, int solenoid) {
		if (o.getClass().equals(c)) {
			if (this.digital == digital
					&& this.pwm == pwm
					&& this.relay == relay
					&& this._12C == _12C
					&& this.analog == analog
					&& this.solenoid == solenoid) {
				return true;
			} else {
				throw new IllegalStateException();
			}
		}
		return false;
	}
    }
    
    // Fields
    private HardwareElement[] hardwares;
    private HardwareElement lastObject;
    // Channels last sent to exists()
    private int lastDigital;
    private int lastPWM;
    private int lastRelay;
    private int last12C;
    private int lastAnalog;
    private int lastSolenoid;
    
    private HardwarePool() {
	hardwares = new HardwareElement[0];
    }
    
    private boolean exists(Class c, int digital, int pwm, int relay, int solenoid) {
    	return exists(c, digital, pwm, relay, UNUSED, UNUSED, solenoid);
    }
    
    private boolean exists(Class c, int digital, int pwm, int relay, int _12C, int analog, int solenoid) {
	this.lastDigital = digital;
	this.lastPWM = pwm;
	this.lastRelay = relay;
	this.last12C = _12C;
	this.lastAnalog = analog;
	this.lastSolenoid = solenoid;
    	for (int i = 0; i < hardwares.length; i++) {
	    final HardwareElement h = hardwares[i];
		if (h.matches(c, digital, pwm, relay, _12C, analog, solenoid)) {
	    	lastObject = h;
	    	return true;
	    }
	}
	return false;
    }
    
    private Object getFoundObject() {
    	return lastObject;
    }
    
    private void addObject(Object o) {
		addToArray(new HardwareElement(o, lastDigital, lastPWM, lastRelay, last12C, lastAnalog, lastSolenoid));
	}
    
    private void addToArray(HardwareElement h) {
    	final HardwareElement[] temp = hardwares;
    	final int length = hardwares.length;
    	hardwares = new HardwareElement[length + 1];
    	System.arraycopy(temp, 0, hardwares, 0, length);
    	hardwares[length] = h;
    }
    
    // ============== PUBLIC METHODS ==========================

    /**
     * Gets a Jaguar on the given channel
     *
     * @param channel
     * @param multiplier The multiplier to apply to speed input
     * @return
     */
    public synchronized RMRJaguar getJaguar(int channel, double multiplier) {
	if (exists(Jaguar.class, UNUSED, channel, UNUSED, UNUSED)) {
		return new RMRJaguar((Jaguar) getFoundObject(), multiplier);
	} else {
		Jaguar j = new Jaguar(channel);
		addObject(j);
		return new RMRJaguar(j, multiplier);
	}
    }

	/**
     * Gets an RMRSolenoid on the given channel
     */
    public synchronized RMRSolenoid getSolenoid(int channel, boolean inverted) {
    	if (exists(Solenoid.class, UNUSED, UNUSED, UNUSED, channel)) {
    		return new RMRSolenoid((Solenoid) getFoundObject(), inverted);
    	} else {
    		RMRSolenoid c = new RMRSolenoid(channel, inverted);
    		addObject(c);
    		return c;
    	}
    }

    public synchronized RMRDigitalInput getDigitalInput(int channel,
	    boolean inverted) {
	if (exists(Compressor.class, channel, UNUSED, UNUSED, UNUSED)) {
		return new RMRDigitalInput((DigitalInput) getFoundObject(), inverted);
	} else {
		RMRDigitalInput rmrDI = new RMRDigitalInput(channel, inverted);
		addObject(rmrDI.di);
		return rmrDI;
	}
    }
    
    public synchronized Compressor getCompressor(int digitalChannel, int relayChannel) {
	if (exists(Compressor.class, digitalChannel, UNUSED, relayChannel, UNUSED)) {
		return (Compressor) getFoundObject();
	} else {
		Compressor c = new Compressor(digitalChannel, relayChannel);
		addObject(c);
		return c;
	}
    }
    
    public synchronized Servo getServo(int channel) {
    if (exists(Servo.class, UNUSED, channel, UNUSED, UNUSED)) {
    	return (Servo) getFoundObject();
    } else {
    	Servo s = new Servo(channel);
    	addObject(s);
    	return s;
    }
    }
    
    public synchronized Encoder getEncoder(int channelA, int channelB) {
    	if (exists(Encoder.class, channelA, UNUSED, UNUSED, UNUSED)) {
    		return (Encoder) getFoundObject();
    	} else {
    		Encoder e = new Encoder(channelA, channelB);
    		addObject(e);
    		return e;
    	}
    }
}
