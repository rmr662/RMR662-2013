package com.frc2013.rmr662.system;

import java.util.LinkedList;

import com.frc2013.rmr662.system.generic.TeleopMode;

import edu.wpi.first.wpilibj.Joystick;

/**
 * @author Dan Mercer
 *
 */
public final class JoystickThread extends Thread {
	private static final int NUM_OF_BUTTONS = 5;

	private volatile boolean ending;
	
	private final LinkedList<Joystick> joysticks;
	private final boolean[][] joystickStates;
	
	private TeleopMode mode;
	
	public JoystickThread (int... ports) {
		this.setDaemon(true); // this thread is a background thread
		
		// Initialize joysticks
		this.joysticks = new LinkedList<Joystick>();
		final int joystickCount = ports.length;
		for (int port : ports) {
			joysticks.add(new Joystick(port));
		}
		
		// Initialize joystickStates
		this.joystickStates = new boolean[joystickCount][NUM_OF_BUTTONS];
	}
	
	@Deprecated
	@Override
	public void run() {
		// Safety check
		if (Thread.currentThread() != this) {
			throw new UnsupportedOperationException("Don't call JoystickThread.run()");
		}
		
		// Loop until end() is called
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
						final ButtonEvent be = new ButtonEvent(joystickIndex, i, System.currentTimeMillis(), ButtonEvent.PRESSED);
						mode.addButtonEvent(be);
						
					} else if (lastState && !currentState) {
						// Button was released
						states[i] = false;
						final ButtonEvent be = new ButtonEvent(joystickIndex, i, System.currentTimeMillis(), ButtonEvent.RELEASED);
						mode.addButtonEvent(be);
					}
				}
				joystickIndex++;
			}
		}
	}
	
	@Override
	public void start() {
		// Assert that a mode has been set
		boolean modeIsSet = false;
		synchronized (joysticks) {
			modeIsSet = (mode != null);
		}
		// Start only if a mode has been set.
		if (modeIsSet) {
			super.start();
		} else {
			throw new IllegalStateException("JoystickThread.setMode() has not been called.");
		}
	}

	/**
	 * Sets the {@link TeleopMode} with which the JoystickThread will interface.
	 * @param newMode
	 */
	public void setMode(TeleopMode newMode) {
		synchronized (joysticks) {
			if (mode == null) {
				this.mode = newMode;
			} else {
				throw new UnsupportedOperationException("setMode() has already been called");
			}
		}
	}
	
	/**
	 * Called to end the JoystickThread
	 */
	public void end() {
		ending = true;
	}
}
