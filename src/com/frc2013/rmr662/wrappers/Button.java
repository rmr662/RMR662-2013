
package com.frc2013.rmr662.wrappers;

import edu.wpi.first.wpilibj.Joystick;

/**
 *
 * @author ROBOTics
 */
public class Button {
    private final Joystick joystick;
    private final int button;
    private boolean lastState;
    
    public Button(Joystick joystick, int button) {
	this.joystick = joystick;
	this.button = button;
	lastState = joystick.getRawButton(button);
    }
    
    public boolean wasPressed() {
	final boolean currentState = joystick.getRawButton(button);
	final boolean pressed = (!lastState && currentState);
	lastState = currentState;
	return pressed;
    }
    
    public boolean get() {
	lastState = joystick.getRawButton(button);
	return lastState;
    }
}
