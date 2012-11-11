package com.frc2013.rmr662.system;

import java.util.Arrays;
import java.util.LinkedList;

import com.frc2013.rmr662.system.generic.TeleopMode;

import edu.wpi.first.wpilibj.Joystick;

/**
 * @author Dan Mercer
 *
 */
final class JoystickThread extends Thread {
	private static final int NUM_OF_BUTTONS = 5;

	private volatile boolean ending;
	
	private final LinkedList<Joystick> joysticks;
	private final boolean[][] joystickStates;
	
	private TeleopMode mode;
	
	public JoystickThread (Joystick... joysticks) {
		this.setDaemon(true);
		this.joysticks = new LinkedList<Joystick>(Arrays.asList(joysticks));
		final int joystickCount = joysticks.length;
		this.joystickStates = new boolean[joystickCount][NUM_OF_BUTTONS];
	}
	
	@Override
	public void run() {
		if (Thread.currentThread() != this) {
			throw new UnsupportedOperationException("Don't call JoystickThread.run()");
		}
		while(!ending) {
			int joystickIndex = 0;
			for (Joystick stick : this.joysticks) {
				// Localize button state array
				final boolean[] states = this.joystickStates[joystickIndex];
				
				// Check buttons
				for (int i = 0; i < NUM_OF_BUTTONS; i++) {
					final boolean currentState = stick.getRawButton(i);
					final boolean lastState = states[i];
					
					if ((!lastState) && currentState) {
						// Button was pressed
						states[i] = true;
						onButtonPressed(joystickIndex, i, System.currentTimeMillis());
						
					} else if (lastState && !currentState) {
						// Button was released
						states[i] = false;
						onButtonReleased(joystickIndex, i, System.currentTimeMillis());
					}
				}
				joystickIndex++;
			}
		}
	}
	
	@Override
	public synchronized void start() {
		boolean modeIsSet = false;
		synchronized (mode) {
			modeIsSet = (mode != null);
		}
		if (modeIsSet) {
			super.start();
		} else {
			throw new IllegalStateException("JoystickThread.setMode() has not been called.");
		}
	}

	/**
	 * @param button The index of the button that was pressed.
	 * @param time The current time
	 */
	private void onButtonReleased(int joystick, int button, long time) {
		final ButtonEvent be = new ButtonEvent(joystick, button, time, ButtonEvent.RELEASED);
		this.mode.addButtonEvent(be);
	}

	/**
	 * @param button The index of the button that was released.
	 * @param time The current time
	 */
	private void onButtonPressed(int joystick, int button, long time) {
		final ButtonEvent be = new ButtonEvent(joystick, button, time, ButtonEvent.RELEASED);
		this.mode.addButtonEvent(be);
	}

	public void setMode(TeleopMode newMode) {
		synchronized (mode) {
			if (mode == null) {
				this.mode = newMode;
			} else {
				throw new UnsupportedOperationException("setMode() has already been called");
			}
		}
	}
	
	public void end() {
		ending = true;
	}
}
