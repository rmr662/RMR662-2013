package com.frc2013.rmr662.system.generic;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.frc2013.rmr662.system.ButtonEvent;

/**
 * If {@link #loop()} is overridden, call <code>super.loop()</code> sometime
 * during its execution
 * 
 * @author Dan Mercer
 * 
 */
public abstract class TeleopMode extends RobotMode {
	private final ConcurrentLinkedQueue<ButtonEvent> events;

	public TeleopMode() {
		events = new ConcurrentLinkedQueue<ButtonEvent>();
	}

	/**
	 * Called by {@link com.frc2013.rmr662.system.JoystickThread}. If you don't
	 * know what this does, you probably don't need to care.
	 * 
	 * @param be
	 *            The ButtonEvent
	 */
	public final void addButtonEvent(ButtonEvent be) {
		events.add(be);
	}

	@Override
	protected void loop() {
		// Handle events
		for (ButtonEvent be : events) {
			if (be.change == ButtonEvent.PRESSED) {
				onButtonPressed(be);
			} else {
				onButtonReleased(be);
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
