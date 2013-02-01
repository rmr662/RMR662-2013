/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frc2013.rmr662.pneumatic;

import com.frc2013.rmr662.system.HardwarePool;
import com.frc2013.rmr662.system.generic.Component;
import edu.wpi.first.wpilibj.Compressor;

/**
 *
 * @author mcoffin
 */
public class PneumaticSystem extends Component {
    
    // TODO
    public static final int PRESSURE_SWITCH_CHANNEL = 5;
    public static final int RELAY_CHANNEL = 1;
    
    private Compressor compressor;
    private boolean isRunning = false;
    
    public PneumaticSystem() {
        super();
        compressor = HardwarePool.getInstance().getCompressor(PRESSURE_SWITCH_CHANNEL, RELAY_CHANNEL);
    }
    
    protected void onBegin() {
        compressor.start();
        isRunning = true;
    }
    
    protected void onEnd() {
        compressor.stop();
        isRunning = false;
    }
    
    protected void update() {
        // TODO check the compressor disable switch.
    }
}
