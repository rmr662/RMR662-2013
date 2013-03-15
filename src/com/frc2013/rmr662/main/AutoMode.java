/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frc2013.rmr662.main;

import com.frc2013.rmr662.system.generic.RobotMode;
import com.frc2013.rmr662.wrappers.RMRJaguar;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author mcoffin
 */
public class AutoMode extends RobotMode {
    
    private RMRJaguar leftMotor;
    private RMRJaguar rightMotor;
    
    private DoubleSolenoid dumper;
    
    public static final double SPEED = -0.55;
    public static final int DELAY = 5500;
    
    public AutoMode() {
        super("Autonomous");
    }

    protected void onBegin() {
        leftMotor = new RMRJaguar(1, 1.0);
        rightMotor = new RMRJaguar(2, 1.0);
        dumper = new DoubleSolenoid(2, 3);
        dumper.set(DoubleSolenoid.Value.kReverse);
        leftMotor.set(SPEED);
        rightMotor.set(SPEED);
        try {
            Thread.sleep(DELAY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        leftMotor.set(0);
        rightMotor.set(0);
        dumper.set(DoubleSolenoid.Value.kForward);
    }

    protected void loop() {
    }

    protected void onEnd() {
        leftMotor.free();
        rightMotor.free();
        dumper.free();
    }
    
}
