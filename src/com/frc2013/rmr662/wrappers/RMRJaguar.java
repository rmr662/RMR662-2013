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
public class RMRJaguar {
    public final Jaguar jag;
	public final double multiplier;
    
    public RMRJaguar(int channel, double multiplier) {
	jag = new Jaguar(channel);
	this.multiplier = multiplier;
    }
    
    public RMRJaguar(Jaguar jag, double multiplier) {
    	this.jag = jag;
    	this.multiplier = multiplier;
    }
    
    public void set(double speed) {
	jag.set(speed * multiplier);
    }
}
