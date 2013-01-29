package com.frc2013.rmr662.wrappers;

import edu.wpi.first.wpilibj.Solenoid;

/**
 * @author Dan Mercer
 * 
 */
public class RMRSolenoid extends Solenoid {
	private final boolean inverted;
	
	/**
	 * @param channel
	 * @param inverted
	 */
	public RMRSolenoid(int channel, boolean inverted) {
		super(channel);
		this.inverted = inverted;
	}

	/**
	 * @param moduleNumber
	 * @param channel
	 * @param inverted
	 */
	public RMRSolenoid(int moduleNumber, int channel, boolean inverted) {
		super(moduleNumber, channel);
		this.inverted = inverted;
	}

	public void set(boolean on) {
		if (inverted) {
			super.set(!on);
		} else {
			super.set(on);
		}
	}

	public boolean get() {
		final boolean b = super.get();
		if (inverted) {
			return !b;
		} else {
			return b;
		}
	}
	
}
