package com.frc2013.rmr662.system.generic;

/**
 * An abstraction of the mode of the robot.
 */
public abstract class RobotMode extends Thread {
    public RobotMode(String name) {
	super(name);
    }

    private volatile boolean ending = false;

    /**
     * @deprecated Do not call this method. It should only be called by the
     * system.
     * @see java.lang.Thread#run()
     */
    public final void run() {
	// begin
	onBegin();
	while (!isEnding()) {
	    // Call loop() method
	    loop();
	}
	System.out.println("RobotMode ending");
	// end
	onEnd();
    }
    
    private synchronized boolean isEnding() {
	return ending;
    }

    /**
     * Called when the RobotMode begins running. Override this to do something
     * here.
     */
    private void onBegin() {
	// Can be overridden by subclass
    }

    /**
     * Called repeatedly while the RobotMode is running
     */
    protected abstract void loop();

    /**
     * Called before the RobotMode finishes running. Override this to do
     * something here.
     */
    protected abstract void onEnd();

    /**
     * Called to end the RobotMode thread
     */
    public final synchronized void end() {
	ending = true;
	
    }
}
