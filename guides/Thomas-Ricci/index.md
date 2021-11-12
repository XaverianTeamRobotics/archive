# Docs for Thomas Ricci

> Note:
> This only contains code by me.

### Contents
* Motor API
* Tank Drivetrain
* Servo API

### Motor API
The motor API is a simpler way to interact with motors than the default one FTC provides.
#### Using the motor API
To use the motor API, make a new simple `Motor`:
```java
Motor motor = new Motor(opModeTelemetry, opModeHardwareMap, nameOfMotorInConfiguration, DcMotorSimple.Direction.DIRECTION_OF_MOTOR);
```
Or complex `Motor`:
```java
Motor motor = new Motor(opModeTelemetry, opModeHardwareMap, nameOfMotorInConfiguration, DcMotorSimple.Direction.DIRECTION_OF_MOTOR, encoderCountsPerMotorRevolution, physicalGearReductionOfMotor, radiusOfAttachmentToMotor);
```
> Note:
> Simple motors can **not** drive a predetermined distance using the `Motor#driveDistance` method. Thus, simple motors should be used when driving does not require moving a specified distance, like in TeleOp OpModes where driving is determined by driver input. This is because they don't have access to information to determine how much they need to run.

Once you create a `Motor`, you can drive it a specified distance (if it's complex):
```java
// begin driving
motor.driveDistance(distanceInInches, maxSpeedRobotIsAllowedToDrive);
// wait until the motor has finished driving
while(motor.getDcMotor.isBusy());
```
You can also set its speed:
```java
motor.driveWithEncoder(speed);
```
Or set its voltage:
```java
motor.driveWithoutEncoder(power);
```
You can bring it to a stop:
```java
motor.break();
```
And reset the `Motor`'s encoder (although this isn't recommended unless the `Motor` is already stationary):
```java
motor.reset();
```
When you're done using the `Motor`, you can bring it to a stop and reset it with one method instead of two:
```java
motor.stop();
```

### Tank Drivetrains
There is also a `Tank` that automatically controls all of the `Motor`s of a physical tank. You'll probably only use this in Autonomous OpModes as there is another controller for controlling `Tank`s in TeleOp OpModes, which controls a `Tank` which controls four `Motor`s which each control a `DcMotor` which controls a Qualcomm `Motor` which controls a physical motor. Welcome to object oriented programming.
#### Using Tanks
Start by making a new `Tank`:
```java
Tank tank = new Tank(opModeTelemetry, rightTopMotor, rightBottomMotor, leftTopMotor, leftBottomMotor);
```
Then you can control the `Tank` just as you would a single `Motor`. Obviously if the `Motor`s are simple, they won't be able to drive a specified distance. Like a normal `Motor`, you should stop and reset a `Tank` with `Tank#stop`.
> Note:
> `Tank`s include shorthand methods to drive both sides of the tank at once rather than independently. This means every method will have two sets of arguments, one for moving all four `Motor`s with the same parameters and one for moving two sets of `Motor`s with different parameters.
> For example, you can use `Tank#driveWithEncoder` with the arguments `(speedOfRightSide, speedOfLeftSide)` to drive each side at a different speed or `(speedOfAllSides)` to drive both sides at the same speed.

### Servo API
The servo API is an object-oriented way to interact with servos. The servo API (currently) does not support continous servos, only standard ones.
### Using the servo API
To use the servo API, make a new `StandardServo`:
```java
StandardServo servo = new StandardServo(opModeHardwareMap, nameOfServo);
```
Once you've made a `StandardServo`, you can set its position:
```java
servo.setPosition(position);
```
> Note:
> The position must be between -100 and 100!
And change its direction:
```java
servo.setDirection(Servo.Direction.DirectionOfServo);
```
There also exists getters for its `ServoController`, specified position, etc.