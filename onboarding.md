# Onboarding
Welcome to the Hawks Robotics Onboarding Guide. This shows you how to get set up with the repository, running Android Studio, coding, testing, and uploading your code. In this tutorial, we'll create a development environment, make a branch, write a program to control a robot with a tank drivetrain, upload it, and document it to explain how to use it.

### Installation
First, if you don't already have one, make a [GitHub](https://github.com) account. From there, ask Roche or Tassinari to invite you to the GitHub organization, and accept the invite. You also need to be added to the organization's team.

Then, head over to [Git Bash](https://git-scm.com/downloads) and install it. Then install [JDK 16](https://www.oracle.com/java/technologies/javase/jdk16-archive-downloads.html) (not 17!) and [Android Studio](https://developer.android.com/studio).

> Note:\
> If you have apt you can use `sudo apt install git openjdk-16-jdk -y` for Git and JDK, but you still have to download Android Studio from the website and probably don't need a tutorial anyway. :P

Lastly, download [GitHub Desktop](https://desktop.github.com). If you're on Linux, you can use [this fork](https://github.com/shiftkey/desktop/releases) as a replacement for the original. Obviously if you're already comfortable with Git and know how to use the command line you don't need to install GitHub desktop though.

### Setup
Open Command Prompt as an administrator (Windows) or Terminal (macOS & Linux), then run:
```bash
# Windows

git config --global user.name "your github username"
git config --global user.email "your github email address"

# macOS & Linux

sudo git config --global user.name "your github username"
sudo git config --global user.email "your github email address"
``` 
> Note:\
> If you've never used a command line before, to run a command simply type the command in and press enter. If you're on macOS or Linux, you may need to enter your password after you press enter. Wrap your username and email in quotes. For example, one might write `git config --global user.name "Foo Bar"` and `git config --global user.email "foobar@example.com"`.

From there, log into GitHub and head over to [our repository](https://github.com/xaverianteamrobotics/ftcrobotcontroller). Click on the button named master, and type `testing-Your-Name` into the box. If it already exists, ask someone for help, but if not, click the Create Branch button.

Next, open GitHub Desktop. Go to *File > Options > Accounts* and log into GitHub. When you click the log in button, GitHub Desktop will open your browser to log in. Once you're logged in, go to *File > Clone repository* and find the XaverianTeamRobotics/FtcRobotController repository. **Keep track of the folder the repository will clone into found at the bottom of the clone dialog.** Then, click clone.

> Note:\
> Originally, we cloned repositories inside of Android Studio itself. This is harder as it requires the creation of a Personal Access Token. If you want to go that route, make sure you know what you're doing.

Now, open Android Studio. You should be greeted with a main menu, but you may open into a project if you had opened Android Studio and opened a project inside it previously. If you see the menu, click on Open and navigate to the folder you cloned the repository into before, and click Ok with the folder selected. If you load into a project, go to *File > Open* and find the folder, then click Ok with it selected. This will open the repository as a new project inside Android Studio.

Once everything loads in, sync your Gradle environment. First, go to *File > Project Structure > Project* and make sure the Android Gradle Plugin Version is 7.0.3 and the Gradle Version is 7.0.2. Then close that window and click Sync project with Gradle Files at the top right of the editor (it's just an icon, so you'll have to hover over it to find the right one). After a few minutes Gradle should be set up and ready to go. Then, navigate to the bottom right of the editor and find the button named "master". Click on that, and find the option labeled `origin/testing-Your-Name`. Click it, and click "Checkout". Once everything seems to have finished loading, I'd recommend syncing your Gradle environment again, just in case. Finally, close and reopen Android Studio to make sure everything's configured properly (you can restart your entire computer too, if you want).

> Note:\
> Android Studio is weird and buggy when it comes to using the Android SDK, and as a result syncing the environment might fail. Why? I have no idea. Ask for help if that happens to you.
> 
> If you can determine the bugs you experience are because of a licensing issue, [this](https://stackoverflow.com/questions/39760172/you-have-not-accepted-the-license-agreements-of-the-following-sdk-components) might help. None of the answers in that thread on their own work, but combining them you might figure it out. *Summarized answer for experts: Install the commandline SDK manager in Android Studio, then execute `$INSERT_ANDROID_SDK_LOCATION_HERE/cmdline-tools/latest/bin/sdkmanager --licenses`, then re-sync your Gradle environment.*

You're now in Android Studio with a working development environment! The next steps go over how to actually write code.

### Coding
> Note:\
> This section is old and probably won't work.

In Android Studio, you can find the directory you're working on in the Project tab on the left, and the file you're working on in the middle. 

In the Project tab, find the TeamCode folder and open it to find our code. Nativate to *org.firstinspires.ftc.teamcode.competition.opmodes.templates.linear* and open LinearTeleOpTemplate.java. Right click and click Copy to copy this file, or class. Now navigate to *org.firstinspires.ftc.teamcode.competition.opmodes.teleop* and paste the class inside the directory, renaming it to whatever you want. Now, double click to open the class and edit it.

Now we're going to finally start writing our first program, or OpMode. First of all, change:
```java
@TeleOp(name="LinearAutonomousTemplate", group="linear")
```
To:
```java
@TeleOp(name="TutorialYourName", group="tutorial")
```
Next, we're going to write the logic to control a tank drivetrain, but first, we should understand how this works. 

Basically, we have four motors on the physical robot. With code, these motors are considered `DcMotor`s. We then have a `Motor` which controls a `DcMotor`, controlling the physical motors themselves. Now, instead of having to control four `Motor`s, to move the tank's treads, we have a `Tank`, which controls the four `Motor`s needed.

Since we're going to be using a physical controller to drive this `Tank`, we're going to use a `TankTeleOpManager` which uses a `Gamepad` to drive a `Tank`. But, before we can manage a `Tank`, we need to tell the `TankTeleOpManager` what it can and cannot do. If the `TankTeleOpManager` tries to control a `Motor` that doesn't exist, for example, the OpMode will crash. So, we need to use a `TeleOpHWDevices` to define which devices the `TankTeleOpManager` can control.

> Note:\
> The existence of a drivetrain is a given with a `TankTeleOpManager`, meaning the rules in `TeleOpHWDevices` only apply to peripherals like a spinner, elevator, etc. Thus, we don't really need to worry much about this but still need it.

We also need to tell the `TankTeleOpManager` which controllers can control which parts of the robot, as multiple controllers can control a robot at once. We're going to use two `GamepadFunctions` for each controller, or `Gampad`. A `GamepadFunctions` defines the functions of a `Gamepad`. For example, if a `GamepadFunctions` says a `Gamepad` is allowed to use function 1, then any `Motor`s inside the `TankTeleOpManager` which are controlled by the `TankTeleOpManager`'s first function can only be controlled by a `Gamepad` allowed to control function 1. This way, we can determine which `Gamepad` can control which part of the robot, so multiple people can't try to control the same part of the robot at once.

Now it's time to code. While that may seem complex, we only actually need to write a few lines. First, find:
```java
    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        resetStartTime();
    }
```
Above `waitForStart();`, initalize two `GamepadFunctions` for each `Gamepad`:
```java
GamepadFunctions gamepad1Functions = new GamepadFunctions(gamepad1, true, false, false, false, false, false);
GamepadFunctions gamepad2Functions = new GamepadFunctions(gamepad2, false, false, false, false, false, false);
```
This will configure the rules for both `Gamepad`s, setting only function 1 of `Gamepad` 1 to be enabled and all other functions to be disabled. All we need to control a `Tank` with a `Gamepad` is one function, so that's all we need to enable.

Next, we need to initalize a `TeleOpHWDevices` to define the peripheral devices the `TankTeleOpManager` is allowed to manage, which in our case is none:
```java
TeleOpHWDevices devices = new TeleOpHWDevices(false, false, false, false, false, false);
```
Now, we need to initalize a new `TankTeleOpManager`:
```java
TankTeleOpManager manager = new TankTeleOpManager(telemetry, hardwareMap, gamepad1, gamepad2, gamepad1Functions, gamepad2Functions, devices);
```
Finally we have all our objects created and our robot ready to go. Move down below `resetStartTime();` and add this:
```java
while(opModeIsActive()) {
    manager.main();
}
```
While the OpMode is active, this will call the `TankTeleOpManager`'s `main()` method which will take in the current status of the `Gamepad` and translate it to voltage to send to each `Motor`. This will stop when the OpMode has been requested to stop, crashes, or the time runs out.

Lastly, we need to add the final line under the while loop:
```java
manager.stop();
```
This will stop the `TankTeleOpManager` and reset the `Motor`s it controls to their default state.

The entire class should look like this:
```java
package org.firstinspires.ftc.teamcode.competition.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.competition.utils.teleop.GamepadFunctions;
import org.firstinspires.ftc.teamcode.competition.utils.teleop.TankTeleOpManager;
import org.firstinspires.ftc.teamcode.competition.utils.teleop.TeleOpHWDevices;

@TeleOp(name="TutorialYourName", group="tutorial")
public class NameYouChose extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        GamepadFunctions gamepad1Functions = new GamepadFunctions(gamepad1, true, false, false, false, false, false);
        GamepadFunctions gamepad2Functions = new GamepadFunctions(gamepad2, false, false, false, false, false, false);
        TeleOpHWDevices devices = new TeleOpHWDevices(false, false, false, false, false, false);
        TankTeleOpManager manager = new TankTeleOpManager(telemetry, hardwareMap, gamepad1, gamepad2, gamepad1Functions, gamepad2Functions, devices);
        waitForStart();
        resetStartTime();
        while(opModeIsActive()) {
            manager.main();
        }
        manager.stop();
    }

}
```
Congratulations, you've written your first OpMode! Android Studio saves everything for you automatically, so you don't need to worry about that. It does not commit (keep track of the code's version) code for you, however, so you'll have to do that yourself. Dont worry though, we'll go over that later on in the guide. The next step is testing your OpMode on the robot to make sure it works properly.

### Testing
To test your code, you need to upload it to a robot. First of all, plug a USB-C cable into a robot's Controller Hub and your computer. 

Once the robot is connected to your computer, focus on the top of Android Studio's window. You should see a button labeled "TEAMCODE" and another button beside it labeled "NO DEVICE" or "DEVICE" or something along the lines of "CONTROLLER HUB". If it says "NO DEVICE" or "DEVICE", wait for it to be similar to "CONTROLLER HUB". Once that happens, click the triangle "Run" button to the right. This will build and install your code onto the robot.

> Note:\
> This will remove the current code on the robot. Make sure it's okay to overwrite the robot's current code before you do this!

Once the code is on the robot and it's ready to run, run it the same way you would any other OpMode. This isn't a detailed guide on how to run an OpMode, but you should do the following:
* Either make sure the current configuration is correct, or make a new configuration naming the motors by "rd1, rd2, ld1, ld2".
* Plug in a controller and turn it on.
* Choose the TeleOp OpMode named `TutorialYourName`.
* Run and start the OpMode. I would recommend waiting a few seconds before starting just in case.

Now, you can drive the robot! If you move the left and right joysticks, it should drive. Since this is a tank, the left stick controls the left motors, while the right stick controls the right.

### Publishing
Once you have code you want to share with the rest of the team, you need to **commit** and **push** the code. Before doing this, make sure you're on the correct branch. You can find this at the bottom right of Android Studio, in the same place as you found the "master" button while setting up Android Studio. If you're not on your branch, checkout to the correct one the same way you checked out to your branch from master.

Once you're ready to commit, go to the Commit tab on the left of the screen. Select the files you want to commit (the entire default changelist, and only the default changelist) and write a commit message, then click commit.

> Note:\
> If Android Studio tells you there are warnings, TODOS, erorrs, or something else, ignore it. It doesn't matter in this case.

To push, open GitHub desktop. You should load right back into your project's repository. At the top, you should see a button called "Push" or "Fetch". If it's "Fetch", click it and follow the dialogs to merge your changes with origin, if they exist. Once it's "Push", simply click it. Your code will be uploaded, or pushed, to your branch for the whole team to access!

### Final Notes
Congratulations, you've made your first contribution to the codebase! Assuming your code gets merged into the master branch, it will be used in competition. When you go to code again, make sure you update the project before getting started to sync your local copy with the shared GitHub repository. In Android Studio, you can do this in *Git > Update Project*. When your code gets to the point where it needs documentation, ask a programmer to make a documentation page and Javadocs for your code, or try it yourself if you know what you're doing.
