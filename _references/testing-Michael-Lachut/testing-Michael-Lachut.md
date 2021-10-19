---
permalink: /reference/testing-Michael-Lachut
---

# Docs for testing-Michael-Lachut

To get started, you can use the [TankRobot](./testing-Michael-Lachut/TankRobot) class to quickly drive a tank-style robot,
or you could use the [RobotWithSpinner](./testing-Michael-Lachut/RobotWithSpinner) class if you have a basic 4WD robot with a spinner.

They both inherit from [GamepadExtended](./testing-Michael-Lachut/backend/GamepadExtended) and should be enclosed in an external loop.

The other classes shouldn't be used directly, but I will still provide docs for them.


# NOTE: THIS ONLY INCLUDES COMMITS MADE BY _ME_

# Main APIs

  * [TankRobot](./testing-Michael-Lachut/TankRobot)
  * [RobotWithSpinner](./testing-Michael-Lachut/RobotWithSpinner)

# Advanced Backends
  
  * [ButtonPriority](./testing-Michael-Lachut/backend/ButtonPriority)
  * [GamepadExtended](./testing-Michael-Lachut/backend/GamepadExtended)
  * [Drivetrain](./testing-Michael-Lachut/backend/Drivetrain)

This stuff should **only** be used directly if you are modifying existing code