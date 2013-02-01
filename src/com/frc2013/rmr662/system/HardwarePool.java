package com.frc2013.rmr662.system;

import com.frc2013.rmr662.wrappers.Invertable;
import com.frc2013.rmr662.wrappers.RMRDigitalInput;
import com.frc2013.rmr662.wrappers.RMRSolenoid;

import edu.wpi.first.wpilibj.Jaguar;

/**
 * A pool of Jaguars, RMRSolenoids, RMRDigitalInputs, etc.
 * TODO: Make getWhatever methods static for neater use
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
	public synchronized Jaguar getJaguar(int channel) {
		Jaguar o = (Jaguar) getObject(channel,
				Jaguar.class);
		
		if (o == null) { // Need to create a new Jaguar
		
			o = new Jaguar(channel);
			hardwares[channel] = o;
			
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
	
	private Object getObject(int channel, Class c) {
		return getObject(channel, c, false);
	}
	
	private Object getObject(int channel, Class c, boolean inverted) {
		ensureArrayFits(channel);
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
	
	private void ensureArrayFits(int newIndex) {
		final int tempLength = hardwares.length;
		if (tempLength <= newIndex) {
			final Object[] temp = hardwares;
			hardwares = new Object[newIndex + 1];
			System.arraycopy(temp, 0, hardwares, 0, tempLength);
		}
	}
}
