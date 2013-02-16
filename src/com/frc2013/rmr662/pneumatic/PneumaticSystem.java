/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frc2013.rmr662.pneumatic;

import com.frc2013.rmr662.system.generic.Component;
import com.frc2013.rmr662.wrappers.RMRDigitalInput;

import edu.wpi.first.wpilibj.Compressor;

/**
 *
 * @author mcoffin
 */
public class PneumaticSystem extends Component {
    
    // TODO
    public static final int PRESSURE_SWITCH_CHANNEL = 5;
    public static final int RELAY_CHANNEL = 1;
    
    private final Compressor compressor;
    private final RMRDigitalInput disableSwitch;
    private boolean isRunning = false;
    
    public PneumaticSystem() {
        super();
        compressor = new Compressor(PRESSURE_SWITCH_CHANNEL, RELAY_CHANNEL);
        disableSwitch = new RMRDigitalInput(6, false);
    }
    
    protected void onBegin() {
        
    }
    
    protected void onEnd() {
        if (compressor.enabled()) {
            System.out.println("Stopping compressor.");
            compressor.stop();
        }
	
	compressor.free();
	disableSwitch.free();
    }
    
    protected void update() {
        if (disableSwitch.get() && compressor.enabled()) {
            compressor.stop();
        }
        if (!(disableSwitch.get() || compressor.enabled())) {
            compressor.start();
        }
    }
}
