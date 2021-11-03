---
permalink: /reference/Michael-Lachut
---

# Docs for Michael Lachut

## Image Processing
[Here](./Michael-Lachut/image-pain)

## Old drivetrain stuff

To get started, you can use the [TankRobot](./Michael-Lachut/TankRobot) class to quickly drive a tank-style robot,
or you could use the [RobotWithSpinner](./Michael-Lachut/RobotWithSpinner) class if you have a basic 4WD robot with a spinner.

They both inherit from [GamepadExtended](./Michael-Lachut/backend/GamepadExtended) and should be enclosed in an external loop.

The other classes shouldn't be used directly, but I will still provide docs for them.


# NOTE: THIS ONLY INCLUDES COMMITS MADE BY _ME_

# Main APIs

  * [TankRobot](./Michael-Lachut/TankRobot)
  * [RobotWithSpinner](./Michael-Lachut/RobotWithSpinner)

# Advanced Backends
  
  * [ButtonPriority](./Michael-Lachut/backend/ButtonPriority)
  * [GamepadExtended](./Michael-Lachut/backend/GamepadExtended)
  * [Drivetrain](./Michael-Lachut/backend/Drivetrain)

This stuff should **only** be used directly if you are modifying existing code