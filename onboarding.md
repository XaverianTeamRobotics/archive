# Onboarding
Welcome to the Hawks Robotics Onboarding Guide. This shows you how to get set up with the repository, running Android Studio, coding, testing, and uploading your code. In this tutorial, we'll create a development environment, make a branch, write a program to control a robot with a tank drivetrain, upload it, and document it to explain how to use it.

### Installation
First, if you don't already have one, make a [GitHub](https://github.com) account. From there, ask Roche or Tassinari to invite you to the GitHub organization, and accept the invite. You also need to be added to the organization's team.

Then, head over to [Git Bash](https://git-scm.com/downloads) and install it. Then install [JDK 16](https://www.oracle.com/java/technologies/downloads/) (not 17!) and [Android Studio](https://developer.android.com/studio).

> Note:
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
> Note:
> If you've never used a command line before, to run a command simply type the command in and press enter. If you're on macOS or Linux, you may need to enter your password after you press enter. Wrap your username and email in quotes. For example, one might write `git config --global user.name "Foo Bar"` and `git config --global user.email "foobar@example.com"`.

From there, log into GitHub and head over to [our repository](https://github.com/xaverianteamrobotics/ftcrobotcontroller). Click on the button named master, and type `testing-Your-Name` into the box. If it already exists, ask someone for help, but if not, click the Create Branch button.

Next, open GitHub Desktop. Go to *File > Options > Accounts* and log into GitHub. When you click the log in button, GitHub Desktop will open your browser to log in. Once you're logged in, go to *File > Clone repository* and find the XaverianTeamRobotics/FtcRobotController repository. **Keep track of the folder the repository will clone into found at the bottom of the clone dialog.** Then, click clone.

> Note:
> Originally, we cloned repositories inside of Android Studio itself. This is harder as it requires the creation of a Personal Access Token. If you want to go that route, make sure you know what you're doing.

Now, open Android Studio. You should be greeted with a main menu, but you may open into a project if you had opened Android Studio and opened a project inside it previously. If you see the menu, click on Open and navigate to the folder you cloned the repository into before, and click Ok with the folder selected. If you load into a project, go to *File > Open* and find the folder, then click Ok with it selected. This will open the repository as a new project inside Android Studio.

Once everything loads in, sync your Gradle environment. First, go to *File > Project Structure > Project* and make sure the Android Gradle Plugin Version is 7.0.3 and the Gradle Version is 7.0.2. Then close that window and click Sync project with Gradle Files at the top right of the editor (it's just an icon, so you'll have to hover over it to find the right one). After a few minutes Gradle should be set up and ready to go. Then, navigate to the bottom right of the editor and find the button named "master". Click on that, and find the option labeled `origin/testing-Your-Name`. Click it, and click "Checkout". Once everything seems to have finished loading, I'd recommend syncing your Gradle environment again, just in case. Finally, close and reopen Android Studio to make sure everything's configured properly (you can restart your entire computer too, if you want).

> Note:
> Android Studio is weird and buggy when it comes to using the Android SDK, and as a result syncing the environment might fail. Why? I have no idea. Ask for help if that happens to you.

You're now in Android Studio with a working development environment! The next steps go over how to actually write code.

### Coding
In Android Studio, you can find the directory you're working on in the Project tab on the left, and the file you're working on in the middle. This isn't a guide on how to use Android Studio nor is it a guide on how to use Java, so we'll only go over the basics to open a file and edit it. 

In the Project tab, find the TeamCode folder and open it to find our code. From there just use guides on this site, the Javadocs, and the examples found in the FtcRobotController folder to learn how to code. Android Studio saves everything for you automatically, so you don't need to worry about that. It does not commit code for you, however, so you'll have to do that yourself. Dont worry though, we'll go over that later on in the guide.

### Testing
To test your code, you need to upload it to a robot. Plug a USB-C cable into a robot and, once the robot is connected to your computer, run the Teamcode run configuration at the top middle of the editor. This will build and install the code onto the robot. 

Once the code is on the robot and it's ready to run, run it the same way you would any other OpMode.

### Publishing
Once you have code you want to share with the rest of the team, you need to **commit** and **push** the code. Before doing this, make sure you're on the correct branch. You can find this at the bottom right, in the same place as you found the master branch while setting up Android Studio. If you're not on your branch, checkout to the correct one.

Once you're ready to commit, go to the Commit tab on the left of the screen. Select the files you want to commit from the default changelist and write a commit message, then click commit.

To push, open GitHub desktop. Inside GitHub desktop, assuming it isn't already tracking your local copy of the repository, import the folder containing the project you were working on in Android Studio. Then you can click Push to push your changes to the public repository.

### Final Notes
Congratulations, you've made your first contribution to the codebase! Assuming your code gets merged into the master branch, it will be used in competition. When you go to code again, make sure you update the project before getting started to sync your local copy with the shared GitHub repository. In Android Studio, you can do this in *Git > Update Project*. When your code gets to the point where it needs documentation, ask a programmer to make a documentation page and Javadocs for your code, or try it yourself if you know what you're doing.
