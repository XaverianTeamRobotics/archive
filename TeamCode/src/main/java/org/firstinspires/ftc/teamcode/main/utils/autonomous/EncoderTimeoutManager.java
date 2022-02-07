package org.firstinspires.ftc.teamcode.main.utils.autonomous;

import com.qualcomm.robotcore.util.ElapsedTime;

public class EncoderTimeoutManager {

    public ElapsedTime elapsedTime = new ElapsedTime();
    public int durationMillis;

    public EncoderTimeoutManager(int durationMillis) {
        this.durationMillis = durationMillis;
    }

    public double getOperationTime() { return elapsedTime.milliseconds(); }

    public void restart() { elapsedTime.reset(); }

    public boolean hasTimedOut() {
        return elapsedTime.milliseconds() > durationMillis;
    }
}
