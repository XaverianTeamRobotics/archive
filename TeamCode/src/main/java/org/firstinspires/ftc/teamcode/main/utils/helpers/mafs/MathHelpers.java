package org.firstinspires.ftc.teamcode.main.utils.helpers.mafs;

public class MathHelpers {
    public static double mean(double... doubles) {
        double counter;
        counter = 0;

        for (double i:
             doubles) {
            counter += i;
        }

        return counter/(double) doubles.length;
    }

    public static int mean(int... ints) {
        int counter;
        counter = 0;

        for (int i:
                ints) {
            counter += i;
        }

        return counter/ints.length;
    }
}
