package com.frc2013.rmr662.system.generic;

import com.frc2013.rmr662.system.ButtonEvent;

/**
 * If {@link #loop()} is overridden, call <code>super.loop()</code> sometime
 * during its execution
 * 
 * @author Dan Mercer
 * 
 */
public abstract class TeleopMode extends RobotMode {
	private ButtonEvent buttonEvent;
	private boolean buttonEventIsRead = true;
	
	public TeleopMode(String name) {
	    super(name);
	}

	/**
	 * Called by {@link com.frc2013.rmr662.system.JoystickThread}. If you don't
	 * know what this does, you probably don't need to care.
	 * 
	 * @param be
	 *            The ButtonEvent
	 */
	public final void addButtonEvent(ButtonEvent be) {
		synchronized (this) {
		    if (buttonEventIsRead) {
			buttonEvent = be;
			buttonEventIsRead = true;
		    }
		}
	}
	
	public abstract int[] getJoystickPorts();
	
	/**
	 * TODO Left off here
	 */
//	@Override
	protected void loop() {
		// Handle event
		synchronized(this) {
		    if (!buttonEventIsRead) {
			final ButtonEvent be = buttonEvent;
			if (be.change == ButtonEvent.PRESSED) {
				onButtonPressed(be);
			} else {
				onButtonReleased(be);
			}
		    }
		}
	}

	/**
	 * Called when a button is pressed.
	 * 
	 * @param be
	 *            The ButtonEvent object describing the event.
	 */
	protected abstract void onButtonPressed(ButtonEvent be);

	/**
	 * Called when a button is released. Override this to do something here.
	 * 
	 * @param be
	 *            The ButtonEvent object describing the event.
	 */
	protected void onButtonReleased(ButtonEvent be) {
		// Subclasses can override this to do something here.
	}

}
