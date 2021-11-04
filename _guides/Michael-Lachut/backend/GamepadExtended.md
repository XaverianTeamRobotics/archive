---
permalink: /guide/Michael-Lachut/backend/GamepadExtended
---

# GamepadExtended

## About

**Location:**                   utils   <br>
**Type:**                       class   <br>
**Abstract:**                   yes


**Constructor Arguments**
  *  _Gamepad_ gamepad1
  *  _Gamepad_ gamepad2
  *  _HardwareMap_ hardwareMap
  *  _Telemetry_ telemetry

**Description:**
This class functions as the interface between the Gamepads and the drivetrain. It has a [ButtonPriority](./ButtonPriority) attribute which should be ussed in the main function.

<br>
<br>

# Methods

## main

**Abstract:**                   yes     <br>
**Arguments:**                  none    <br>
**Return type:**                void    <br>
**Public:**                     yes

<br>
<br>

**Description:**
The main function. Checks all gamepads and does the appropriate actions. Shouldn't include a **while** loop.