package com.frc2013.rmr662.wrappers;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 * @author Dan Mercer
 *
 */
public class RMRDigitalInput extends DigitalInput {
	public final boolean inverted;

	/**
	 * @param channel
	 * @param inverted
	 */
	public RMRDigitalInput(int channel, boolean inverted) {
		super(channel);
		this.inverted = true;
	}

	/**
	 * @param moduleNumber
	 * @param channel
	 * @param inverted
	 */
	public RMRDigitalInput(int moduleNumber, int channel, boolean inverted) {
		super(moduleNumber, channel);
		this.inverted = inverted;
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
