---
permalink: /reference/testing-Michael-Lachut/TankRobot
---

# TankRobot

## About

**Location:**           utils   <br>
**Type:**               class   <br>
**Extends:**            [GamepadExtended](/reference/testing-Michael-Lachut/backend/GamepadExtended)


**Constructor Arguments**
  *  _Gamepad_ gamepad1
  *  _Gamepad_ gamepad2
  *  _HardwareMap_ hardwareMap
  *  _Telemetry_ telemetry

**Description:**
This class is responsible for doing all the driving for a tank-style robot with 4 drive motors. The **main** function can be placed inside another loop, but **isn't a loop itself**.
This is due to the RobotController's tendency to crash in most loops.

<br>
<br>

# Methods

## main

**Overrides parent:**   yes

**Arguments:**          none

**Return type:**        void

**Public:**             yes


**Description:**
The main function. Checks all gamepads and does the appropriate actions