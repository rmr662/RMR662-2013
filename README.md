# Rocky Mountain Robotics (FRC Team 662)
RMR's robot code for the 2013 build season.

## The Architecture
The architecture is a very loose structure of concurrent Threads. When the [Robot][] starts, it spawns a [RobotMode][] thread which then spawns [Component][] threads, as illustrated in the diagram below:

	Robot
	  |
	  |-----------+
	  |       RobotMode
	  |           |
	  |        onBegin()--------+----...-------+
	  |           |         Component      Component
	  |           |             |              |
	  |           |<----,     onBegin()      onBegin()
	[...]         |     |       |              |
	  |         loop()  |       |<-----,       |<-----,
	  |           |-----'       |      |       |      |
	  |			  |           update() |     update() |
	  |           |             |------'       |------'
	  |			  |		        |              |
	  |-------> onEnd()-----> onEnd() ...--> onEnd()
	  |			  |		        |              |
	 END		 END		   END			  END

Each sub-team may have as many Components as they need.

[Robot]: https://github.com/rmr662/RMR662-2013/blob/master/src/com/frc2013/rmr662/system/generic/Robot.java
[RobotMode]: https://github.com/rmr662/RMR662-2013/blob/master/src/com/frc2013/rmr662/system/generic/RobotMode.java
[Component]: https://github.com/rmr662/RMR662-2013/blob/master/src/com/frc2013/rmr662/system/generic/Component.java
