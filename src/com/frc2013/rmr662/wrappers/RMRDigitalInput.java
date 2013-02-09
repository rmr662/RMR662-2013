package com.frc2013.rmr662.wrappers;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 * @author Dan Mercer
 *
 */
public class RMRDigitalInput {
	public final boolean inverted;
	public final DigitalInput di;

	/**
	 * @param channel
	 * @param inverted
	 */
	public RMRDigitalInput(int channel, boolean inverted) {
		this.di = new DigitalInput(channel);
		this.inverted = inverted;
	}
	
	public RMRDigitalInput(DigitalInput di, boolean inverted) {
		this.di = di;
		this.inverted = inverted;
	}

	public boolean get() {
		final boolean b = di.get();
		if (inverted) {
			return !b;
		} else {
			return b;
		}
	}

	public boolean isInverted() {
		return inverted;
	}
	
}
