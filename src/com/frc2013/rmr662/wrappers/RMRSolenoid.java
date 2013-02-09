package com.frc2013.rmr662.wrappers;

import edu.wpi.first.wpilibj.Solenoid;

/**
 * @author Dan Mercer
 * 
 */
public class RMRSolenoid {
	public final boolean inverted;
	public final Solenoid s;
	
	/**
	 * @param channel
	 * @param inverted
	 */
	public RMRSolenoid(int channel, boolean inverted) {
		this.s = new Solenoid(channel);
		this.inverted = inverted;
	}

	/**
	 * @param s
	 * @param inverted2
	 */
	public RMRSolenoid(Solenoid s, boolean inverted) {
		this.s = s;
		this.inverted = inverted;
	}

	public void set(boolean on) {
		if (inverted) {
			s.set(!on);
		} else {
			s.set(on);
		}
	}

	public boolean get() {
		final boolean b = s.get();
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
