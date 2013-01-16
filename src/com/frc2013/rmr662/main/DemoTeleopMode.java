package com.frc2013.rmr662.main;

import com.frc2013.rmr662.system.ButtonEvent;
import com.frc2013.rmr662.system.generic.TeleopMode;

public class DemoTeleopMode extends TeleopMode {
    
    public DemoTeleopMode() {
	super("DemoTeleopMode");
	System.out.println(Thread.currentThread().getName() + ": MY ROFLCOPTER GOES");
    }

    public int[] getJoystickPorts() {
	return new int[]{1, 2};
    }

//  @Override
    protected void onButtonPressed(ButtonEvent be) {
	System.out.println(Thread.currentThread().getName() + ": Button pressed.");
    }

//  @Override
    protected void loop() {
	super.loop();

	// STUFF GOES HERE
	System.out.println(Thread.currentThread().getName() + ": ROFL");

    }
}
