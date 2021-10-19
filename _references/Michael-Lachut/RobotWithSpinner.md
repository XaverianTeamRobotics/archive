---
permalink: /reference/Michael-Lachut/RobotWithSpinner
---

# RobotWithSpinner

## About

**Location:**                   utils   <br>
**Type:**                       class   <br>
**Extends:**                    [GamepadExtended](./backend/GamepadExtended)


**Constructor Arguments**
  *  _Gamepad_ gamepad1
  *  _Gamepad_ gamepad2
  *  _HardwareMap_ hardwareMap
  *  _Telemetry_ telemetry

**Uses parent constructor:**    yes

**Description:**
This class is responsible for doing all the driving for a 4WD robot with a spinner motor attachment. The **main** function can be placed inside another loop, but **isn't a loop itself**.
This is due to the RobotController's tendency to crash in most loops.

<br>
<br>

# Methods

## main

**Overrides parent:**           yes     <br>
**Arguments:**                  none    <br>
**Return type:**                void    <br>
**Public:**                     yes

<br>
<br>

**Description:**
The main function. Checks all gamepads and does the appropriate actions