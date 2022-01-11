# Docs for Michael Lachut

## Image Processing
[Here](./image-pain)

## Old drivetrain stuff

To get started, you can use the [TankRobot](./TankRobot) class to quickly drive a tank-style robot,
or you could use the [RobotWithSpinner](./RobotWithSpinner) class if you have a basic 4WD robot with a spinner.

They both inherit from [GamepadExtended](./backend/GamepadExtended) and should be enclosed in an external loop.

The other classes shouldn't be used directly, but I will still provide docs for them.


# NOTE: THIS ONLY INCLUDES COMMITS MADE BY _ME_

# Main APIs

  * [TankRobot](./TankRobot)
  * [RobotWithSpinner](./RobotWithSpinner)

# Advanced Backends
  
  * [ButtonPriority](./backend/ButtonPriority)
  * [GamepadExtended](./backend/GamepadExtended)
  * [Drivetrain](./backend/Drivetrain)

This stuff should **only** be used directly if you are modifying existing code