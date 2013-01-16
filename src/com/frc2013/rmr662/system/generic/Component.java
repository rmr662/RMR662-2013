package com.frc2013.rmr662.system.generic;

import com.frc2013.rmr662.system.Fluffy;

/**
 * Represents a component of the robot. Subclasses should implement
 * {@link #update()}. Subclasses can also override {@link #onBegin()} and/or
 * {@link #onEnd()} as desired.
 * 
 * @author Dan Mercer
 * 
 */
public abstract class Component extends Thread {
	private final int index;
	private volatile boolean ending = false;
	
	public Component() {
	    index = Fluffy.getInstance().getNewIndex();
	}
	
	// Final methods

	/**
	 * @deprecated Do not call this method. It should only be called by the
	 *             system.
	 * @see java.lang.Thread#run()
	 */
//	@Override
	public final void run() {
		if (Thread.currentThread() != this) {
			throw new Error("Do not call Component.run()");
		}
		
		onBegin();
		while (!ending) {
			update();
		}
		Fluffy.getInstance().feed(index);
		onEnd();
	}

	/**
	 * Call this to end the Component thread
	 */
	public final void end() {
		this.ending = true;
	}
	
	// Subclass methods

	/**
	 * Called when the Component begins. Override this to do something here.
	 */
	protected void onBegin() {
	}

	/**
	 * Called before the Component ends. Override this to do something here.
	 */
	protected void onEnd() {
	}

	/**
	 * Called repeatedly while the component is enabled. This method is called
	 * in a synchronized block (synchronized on this Component).
	 */
	protected abstract void update();

}
