package org.firstinspires.ftc.teamcode.main.utils.autonomous.image.opencv.pipelines;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class StorageLocatorPipeline extends OpenCvPipeline {

    Mat YCrCb = new Mat();
    Mat targetChannel = new Mat();

    private volatile int channel = 1;

    public static final int LUMINANCE_CHANNEL = 0;
    public static final int RED_CHANNEL = 1;
    public static final int BLUE_CHANNEL = 2;

    public static final Scalar GREEN_RGB = new Scalar(0, 255, 0);

    private volatile int result = 1;

    /*
     * REGION SYSTEM
     *
     * TOP LEFT: rect1
     * TOP MIDDLE: rect2
     * TOP RIGHT: rect3
     *
     * MIDDLE LEFT: rect4
     * MIDDLE MIDDLE: rect5
     * MIDDLE RIGHT: rect6
     *
     * BOTTOM LEFT: rect7
     * BOTTOM MIDDLE: rect8
     * BOTTOM RIGHT: rect9
     */

    Mat rect1, rect2, rect3, rect4, rect5, rect6, rect7, rect8, rect9;
    final int inputWidth = 640;
    final int inputHeight = 360;
    final int subRegionHeight = inputHeight/3;
    final int subRegionWidth = inputWidth/3;

    final Point rect1_A = new Point(0, 0), rect1_B = new Point(rect1_A.x + subRegionWidth, rect1_A.y + subRegionHeight);
    final Point rect2_A = new Point(0, 0), rect2_B = new Point(rect2_A.x + subRegionWidth, rect2_A.y + subRegionHeight);
    final Point rect3_A = new Point(0, 0), rect3_B = new Point(rect3_A.x + subRegionWidth, rect3_A.y + subRegionHeight);
    final Point rect4_A = new Point(0, 0), rect4_B = new Point(rect4_A.x + subRegionWidth, rect4_A.y + subRegionHeight);
    final Point rect5_A = new Point(0, 0), rect5_B = new Point(rect5_A.x + subRegionWidth, rect5_A.y + subRegionHeight);
    final Point rect6_A = new Point(0, 0), rect6_B = new Point(rect6_A.x + subRegionWidth, rect6_A.y + subRegionHeight);
    final Point rect7_A = new Point(0, 0), rect7_B = new Point(rect7_A.x + subRegionWidth, rect7_A.y + subRegionHeight);
    final Point rect8_A = new Point(0, 0), rect8_B = new Point(rect8_A.x + subRegionWidth, rect8_A.y + subRegionHeight);
    final Point rect9_A = new Point(0, 0), rect9_B = new Point(rect9_A.x + subRegionWidth, rect9_A.y + subRegionHeight);

    int avg1, avg2, avg3, avg4, avg5, avg6, avg7, avg8, avg9;

    @Override
    public void init(Mat firstFrame) {
        convertToYCrCb(firstFrame);

        // These will always be persistent. Any change to them affects the parent and vice-versa
        rect1 = targetChannel.submat(new Rect(rect1_A, rect1_B));
        rect2 = targetChannel.submat(new Rect(rect2_A, rect2_B));
        rect3 = targetChannel.submat(new Rect(rect3_A, rect3_B));
        rect4 = targetChannel.submat(new Rect(rect4_A, rect4_B));
        rect5 = targetChannel.submat(new Rect(rect5_A, rect5_B));
        rect6 = targetChannel.submat(new Rect(rect6_A, rect6_B));
        rect7 = targetChannel.submat(new Rect(rect7_A, rect7_B));
        rect8 = targetChannel.submat(new Rect(rect8_A, rect8_B));
        rect9 = targetChannel.submat(new Rect(rect9_A, rect9_B));
    }

    @Override
    public Mat processFrame(Mat input) {
        convertToYCrCb(input);

        // Get the average value of every rect in our target channel
        avg1 = (int) Core.mean(rect1).val[0];
        avg2 = (int) Core.mean(rect2).val[0];
        avg3 = (int) Core.mean(rect3).val[0];
        avg4 = (int) Core.mean(rect4).val[0];
        avg5 = (int) Core.mean(rect5).val[0];
        avg6 = (int) Core.mean(rect6).val[0];
        avg7 = (int) Core.mean(rect7).val[0];
        avg8 = (int) Core.mean(rect8).val[0];
        avg9 = (int) Core.mean(rect9).val[0];

        // Find the max
        int max = Math.max(avg1,
                Math.max(avg2,
                        Math.max(avg3,
                                Math.max(avg4,
                                        Math.max(avg5,
                                                Math.max(avg6,
                                                        Math.max(avg7,
                                                                Math.max(avg8, avg9))))))));

        // Find where the max came from
        if (avg1 == max) result = 1;
        else if (avg2 == max) result = 2;
        else if (avg3 == max) result = 3;
        else if (avg4 == max) result = 4;
        else if (avg5 == max) result = 5;
        else if (avg6 == max) result = 6;
        else if (avg7 == max) result = 7;
        else if (avg8 == max) result = 8;
        else result = 9;

        return input;
    }

    public void convertToYCrCb(Mat toConvert) {
        Imgproc.cvtColor(toConvert, YCrCb, Imgproc.COLOR_RGB2YCrCb);
        Core.extractChannel(YCrCb, targetChannel, channel);
    }

    public void setChannel(int channelID) {
        channel = channelID;
    }

    public int getResult() {
        return result;
    }
}
