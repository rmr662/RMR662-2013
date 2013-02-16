/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frc2013.rmr662.manipulator;

import com.frc2013.rmr662.system.generic.Component;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;

/**
 *
 * @author ROBOTics
 */
public class Foot extends Component {
    
    private DoubleSolenoid footSolenoid;
    private Joystick controller;
    
    private Jaguar motor;
    
    public Foot() {
	footSolenoid = new DoubleSolenoid(4, 5);
	controller = new Joystick(3);
	
	motor = new Jaguar(3);
    }
    
    protected void update() {
	if (controller.getRawButton(1)) {
	    footSolenoid.set(DoubleSolenoid.Value.kForward);
	} else {
	    footSolenoid.set(DoubleSolenoid.Value.kReverse);
	}
	
	double speed = controller.getRawAxis(3);
	motor.set(speed);
    }
    
    protected void onEnd() {
	footSolenoid.free();
	motor.free();
    }
    
    
    
}
