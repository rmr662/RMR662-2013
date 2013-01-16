package com.frc2013.rmr662.system;

import com.frc2013.rmr662.system.generic.TeleopMode;

import edu.wpi.first.wpilibj.Joystick;

/**
 * @author Dan Mercer
 *
 */
public final class JoystickThread extends Thread {

    private static final int NUM_OF_BUTTONS = 5;
    private volatile boolean ending;
    
    private final Joystick[] joysticks;
    private final int joystickCount;
    private final boolean[][] joystickStates;
    private TeleopMode mode;

    public JoystickThread(int[] ports) {
	// Initialize joysticks
	this.joystickCount = ports.length;
	this.joysticks = new Joystick[ports.length];
	for (int i = 0; i < joystickCount; i++) {
	    joysticks[i] = new Joystick(ports[i]);
	}

	// Initialize joystickStates
	this.joystickStates = new boolean[joystickCount][NUM_OF_BUTTONS];
    }

    /**
     * @deprecated Do not call this method. It should only be called by the
     * system.
     */
//	@Override
    public void run() {
	// Safety check
	if (Thread.currentThread() != this) {
	    throw new Error("Don't call JoystickThread.run()");
	}

	// Loop until end() is called
	while (!ending) {
	    for (int joystickIndex = 0; joystickIndex < joystickCount; joystickIndex++) {
		// Localize Joystick and button state array
		final Joystick stick = joysticks[joystickIndex];
		final boolean[] states = joystickStates[joystickIndex];

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
	    }
	}
    }

//  @Override
    public void start() {
	// Assert that a mode has been set
	boolean modeIsSet;
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
     *
     * @param newMode
     */
    public void setMode(TeleopMode newMode) {
	synchronized (joysticks) {
	    if (mode == null) {
		this.mode = newMode;
	    } else {
		throw new Error("setMode() has already been called");
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
