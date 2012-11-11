package com.frc2013.rmr662.system;

/**
 * @author Dan Mercer
 *
 */
public final class ButtonEvent {
	public static final int RELEASED = 0;
	public static final int PRESSED = 1;
	
	/**
	 * The index of the joystick where the event happened
	 */
	public final int joystick;
	
	/**
	 * The index of the button that changed state
	 */
	public final int button;
	
	/**
	 * The time when the state change was detected
	 */
	public final long time;
	
	public final int change;
	
	ButtonEvent(int joystick, int button, long time, int change) {
		this.joystick = joystick;
		this.button = button;
		this.time = time;
		this.change = change;
	}

	/**
	 * @return The index of the joystick where the event happened
	 */
	public int getJoystick() {
		return joystick;
	}

	/**
	 * @return The index of the button that changed state
	 */
	public int getButton() {
		return button;
	}

	/**
	 * @return The time when the state change was detected
	 */
	public long getTime() {
		return time;
	}

}
