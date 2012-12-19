package com.frc2013.rmr662.system;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import com.frc2013.rmr662.system.generic.Component;
import com.frc2013.rmr662.system.generic.Robot;

import edu.wpi.first.wpilibj.Watchdog;

/**
 * A multi-headed watchdog.
 * @author Dan Mercer
 */
public enum Fluffy {
	// Singleton instance
	INSTANCE;
	
	// Fields
	private final HashMap<Component, Boolean> components;
	private final Watchdog watchdog;
	
	// Constructor
	private Fluffy() {
		components = new HashMap<Component, Boolean>();
		watchdog = Watchdog.getInstance();
	}
	
	// Public Methods
	
	/**
	 * Called by {@link Component} to "check in" with the watchdog.
	 * @param c
	 */
	public void feed(Component c) {
		synchronized (components) {
			components.put(c, true);
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
	 * Checks for false values in the components Map
	 * @return true if all values in components are true, false if otherwise
	 */
	private boolean check() {
		// Get value list
		final Collection<Boolean> values = components.values();
		// Check for false values
		for (Boolean value : values) {
			if (!value) {
				return false;
			}
		}
		return true; // no false values found
	}
	
	/**
	 * Resets all Component statuses to false
	 */
	private void reset() {
		// Get component list
		final Set<Component> set = components.keySet();
		// For each component...
		for (Component c : set) {
			// ... reset value
			components.put(c, false);
		}
	}
}
