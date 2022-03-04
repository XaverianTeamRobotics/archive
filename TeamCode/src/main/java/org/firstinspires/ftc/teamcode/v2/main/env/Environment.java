package org.firstinspires.ftc.teamcode.v2.main.env;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * This defines the persistent environment of the robot. It holds static values which are intended to be used as globals. It should not be instantiated.
 */
public class Environment {

    private static HardwareMap currentHardwareMap = null;
    private static final String SETTER_TOKEN = "Ymd1ZWd0MzEweTl1Z2oyNGJqdXZuODlyM3VyOHYyaWt0LnQuLXA0Lnk7cHJoajk4ZzNodGlxZWc$paJx2IRk4Qk5uRMXATkFEq6KIxdtLtRmeMQRlay9R8YuC+ajBnLWRrnqOn1fURwibMfMaKZYx6RZUYgwWqTiyg";

    /**
     * Private constructor to prevent instantiation of the environment, as it should only contain static members. Do not make this public, <em>cough</em> <em>cough</em> Michael <em>cough</em>.
     */
    private Environment() {}

    /**
     * Sets the environment {@link #currentHardwareMap} variable. This is called by OpModes automatically as they are ran. It requires a token which can be found in the Environment's source file. This helps prevent people from calling this by accident, as it basically ensures people know what they're doing before calling this.
     * @param map The hardware map
     * @param token The required token
     * @throws IllegalArgumentException Thrown when the token is incorrect
     */
    public void setHardwareMap(HardwareMap map, String token) throws IllegalArgumentException {
        if(!token.equals(SETTER_TOKEN)) {
            throw new IllegalArgumentException("The token is incorrect! Are you sure you know what you're doing? If you don't, please do not call this.");
        }
        currentHardwareMap = map;
    }

    /**
     * Gets the hardware map currenty stored in {@link #currentHardwareMap}, with null safety. In this case, attempting to get the hardware map while it is null will throw an exception instead of creating a non-null map. This is because there is almost no reason whatsoever in which you would want to get a null hardware map from the environment, nor is there a reason justifying this method's creaton of a new map. If for some reason you do need to disable null safety, use {@link #getHardwareMapDangerously()}.
     * @return The current hardware map
     * @throws NullPointerException Thrown when the map is null
     */
    public HardwareMap getHardwareMap() throws NullPointerException {
        if(currentHardwareMap == null) {
            throw new NullPointerException("The hardware map to return is null, was it set to the OpMode's given hardware map at the start of the OpMode?");
        }
        return currentHardwareMap;
    }

    /**
     * {@link #getHardwareMap()} without null safety.
     */
    public HardwareMap getHardwareMapDangerously() {
        return currentHardwareMap;
    }

}
