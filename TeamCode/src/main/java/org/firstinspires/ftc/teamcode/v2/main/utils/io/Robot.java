package org.firstinspires.ftc.teamcode.v2.main.utils.io;

import org.firstinspires.ftc.teamcode.v2.main.utils.env.Environment;
import org.firstinspires.ftc.teamcode.v2.main.utils.opmode.OperationMode;

/**
 * Represents the physical robot. A new instance is instantiated on every {@link OperationMode} run, and said instance should be accessed by {@link Environment#getRobot()}.
 */
public class Robot {

    /**
     * Creates a new robot, assuming the token provided is correct. It's not secret or anything; it just exists to prevent someone from accidentely instantiating a new robot.
     * @param token The token, or password, to instantiate this with.
     */
    public Robot(String token) {
        if(token.equals("Ymd1ZWd0MzEweTl1Z2oyNGJqdXZuODlyM3VyOHYyaWt0LnQuLXA0Lnk7cHJoajk4ZzNodGlxZWc$paJx2IRk4Qk5uRMXATkFEq6KIxdtLtRmeMQRlay9R8YuC+ajBnLWRrnqOn1fURwibMfMaKZYx6RZUYgwWqTiyg")) {
            // instantiate robot here
        }
    }

}
