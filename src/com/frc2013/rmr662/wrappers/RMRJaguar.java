/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frc2013.rmr662.wrappers;

import edu.wpi.first.wpilibj.Jaguar;

/**
 *
 * @author ROBOTics
 */
public class RMRJaguar extends Jaguar {
    public final double multiplier;
    
    public RMRJaguar(int channel, double multiplier) {
	super(channel);
	this.multiplier = multiplier;
    }
    
    public RMRJaguar(int slot, int channel, double multiplier) {
	super(slot, channel);
	this.multiplier = multiplier;
    }
    
    public void set(double speed) {
	super.set(speed * multiplier);
    }
}
