package com.frc2013.rmr662.system.generic;

/**
 * @author Dan Mercer
 * 
 */
public abstract class Component extends Thread {
	private volatile boolean ending = false;

	/**
	 * Constructs a new Component.
	 */
	protected Component() {
		// Nothing necessary here.
	}

	/**
	 * @deprecated Do not call this method. It should only be called by the system.
	 * @see java.lang.Thread#run()
	 */
	@Override
	public final void run() {
		if (Thread.currentThread() != this) {
			throw new UnsupportedOperationException(
					"Do not call Component.run()");
		}
		onBegin();
		while (!ending) {
			synchronized (this) {
				update();
			}
		}
		onEnd();
	}

	public final void end() {
		this.ending = true;
	}
	
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
