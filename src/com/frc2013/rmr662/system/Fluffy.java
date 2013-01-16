package com.frc2013.rmr662.system;

import com.frc2013.rmr662.system.generic.Component;
import com.frc2013.rmr662.system.generic.Robot;

import edu.wpi.first.wpilibj.Watchdog;

/**
 * A multi-headed watchdog.
 *
 * @author Dan Mercer
 */
public class Fluffy {
    // Singleton instance
    private static Fluffy instance;

    public static Fluffy getInstance() {
	synchronized (Fluffy.class) {
	    if (instance == null) {
		instance = new Fluffy();
	    }
	}
	return instance;
    }
    // Constants
    private static final int NUM_OF_COMPONENTS = 10;
    
    // Fields
    private final boolean[] components;
    private final Watchdog watchdog;
    private int nextNewIndex = 0;

    // Constructor
    private Fluffy() {
	components = new boolean[NUM_OF_COMPONENTS];
	watchdog = Watchdog.getInstance();
    }

    // Public Methods
    /**
     * Called by {@link Component#Component() } to get a unique index for that
     * Component.
     *
     * @return a new index
     */
    public int getNewIndex() {
	final int index = nextNewIndex;
	nextNewIndex += 1;
	return index;
    }

    /**
     * Called by {@link Component} to "check in" with the watchdog.
     *
     * @param componentIndex The index of the Component checking in.
     */
    public void feed(int componentIndex) {
	synchronized (components) {
	    components[componentIndex] = true;
	    components.notify();
	}
    }

    /**
     * Called by {@link Robot}
     */
    public void update() {
	synchronized (components) {
	    while (!check()) {
		// If check failed, wait for components to change
		try {
		    components.wait();
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	    // Reset values
	    reset();
	}
	watchdog.feed();
    }

    // Private methods
    /**
     * Checks for false values in the components array
     *
     * @return true if all values in components are true, false if otherwise
     */
    private boolean check() {
	// Check for false values
	for (int i = 0; i < nextNewIndex; i++) {
	    if (!components[i]) {
		return false;
	    }
	}
	return true; // no false values found
    }

    /**
     * Resets all Component statuses to false
     */
    private void reset() {
	for (int i = 0; i < nextNewIndex; i++) {
	    components[i] = false;
	}
    }
}
