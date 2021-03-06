/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frc2013.rmr662.manipulator;

import com.frc2013.rmr662.system.generic.Component;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Talon;

/**
 *
 * @author ROBOTics
 */
public class Foot extends Component {
    
    private Joystick controller;
    private Carriage carriage;
    
    private class Carriage extends Talon {
        
        private Servo lockingServo;
        
        private static final double SERVO_LOCK = 0.0; // TODO
        private static final double SERVO_UNLOCK = SERVO_LOCK + 0.6;
        
        public Carriage(int channel, double multiplier) {
            super(channel);
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
	controller = new Joystick(1);
        carriage = new Carriage(3, 1.0);
    }
    
    protected void update() {
	double speed = controller.getRawAxis(2);
        carriage.set(speed);
    }
    
    protected void onEnd() {
        carriage.free();
    }
    
    
    
}
