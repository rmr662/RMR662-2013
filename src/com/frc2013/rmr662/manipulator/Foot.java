/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frc2013.rmr662.manipulator;

import com.frc2013.rmr662.system.generic.Component;
import com.frc2013.rmr662.wrappers.RMRJaguar;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;

/**
 *
 * @author ROBOTics
 */
public class Foot extends Component {
    
    private Joystick controller;
    private Carriage carriage;
    
    private class Carriage extends RMRJaguar {
        
        private Servo lockingServo;
        
        private static final double SERVO_LOCK = 0.0; // TODO
        private static final double SERVO_UNLOCK = SERVO_LOCK + 0.6;
        
        public Carriage(int channel, double multiplier) {
            super(channel, multiplier);
            lockingServo = new Servo(4);
        }
        
        public void set(double speed) {
            
            if (speed > 0 || controller.getRawButton(2) || Math.abs(speed) < 0.05) {
                lockingServo.set(SERVO_LOCK);
            } else {
                lockingServo.set(SERVO_UNLOCK);
            }
            
            super.set(speed);
        }
        
        public void free() {
            lockingServo.free();
            super.free();
        }
        
    }
    
    public Foot() {
	controller = new Joystick(3);
        carriage = new Carriage(3, 1.0);
    }
    
    protected void update() {
	double speed = controller.getRawAxis(3);
        carriage.set(speed);
    }
    
    protected void onEnd() {
        carriage.free();
    }
    
    
    
}
