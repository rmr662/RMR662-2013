package com.frc2013.rmr662.main;

import com.frc2013.rmr662.eastereggs.DanceMode;
import com.frc2013.rmr662.system.generic.Robot;
import com.frc2013.rmr662.system.generic.RobotMode;

public class MainRobot extends Robot {
    
        private RobotMode autoMode;
        private RobotMode teleopMode;
        
        public MainRobot() {
            teleopMode = new TeleopMode();
        }

	protected RobotMode getAutoMode() {
		return autoMode;
	}

	protected RobotMode getTeleopMode() {
		return teleopMode;
	}

}
