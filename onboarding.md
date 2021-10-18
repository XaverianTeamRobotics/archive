# Onboarding
Welcome to the Hawks Robotics Onboarding Guide. This is a tutorial on how to get set up with the repository, running Android Studio, coding, testing, and uploading your code.

### Installation
First, if you don't already have one, make a [GitHub](https://github.com) account. From there, ask Roche or Tassinari to invite you to the GitHub organization, and accept the invite.

Then, head over to [Git Bash](https://git-scm.com/downloads) and install it. Then install [JDK 16](https://www.oracle.com/java/technologies/downloads/) (not 17!) and [Android Studio](https://developer.android.com/studio).

> Note:
> If you have apt you can use `sudo apt install git openjdk-16-jdk -y` for Git and JDK, but you still have to download Android Studio from the website.

Lastly, download [GitHub Desktop](https://desktop.github.com). If you're on Linux, you can use [this fork](https://github.com/shiftkey/desktop/releases) as a replacement for the original. Obviously if you're already comfortable with Git and know how to use the command line you don't need to install GitHub desktop though.

### Setup
Open Command Prompt as an administrator (Windows) or Terminal (macOS & Linux), then run:
```git
# Windows

git config user.name "your github username"
git config user.email "your github email address"

# macOS & Linux

sudo git config user.name "your github username"
sudo git config user.email "your github email address"
```
From there, log into GitHub and head over to [our repository](https://github.com/xaverianteamrobotics/ftcrobotcontroller). Click on the button named master, type `testing-Your-Name` into the box, and click create branch.

Next, go to [GitHub's PAT ceation page](https://github.com/settings/tokens/new) and make a new token with the following scopes:
* repo
* workflow
* admin:org
* user

Once this token is generated, copy it and keep it somewhere. **You will lose access to the token once you close the page, so make sure it's copied.** Then, open Android Studio and go to *Customize > All Settings > Version Control > GitHub* and click + to add a new GitHub account. Use the token you just generated to log in.

Now, close Settings and go to the main menu, and click Get From VCS. Click on the GitHub tab and click on the FtcRobotController repository, then press Clone to download it. Keep track of the directory it's cloning to, because you'll need that later. Once you're in the project in Android Studio, head down to the bottom right of the editor and click the branch button (it should be named master). Find the branch titled `origin/testing-Your-Name` and click Checkout.

Once everything loads in, sync your Gradle environment. First, go to *File > Project Structure > Project* and make sure both versions are 7.0.2. Then close that window and click Sync project with Gradle Files at the top right of the editor (it's just an icon, so you'll have to hover over it to find the right one). After a few minutes Gradle should be set up and ready to go. To finish, just close and reopen Android Studio.

You're now in Android Studio with a working development environment! The next steps go over how to actually do things with the code.

### Coding
In Android Studio, you can find the directory you're working on in the Project tab on the left, and the file you're working on in the middle. This isn't a guide on how to use Android Studio nor is it a guide on how to use Java, so we'll only go over the basics to open a file and edit it. 

In the Project tab, find the TeamCode folder and open it to find our code. From there just use references on this site, the Javadocs, and the examples found in the FtcRobotController folder to learn how to code. Android Studio saves everything for you automatically, so you don't need to worry about that. It does not commit code for you, however, so you'll have to do that yourself. Dont worry though, we'll go over that later on in the guide.

### Testing
To test your code, you need to upload it to a robot. Plug a USB-C cable into a robot and, once the robot is connected to your computer, run the Teamcode run configuration at the top middle of the editor. This will build and install the code onto the robot. 

Once the code is on the robot and it's ready to run, run it the same way you would any other OpMode.

### Publishing
Once you have code you want to share with the rest of the team, you need to **commit** and **push** the code. Before doing this, make sure you're on the correct branch. You can find this at the bottom right, in the same place as you found the master branch while setting up Android Studio. If you're not on your branch, checkout to the correct one.

Once you're ready to commit, go to the Commit tab on the left of the screen. Select the files you want to commit from the default changelist and write a commit message, then click commit.

To push, open GitHub desktop. While Android Studio *can* push code, oftentimes it doesn't like to and will return an error if you try. Inside GitHub desktop, assuming it isn't already tracking your local copy of the repository, import the folder containing the project you were working on in Android Studio. Then you can click Push to push your changes to the public repository.

### Final Notes
Congratulations, you've made your first contribution to the codebase! Assuming your code gets merged into the master branch, it will be used in competition. When you go to code again, make sure you update the project before getting started to sync your local copy with the shared GitHub repository. In Android Studio, you can do this in *Git > Update Project*. When your code gets to the point where it needs documentation, ask me (Thomas Ricci) to make a documentation page and Javadocs for your code, or try it yourself if you know what you're doing.