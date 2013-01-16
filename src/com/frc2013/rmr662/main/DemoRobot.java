package com.frc2013.rmr662.main;

import com.frc2013.rmr662.system.generic.Robot;
import com.frc2013.rmr662.system.generic.RobotMode;
import com.frc2013.rmr662.system.generic.TeleopMode;

public class DemoRobot extends Robot {

//	@Override
	protected RobotMode getAutoMode() {
		return new DemoAutoMode();
	}

//	@Override
	protected TeleopMode getTeleOpMode() {
		return new DemoTeleopMode();
	}

}
